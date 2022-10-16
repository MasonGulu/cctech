local drive = peripheral.find("cassette_deck") or peripheral.find("reel_to_reel")
assert(drive, "No cassette deck or reel to reel connected!")

local arg = {...}

local function printHelp()
  print("Usage: ")
  print("load tapefn [diskfn]")
  print("save diskfn [tapefn]")
  print("delete tape-filename")
  print("seek target")
  print("list")
  print("wipe")
  print("size")
end

local function seek(target)
  if drive.seekAbs then
    drive.seekAbs(tonumber(target))
  else
    drive.seek(-drive.getSize())
    drive.seek(target) -- seek to destination
  end
end

--- Find the next sequence of 10 FF bytes
-- @treturn bool success
local function findNextHeader()
  local FFCount = 0
  local distance = 0
  while FFCount < 10 do -- look for 10 FF bytes in a row
    distance = distance + 1 -- just keep track of distance to occasionally yield
    local char = drive.read(1)
    if char == "\xff" then
      FFCount = FFCount + 1
    elseif char == "" then
      -- empty string indicates end of tape
      return false
    else
      FFCount = 0 -- this character wasn't an FF byte
    end
    if math.mod(distance, 500000) == 0 then
      sleep(0) -- yield to avoid a crash
    end
  end
  return true
end

--- Find the next file
-- @return filename or nil
-- @return file data
local function findNextFile()
  -- file will be saved with
  -- 10x FF bytes
  -- filename as a null terminated string
  -- 4 bytes LSB of file length
  -- File data
  if not findNextHeader() then
    return -- no more files on tape
  end
  -- at this point we're at the filename
  local filename = ""
  local lastChar = ""
  repeat
    lastChar = drive.read(1)
    assert(lastChar~="", "End of tape")
    if lastChar ~= "\0" then
      filename = filename..lastChar
    end
  until lastChar == "\0" -- null terminated filename
  -- we have the filename now
  local byteString = drive.read(4)
  local length = string.unpack("<I4", byteString) -- convert 4 characters to a 4 byte unsigned int
  return filename, length
end

local function findFile(filename)
  seek(0) -- goto start of tape
  local name, len
  repeat
    name, len = findNextFile()
    if name ~= filename and name ~= nil then
      drive.seek(len) -- this file isn't what we're looking for, skip it
    end
    if name ~= nil then
      print(string.format("Found %s", name))
    end
  until name == filename or name == nil
  return name, len
end

local function isSpaceEmpty(size)
  for i = 1, size do
    if drive.read(1) ~= "\0" then
      drive.seek(-i) -- seek back to where we were when this function was called
      return false
    end
  end
  drive.seek(-size) -- seek back to start
  return true
end

local function findEmptySpace(size)
  seek(0) -- goto start of drive
  while (not isSpaceEmpty(size)) do
    local name, s = findNextFile()
    assert(name~=nil, "Tape is full or has unknown data")
    drive.seek(s) -- skip over this file, next iteration isSpaceEmpty will run again
    sleep(0) -- yield
  end
end

local function main()
  if arg[1] == "load" then
    if not arg[2] then
      printHelp()
      return
    end
    arg[3] = arg[3] or arg[2] -- setup default output filename
    local f, e = fs.open(arg[3], "wb")
    assert(f, e) -- ensure the file is open, if it can't be, error with the reason
    seek(0) -- seek to beginning of tape
    local filename, len = findFile(arg[2])
    assert(filename == arg[2], "File not found")
    f.write(drive.read(len))
    f.close()
    print("File loaded")
    
  elseif arg[1] == "save" then
    if not arg[2] then
      printHelp()
      return
    end
    arg[3] = arg[3] or arg[2] -- setup default tape filename
    local f, e = fs.open(arg[2], "rb")
    assert(f, e)
    local str = f.readAll() -- read whole file into a string
    local len = str:len()
    findEmptySpace(10+4+#arg[3]+1+len) -- find an empty space on the tape large enough for this file

    drive.write(string.rep("\xff", 10)) -- write the 10 byte lead-in
    drive.write(arg[3]) -- tape filename
    drive.write("\0") -- null terminated string
    drive.write(string.pack("<I4", len)) -- length of file
    assert(drive.write(str), "End of tape reached") -- write data

    print("File written")
  elseif arg[1] == "seek" then
    if not tonumber(arg[2]) then
      printHelp()
      return
    end
    seek(tonumber(arg[2]))
    print("Seeked")
  elseif arg[1] == "delete" then
    if not arg[2] then
      printHelp()
      return
    end
    local name, len = findFile(arg[2])
    assert(name == arg[2], "File not found")
    drive.seek(-14 - (name:len()+1)) -- seek to start of lead-in
    drive.write(string.rep("\0", 14 + name:len()+1 + len)) -- wipe section of tape
    print("File deleted")

  elseif arg[1] == "list" then
    seek(0)
    local used = 0
    repeat
      local name, len = findNextFile()
      if (name ~= nil) then
        drive.seek(len) -- skip past this file
        print(name, len)
        used = used + len
        used = used + 14 -- 10 bytes FF + 4 bytes file length
        used = used + #name + 1 -- null terminated filename
      end
    until name == nil
    print(used, "used of", drive.getSize())

  elseif arg[1] == "wipe" then
    term.write("Are you sure (y/n)? ")
    local selection = read():lower()
    if selection == "y" then
      local size = drive.getSize()
      local n = 0
      term.write(("%8u / %u bytes wiped."):format(n, size))
      seek(0)
      local dataWipeSize = 10000
      while drive.write(string.rep("\0", dataWipeSize)) do
        n = math.min(n + dataWipeSize, size)
        local x, y = term.getCursorPos()
        term.clearLine()
        term.setCursorPos(1, y)
        term.write(("%8u / %u bytes wiped."):format(n, size))
        if (n == size) then
          break
        end

        sleep(0) -- yield
      end
      print()
    end
  elseif arg[1] == "size" then
    print(drive.getSize())
  else
    printHelp()
  end
end

main()

local drive = peripheral.find("cassette_deck") or peripheral.find("reel_to_reel")
assert(drive, "No cassette deck or reel to reel connected!")

local function printHelp()
  print("Usage: ")
  print("load tapefilename [diskfilename]")
  print("save diskfilename [tapefilename]")
  print("delete tapefilename")
  print("seek target")
  print("list")
  print("wipe")
end

local function seek(target)
  if drive.seekAbs then
    drive.seekAbs(tonumber(target))
  else
    while drive.seek(-100000) do
      -- seek to beginning
      sleep(0) -- yield so we don't crash
    end
    drive.seek(target) -- seek to destination
  end
end

--- Find the next sequence of 10 FF bytes
-- @treturn bool success
local function findNextHeader()
  local FFCount = 0
  local distance = 0
  while FFCount < 10 do
    distance = distance + 1
    local char = drive.read(1)
    if char == "\255" then
      FFCount = FFCount + 1
    elseif char == "" then
      -- empty string indicates end of tape
      return false
    else
      FFCount = 0
    end
    if math.mod(distance, 500000) == 0 then
      sleep(0)
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
    return
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
  until lastChar == "\0"
  -- we have the filename now
  local byteString = drive.read(4)
  local length = 0
  for i = 4, 1, -1 do
    length = bit32.lshift(length, 8) + string.byte(byteString, i)
  end
  return filename, length
end

local function lenToByteString(length)
  local str = ""
  for i = 1, 4 do
    str = str..string.char(bit32.band(length, 255))
    length = bit32.rshift(length, 8)
  end
  return str
end

local function findFile(filename)
  seek(0)
  local name, len
  repeat
    name, len = findNextFile()
    if name ~= filename and name ~= nil then
      drive.seek(len) -- skip past this file
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
    arg[3] = arg[3] or arg[2]
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
    arg[3] = arg[3] or arg[2]
    local f, e = fs.open(arg[2], "rb")
    assert(f, e)
    local str = f.readAll()
    findEmptySpace(str:len())

    drive.write(string.rep("\255", 10))
    drive.write(arg[3])
    drive.write("\0") -- null terminated string
    drive.write(lenToByteString(str:len())) -- length of file
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
    drive.seek(-14 - (name:len()+1))
    drive.write(string.rep("\0", 14 + name:len()+1 + len))
    print("File deleted")

  elseif arg[1] == "list" then
    seek(0)
    local name, len
    repeat
      name, len = findNextFile()
      if (name ~= nil) then
        drive.seek(len) -- skip past this file
        print(name)
      end
    until name == nil
  elseif arg[1] == "wipe" then
    term.write("Are you sure (y/n)? ")
    local selection = read():lower()
    if selection == "y" then
      seek(0)
      local i = 0
      while drive.write(string.rep("\0", 1000)) do
        sleep(0) -- yield
      end
    else
      print("User cancelled")
    end
  else
    printHelp()
  end
end

main()
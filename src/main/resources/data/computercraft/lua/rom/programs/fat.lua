-- FAT implementation

local FAT
FAT = {
  modes = {
    FAT = "FAT",
    FAT12 = "FAT12",
    FAT16 = "FAT16",
    FAT32 = "FAT32"
  },
  tostring = function(self)

  end,
  fromstring = function()

  end
}
FAT.new = function(device, mode) -- device should be a table of geometry
  local f = {}

  f.mode = mode

  f.boot = "\xEB\xFE\x90" -- x86 infinite loop code
  f.oem = "CCTech  " -- 8 byte OEM name
  f.bytesPerSector = 512
  f.sectorsPerCluster = 1
  f.reservedSectors = 1
  f.fatAmount = 2
  f.rootDirAmount = 1
  f.totalSectors = 0 -- when 0 indicates that the amount is stored in totalSectorsLarge
  f.mediaDescriptorType = 0x80
  f.sectorsPerFAT = 0 -- FAT12/FAT16 only
  f.sectorsPerTrack = device.sectorSize
  f.heads = device.heads
  f.hiddenSectors = 2
  f.totalSectorsLarge = 0

  local totalSectors = device.sectors * device.cylinders * device.heads
  if totalSectors > 0xFFFF then
    f.totalSectors = 0
    f.totalSectorsLarge = totalSectors
  end

  if mode == FAT.modes.FAT12 or mode == FAT.modes.FAT16 then
    f.driveNumber = 0x80
    f.label = (" "):rep(11)
    f.systemIdentifier = mode.."   "
  elseif mode == FAT.modes.FAT32 then
    f.sectorsPerFatLarge = 1
    f.flags = 0
    f.version = 0
    f.rootCluster = 2
    f.fsInfoCluster = 3
    f.backupBootSector = 4
    f.driveNumber = 0x80
    f.systemIdentifier = "FAT32   "
  end
end

local function createFAT(bytesPerSector,sectorsPerCluster,reservedSectors,FATAmount)
  local FAT_boot = "" -- x86 infinite loop code
  local FAT_oem = "CCTech  " -- 8 byte OEM name
  local FAT = string.pack("<HBHB",bytesPerSector,sectorsPerCluster,reservedSectors,FATAmount)
end
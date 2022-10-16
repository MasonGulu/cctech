--- class to make managing a fat file easier

local fatman = {}
local FAT = require("fatcommon")

local function getClusterSize(mode, clusters)
  -- print(textutils.serialize(FAT.DskTable[mode]))
  local clusterSize = 0
  for k,v in ipairs(FAT.DskTable[mode]) do
    if (v[1] <= clusters) then
      clusterSize = v[2]
    end
  end
  return clusterSize
end

function fatman.new(device, label)
  local self = {}
  local BS = {}
  local f = {BS = BS}
  f.device = device

  local deviceInfo = device.getGeometry()
  label = label or string.rep(" ", 11)

  --- https://academy.cba.mit.edu/classes/networking_communications/SD/FAT.pdf
  -- Offset starts at 0, this is the order that they appear in the boot sector
  BS.BS_jmpBoot = "\xEB\xFE\x90" -- 3 byte - x86 infinite loop code
  BS.BS_OEMName = "CCTech  " -- 8 byte - OEM name
  BS.BPB_bytsPerSec = deviceInfo.sectorSize -- 2 byte - count of bytes per sector
  BS.BPB_SecPerClus = 1 -- 1 byte - Sectors per cluster (must be power of 2)
  BS.BPB_RsvdSecCnt = 1 -- 2 byte - # of reserved sectors in reserved region
  BS.BPB_NumFATs = 2 -- 1 byte - Count of FAT tables on the drive
  BS.BPB_RootEntCnt = 512 -- 2 byte - FAT12/16 contains # of 32-byte directory entries in root dir
  -- when multiplied by 32 should result in an even multiple of BPB_BytesPerSec. FAT16 should use 512
  -- FAT32 must be set to 0.
  BS.BPB_TotSec16 = 0 -- 2 byte - 16-bit count of sectors, when 0 BPB_TotSec32 is used instead. For FAT32 must be 0.
  BS.BPB_Media = 0xF8 -- 1 byte - Media descriptor byte (0xF8 for fixed disks)
  BS.BPB_FATSz16 = 0 -- 2 byte - FAT12/16 count of sectors occupied by one FAT (FAT32 must be 0, at BPB_FATSz32 instead)
  BS.BPB_SecPerTrk = deviceInfo.sectors -- 2 byte - Sectors per track
  BS.BPB_NumHeads = deviceInfo.heads -- 2 byte - Number of heads
  BS.BPB_hiddSec = 2 -- 4 byte - count of hidden sectors preceding the FAT volume sector
  BS.BPB_TotSec32 = 0 -- 4 byte - total sector count (if BPB_TotSec16 is 0 this is used instead)

  local totalSectors = deviceInfo.sectors * deviceInfo.cylinders * deviceInfo.heads
  if totalSectors > 0xFFFF then -- handle cases when there are more than 0xFFFF sectors
    BS.BPB_TotSec16 = 0
    BS.BPB_TotSec32 = totalSectors
  end

  if totalSectors * deviceInfo.sectorSize < 536870912 then
    -- less than 512MB
    f.mode = FAT.modes.FAT16
  else
    f.mode = FAT.modes.FAT32
  end

  BS.BPB_SecPerClus = getClusterSize(f.mode, totalSectors) -- setup the amount of sectors per cluster based on Microsoft recommendations

  -- Offset should be 36 at this point
  if f.mode == FAT.modes.FAT12 or f.mode == FAT.modes.FAT16 then
    -- setup stuff so that the tables provided by MS work
    BS.BPB_RsvdSecCnt = 1
    BS.BPB_NumFATs = 2
    BS.BPB_RootEntCnt = 512

    BS.BS_DrvNum = 0x80 -- 1 byte - Drive number (0x80 or 0x00)
    BS.BS_Reserved1 = 0x00 -- 1 byte - Reserved
    BS.BS_BootSig = 0x29 -- 1 byte - Extended boot signature
    BS.BS_VolID = 0x0000 -- 4 byte - Volume serial number
    BS.BS_VolLab = label..string.rep(" ", 11 - string.len(label)) -- 11 byte - Volume label (matches 11-byte volume label in root dir)
    BS.BS_FilSysType = f.mode.."   " -- 8 byte - FAT identifying string, don't use for identification
  elseif f.mode == FAT.modes.FAT32 then
    -- setup stuff so that the tables provided by MS work
    BS.BPB_RsvdSecCnt = 32
    BS.BPB_NumFATs = 2
    BS.BPB_RootEntCnt = 0 -- must be set to 0 for FAT32

    BS.BPB_FATSz32 = 1 -- 4 byte - FAT32 count of sectors occupied by 1 FAT
    BS.BPB_ExtFlags = 0 -- 2 byte - bits 0-3 are index of active FAT; bit 7 = 0 when FAT is mirrored (1 means FAT indicated by 0-3 is active)
    BS.BPB_FSVer = 0 -- 2 byte - FAT32 version (set to 0x0000)
    BS.BPB_RootClus = 2 -- 4 byte - cluster number of first cluster of root dir
    BS.BPB_FSInfo = 1 -- 2 byte - sector number of FSINFO structure in reserved area of FAT32. usually 1
    BS.BPB_BkBootSec = 0 -- 2 byte - sector number in reserved area of a copy of the boot record
    BS.BPB_Reserved = 0 -- 12 byte - reserved, set to 0
    BS.BS_DrvNum = 0x80 -- 1 byte - drive number
    BS.BS_Reserved1 = 0 -- 1 byte - reserved
    BS.BS_BootSig = 0x29 -- 1 byte - extended boot signature
    BS.BS_VolID = 0 -- 4 byte - Volume serial number
    BS.BS_VolLab = label..string.rep(" ", 11 - string.len(label)) -- 11 byte - Volume label (matches 11-byte volume label in root dir)
    BS.BS_FilSysType = "FAT32   " -- 8 byte - File system type, informational only

    BS.BPB_TotSec16 = 0 -- FAT32 mandates using BPB_TotSec32 instead
    BS.BPB_TotSec32 = totalSectors
  end
  -- Offset 510

  -- setup BPB_FATSz16 / BPB_FATSz32, adapted from MS document
  local RootDirSectors = math.floor(((BS.BPB_RootEntCnt * 32) + (BS.BPB_bytsPerSec - 1)) / BS.BPB_bytsPerSec)
  local TmpVal1 = totalSectors - (BS.BPB_RsvdSecCnt + RootDirSectors)
  local TmpVal2 = (256 * BS.BPB_SecPerClus) + BS.BPB_NumFATs
  if (f.mode == FAT.modes.FAT32) then
    TmpVal2 = math.floor(TmpVal2 / 2)
  end
  local FATSz = math.floor((TmpVal1 + (TmpVal2 - 1)) / TmpVal2)
  if (f.mode == FAT.modes.FAT32) then
    BS.BPB_FATSz16 = 0
    BS.BPB_FATSz32 = FATSz
  else
    BS.BPB_FATSz16 = FATSz
  end

  BS.Signature_word = 0x55AA -- 2 byte - signature

  -- create FAT table
  local dataSectors = totalSectors - (BS.BPB_RsvdSecCnt + (BS.BPB_NumFATs * FATSz) + RootDirSectors)
  local clusterCount = math.floor(dataSectors / BS.BPB_SecPerClus)

  f.FAT = {}
  for i = 0, clusterCount-1 do -- 0 indexed because C
    f.FAT[i] = 0 -- setup an empty FAT table
  end

  f.FAT[0] = ({[FAT.modes.FAT12]=0xFF8,[FAT.modes.FAT16]=0xFFF8,[FAT.modes.FAT32]=0xFFFFFFF8})[f.mode]
  -- BPB_Media byte

  self.BS = BS

  return self
end

return fatman
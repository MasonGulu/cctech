-- FAT implementation

local FAT = {}
FAT.modes = {
  FAT = "FAT",
  FAT12 = "FAT12",
  FAT16 = "FAT16",
  FAT32 = "FAT32"
}
FAT.DskTable = {
  [FAT.modes.FAT12] = { -- this table is completely made up
    {700,1}, -- up to ~360KB, 512B clusters
    {1400,2}, -- up to ~716KB, 1k clusters
    {4000,4}, -- up to 2MB, 2k clusters
    {8400,8}, -- up to 4.1MB, 4k clusters
    {16000,16}, -- up to 8MB, 8k clusters
    {0xFFFFFFFF, 0} -- Intentional error
  },
  -- these tables are taken from https://academy.cba.mit.edu/classes/networking_communications/SD/FAT.pdf
  [FAT.modes.FAT16] = {
    {8400,0}, -- up to 4.1MB, intentional error
    {32680,2}, -- up to 16MB, 1k clusters
    {262144,4}, -- up to 128MB, 2k clusters
    {524288,8}, -- up to 256MB, 4k clusters
    {1048576,16}, -- up to 512MB, 8k clusters
    -- sizes below this shouldn't be used
    {2097152,32}, -- up to 1GB, 16k clusters
    {4194304,64}, -- up to 2GB, 32k clusters
    {0xFFFFFFFF, 0} -- > 2GB, intentional error
  },
  [FAT.modes.FAT32] = {
    {66600,0}, -- up to 32.5MB, intentional error
    {532480,1}, -- up to 260MB, 512B cluster
    {16777216,8}, -- up to 8GB, 4k cluster
    {33554432,16}, -- up to 16GB 8k cluster
    {67108864,32}, -- up to 32GB, 16k cluster
    {0xFFFFFFFF, 64} -- > 32GB, 32k cluster
  }
}
FAT._index = FAT
function FAT.new(device, label) -- device should be a table of geometry
  local BS = {}
  local f = {BS = BS}

  local deviceInfo = device.getGeometry()
  label = label or string.rep(" ", 11)

  --- https://academy.cba.mit.edu/classes/networking_communications/SD/FAT.pdf
  -- Offset starts at 0
  BS.BS_jmpBoot = "\xEB\xFE\x90" -- 3 byte - x86 infinite loop code
  BS.BS_OEMName = "CCTech  " -- 8 byte - OEM name
  BS.BPB_bytsPerSec = 512 -- 2 byte - count of bytes per sector
  BS.BPB_SecPerClus = 1 -- 1 byte - Sectors per cluster (must be power of 2)
  BS.BPB_RsvdSecCnt = 1 -- 2 byte - # of reserved sectors in reserved region
  BS.BPB_NumFATs = 2 -- 1 byte - Count of FAT tables on the drive
  BS.BPB_RootEntCnt = 1 -- 2 byte - FAT12/16 contains # of 32-byte directory entries in root dir
  -- when multiplied by 32 should result in an even multiple of BPB_BytesPerSec. FAT16 should use 512
  -- FAT32 must be set to 0.
  BS.BPB_TotSec16 = 0 -- 2 byte - 16-bit count of sectors, when 0 BPB_TotSec32 is used instead. For FAT32 must be 0.
  BS.BPB_Media = 0xF8 -- 1 byte - Media descriptor byte (0xF8 for fixed disks)
  BS.BPB_FATSz16 = 0 -- 2 byte - FAT12/16 count of sectors occupied by one FAT (FAT32 must be 0, at BPB_FATSz32 instead)
  BS.BPB_SecPerTrk = deviceInfo.sectorSize -- 2 byte - Sectors per track
  BS.BPB_NumHeads = deviceInfo.heads -- 2 byte - Number of heads
  BS.BPB_hiddSec = 2 -- 4 byte - count of hidden sectors preceding the FAT volume sector
  BS.BPB_TotSec32 = 0 -- 4 byte - total sector count (if BPB_TotSec16 is 0 this is used instead)

  local totalSectors = deviceInfo.sectors * deviceInfo.cylinders * deviceInfo.heads
  if totalSectors > 0xFFFF then -- handle cases when there are more than 0xFFFF sectors
    BS.BPB_TotSec16 = 0
    BS.BPB_TotSec32 = totalSectors
  end

  -- Offset should be 36 at this point
  if f.mode == FAT.modes.FAT12 or f.mode == FAT.modes.FAT16 then
    BS.BS_DrvNum = 0x80 -- 1 byte - Drive number (0x80 or 0x00)
    BS.BS_Reserved1 = 0x00 -- 1 byte - Reserved
    BS.BS_BootSig = 0x29 -- 1 byte - Extended boot signature
    BS.BS_VolID = 0x0000 -- 4 byte - Volume serial number
    BS.BS_VolLab = label..string.rep(" ", 11 - string.len(label)) -- 11 byte - Volume label (matches 11-byte volume label in root dir)
    BS.BS_FilSysType = BS.mode.."   " -- 8 byte - FAT identifying string, don't use for identification
  elseif f.mode == FAT.modes.FAT32 then
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

  BS.Signature_word = 0x55AA -- 2 byte - signature
  setmetatable(f, FAT)
  return f
end

-- common constants for FAT stuff

local FAT = {}
FAT.modes = {
  FAT = "FAT",
  FAT12 = "FAT12",
  FAT16 = "FAT16",
  FAT32 = "FAT32"
}
FAT.DskTable = {
  [FAT.modes.FAT12] = { -- this table is completely made up
    -- {700,1}, -- up to ~360KB, 512B clusters
    -- {1400,2}, -- up to ~716KB, 1k clusters
    -- {8400,16}, -- up to 4.1MB, 8k clusters
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
FAT.FATLookup = {
  [FAT.modes.FAT12] = {
    FREE = 0,
    EOF = 0xFFF,
    BAD = 0xFF7
  },
  [FAT.modes.FAT16] = {
    FREE = 0,
    EOF = 0xFFFF,
    BAD = 0xFFF7
  },
  [FAT.modes.FAT32] = {
    FREE = 0,
    EOF = 0xFFFFFFFF,
    BAD = 0xFFFFFFF7
  }
}
return FAT
-- FAT implementation

local FAT = {}


FAT._index = FAT
-- create a new fat object on a device


local tmp = {
  getGeometry = function()
    return {cylinders = 96, heads = 64, sectors = 32, sectorSize = 512, logicalBlocks = 196608}
  end
}

local test = FAT.new(tmp, "test")
for k,v in pairs(test) do
  print(k, v)
end
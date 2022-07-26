package github.shrekshellraiser.cctech.common.peripheral.sectormedia;

import github.shrekshellraiser.cctech.common.item.sectormedia.SectorItem;
import github.shrekshellraiser.cctech.common.peripheral.StorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SectorBlockEntity extends StorageBlockEntity {
    public SectorBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    private int CHSToLBA(int cylinder, int head, int sector) {
        SectorItem item = ((SectorItem)this.getItem());
        int sectors = item.getSectors(); // sectors start at 1
        int heads = item.getHeads();
        return (cylinder*heads+head) * sectors + (sector - 1);
    }

    private boolean notOnDisk(int cylinder, int head, int sector) {
        SectorItem item = ((SectorItem)this.getItem());
        int sectors = item.getSectors(); // sectors start at 1
        int heads = item.getHeads();
        int cylinders = item.getCylinders(); // cylinders are tracks
        return sector < 1 || sector > sectors || head < 0 || head >= heads || cylinder < 0 || cylinder >= cylinders;
    }

    private boolean notOnDisk(int logicalBlock) {
        SectorItem item = ((SectorItem)this.getItem());
        int sectors = item.getSectors(); // sectors start at 1
        int heads = item.getHeads();
        int cylinders = item.getCylinders(); // cylinders are tracks
        return logicalBlock < 0 || logicalBlock > CHSToLBA(cylinders-1, heads-1, sectors);
    }

    public byte[] read(int cylinder, int head, int sector) {

        int logicalBlock = CHSToLBA(cylinder, head, sector);
        return read(logicalBlock);
    }
    public byte[] read(int logicalBlock) {
        if (!(this.getItem() instanceof SectorItem item))
            return new byte[0];
        if (notOnDisk(logicalBlock))
            return null; // indicate an invalid position by returning null
        int sectorSize = item.getSectorSize();
        int LBALocation = logicalBlock * sectorSize;
        byte[] data = new byte[sectorSize];
        System.arraycopy(this.data, LBALocation, data, 0, sectorSize);
        return data;
    }

    public writeStatus write(int cylinder, int head, int sector, byte[] data) {
        int logicalBlock = CHSToLBA(cylinder,head,sector);
        return write(logicalBlock, data);
    }
    public writeStatus write(int logicalBlock, byte[] data) {
        SectorItem item = ((SectorItem)this.getItem());
        int sectorSize = item.getSectorSize();
        if (notOnDisk(logicalBlock))
            return writeStatus.INVALID; // invalid position or data size is wrong
        if (data.length > sectorSize)
            return writeStatus.TOO_MUCH;
        int LBALocation = logicalBlock * sectorSize;
        System.arraycopy(data, 0, this.data, LBALocation, data.length);
        int emptySpace = sectorSize - data.length;
        if (emptySpace > 0) {
            System.arraycopy(new byte[emptySpace], 0, this.data, LBALocation + data.length, emptySpace);
            // empty the rest of the sector
        }
        return writeStatus.SUCCESS;
    }
    public enum writeStatus {
        INVALID, SUCCESS, FAILURE, TOO_MUCH
    }

    public int getSectorSize() {
        return ((SectorItem)this.getItem()).getSectorSize();
    }
}


package github.shrekshellraiser.cctech.common.peripheral.sectormedia;

import cc.tweaked.internal.cobalt.LuaTable;
import cc.tweaked.internal.cobalt.LuaValue;
import cc.tweaked.internal.cobalt.ValueFactory;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.ObjectLuaTable;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.common.item.sectormedia.SectorItem;
import github.shrekshellraiser.cctech.common.peripheral.StorageBlockEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SectorPeripheral implements IPeripheral {
    private final List<IComputerAccess> connectedComputers = new ArrayList<>();

    protected final SectorBlockEntity tileEntity;

    protected SectorPeripheral(SectorBlockEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other;
    }

    @Override
    public void detach(@Nonnull IComputerAccess computer) {
        connectedComputers.remove(computer);
    }

    @Override
    public void attach(@Nonnull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    public void deviceInserted() {
        for (IComputerAccess computer : connectedComputers) {
            computer.queueEvent("disk_inserted", computer.getAttachmentName());
        }
    }
    public void deviceRemoved() {
        for (IComputerAccess computer : connectedComputers) {
            computer.queueEvent("disk_removed", computer.getAttachmentName());
        }
    }

    @LuaFunction
    public final String readCHS(int cylinder, int head, int sector) throws LuaException {
        byte[] data = tileEntity.read(cylinder,head,sector);
        if (data == null)
            throw new LuaException("Invalid location");
        return new String(data, StandardCharsets.ISO_8859_1);
    }
    @LuaFunction
    public final String readLBA(int LBA) throws LuaException {
        byte[] data = tileEntity.read(LBA);
        if (data == null)
            throw new LuaException("Invalid location");
        return new String(data, StandardCharsets.ISO_8859_1);
    }
    @LuaFunction
    public final boolean writeCHS(int cylinder, int head, int sector, String data) throws LuaException {
        byte[] dataBytes = data.getBytes(StandardCharsets.ISO_8859_1);
        SectorBlockEntity.writeStatus status = tileEntity.write(cylinder, head, sector, dataBytes);
        return switch (status) {
            case INVALID -> throw new LuaException("Invalid location");
            case SUCCESS -> true;
            case FAILURE -> false;
            case TOO_MUCH -> throw new LuaException("Data too large");
        };
    }
    @LuaFunction
    public final boolean writeLBA(int LBA, String data) throws LuaException {
        byte[] dataBytes = data.getBytes(StandardCharsets.ISO_8859_1);
        SectorBlockEntity.writeStatus status = tileEntity.write(LBA, dataBytes);
        return switch (status) {
            case INVALID -> throw new LuaException("Invalid location");
            case SUCCESS -> true;
            case FAILURE -> false;
            case TOO_MUCH -> throw new LuaException("Data too large");
        };
    }
    @LuaFunction
    public final boolean hasDevice(){
        return tileEntity.getItem() instanceof SectorItem;
    }
    @LuaFunction
    public final Map<String, Integer> getGeometry() {
        Map<String, Integer> geometry = new HashMap<>();
        if (!(tileEntity.getItem() instanceof SectorItem))
            return null;
        SectorItem item = (SectorItem) tileEntity.getItem().asItem();
        geometry.put("sectors", item.getSectors());
        geometry.put("cylinders", item.getCylinders());
        geometry.put("heads", item.getHeads());
        geometry.put("sectorSize", item.getSectorSize());
        geometry.put("logicalBlocks", item.getCylinders() * item.getHeads() * item.getSectors());
          // number of logical blocks (indexed from 0) so max is N-1
        return geometry;
    }
}

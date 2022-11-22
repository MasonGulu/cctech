package github.shrekshellraiser.cctech.common.peripheral.tape.reel;

import cc.tweaked.internal.cobalt.Lua;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.common.item.tape.ReelItem;
import github.shrekshellraiser.cctech.common.peripheral.NoDeviceException;
import github.shrekshellraiser.cctech.common.peripheral.tape.TapeBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.TapePeripheral;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReelToReelPeripheral extends TapePeripheral {

    public ReelToReelPeripheral(TapeBlockEntity tileEntity) {
        super(tileEntity);
    }

    @NotNull
    @Override
    public String getType() {
        return "reel_to_reel";
    }

    @LuaFunction
    public final MethodResult seekAbs(IComputerAccess computerAccess, int loc, Optional<Boolean> async) throws LuaException {
        return tileEntity.seekAbs(computerAccess, loc, async.orElse(false));
    }

    @LuaFunction
    public final int getPos() {
        return ((ReelToReelBlockEntity)tileEntity).getPointer();
    }

    @LuaFunction
    public final boolean lock(Optional<Boolean> lock) throws LuaException {
        boolean newState;
        newState = lock.orElseGet(() -> !((ReelToReelBlockEntity) tileEntity).isLocked);
        return ((ReelToReelBlockEntity) tileEntity).setLock(newState);
    }
}

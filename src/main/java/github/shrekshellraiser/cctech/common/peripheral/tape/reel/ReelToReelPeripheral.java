package github.shrekshellraiser.cctech.common.peripheral.tape.reel;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
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
    public final int seekAbs(int loc) throws LuaException {
        try {
            return tileEntity.seekAbs(loc);
        } catch(NoDeviceException e) {
            throw new LuaException("No tape in drive");
        }
    }

    @LuaFunction
    public final int getPos() {
        return ((ReelToReelBlockEntity)tileEntity).getPointer();
    }
}

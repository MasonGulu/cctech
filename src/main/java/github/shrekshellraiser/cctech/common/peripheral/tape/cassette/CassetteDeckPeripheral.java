package github.shrekshellraiser.cctech.common.peripheral.tape.cassette;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.peripheral.tape.TapePeripheral;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CassetteDeckPeripheral extends TapePeripheral {
    // TODO add check for if door is open
    public CassetteDeckPeripheral(CassetteDeckBlockEntity tileEntity) {
        super(tileEntity);
    }

    @NotNull
    @Override
    public String getType() {
        return "cassette_deck";
    }
}

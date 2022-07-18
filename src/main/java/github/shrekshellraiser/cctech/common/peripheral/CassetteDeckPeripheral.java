package github.shrekshellraiser.cctech.common.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.common.blockentities.tape.CassetteDeckBlockEntity;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CassetteDeckPeripheral implements IPeripheral {

    private final List<IComputerAccess> connectedComputers = new ArrayList<>();

    private final CassetteDeckBlockEntity tileEntity;

    public CassetteDeckPeripheral(CassetteDeckBlockEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return "cassette_deck";
    }

    @Override
    public boolean equals(IPeripheral other) {
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

    @LuaFunction
    public final boolean hasTape() {
        return tileEntity.getItem() instanceof CassetteItem;
    }

    @LuaFunction
    public final String read(int characters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < characters; i++) {
            String readChar = tileEntity.readChar();
            stringBuilder.append(readChar);
            if (readChar.equals("")) break;
        }
        return stringBuilder.toString();
    }

    @LuaFunction
    public final boolean write(String ch) {
        char[] chars = ch.toCharArray();
        for (char c : chars) {
            if (!tileEntity.writeChar(c))
                return false;
        }
        return true;
    }

    @LuaFunction
    public final boolean seek(int offset) {
        return tileEntity.seekRel(offset);
    }
    @LuaFunction
    public final boolean setLabel(String label) {
        return tileEntity.setLabel(label);
    }
    @LuaFunction
    public final boolean clearLabel() {
        return tileEntity.clearLabel();
    }
}

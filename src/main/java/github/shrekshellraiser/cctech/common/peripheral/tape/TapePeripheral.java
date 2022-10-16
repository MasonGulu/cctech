package github.shrekshellraiser.cctech.common.peripheral.tape;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.common.item.tape.CassetteItem;
import github.shrekshellraiser.cctech.common.peripheral.NoDeviceException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class TapePeripheral implements IPeripheral {
    private final List<IComputerAccess> connectedComputers = new ArrayList<>();

    protected final TapeBlockEntity tileEntity;

    public TapePeripheral(TapeBlockEntity tileEntity) {
        this.tileEntity = tileEntity;
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
    public final boolean isReady() {
        return tileEntity.getItem() instanceof CassetteItem;
    }

    @LuaFunction
    public final String read(Optional<Integer> targetChars) throws LuaException {
        int characters = targetChars.orElse(1);
        if (characters < 1) {
            throw new LuaException("Cannot read <1 bytes.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < characters; i++) {
            String readChar = tileEntity.readChar();
            stringBuilder.append(readChar);
            if (readChar.equals("")) break;
        }
        return stringBuilder.toString();
    }

    @LuaFunction
    public final boolean write(String ch) throws LuaException {
        char[] chars = ch.toCharArray();
        for (char c : chars) {
            if (!tileEntity.writeChar(c)) {
                return false;
            }
        }
        return true;
    }

    @LuaFunction
    public final int seek(int offset) throws LuaException {
        return tileEntity.seekRel(offset);
    }
    @LuaFunction
    public final void setLabel(String label) throws LuaException {
        tileEntity.setLabel(label);
    }
    @LuaFunction
    public final void clearLabel() throws LuaException {
        tileEntity.clearLabel();
    }
    @LuaFunction
    public final int getSize() throws LuaException {
        return tileEntity.getSize();
    }
}

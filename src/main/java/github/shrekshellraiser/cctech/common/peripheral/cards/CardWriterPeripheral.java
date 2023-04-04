package github.shrekshellraiser.cctech.common.peripheral.cards;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import github.shrekshellraiser.cctech.common.item.cards.MagCardItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CardWriterPeripheral implements IPeripheral {
    private final List<IComputerAccess> connectedComputers = new ArrayList<>();

    protected final CardWriterBlockEntity tileEntity;

    public CardWriterPeripheral(CardWriterBlockEntity tileEntity) {
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

    /**
     * Should return a string that uniquely identifies this type of peripheral.
     * This can be queried from lua by calling {@code peripheral.getType()}
     *
     * @return A string identifying the type of peripheral.
     */
    @NotNull
    @Override
    public String getType() {
        return "magnetic_card_reader";
    }

    @Override
    public void attach(@Nonnull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    @LuaFunction
    public final void writeCard(String data) throws LuaException {
        tileEntity.writeCard(data);
    }

    @LuaFunction
    public final void setLabel(String label) throws LuaException {
        tileEntity.setLabel(label);
    }

    @LuaFunction
    public final String getLabel() throws LuaException {
        return tileEntity.getLabel();
    }

    @LuaFunction
    public final boolean clearLabel() throws LuaException {
        return tileEntity.clearLabel();
    }

    @LuaFunction
    public final HashMap<String,String> readCard() throws LuaException {
        return tileEntity.readCard();
    }

    @LuaFunction
    public final String getUUID() {
        return tileEntity.getUUID();
    }
}

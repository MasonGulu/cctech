package com.shrekshellraiser.cctech.common.peripheral;

import com.shrekshellraiser.cctech.common.blockentities.ReelToReelBlockEntity;
import com.shrekshellraiser.cctech.common.item.ReelItem;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ReelToReelPeripheral implements IPeripheral {

    private final List<IComputerAccess> connectedComputers = new ArrayList<>();

    private final ReelToReelBlockEntity tileEntity;

    public ReelToReelPeripheral(ReelToReelBlockEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return "cassette_deck";
    }

    @Override
    public boolean equals(@Nonnull IPeripheral other) {
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
        return tileEntity.getItem() instanceof ReelItem;
    }
    @LuaFunction
    public final String read(int characters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < characters; i++) {
            stringBuilder.append(tileEntity.readChar());
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
    public final boolean seekAbs(int loc) {
        return tileEntity.seekAbs(loc);
    }
}

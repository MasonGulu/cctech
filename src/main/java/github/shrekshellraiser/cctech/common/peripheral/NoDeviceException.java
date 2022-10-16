package github.shrekshellraiser.cctech.common.peripheral;

import dan200.computercraft.api.lua.LuaException;
import org.jetbrains.annotations.Nullable;

public class NoDeviceException extends LuaException {
    public NoDeviceException() {
        super("No device inserted.");
    }
}

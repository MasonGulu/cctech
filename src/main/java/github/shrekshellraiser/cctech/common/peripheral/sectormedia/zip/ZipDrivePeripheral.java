package github.shrekshellraiser.cctech.common.peripheral.sectormedia.zip;

import github.shrekshellraiser.cctech.common.peripheral.sectormedia.SectorPeripheral;
import github.shrekshellraiser.cctech.common.peripheral.tape.TapePeripheral;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlockEntity;
import org.jetbrains.annotations.NotNull;

public class ZipDrivePeripheral extends SectorPeripheral {
    public ZipDrivePeripheral(ZipDriveBlockEntity tileEntity) {
        super(tileEntity);
    }

    @NotNull
    @Override
    public String getType() {
        return "zip_drive";
    }
}

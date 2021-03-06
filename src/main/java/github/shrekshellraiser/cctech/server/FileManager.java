package github.shrekshellraiser.cctech.server;

import github.shrekshellraiser.cctech.CCTech;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    // this class will provide utilities for saving/loading data from files based on UUID and directory
    private static void checkDir(String folder) {
        File world = new File(ServerLifecycleHooks.getCurrentServer()
                .getWorldPath(new LevelResource(CCTech.MODID)).toString());
        if (!world.isDirectory()) {
            world.mkdir();
        }
        File folderFile = new File(world, folder);
        if (!folderFile.isDirectory()) {
            folderFile.mkdir();
        }
    }

    public static void deleteDevice(String folder, String UUID) {
        checkDir(folder);
        CCTech.LOGGER.debug("Deleting data at " + folder + " " + UUID);
        Path path = Paths.get(ServerLifecycleHooks.getCurrentServer()
                .getWorldPath(new LevelResource(CCTech.MODID)).toString(), folder, UUID);
        if (!path.toFile().delete())
            CCTech.LOGGER.warn("Unable to delete data at " + folder + " " + UUID);
    }

    public static byte[] getData(String folder, String UUID) {
        checkDir(folder);
        CCTech.LOGGER.debug("Getting data for " + folder + " " + UUID);
        Path path = Paths.get(ServerLifecycleHooks.getCurrentServer()
                .getWorldPath(new LevelResource(CCTech.MODID)).toString(), folder, UUID);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            CCTech.LOGGER.error(e);
            byte[] data = new byte[POINTER_SIZE]; // Ensure the data array is at least large enough for a pointer
            saveData(data, folder, UUID);
            return data;
        }
    }

    public static byte[] getData(String folder, String UUID, int targetLength) {
        byte[] data = getData(folder, UUID);
        if (data.length < targetLength) {
            // length of loaded data is smaller than intended
            CCTech.LOGGER.warn("Loaded data is smaller than target size, adjusting..");
            byte[] newData = new byte[targetLength];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData; // corrected array size
        }
        return data;
    }

    public static void saveData(byte[] data, String folder, String UUID) {
        checkDir(folder);
        CCTech.LOGGER.debug("Saving data for " + folder + " " + UUID);
        Path path = Paths.get(ServerLifecycleHooks.getCurrentServer()
                .getWorldPath(new LevelResource(CCTech.MODID)).toString(), folder, UUID);
        try (FileOutputStream fos = new FileOutputStream(path.toString())) {
            fos.write(data);
        } catch (IOException e) {
            CCTech.LOGGER.error(e);
        }
    }

    public static void saveData(byte[] data, int pointer, String folder, String UUID) {
        setPointer(data, pointer);
        saveData(data, folder, UUID);
    }

    public static int getPointer(byte @NotNull [] data) {
        return data[0] + (data[1] << 8) + (data[2] << 16) + (data[3] << 24); // LSB
    }

    public static void setPointer(byte @NotNull [] data, int pointer) {
        data[0] = (byte) (pointer & 0xff);
        data[1] = (byte) ((pointer >> 8) & 0xff);
        data[2] = (byte) ((pointer >> 16) & 0xff);
        data[3] = (byte) ((pointer >> 24) & 0xff);
    }

    public static int POINTER_SIZE = 4;
}

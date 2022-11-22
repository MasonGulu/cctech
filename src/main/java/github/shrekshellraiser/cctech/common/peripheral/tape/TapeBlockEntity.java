package github.shrekshellraiser.cctech.common.peripheral.tape;

import cc.tweaked.internal.cobalt.Lua;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.NotAttachedException;
import github.shrekshellraiser.cctech.common.item.StorageItem;
import github.shrekshellraiser.cctech.common.item.tape.TapeItem;
import github.shrekshellraiser.cctech.common.peripheral.NoDeviceException;
import github.shrekshellraiser.cctech.common.peripheral.StorageBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlockEntity;
import github.shrekshellraiser.cctech.server.FileManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;

import static github.shrekshellraiser.cctech.server.FileManager.POINTER_SIZE;

public abstract class TapeBlockEntity extends StorageBlockEntity {
    protected byte[] data = new byte[2]; // data of cassette loaded
    protected int pointer;
    protected String deviceDir;
    protected double ticksPerByte;

    protected ArrayDeque<QueueItem> eventQueue = new ArrayDeque<>();

    public TapeBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return null;
    }

    protected void assertReady() throws LuaException {
        if (!deviceInserted) {
            throw new NoDeviceException();
        }
    }

    public void setHandler(ItemStackHandler itemStackHandler) {
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, itemStackHandler.getStackInSlot(i));
        }
    }

    private MethodResult syncQueue(Object value, String eventName, int ticks, IComputerAccess computerAccess) throws LuaException {
//        if (ticks < 1) {
//            assertReady();
//            return MethodResult.of(value);
//        }
        eventQueue.add(new QueueItem(value, eventName, ticks, computerAccess));
        return new EventFinished(eventName).getMethodResult();
    }

    // TODO method result
    public MethodResult readData(IComputerAccess computerAccess, int amount, boolean async) throws LuaException {
        assertReady();
        dataChanged = true;
        byte[] chars = new byte[amount];
        try {
            System.arraycopy(data, pointer+POINTER_SIZE, chars, 0, amount);
            pointer += amount;
        } catch (IndexOutOfBoundsException e) {
            pointer = Math.max(pointer, 0);
            pointer = Math.min(pointer, data.length - POINTER_SIZE);
            throw new LuaException("Attempt to read past tape end.");
        }
        var event = syncQueue(new String(chars, StandardCharsets.ISO_8859_1), "tape_read",
                (int) (ticksPerByte * amount), computerAccess);
        if (async) {
            return MethodResult.of(true);
        }
        return event;
    }

    // TODO method result
    public MethodResult seekRel(IComputerAccess computerAccess, int offset, boolean async) throws LuaException {
        assertReady();
        dataChanged = true;
        int startingPointer = pointer;
        pointer += offset;
        pointer = Math.max(pointer, 0);
        pointer = Math.min(pointer, data.length - POINTER_SIZE);

        int distanceMoved = pointer - startingPointer;
        var event = syncQueue(distanceMoved, "tape_seek", (int)(distanceMoved * ticksPerByte), computerAccess);
        if (async) {
            return MethodResult.of(true);
        }
        return event;
    }

    // TODO method result
    public MethodResult seekAbs(IComputerAccess computerAccess, int target, boolean async) throws LuaException {
        assertReady();
        dataChanged = true;
        int oldPos = pointer;
        pointer = target;
        pointer = Math.max(pointer, 0);
        pointer = Math.min(pointer, data.length - POINTER_SIZE);

        int distanceMoved = pointer - oldPos;
        var event = syncQueue(distanceMoved, "tape_seek",
                (int) (ticksPerByte * distanceMoved), computerAccess);
        if (async) {
            return MethodResult.of(true);
        }
        return event;
    }

    // TODO method result
    public MethodResult write(IComputerAccess computerAccess, String str, boolean async) throws LuaException {
        assertReady();
        byte[] chars = str.getBytes(StandardCharsets.ISO_8859_1);
        dataChanged = true;

        int maxPointer = data.length - POINTER_SIZE;
        if (pointer > maxPointer) {
            // this would normally error
            eventQueue.add(new QueueItem(false, "tape_write", str.length(), computerAccess));
            return new EventFinished("tape_write").getMethodResult();
        }
        System.arraycopy(chars, 0, data, pointer+POINTER_SIZE, str.length());
        pointer += str.length();
        var event = syncQueue(true, "tape_write",
                (int) (ticksPerByte * str.length()), computerAccess);
        if (async) {
            return MethodResult.of(true);
        }
        return event;

    }

    public boolean setLabel(String label) throws LuaException {
        assertReady();
        ItemStack item = itemHandler.getStackInSlot(0);
        ((StorageItem) item.getItem()).setLabel(item, label);
        return true;
    }

    public boolean clearLabel() throws LuaException {
        assertReady();
        ItemStack item = itemHandler.getStackInSlot(0);
        ((StorageItem) item.getItem()).removeLabel(item);
        return true;
    }

    public int getSize() throws LuaException {
        assertReady();
        return data.length - POINTER_SIZE;
    }

    @Override
    protected void loadData(ItemStack item) {
        uuid = ((StorageItem) item.getItem()).getUUID(item);
        data = FileManager.getData(deviceDir, uuid, ((TapeItem) item.getItem()).getLength(item) + POINTER_SIZE);
        pointer = FileManager.getPointer(data);
    }

    @Override
    protected void saveData(ItemStack item) {
        FileManager.saveData(data, pointer, deviceDir, uuid);
    }

    protected QueueState queueState = QueueState.FREE;

    protected QueueItem currentItem;

    protected int timer;

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, TapeBlockEntity pBlockEntity) {
        switch (pBlockEntity.queueState) {
            case FREE -> {
                while (!pBlockEntity.eventQueue.isEmpty() && pBlockEntity.queueState == QueueState.FREE) {
                    pBlockEntity.currentItem = pBlockEntity.eventQueue.poll();
                    pBlockEntity.queueState = QueueState.WAITING;
                    pBlockEntity.timer = pBlockEntity.currentItem.ticks;
                    if (pBlockEntity.timer == 0) {
                        // this is a 0 tick timer, so queue event immediately
                        QueueItem item = pBlockEntity.currentItem;
                        item.computerAccess.queueEvent(item.eventName,
                                item.computerAccess.getAttachmentName(), true, item.returnValue);
                        pBlockEntity.queueState = QueueState.FREE;
                    }
                }
            }
            case WAITING -> {
                QueueItem item = pBlockEntity.currentItem;
                try {
                    item.computerAccess.getAttachmentName();
                } catch (NotAttachedException NAE) {
                    pBlockEntity.queueState = QueueState.FREE; // If the computer is removed cancel this event
                }
                if (pBlockEntity.timer-- <= 0) {
                    if (item.assertReady) {
                        try {
                            pBlockEntity.assertReady();
                        } catch (LuaException e) {
                            item.computerAccess.queueEvent(item.eventName,
                                    item.computerAccess.getAttachmentName(), false, e.toString());
                            return;
                        }
                    }
                    // This timer is done, queue the event
                    pBlockEntity.queueState = QueueState.FREE;
                    if (item.assertReady) {
                        try {
                            pBlockEntity.assertReady();
                        } catch (LuaException e) {
                            item.computerAccess.queueEvent(item.eventName,
                                    item.computerAccess.getAttachmentName(), false, e.toString());
                            return;
                        }
                    }
                    item.computerAccess.queueEvent(item.eventName,
                            item.computerAccess.getAttachmentName(), true, item.returnValue);
                }
            }
        }
    }


    protected final class EventFinished implements ILuaCallback {

        private final String filter;

        public EventFinished(String filter) {
            this.filter = filter;
        }

        public MethodResult getMethodResult() {
            return MethodResult.pullEvent(filter, this);
        }

        @NotNull
        @Override
        public MethodResult resume(Object[] args) throws LuaException {
            // operation_finish, device, success, data (or fail message)
            assertReady();
            if (args.length < 4) {
                throw new LuaException("Invalid amount of arguments for event");
            }
            if (Boolean.valueOf(false).equals(args[2])) {
                // this failed
                throw new LuaException((String) args[3]);
            }
            return MethodResult.of(args[3]);
        }
    }

    protected static final class QueueItem {

        public final Object returnValue;

        public final String eventName;

        public final boolean assertReady;

        public final int ticks;

        public final IComputerAccess computerAccess;

        QueueItem(Object returnValue, String eventName, int ticks, IComputerAccess computerAccess, boolean assertReady) {
            this.returnValue = returnValue;
            this.eventName = eventName;
            this.ticks = ticks;
            this.computerAccess = computerAccess;
            this.assertReady = assertReady;
        }

        QueueItem(Object returnValue, String eventName, int ticks, IComputerAccess computerAccess) {
            this(returnValue, eventName, ticks, computerAccess, false);
        }
    }

    protected enum QueueState {
        FREE,
        WAITING,
    }
}
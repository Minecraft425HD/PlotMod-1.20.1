package de.beispielmod.plotmod.tobacco.blockentity;

import de.beispielmod.plotmod.economy.items.CashItem;
import de.beispielmod.plotmod.tobacco.items.SoilBagItem;
import de.beispielmod.plotmod.tobacco.items.TobaccoBottleItem;
import de.beispielmod.plotmod.tobacco.items.TobaccoSeedItem;
import de.beispielmod.plotmod.tobacco.items.WateringCanItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Arbeiter-Schrank BlockEntity
 * 
 * Slots:
 * 0 = Geld (nur CashItem)
 * 1-4 = Samen (4 Sorten)
 * 5 = Erdsäcke
 * 6-8 = Flaschen (Dünger, Beschleuniger, Qualität)
 * 9 = Gießkanne (optional, für Spieler)
 * 10-14 = Output Slots (geerntete Items)
 */
public class WorkerStorageBlockEntity extends BlockEntity implements MenuProvider {
    
    public static final int TOTAL_SLOTS = 15;
    
    // Slot-Definitionen
    public static final int SLOT_CASH = 0;
    public static final int SLOT_SEEDS_START = 1;
    public static final int SLOT_SEEDS_END = 4;
    public static final int SLOT_SOIL_BAG = 5;
    public static final int SLOT_BOTTLES_START = 6;
    public static final int SLOT_BOTTLES_END = 8;
    public static final int SLOT_WATERING_CAN = 9;
    public static final int SLOT_OUTPUT_START = 10;
    public static final int SLOT_OUTPUT_END = 14;
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
        
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return isValidForSlot(slot, stack);
        }
    };
    
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    
    public WorkerStorageBlockEntity(BlockPos pos, BlockState state) {
        super(TobaccoBlockEntities.WORKER_STORAGE.get(), pos, state);
    }
    
    /**
     * Prüft ob Item in Slot valide ist
     */
    private boolean isValidForSlot(int slot, ItemStack stack) {
        if (slot == SLOT_CASH) {
            return stack.getItem() instanceof CashItem;
        } else if (slot >= SLOT_SEEDS_START && slot <= SLOT_SEEDS_END) {
            return stack.getItem() instanceof TobaccoSeedItem;
        } else if (slot == SLOT_SOIL_BAG) {
            return stack.getItem() instanceof SoilBagItem;
        } else if (slot >= SLOT_BOTTLES_START && slot <= SLOT_BOTTLES_END) {
            return stack.getItem() instanceof TobaccoBottleItem;
        } else if (slot == SLOT_WATERING_CAN) {
            return stack.getItem() instanceof WateringCanItem;
        } else if (slot >= SLOT_OUTPUT_START && slot <= SLOT_OUTPUT_END) {
            return true; // Output slots akzeptieren alles
        }
        return false;
    }
    
    /**
     * Gibt Inventar zurück
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
    
    /**
     * Gibt Geld zurück
     */
    public double getMoney() {
        ItemStack cashStack = itemHandler.getStackInSlot(SLOT_CASH);
        if (cashStack.getItem() instanceof CashItem) {
            return CashItem.getValue(cashStack);
        }
        return 0.0;
    }
    
    /**
     * Verbraucht Geld
     */
    public boolean consumeMoney(double amount) {
        ItemStack cashStack = itemHandler.getStackInSlot(SLOT_CASH);
        if (cashStack.getItem() instanceof CashItem) {
            return CashItem.removeValue(cashStack, amount);
        }
        return false;
    }
    
    /**
     * Gibt Samen zurück (zufälligen verfügbaren)
     */
    public ItemStack getRandomSeed() {
        for (int i = SLOT_SEEDS_START; i <= SLOT_SEEDS_END; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof TobaccoSeedItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
    
    /**
     * Verbraucht Samen
     */
    public boolean consumeSeed() {
        for (int i = SLOT_SEEDS_START; i <= SLOT_SEEDS_END; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof TobaccoSeedItem) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gibt Erdsack zurück
     */
    public ItemStack getSoilBag() {
        return itemHandler.getStackInSlot(SLOT_SOIL_BAG);
    }
    
    /**
     * Verbraucht Erde (1 Einheit)
     */
    public boolean consumeSoil() {
        ItemStack soilBag = getSoilBag();
        if (!soilBag.isEmpty() && soilBag.getItem() instanceof SoilBagItem) {
            return SoilBagItem.consumeUnits(soilBag, 1);
        }
        return false;
    }
    
    /**
     * Gibt Flasche zurück (zufällige verfügbare)
     */
    public ItemStack getRandomBottle() {
        for (int i = SLOT_BOTTLES_START; i <= SLOT_BOTTLES_END; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof TobaccoBottleItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
    
    /**
     * Verbraucht Flasche
     */
    public boolean consumeBottle() {
        for (int i = SLOT_BOTTLES_START; i <= SLOT_BOTTLES_END; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof TobaccoBottleItem) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Fügt geerntete Items hinzu
     */
    public boolean addHarvestedItem(ItemStack stack) {
        for (int i = SLOT_OUTPUT_START; i <= SLOT_OUTPUT_END; i++) {
            ItemStack slotStack = itemHandler.getStackInSlot(i);
            if (slotStack.isEmpty()) {
                itemHandler.setStackInSlot(i, stack.copy());
                return true;
            } else if (ItemStack.isSameItemSameTags(slotStack, stack) && 
                       slotStack.getCount() + stack.getCount() <= slotStack.getMaxStackSize()) {
                slotStack.grow(stack.getCount());
                return true;
            }
        }
        return false;
    }
    
    /**
     * Tick-Update
     */
    public void tick() {
        if (level == null || level.isClientSide) return;
        
        // Hier könnte zusätzliche Logik hin (z.B. Auto-Auffüllen)
    }
    
    /**
     * Drops Items wenn Block zerstört wird
     */
    public void dropContents() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inventory);
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }
    
    @Override
    public Component getDisplayName() {
        return Component.literal("Arbeiter-Schrank");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new de.beispielmod.plotmod.tobacco.menu.WorkerStorageMenu(id, playerInventory, this, worldPosition);
    }
}

package de.beispielmod.plotmod.tobacco.menu;

import de.beispielmod.plotmod.tobacco.blockentity.WorkerStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Container Menu für Arbeiter-Schrank
 */
public class WorkerStorageMenu extends AbstractContainerMenu {
    
    private final WorkerStorageBlockEntity blockEntity;
    private final Level level;
    private final BlockPos pos;
    
    public WorkerStorageMenu(int id, Inventory playerInventory, WorkerStorageBlockEntity blockEntity, BlockPos pos) {
        super(de.beispielmod.plotmod.tobacco.menu.ModMenuTypes.WORKER_STORAGE.get(), id);
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();
        this.pos = pos;
        
        // Arbeiter-Schrank Slots
        addWorkerSlots();
        
        // Spieler Inventar
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }
    
    private void addWorkerSlots() {
        var handler = blockEntity.getItemHandler();
        
        // Slot 0: Geld (links oben)
        this.addSlot(new SlotItemHandler(handler, 0, 8, 18));
        
        // Slots 1-4: Samen (horizontal)
        for (int i = 0; i < 4; i++) {
            this.addSlot(new SlotItemHandler(handler, 1 + i, 44 + i * 18, 18));
        }
        
        // Slot 5: Erdsack
        this.addSlot(new SlotItemHandler(handler, 5, 8, 54));
        
        // Slots 6-8: Flaschen
        for (int i = 0; i < 3; i++) {
            this.addSlot(new SlotItemHandler(handler, 6 + i, 44 + i * 18, 54));
        }
        
        // Slot 9: Gießkanne
        this.addSlot(new SlotItemHandler(handler, 9, 116, 54));
        
        // Slots 10-14: Output (rechts)
        for (int i = 0; i < 5; i++) {
            this.addSlot(new SlotItemHandler(handler, 10 + i, 152, 18 + i * 18));
        }
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 
                    8 + col * 18, 84 + row * 18));
            }
        }
    }
    
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            
            if (index < WorkerStorageBlockEntity.TOTAL_SLOTS) {
                // Vom Schrank zum Spieler
                if (!this.moveItemStackTo(slotStack, WorkerStorageBlockEntity.TOTAL_SLOTS, 
                    this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Vom Spieler zum Schrank
                if (!this.moveItemStackTo(slotStack, 0, WorkerStorageBlockEntity.TOTAL_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        
        return stack;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(
            net.minecraft.world.inventory.ContainerLevelAccess.create(level, pos),
            player,
            de.beispielmod.plotmod.tobacco.blocks.TobaccoBlocks.WORKER_STORAGE.get()
        );
    }
    
    public WorkerStorageBlockEntity getBlockEntity() {
        return blockEntity;
    }
}

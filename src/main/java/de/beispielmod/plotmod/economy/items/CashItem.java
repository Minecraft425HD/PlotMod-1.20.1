package de.beispielmod.plotmod.economy.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Bargeld-Item (Geldbörse)
 * - UNLIMITED Speicher
 * - NUR in Slot 9 (Slot 10 im UI)
 * - NICHT entfernbar
 */
public class CashItem extends Item {
    
    // KEIN LIMIT!
    private static final int PLACE_AMOUNT = 100; // 100€ pro Rechtsklick
    
    public CashItem() {
        super(new Properties()
                .stacksTo(1)); // Nur 1 Stack, Wert in NBT
    }
    
    /**
     * Erstellt Bargeld mit Wert
     */
    public static ItemStack create(double amount) {
        ItemStack stack = new ItemStack(de.beispielmod.plotmod.items.ModItems.CASH.get());
        setValue(stack, amount);
        return stack;
    }
    
    /**
     * Setzt Wert (UNLIMITED!)
     */
    public static void setValue(ItemStack stack, double value) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putDouble("CashValue", Math.max(0, value)); // KEIN Maximum!
    }
    
    /**
     * Gibt Wert zurück
     */
    public static double getValue(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CashValue")) {
            return tag.getDouble("CashValue");
        }
        return 0.0;
    }
    
    /**
     * Fügt Wert hinzu (UNLIMITED!)
     */
    public static boolean addValue(ItemStack stack, double amount) {
        double current = getValue(stack);
        setValue(stack, current + amount);
        return true; // Immer erfolgreich
    }
    
    /**
     * Entfernt Wert
     */
    public static boolean removeValue(ItemStack stack, double amount) {
        double current = getValue(stack);
        if (current >= amount) {
            setValue(stack, current - amount);
            return true;
        }
        return false;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        double value = getValue(stack);
        
        tooltip.add(Component.literal("§7Guthaben: §a" + String.format("%.2f€", value)));
        tooltip.add(Component.literal("§7Kapazität: §aUnlimitiert"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§c§lGESPERRT IN SLOT 9!"));
        tooltip.add(Component.literal("§8Kann nicht entfernt werden"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Rechtsklick: §ePlatziere " + PLACE_AMOUNT + "€"));
        tooltip.add(Component.literal("§7Linksklick auf Block: §eAbbauen"));
    }
    
    @Override
    public Component getName(ItemStack stack) {
        double value = getValue(stack);
        if (value <= 0) {
            return Component.literal("§7Geldbörse (Leer)");
        } else {
            return Component.literal("§aGeldbörse §7(§e" + String.format("%.0f€", value) + "§7)");
        }
    }
    
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false; // Kein Bar da unlimited
    }
}

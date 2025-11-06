package de.beispielmod.plotmod.items;

import de.beispielmod.plotmod.PlotMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registrierung aller PlotMod Items
 */
public class ModItems {
    
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, PlotMod.MOD_ID);
    
    // Plot-Auswahl-Werkzeug (wie WorldEdit Axe)
    public static final RegistryObject<Item> PLOT_SELECTION_TOOL = 
        ITEMS.register("plot_selection_tool", PlotSelectionTool::new);
}

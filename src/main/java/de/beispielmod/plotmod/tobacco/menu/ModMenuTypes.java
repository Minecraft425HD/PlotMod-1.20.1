package de.beispielmod.plotmod.tobacco.menu;

import de.beispielmod.plotmod.PlotMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registrierung aller Menu Types
 */
public class ModMenuTypes {
    
    public static final DeferredRegister<MenuType<?>> MENUS = 
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, PlotMod.MOD_ID);
    
    public static final RegistryObject<MenuType<WorkerStorageMenu>> WORKER_STORAGE = 
        MENUS.register("worker_storage", () -> IForgeMenuType.create((windowId, inv, data) -> {
            return new WorkerStorageMenu(windowId, inv, 
                (de.beispielmod.plotmod.tobacco.blockentity.WorkerStorageBlockEntity) inv.player.level()
                    .getBlockEntity(data.readBlockPos()), 
                data.readBlockPos());
        }));
}

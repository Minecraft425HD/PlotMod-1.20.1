package de.beispielmod.plotmod.tobacco.entity;

import de.beispielmod.plotmod.PlotMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registrierung aller Entities
 */
public class ModEntities {
    
    public static final DeferredRegister<EntityType<?>> ENTITIES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PlotMod.MOD_ID);
    
    public static final RegistryObject<EntityType<WorkerNPC>> WORKER_NPC = 
        ENTITIES.register("worker_npc", () -> EntityType.Builder.of(WorkerNPC::new, MobCategory.CREATURE)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(10)
            .build("worker_npc"));
}

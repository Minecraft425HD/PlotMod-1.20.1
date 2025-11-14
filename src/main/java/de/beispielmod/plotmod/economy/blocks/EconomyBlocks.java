package de.beispielmod.plotmod.economy.blocks;

import de.beispielmod.plotmod.PlotMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registrierung aller Economy-Blöcke
 */
public class EconomyBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, PlotMod.MOD_ID);
    
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, PlotMod.MOD_ID);
    
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PlotMod.MOD_ID);
    
    // Bargeld-Block
    public static final RegistryObject<Block> CASH_BLOCK = BLOCKS.register("cash_block",
        () -> new CashBlock());
    
    public static final RegistryObject<Item> CASH_BLOCK_ITEM = ITEMS.register("cash_block",
        () -> new BlockItem(CASH_BLOCK.get(), new Item.Properties()));
    
    // Bargeld-Block Entity
    public static final RegistryObject<BlockEntityType<CashBlockEntity>> CASH_BLOCK_ENTITY = 
        BLOCK_ENTITIES.register("cash_block_entity", () -> 
            BlockEntityType.Builder.of(CashBlockEntity::new, CASH_BLOCK.get()).build(null));
}

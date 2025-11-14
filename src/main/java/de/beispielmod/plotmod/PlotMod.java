package de.beispielmod.plotmod;

import com.mojang.logging.LogUtils;
import de.beispielmod.plotmod.commands.PlotCommand;
import de.beispielmod.plotmod.commands.MoneyCommand;
import de.beispielmod.plotmod.commands.DailyCommand;
import de.beispielmod.plotmod.commands.ShopCommand;
import de.beispielmod.plotmod.tobacco.commands.TobaccoCommand;
import de.beispielmod.plotmod.economy.PlayerJoinHandler;
import de.beispielmod.plotmod.events.BlockProtectionHandler;
import de.beispielmod.plotmod.tobacco.events.TobaccoBottleHandler;
import de.beispielmod.plotmod.economy.events.CashSlotRestrictionHandler;
import de.beispielmod.plotmod.economy.EconomyManager;
import de.beispielmod.plotmod.region.PlotManager;
import de.beispielmod.plotmod.managers.DailyRewardManager;
import de.beispielmod.plotmod.managers.ShopManager;
import de.beispielmod.plotmod.managers.RentManager;
import de.beispielmod.plotmod.config.ModConfigHandler;
import de.beispielmod.plotmod.items.ModItems;
import de.beispielmod.plotmod.items.PlotSelectionTool;
import de.beispielmod.plotmod.tobacco.TobaccoShopIntegration;
import de.beispielmod.plotmod.tobacco.items.TobaccoItems;
import de.beispielmod.plotmod.tobacco.blocks.TobaccoBlocks;
import de.beispielmod.plotmod.tobacco.blockentity.TobaccoBlockEntities;
import de.beispielmod.plotmod.tobacco.menu.ModMenuTypes;
import de.beispielmod.plotmod.tobacco.entity.ModEntities;
import de.beispielmod.plotmod.tobacco.entity.WorkerNPC;
import de.beispielmod.plotmod.economy.blocks.EconomyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(PlotMod.MOD_ID)
public class PlotMod {
    
    public static final String MOD_ID = "plotmod";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static final int SAVE_INTERVAL = 6000; // 5 Minuten
    private int tickCounter = 0;

    public PlotMod() {
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  PlotMod 3.0 ULTIMATE EDITION");
        LOGGER.info("  + Tabak-System");
        LOGGER.info("  + Erdsäcke");
        LOGGER.info("  + Bargeld-System");
        LOGGER.info("  + Arbeiter-NPC");
        LOGGER.info("  + Creative Tabs");
        LOGGER.info("═══════════════════════════════════════");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Items registrieren
        ModItems.ITEMS.register(modEventBus);
        TobaccoItems.ITEMS.register(modEventBus);
        LOGGER.info("✓ Items registriert");
        
        // Blöcke registrieren
        TobaccoBlocks.BLOCKS.register(modEventBus);
        TobaccoBlocks.ITEMS.register(modEventBus);
        EconomyBlocks.BLOCKS.register(modEventBus);
        EconomyBlocks.ITEMS.register(modEventBus);
        LOGGER.info("✓ Blöcke registriert");
        
        // BlockEntities registrieren
        TobaccoBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        EconomyBlocks.BLOCK_ENTITIES.register(modEventBus);
        LOGGER.info("✓ TileEntities registriert");
        
        // Menus registrieren
        ModMenuTypes.MENUS.register(modEventBus);
        LOGGER.info("✓ GUI Menus registriert");
        
        // Entities registrieren
        ModEntities.ENTITIES.register(modEventBus);
        LOGGER.info("✓ Entities registriert");
        
        // Creative Tabs registrieren
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        LOGGER.info("✓ Creative Tabs registriert");
        
        // Entity Attributes Event
        modEventBus.addListener(this::onEntityAttributeCreation);

        // Config registrieren
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigHandler.SPEC);

        // Event-Handler registrieren
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new BlockProtectionHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerJoinHandler());
        MinecraftForge.EVENT_BUS.register(new TobaccoBottleHandler());
        MinecraftForge.EVENT_BUS.register(new CashSlotRestrictionHandler());
        LOGGER.info("✓ Event-Handler registriert");

        LOGGER.info("PlotMod 3.0 ULTIMATE initialisiert!");
        LOGGER.info("═══════════════════════════════════════");
    }
    
    public void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WORKER_NPC.get(), WorkerNPC.createAttributes().build());
        LOGGER.info("✓ Worker-NPC Attributes registriert");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LOGGER.info("Registriere Commands...");
        
        PlotCommand.register(event.getDispatcher());
        MoneyCommand.register(event.getDispatcher());
        DailyCommand.register(event.getDispatcher());
        ShopCommand.register(event.getDispatcher());
        TobaccoCommand.register(event.getDispatcher());
        
        LOGGER.info("✓ Commands registriert");
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  Server gestartet - Lade Daten...");
        LOGGER.info("═══════════════════════════════════════");
        
        PlotManager.loadPlots();
        EconomyManager.loadAccounts();
        DailyRewardManager.load();
        ShopManager.load();
        
        TobaccoShopIntegration.registerShopItems();
        LOGGER.info("✓ Shop-Items integriert");
        
        LOGGER.info("Daten geladen!");
        LOGGER.info("  • Plots: " + PlotManager.getPlotCount());
        LOGGER.info("  • Economy-Konten: " + EconomyManager.getAllAccounts().size());
        LOGGER.info("  • Shop-Items: " + ShopManager.getItemCount());
        
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  PlotMod 3.0 ULTIMATE vollständig geladen!");
        LOGGER.info("  ✓ Alle Features: AKTIV");
        LOGGER.info("  ✓ Creative Tabs: AKTIV");
        LOGGER.info("═══════════════════════════════════════");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        
        if (tickCounter >= SAVE_INTERVAL) {
            tickCounter = 0;
            
            PlotManager.saveIfNeeded();
            EconomyManager.saveIfNeeded();
            DailyRewardManager.saveIfNeeded();
            ShopManager.saveIfNeeded();
            RentManager.checkExpiredRents();
            
            LOGGER.debug("Auto-Save durchgeführt");
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  Server stoppt - Speichere Daten...");
        LOGGER.info("═══════════════════════════════════════");
        
        PlotManager.savePlots();
        EconomyManager.saveAccounts();
        DailyRewardManager.save();
        ShopManager.save();
        
        LOGGER.info("PlotMod-Daten gespeichert!");
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  PlotMod 3.0 ULTIMATE erfolgreich beendet!");
        LOGGER.info("═══════════════════════════════════════");
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().isClientSide) return;

        Player player = event.getEntity();
        ItemStack heldItem = event.getItemStack();

        if (heldItem.getItem() instanceof PlotSelectionTool) {
            BlockPos pos = event.getPos();
            PlotSelectionTool.setPosition1(player.getUUID(), pos);

            player.displayClientMessage(Component.literal(
                "§a✓ Position 1 gesetzt!\n" +
                "§7Koordinaten: §f" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "\n" +
                "§7Rechtsklick auf Block für Position 2"
            ), true);

            player.playSound(net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            event.setCanceled(true);
            event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
        }
    }
}

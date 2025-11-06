package de.beispielmod.plotmod;

import com.mojang.logging.LogUtils;
import de.beispielmod.plotmod.commands.PlotCommand;
import de.beispielmod.plotmod.commands.MoneyCommand;
import de.beispielmod.plotmod.commands.DailyCommand;
import de.beispielmod.plotmod.commands.ShopCommand;
import de.beispielmod.plotmod.economy.PlayerJoinHandler;
import de.beispielmod.plotmod.events.BlockProtectionHandler;
import de.beispielmod.plotmod.economy.EconomyManager;
import de.beispielmod.plotmod.region.PlotManager;
import de.beispielmod.plotmod.managers.DailyRewardManager;
import de.beispielmod.plotmod.managers.ShopManager;
import de.beispielmod.plotmod.managers.RentManager;
import de.beispielmod.plotmod.managers.HologramManager;
import de.beispielmod.plotmod.config.ModConfigHandler;
import de.beispielmod.plotmod.items.ModItems;
import de.beispielmod.plotmod.items.PlotSelectionTool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
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

/**
 * PlotMod 3.0 - Hauptklasse mit Selection Tool
 */
@Mod(PlotMod.MOD_ID)
public class PlotMod {
    
    public static final String MOD_ID = "plotmod";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static final int SAVE_INTERVAL = 6000; // 5 Minuten
    private int tickCounter = 0;
    private int hologramCounter = 0;

    public PlotMod() {
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  PlotMod 3.0 wird initialisiert...");
        LOGGER.info("═══════════════════════════════════════");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Items registrieren
        ModItems.ITEMS.register(modEventBus);
        LOGGER.info("Items registriert!");

        // Config registrieren
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigHandler.SPEC);

        // Event-Handler registrieren
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new BlockProtectionHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerJoinHandler());

        LOGGER.info("PlotMod 3.0 Basis-Initialisierung abgeschlossen!");
        LOGGER.info("Daten werden beim Server-Start geladen...");
        LOGGER.info("═══════════════════════════════════════");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LOGGER.info("Registriere Commands...");
        
        PlotCommand.register(event.getDispatcher());
        MoneyCommand.register(event.getDispatcher());
        DailyCommand.register(event.getDispatcher());
        ShopCommand.register(event.getDispatcher());
        
        LOGGER.info("Commands registriert!");
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  Server gestartet - Lade Daten...");
        LOGGER.info("═══════════════════════════════════════");
        
        // Daten laden
        PlotManager.loadPlots();
        EconomyManager.loadAccounts();
        DailyRewardManager.load();
        ShopManager.load();
        
        LOGGER.info("Daten geladen!");
        LOGGER.info("  • Plots: " + PlotManager.getPlotCount());
        LOGGER.info("  • Economy-Konten: " + EconomyManager.getAllAccounts().size());
        LOGGER.info("  • Shop-Items: " + ShopManager.getItemCount());
        LOGGER.info("  • Startguthaben: " + EconomyManager.getStartBalance() + " €");
        
        // Hologramme erstellen
        if (ModConfigHandler.COMMON.HOLOGRAMS_ENABLED.get()) {
            LOGGER.info("Initialisiere Hologramme...");
            for (ServerLevel world : event.getServer().getAllLevels()) {
                if (world.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
                    HologramManager.createAllHolograms(world);
                    break;
                }
            }
            LOGGER.info("Hologramme erstellt!");
        } else {
            LOGGER.info("Hologramme deaktiviert (Config)");
        }
        
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  PlotMod 3.0 vollständig geladen!");
        LOGGER.info("═══════════════════════════════════════");
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

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
        
        if (ModConfigHandler.COMMON.HOLOGRAMS_ENABLED.get()) {
            hologramCounter++;
            int interval = ModConfigHandler.COMMON.HOLOGRAM_UPDATE_INTERVAL.get();
            
            if (hologramCounter >= interval) {
                hologramCounter = 0;
                
                for (ServerLevel world : event.getServer().getAllLevels()) {
                    if (world.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
                        HologramManager.updateHolograms(world);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  Server stoppt - Speichere Daten...");
        LOGGER.info("═══════════════════════════════════════");
        
        if (ModConfigHandler.COMMON.HOLOGRAMS_ENABLED.get()) {
            for (ServerLevel world : event.getServer().getAllLevels()) {
                if (world.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) {
                    HologramManager.removeAllHolograms(world);
                    break;
                }
            }
        }
        
        PlotManager.savePlots();
        EconomyManager.saveAccounts();
        DailyRewardManager.save();
        ShopManager.save();
        
        LOGGER.info("PlotMod-Daten gespeichert!");
        LOGGER.info("═══════════════════════════════════════");
        LOGGER.info("  PlotMod 3.0 erfolgreich beendet!");
        LOGGER.info("═══════════════════════════════════════");
    }

    // Event-Handler für Linksklick auf Block
    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        // Nur auf Server-Seite handhaben
        if (event.getLevel().isClientSide) return;

        Player player = event.getEntity();
        ItemStack heldItem = event.getItemStack();

        // Prüfe, ob der Spieler das PlotSelectionTool hält
        if (heldItem.getItem() instanceof PlotSelectionTool) {
            BlockPos pos = event.getPos();

            // Position 1 setzen
            PlotSelectionTool.setPosition1(player.getUUID(), pos);

            // Info anzeigen
            player.displayClientMessage(Component.literal(
                "§a✓ Position 1 gesetzt!\n" +
                "§7Koordinaten: §f" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "\n" +
                "§7Rechtsklick auf Block für Position 2"
            ), true);

            // Sound abspielen
            player.playSound(net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

            // Verhindere das Abbauen des Blocks
            event.setCanceled(true);
            event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
        }
    }
}
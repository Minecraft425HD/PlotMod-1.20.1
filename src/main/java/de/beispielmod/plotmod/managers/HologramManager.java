package de.beispielmod.plotmod.managers;

import com.mojang.logging.LogUtils;
import de.beispielmod.plotmod.config.ModConfigHandler;
import de.beispielmod.plotmod.region.PlotManager;
import de.beispielmod.plotmod.region.PlotRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import org.slf4j.Logger;

import java.util.*;

/**
 * Verwaltet Hologramme über Plots (Forge 47.5.0 – Minecraft 1.20.1)
 *
 * Diese einfache Implementation nutzt TextDisplay-Entities (ab 1.19.4+)
 * und zeigt Plotinformationen über den Grundstücken an.
 */
public class HologramManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<String, List<Display.TextDisplay>> HOLOGRAMS = new HashMap<>();
    private static int updateCounter = 0;

    /**
     * Erstellt Hologramme für alle Plots
     */
    public static void createAllHolograms(ServerLevel world) {
        if (!ModConfigHandler.COMMON.HOLOGRAMS_ENABLED.get()) return;

        LOGGER.info("Erstelle Hologramme für Plots...");

        for (PlotRegion plot : PlotManager.getPlots()) {
            createHologram(world, plot);
        }

        LOGGER.info("Hologramme erstellt für {} Plots", PlotManager.getPlotCount());
    }

    /**
     * Erstellt ein Hologramm für einen Plot
     */
    public static void createHologram(ServerLevel world, PlotRegion plot) {
        if (!ModConfigHandler.COMMON.HOLOGRAMS_ENABLED.get()) return;

        // Altes Hologramm entfernen, falls vorhanden
        removeHologram(world, plot.getPlotId());

        BlockPos center = plot.getCenter();
        BlockPos holoPos = new BlockPos(center.getX(), plot.getMax().getY() + 3, center.getZ());

        List<String> lines = buildHologramLines(plot);
        List<Display.TextDisplay> displays = new ArrayList<>();

        double yOffset = 0;
        for (String line : lines) {
            Display.TextDisplay display = createTextDisplay(world, holoPos, yOffset, line);
            if (display != null) {
                displays.add(display);
                world.addFreshEntity(display);
            }
            yOffset -= 0.3; // jede Zeile 0.3 Blöcke tiefer
        }

        HOLOGRAMS.put(plot.getPlotId(), displays);
    }

    /**
     * Baut die Textzeilen eines Hologramms
     */
    private static List<String> buildHologramLines(PlotRegion plot) {
        List<String> lines = new ArrayList<>();

        // Zeile 1: Plotname
        lines.add("§e§l" + plot.getPlotName());

        // Zeile 2+: Status, Besitzer, Preis etc.
        if (!plot.hasOwner()) {
            if (ModConfigHandler.COMMON.SHOW_PRICE.get()) {
                lines.add("§a§lZU VERKAUFEN");
                lines.add("§7Preis: §e" + String.format("%.2f", plot.getPrice()) + "€");
            }
        } else {
            if (ModConfigHandler.COMMON.SHOW_OWNER.get()) {
                lines.add("§7Besitzer: §b" + (plot.hasOwner() ? "Spieler" : "Niemand"));
            }

            if (plot.isForSale()) {
                lines.add("§e§lZU VERKAUFEN");
                lines.add("§7Preis: §e" + String.format("%.2f", plot.getSalePrice()) + "€");
            }

            if (plot.isForRent() && !plot.isRented()) {
                lines.add("§d§lZU VERMIETEN");
                lines.add("§7Preis: §e" + String.format("%.2f", plot.getRentPricePerDay()) + "€/Tag");
            }

            if (plot.isRented()) {
                lines.add("§d§lVERMIETET");
                lines.add("§7Noch: §e" + plot.getRentDaysLeft() + " Tage");
            }
        }

        // Bewertung
        if (ModConfigHandler.COMMON.SHOW_RATING.get() && plot.getRatingCount() > 0) {
            lines.add("§6" + plot.getRatingStars() + " §7(" + plot.getRatingCount() + ")");
        }

        return lines;
    }

    /**
     * Erstellt ein einzelnes TextDisplay über einem Plot
     */
    private static Display.TextDisplay createTextDisplay(ServerLevel world, BlockPos pos, double yOffset, String text) {
        try {
            Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, world);
            display.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);

            // Text via SynchedEntityData setzen (Forge 47.5.0 – DATA_TEXT_ID ist privat)
            SynchedEntityData data = display.getEntityData();

            // Eigenen DataAccessor anlegen (lokal pro Entity)
            var TEXT_DATA = SynchedEntityData.defineId(
                    Display.TextDisplay.class,
                    EntityDataSerializers.COMPONENT
            );

            // Text definieren und setzen
            data.define(TEXT_DATA, Component.literal(text));
            data.set(TEXT_DATA, Component.literal(text));

            // Optional: Hintergrund / Stil / Ausrichtung
            // display.getEntityData().define(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, 0x40000000);

            return display;
        } catch (Exception e) {
            LOGGER.error("Fehler beim Erstellen des Text-Displays", e);
            return null;
        }
    }

    /**
     * Entfernt alle TextDisplays eines bestimmten Plots
     */
    public static void removeHologram(ServerLevel world, String plotId) {
        List<Display.TextDisplay> displays = HOLOGRAMS.remove(plotId);
        if (displays != null) {
            for (Display.TextDisplay display : displays) {
                display.remove(Display.TextDisplay.RemovalReason.DISCARDED);
            }
        }
    }

    /**
     * Entfernt alle Hologramme
     */
    public static void removeAllHolograms(ServerLevel world) {
        LOGGER.info("Entferne alle Hologramme...");

        for (String plotId : new ArrayList<>(HOLOGRAMS.keySet())) {
            removeHologram(world, plotId);
        }

        HOLOGRAMS.clear();
    }

    /**
     * Aktualisiert Hologramme regelmäßig
     */
    public static void updateHolograms(ServerLevel world) {
        if (!ModConfigHandler.COMMON.HOLOGRAMS_ENABLED.get()) return;

        updateCounter++;
        int interval = ModConfigHandler.COMMON.HOLOGRAM_UPDATE_INTERVAL.get();

        if (updateCounter >= interval) {
            updateCounter = 0;

            for (PlotRegion plot : PlotManager.getPlots()) {
                createHologram(world, plot);
            }
        }
    }

    /**
     * Sendet Hologramme an einen Spieler (optional beim Join)
     */
    public static void sendHologramsToPlayer(ServerPlayer player) {
        if (!ModConfigHandler.COMMON.HOLOGRAMS_ENABLED.get()) return;

        for (List<Display.TextDisplay> displays : HOLOGRAMS.values()) {
            for (Display.TextDisplay display : displays) {
                // Optional: Entity-Paket manuell senden, falls erforderlich
                // player.connection.send(new ClientboundAddEntityPacket(display));
            }
        }
    }
}

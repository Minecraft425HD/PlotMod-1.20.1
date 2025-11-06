package de.beispielmod.plotmod.events;

import de.beispielmod.plotmod.region.PlotManager;
import de.beispielmod.plotmod.region.PlotRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.UUID;

/**
 * PlotMod 3.0 - Block-Schutz mit Trusted Players Support
 */
public class BlockProtectionHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Verhindert das Abbauen von Blöcken in fremden Plots
     */
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();

        if (!checkPlotPermission(player, pos, "abbauen")) {
            event.setCanceled(true);
        }
    }

    /**
     * Verhindert das Platzieren von Blöcken in fremden Plots
     */
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        BlockPos pos = event.getPos();

        if (!checkPlotPermission(player, pos, "platzieren")) {
            event.setCanceled(true);
        }
    }

    /**
     * Zentrale Berechtigungsprüfung mit Trusted Players Support
     * 
     * @param player Der Spieler
     * @param pos Die Position
     * @param action Die Aktion (für Fehlermeldung)
     * @return true wenn erlaubt, false wenn verboten
     */
    private boolean checkPlotPermission(Player player, BlockPos pos, String action) {
        UUID playerUUID = player.getUUID();

        // Prüfe alle Plots
        for (PlotRegion plot : PlotManager.getPlots()) {
            if (plot.contains(pos)) {
                
                // Plot hat keinen Besitzer = öffentlich
                if (!plot.hasOwner()) {
                    return true;
                }

                // ═══════════════════════════════════════════════════════════
                // NEUE LOGIK: hasAccess() prüft:
                // - Besitzer
                // - Trusted Players
                // - Mieter (falls vermietet)
                // ═══════════════════════════════════════════════════════════
                if (plot.hasAccess(playerUUID)) {
                    return true;
                }

                // Keine Berechtigung - zeige Fehlermeldung
                String ownerInfo;
                if (plot.isRented()) {
                    ownerInfo = "Dieser Plot ist vermietet";
                } else {
                    ownerInfo = "Dieser Plot gehört jemand anderem";
                }
                
                player.displayClientMessage(
                    Component.literal(
                        "§c✗ Du darfst hier keine Blöcke " + action + "!\n" +
                        "§7" + ownerInfo
                    ), 
                    true
                );
                
                LOGGER.debug("Plot-Schutz: {} versuchte Block zu {} bei {} (Plot: {})",
                    player.getName().getString(), action, pos, plot.getPlotName());
                
                return false;
            }
        }

        // Nicht in einem Plot = erlaubt
        return true;
    }
}

package de.beispielmod.plotmod.tobacco;

import de.beispielmod.plotmod.managers.ShopManager;
import de.beispielmod.plotmod.tobacco.items.FermentedTobaccoLeafItem;
import net.minecraft.world.item.ItemStack;

/**
 * Shop-Integration für ALLE Tabak-Items + NEUE FEATURES
 */
public class TobaccoShopIntegration {
    
    /**
     * Fügt alle Items zum Shop hinzu
     */
    public static void registerShopItems() {
        // ═══════════════════════════════════════════════════════════
        // SAMEN
        // ═══════════════════════════════════════════════════════════
        ShopManager.addItem("plotmod:virginia_seeds", 10.0, 0.0);
        ShopManager.addItem("plotmod:burley_seeds", 15.0, 0.0);
        ShopManager.addItem("plotmod:oriental_seeds", 20.0, 0.0);
        ShopManager.addItem("plotmod:havana_seeds", 30.0, 0.0);
        
        // ═══════════════════════════════════════════════════════════
        // FLASCHEN
        // ═══════════════════════════════════════════════════════════
        ShopManager.addItem("plotmod:fertilizer_bottle", 50.0, 0.0);
        ShopManager.addItem("plotmod:growth_booster_bottle", 75.0, 0.0);
        ShopManager.addItem("plotmod:quality_booster_bottle", 100.0, 0.0);
        
        // ═══════════════════════════════════════════════════════════
        // TÖPFE
        // ═══════════════════════════════════════════════════════════
        ShopManager.addItem("plotmod:terracotta_pot", 20.0, 10.0);
        ShopManager.addItem("plotmod:ceramic_pot", 40.0, 20.0);
        ShopManager.addItem("plotmod:iron_pot", 80.0, 40.0);
        ShopManager.addItem("plotmod:golden_pot", 150.0, 75.0);
        
        // ═══════════════════════════════════════════════════════════
        // VERARBEITUNGS-BLÖCKE
        // ═══════════════════════════════════════════════════════════
        ShopManager.addItem("plotmod:drying_rack", 100.0, 50.0);
        ShopManager.addItem("plotmod:fermentation_barrel", 200.0, 100.0);
        ShopManager.addItem("plotmod:sink", 150.0, 75.0);
        
        // ═══════════════════════════════════════════════════════════
        // WERKZEUGE
        // ═══════════════════════════════════════════════════════════
        ShopManager.addItem("plotmod:watering_can", 50.0, 25.0);
        
        // ═══════════════════════════════════════════════════════════
        // FERMENTIERTER TABAK (verkaufbar mit dynamischen Preisen)
        // ═══════════════════════════════════════════════════════════
        ShopManager.addItem("plotmod:fermented_virginia_leaf", 0.0, 20.0);
        ShopManager.addItem("plotmod:fermented_burley_leaf", 0.0, 30.0);
        ShopManager.addItem("plotmod:fermented_oriental_leaf", 0.0, 40.0);
        ShopManager.addItem("plotmod:fermented_havana_leaf", 0.0, 60.0);
        
        // ═══════════════════════════════════════════════════════════
        // NEUE FEATURES: ERDSÄCKE
        // ═══════════════════════════════════════════════════════════
        ShopManager.addItem("plotmod:soil_bag_small", 10.0, 5.0);   // 1 Pflanze
        ShopManager.addItem("plotmod:soil_bag_medium", 25.0, 12.0); // 2 Pflanzen
        ShopManager.addItem("plotmod:soil_bag_large", 50.0, 25.0);  // 3 Pflanzen
        
        // ═══════════════════════════════════════════════════════════
        // NEUE FEATURES: ARBEITER-SYSTEM
        // ═══════════════════════════════════════════════════════════
        ShopManager.addItem("plotmod:worker_storage", 500.0, 250.0); // Arbeiter-Schrank
        // Worker-NPC Spawn-Egg wird separat gehandhabt
        
        // ═══════════════════════════════════════════════════════════
        // NEUE FEATURES: BARGELD
        // ═══════════════════════════════════════════════════════════
        // Bargeld wird NICHT im Shop verkauft - nur über Commands
    }
    
    /**
     * Berechnet dynamischen Verkaufspreis für fermentierten Tabak
     */
    public static double calculateFermentedTobaccoSellPrice(ItemStack stack) {
        if (!(stack.getItem() instanceof FermentedTobaccoLeafItem)) {
            return 0.0;
        }
        
        return FermentedTobaccoLeafItem.getPrice(stack);
    }
}

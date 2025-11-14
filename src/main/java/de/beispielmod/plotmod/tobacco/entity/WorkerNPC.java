package de.beispielmod.plotmod.tobacco.entity;

import de.beispielmod.plotmod.tobacco.PotType;
import de.beispielmod.plotmod.tobacco.TobaccoType;
import de.beispielmod.plotmod.tobacco.blockentity.TobaccoPotBlockEntity;
import de.beispielmod.plotmod.tobacco.blockentity.WorkerStorageBlockEntity;
import de.beispielmod.plotmod.tobacco.blocks.TobaccoPotBlock;
import de.beispielmod.plotmod.tobacco.items.FreshTobaccoLeafItem;
import de.beispielmod.plotmod.tobacco.items.TobaccoSeedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Arbeiter-NPC Entity
 * 
 * Aufgaben:
 * 1. Töpfe mit Erde befüllen
 * 2. Töpfe gießen
 * 3. Samen pflanzen
 * 4. Flaschen anwenden
 * 5. Pflanzen ernten
 * 6. Items in Schrank lagern
 */
public class WorkerNPC extends PathfinderMob {
    
    private static final EntityDataAccessor<BlockPos> STORAGE_POS = 
        SynchedEntityData.defineId(WorkerNPC.class, EntityDataSerializers.BLOCK_POS);
    
    private static final int WORK_INTERVAL = 40; // 2 Sekunden
    private static final int SEARCH_RADIUS = 16;
    private static final double COST_PER_ACTION = 5.0;
    
    private int workTimer = 0;
    private WorkState currentState = WorkState.IDLE;
    
    public WorkerNPC(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, SEARCH_RADIUS);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6));
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STORAGE_POS, BlockPos.ZERO);
    }
    
    public void setStoragePos(BlockPos pos) {
        this.entityData.set(STORAGE_POS, pos);
    }
    
    public BlockPos getStoragePos() {
        return this.entityData.get(STORAGE_POS);
    }
    
    @Override
    public void aiStep() {
        super.aiStep();
        
        if (level().isClientSide) return;
        
        workTimer++;
        if (workTimer >= WORK_INTERVAL) {
            workTimer = 0;
            performWork();
        }
    }
    
    /**
     * Führt Arbeit aus
     */
    private void performWork() {
        BlockPos storagePos = getStoragePos();
        if (storagePos.equals(BlockPos.ZERO)) return;
        
        BlockEntity be = level().getBlockEntity(storagePos);
        if (!(be instanceof WorkerStorageBlockEntity storage)) return;
        
        // Prüfe ob genug Geld vorhanden
        if (storage.getMoney() < COST_PER_ACTION) {
            currentState = WorkState.NO_MONEY;
            return;
        }
        
        // Suche Töpfe in Umgebung
        BlockPos pot = findNearbyPot();
        if (pot == null) {
            currentState = WorkState.NO_POTS;
            return;
        }
        
        // Versuche Arbeitsschritte
        if (tryFillSoil(pot, storage)) return;
        if (tryWaterPot(pot, storage)) return;
        if (tryPlantSeed(pot, storage)) return;
        if (tryApplyBottle(pot, storage)) return;
        if (tryHarvest(pot, storage)) return;
        
        currentState = WorkState.IDLE;
    }
    
    /**
     * Sucht Topf in der Nähe
     */
    private BlockPos findNearbyPot() {
        BlockPos pos = blockPosition();
        
        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (level().getBlockState(checkPos).getBlock() instanceof TobaccoPotBlock) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Befüllt Topf mit Erde
     */
    private boolean tryFillSoil(BlockPos pot, WorkerStorageBlockEntity storage) {
        BlockEntity be = level().getBlockEntity(pot);
        if (!(be instanceof TobaccoPotBlockEntity potBE)) return false;
        
        var potData = potBE.getPotData();
        if (potData.hasSoil()) return false;
        
        // Versuche Erde zu verbrauchen
        if (storage.consumeSoil() && storage.consumeMoney(COST_PER_ACTION)) {
            potData.setSoil(true);
            potBE.setChanged();
            currentState = WorkState.FILLING_SOIL;
            return true;
        }
        
        return false;
    }
    
    /**
     * Gießt Topf (unendlich Wasser für NPC)
     */
    private boolean tryWaterPot(BlockPos pot, WorkerStorageBlockEntity storage) {
        BlockEntity be = level().getBlockEntity(pot);
        if (!(be instanceof TobaccoPotBlockEntity potBE)) return false;
        
        var potData = potBE.getPotData();
        if (!potData.hasSoil()) return false;
        if (potData.getWaterLevel() >= potData.getMaxWater() * 0.5) return false;
        
        // Gieße (100 Einheiten, kostet nur Geld)
        if (storage.consumeMoney(COST_PER_ACTION)) {
            potData.addWater(100);
            potBE.setChanged();
            currentState = WorkState.WATERING;
            return true;
        }
        
        return false;
    }
    
    /**
     * Pflanzt Samen
     */
    private boolean tryPlantSeed(BlockPos pot, WorkerStorageBlockEntity storage) {
        BlockEntity be = level().getBlockEntity(pot);
        if (!(be instanceof TobaccoPotBlockEntity potBE)) return false;
        
        var potData = potBE.getPotData();
        if (!potData.hasSoil() || potData.hasPlant()) return false;
        
        ItemStack seed = storage.getRandomSeed();
        if (seed.isEmpty()) return false;
        
        if (seed.getItem() instanceof TobaccoSeedItem seedItem) {
            TobaccoType type = seedItem.getTobaccoType();
            
            if (storage.consumeSeed() && storage.consumeMoney(COST_PER_ACTION)) {
                potData.plantSeed(type);
                potBE.setChanged();
                currentState = WorkState.PLANTING;
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Wendet Flasche an
     */
    private boolean tryApplyBottle(BlockPos pot, WorkerStorageBlockEntity storage) {
        BlockEntity be = level().getBlockEntity(pot);
        if (!(be instanceof TobaccoPotBlockEntity potBE)) return false;
        
        var potData = potBE.getPotData();
        if (!potData.hasPlant()) return false;
        
        var plant = potData.getPlant();
        if (plant.isFullyGrown()) return false;
        
        ItemStack bottle = storage.getRandomBottle();
        if (bottle.isEmpty()) return false;
        
        // Wende Effekt an (vereinfacht - alle Flaschen möglich)
        if (storage.consumeBottle() && storage.consumeMoney(COST_PER_ACTION)) {
            // Hier würde die Flaschenmechanik greifen
            potBE.setChanged();
            currentState = WorkState.APPLYING_BOTTLE;
            return true;
        }
        
        return false;
    }
    
    /**
     * Erntet Pflanze
     */
    private boolean tryHarvest(BlockPos pot, WorkerStorageBlockEntity storage) {
        BlockEntity be = level().getBlockEntity(pot);
        if (!(be instanceof TobaccoPotBlockEntity potBE)) return false;
        
        var potData = potBE.getPotData();
        if (!potData.hasPlant()) return false;
        
        var plant = potData.getPlant();
        if (!plant.isFullyGrown()) return false;
        
        if (storage.consumeMoney(COST_PER_ACTION)) {
            // Ernte
            var harvested = potData.harvest();
            if (harvested != null) {
                ItemStack leaves = FreshTobaccoLeafItem.create(
                    harvested.getType(),
                    harvested.getQuality(),
                    harvested.getHarvestYield()
                );
                
                storage.addHarvestedItem(leaves);
                potBE.setChanged();
                currentState = WorkState.HARVESTING;
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        BlockPos storagePos = getStoragePos();
        tag.putInt("StorageX", storagePos.getX());
        tag.putInt("StorageY", storagePos.getY());
        tag.putInt("StorageZ", storagePos.getZ());
        tag.putString("WorkState", currentState.name());
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("StorageX")) {
            BlockPos storagePos = new BlockPos(
                tag.getInt("StorageX"),
                tag.getInt("StorageY"),
                tag.getInt("StorageZ")
            );
            setStoragePos(storagePos);
        }
        if (tag.contains("WorkState")) {
            currentState = WorkState.valueOf(tag.getString("WorkState"));
        }
    }
    
    public WorkState getCurrentState() {
        return currentState;
    }
}

/**
 * Arbeits-Zustände
 */
enum WorkState {
    IDLE("§7Idle"),
    FILLING_SOIL("§6Erde befüllen..."),
    WATERING("§bGießen..."),
    PLANTING("§aPflanzen..."),
    APPLYING_BOTTLE("§dFlasche anwenden..."),
    HARVESTING("§eErnten..."),
    NO_MONEY("§cKein Geld!"),
    NO_POTS("§7Keine Töpfe gefunden");
    
    private final String displayName;
    
    WorkState(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

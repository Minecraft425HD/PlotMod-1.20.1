package de.beispielmod.plotmod.economy.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Bargeld-Block der in der Welt platziert werden kann
 * Verhält sich wie Slab (halber Block bei 1000€)
 */
public class CashBlock extends Block implements EntityBlock {
    
    public static final IntegerProperty MONEY_LEVEL = IntegerProperty.create("money_level", 0, 10);
    private static final double MAX_MONEY = 1000.0;
    
    // Shapes für verschiedene Füllstände
    private static final VoxelShape[] SHAPES = new VoxelShape[]{
        Block.box(0, 0, 0, 16, 1, 16),   // 0-100€
        Block.box(0, 0, 0, 16, 2, 16),   // 100-200€
        Block.box(0, 0, 0, 16, 3, 16),   // 200-300€
        Block.box(0, 0, 0, 16, 4, 16),   // 300-400€
        Block.box(0, 0, 0, 16, 5, 16),   // 400-500€
        Block.box(0, 0, 0, 16, 6, 16),   // 500-600€
        Block.box(0, 0, 0, 16, 7, 16),   // 600-700€
        Block.box(0, 0, 0, 16, 8, 16),   // 700-800€
        Block.box(0, 0, 0, 16, 9, 16),   // 800-900€
        Block.box(0, 0, 0, 16, 10, 16),  // 900-1000€
        Block.box(0, 0, 0, 16, 8, 16)    // 1000€ = halber Block
    };
    
    public CashBlock() {
        super(BlockBehaviour.Properties.of()
                .strength(0.5f)
                .noOcclusion());
        registerDefaultState(getStateDefinition().any().setValue(MONEY_LEVEL, 0));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MONEY_LEVEL);
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int moneyLevel = state.getValue(MONEY_LEVEL);
        return SHAPES[Math.min(moneyLevel, SHAPES.length - 1)];
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CashBlockEntity(pos, state);
    }
    
    /**
     * Linksklick: Bargeld abbauen
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        
        ItemStack heldItem = player.getItemInHand(hand);
        
        // Nur mit leerem Hand oder Bargeld-Item abbauen
        if (heldItem.isEmpty() || heldItem.getItem() instanceof de.beispielmod.plotmod.economy.items.CashItem) {
            double value = getValue(level, pos);
            
            if (value > 0) {
                // Gib Spieler das Geld
                ItemStack cashStack = findCashInSlot10(player);
                if (cashStack != null && cashStack.getItem() instanceof de.beispielmod.plotmod.economy.items.CashItem) {
                    if (de.beispielmod.plotmod.economy.items.CashItem.addValue(cashStack, value)) {
                        player.displayClientMessage(Component.literal(
                            "§a✓ " + String.format("%.0f€", value) + " aufgesammelt"
                        ), true);
                        
                        level.removeBlock(pos, false);
                        return InteractionResult.SUCCESS;
                    } else {
                        player.displayClientMessage(Component.literal(
                            "§cGeldbörse ist voll! (Max 10.000€)"
                        ), true);
                        return InteractionResult.FAIL;
                    }
                } else {
                    player.displayClientMessage(Component.literal(
                        "§cLege Bargeld in Slot 10!"
                    ), true);
                    return InteractionResult.FAIL;
                }
            }
        }
        
        // Zeige Info
        showInfo(level, pos, player);
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Findet Bargeld in Slot 10
     */
    private ItemStack findCashInSlot10(Player player) {
        ItemStack slot10 = player.getInventory().getItem(9); // Slot 10 = Index 9
        if (slot10.getItem() instanceof de.beispielmod.plotmod.economy.items.CashItem) {
            return slot10;
        }
        return null;
    }
    
    /**
     * Zeigt Block-Info
     */
    private void showInfo(Level level, BlockPos pos, Player player) {
        double value = getValue(level, pos);
        player.displayClientMessage(Component.literal(
            "§6═══ Bargeld ═══\n" +
            "§7Wert: §e" + String.format("%.0f€", value) + "/1000€\n" +
            "§7Linksklick zum Abbauen"
        ), false);
    }
    
    // ═══════════════════════════════════════════════════════════
    // STATIC HELPER METHODS
    // ═══════════════════════════════════════════════════════════
    
    public static BlockState createBlock() {
        return de.beispielmod.plotmod.economy.blocks.EconomyBlocks.CASH_BLOCK.get().defaultBlockState();
    }
    
    public static void setValue(Level level, BlockPos pos, double value) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CashBlockEntity cashBE) {
            cashBE.setValue(value);
            updateBlockState(level, pos, value);
        }
    }
    
    public static void addValue(Level level, BlockPos pos, double amount) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CashBlockEntity cashBE) {
            cashBE.addValue(amount);
            updateBlockState(level, pos, cashBE.getValue());
        }
    }
    
    public static double getValue(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CashBlockEntity cashBE) {
            return cashBE.getValue();
        }
        return 0.0;
    }
    
    public static BlockState getCashBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CashBlock) {
            return state;
        }
        return null;
    }
    
    private static void updateBlockState(Level level, BlockPos pos, double value) {
        int moneyLevel = (int) Math.min(10, (value / MAX_MONEY) * 10);
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CashBlock) {
            level.setBlock(pos, state.setValue(MONEY_LEVEL, moneyLevel), 3);
        }
    }
}

package de.beispielmod.plotmod.tobacco.blocks;

import de.beispielmod.plotmod.tobacco.blockentity.WorkerStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

/**
 * Arbeiter-Schrank
 * Speichert: Geld (für Bezahlung), Samen, Erde, Flaschen
 */
public class WorkerStorageBlock extends Block implements EntityBlock {
    
    public WorkerStorageBlock(Properties properties) {
        super(properties);
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkerStorageBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof WorkerStorageBlockEntity storageBE) {
                storageBE.tick();
            }
        };
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof WorkerStorageBlockEntity storageBE) {
            // Öffne GUI
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, storageBE, pos);
            }
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof WorkerStorageBlockEntity storageBE) {
                storageBE.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}

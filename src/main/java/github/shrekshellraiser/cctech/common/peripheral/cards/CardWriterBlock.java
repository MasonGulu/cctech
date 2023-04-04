package github.shrekshellraiser.cctech.common.peripheral.cards;

import github.shrekshellraiser.cctech.common.ModBlockEntities;
import github.shrekshellraiser.cctech.common.ModBlocks;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.item.cards.MagCardItem;
import github.shrekshellraiser.cctech.common.peripheral.StorageBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CardWriterBlock extends StorageBlock {

    public CardWriterBlock() {
        super(Properties.of(Material.METAL).strength(2f));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                 InteractionHand pHand, BlockHitResult pHit) {
        CardWriterBlockEntity cardWriter = (CardWriterBlockEntity) pLevel.getBlockEntity(pPos);
        if (pPlayer.isShiftKeyDown()) {
            boolean isOpen = pState.getValue(ModProperties.OPEN);
            pState = pState.setValue(ModProperties.OPEN, !isOpen);
            pLevel.setBlock(pPos, pState, 3);
            pLevel.blockEntityChanged(pPos);
            pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
        } else if (pState.getValue(ModProperties.OPEN)) {
            cardWriter.onRightClick(pLevel, pPlayer, pHand);
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        var blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof CardWriterBlockEntity cardWriterEntity){
            ItemStack stack = new ItemStack(ModBlocks.CARD_WRITER.get());
            cardWriterEntity.saveToItem(stack);
            level.addFreshEntity(new ItemEntity(level,pos.getX(),pos.getY(),pos.getZ(),stack));
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.CARD_WRITER.get().create(pPos, pState);
    }
}

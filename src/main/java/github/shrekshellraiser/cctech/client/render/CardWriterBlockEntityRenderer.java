package github.shrekshellraiser.cctech.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.peripheral.cards.CardWriterBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlock;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;

public class CardWriterBlockEntityRenderer implements BlockEntityRenderer<CardWriterBlockEntity> {
    public CardWriterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CardWriterBlockEntity pBlockEntity, float pPartialTick,
                       PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        ItemStack itemStack = pBlockEntity.getRenderStack();
        pPoseStack.pushPose();
        float[] totalRotation = new float[]{90, 0, 180};
        double[] totalTranslate = new double[]{0, 0, -1.01};
        pPoseStack.scale(0.8f, 0.8f, 0.8f);

        // SCALE ROTATE TRANSLATE

        switch (pBlockEntity.getBlockState().getValue(CassetteDeckBlock.FACING)) {
            case NORTH -> {
                totalRotation[1] += 180;
                totalTranslate[0] += 0.625;
                totalTranslate[1] += 0.2;
            }
            case EAST -> {
                totalRotation[1] += 90;
                totalTranslate[0] += 0.625;
                totalTranslate[1] -= 1.05;
            }
            case SOUTH -> {
                totalTranslate[0] -= 0.625;
                totalTranslate[1] -= 1.05;
            }
            case WEST -> {
                totalRotation[1] += 270;
                totalTranslate[0] -= 0.625;
                totalTranslate[1] += 0.2;
            }
        }
        pPoseStack.mulPose(Vector3f.YP.rotationDegrees(totalRotation[1]));
        pPoseStack.mulPose(Vector3f.XP.rotationDegrees(totalRotation[0]));
        pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(totalRotation[2]));

        pPoseStack.translate(totalTranslate[0], totalTranslate[1], totalTranslate[2]);

        itemRenderer.renderStatic(itemStack, ItemTransforms.TransformType.GROUND,
                0xFF,
                OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, 1);
        pPoseStack.popPose();

    }
}

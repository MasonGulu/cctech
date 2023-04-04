package github.shrekshellraiser.cctech.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import github.shrekshellraiser.cctech.common.ModProperties;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlock;
import github.shrekshellraiser.cctech.common.peripheral.tape.cassette.CassetteDeckBlockEntity;
import github.shrekshellraiser.cctech.common.peripheral.tape.reel.ReelToReelBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class ReelToReelBlockEntityRenderer implements BlockEntityRenderer<ReelToReelBlockEntity> {
    public ReelToReelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(ReelToReelBlockEntity pBlockEntity, float pPartialTick,
                       PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        ItemStack itemStack = pBlockEntity.getRenderStack();
        pPoseStack.pushPose();
        float[] totalRotation = new float[]{0, 180, 0};
        double[] totalTranslate = new double[]{0, 0.82, 0};
        pPoseStack.scale(0.7f, 0.7f, 0.7f);

        boolean open = pBlockEntity.getBlockState().getValue(ModProperties.OPEN);

        // SCALE ROTATE TRANSLATE

        switch (pBlockEntity.getBlockState().getValue(CassetteDeckBlock.FACING)) {
            case NORTH -> {
                totalTranslate[0] -= 1.025;
                totalTranslate[2] -= 0.13;
            }
            case EAST -> {
                totalRotation[1] -= 90;
                totalTranslate[0] -= 1.025;
                totalTranslate[2] += 1.28;
            }
            case SOUTH -> {
                totalRotation[1] -= 180;
                totalTranslate[0] += 0.4;
                totalTranslate[2] += 1.28;
            }
            case WEST -> {
                totalRotation[1] += 90;
                totalTranslate[0] += 0.4;
                totalTranslate[2] -= 0.13;

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

package iskallia.ibuilders.block.render;

import iskallia.ibuilders.block.entity.TileEntityMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class TESRMarker extends TileEntitySpecialRenderer<TileEntityMarker> {

    @Override
    public void render(TileEntityMarker te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.isMaster()) renderField(te, x, y, z, partialTicks, destroyStage, alpha);
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }

    protected void renderField(TileEntityMarker te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        boolean facingX = (te.getExtensionX().getX() - te.getPos().getX()) > 0;
        boolean facingZ = (te.getExtensionZ().getZ() - te.getPos().getZ()) > 0;

        // Master bound coordinates
        double masterX = te.getPos().getX();
        double masterY = te.getPos().getY();
        double masterZ = te.getPos().getZ();

        // Extension bound coordinates
        double extensionX = te.getExtensionX().getX() - (facingX ? 1 : -1);
        double extensionY = te.getExtensionY().getY() + 1;
        double extensionZ = te.getExtensionZ().getZ() - (facingZ ? 1 : -1);

        AxisAlignedBB cuboid = new AxisAlignedBB(
                masterX, masterY, masterZ,
                extensionX, extensionY, extensionZ
        ).grow(0.02d);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        GlStateManager.translate(x, y, z);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.glLineWidth(1f);
        GlStateManager.color(1f, 1f, 1f);

        cuboid = cuboid.offset(
                -masterX + (facingX ? 1 : 0),
                -masterY,
                -masterZ + (facingZ ? 1 : 0));

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);

        renderFaces(cuboid, (facingX && facingZ) ? 0x0000FF : 0x00FF00);

        GlStateManager.depthMask(true);
        GlStateManager.glLineWidth(1f);
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableLighting();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        Minecraft.getMinecraft().entityRenderer.enableLightmap();
        GlStateManager.enableTexture2D();

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    private void renderFaces(AxisAlignedBB cube, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        float cR = (color >> 16 & 255) / 255.0f;
        float cG = (color >> 8 & 255) / 255.0f;
        float cB = (color & 255) / 255.0f;
        float cA = 0.30f;

        double x1 = cube.minX;
        double y1 = cube.minY;
        double z1 = cube.minZ;
        double x2 = cube.maxX;
        double y2 = cube.maxY;
        double z2 = cube.maxZ;

        // Draw the faces
        buffer.pos(x1, y1, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x1, y2, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y2, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y1, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x1, y1, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y1, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y2, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x1, y2, z2).color(cR, cG, cB, cA).endVertex();

        buffer.pos(x1, y1, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y1, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y1, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x1, y1, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x1, y2, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x1, y2, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y2, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y2, z1).color(cR, cG, cB, cA).endVertex();

        buffer.pos(x1, y1, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x1, y1, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x1, y2, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x1, y2, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y1, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y2, z1).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y2, z2).color(cR, cG, cB, cA).endVertex();
        buffer.pos(x2, y1, z2).color(cR, cG, cB, cA).endVertex();

        tessellator.draw();
        GlStateManager.enableCull();
    }

}

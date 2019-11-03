package iskallia.ibuilders.block.render;

import iskallia.ibuilders.block.entity.TileEntityMarker;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class TESRMarker extends TileEntitySpecialRenderer<TileEntityMarker> {

    @Override
    public void render(TileEntityMarker te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        // TODO render field on master te
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}

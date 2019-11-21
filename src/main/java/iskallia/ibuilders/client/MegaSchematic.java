package iskallia.ibuilders.client;

import com.github.lunatrius.core.client.renderer.GeometryMasks;
import com.github.lunatrius.core.client.renderer.GeometryTessellator;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MegaSchematic implements ISchematic {

    private Deque<SchematicWrapper> schematics = new ConcurrentLinkedDeque<>();

    public MegaSchematic() {
    }

    public void clear() {
        this.schematics.forEach(schematicWrapper -> {
            Minecraft.getMinecraft().world.markBlockRangeForRenderUpdate(
                        schematicWrapper.pos.x,
                        schematicWrapper.pos.y,
                        schematicWrapper.pos.z,
                        schematicWrapper.pos.x + schematicWrapper.schematic.getWidth(),
                        schematicWrapper.pos.y + schematicWrapper.schematic.getHeight(),
                        schematicWrapper.pos.z + schematicWrapper.schematic.getLength()
                );
        });

        this.schematics.clear();
    }

    public void addSchematic(ISchematic schematic, BlockPos pos) {
        SchematicWrapper schematicWrapper = new SchematicWrapper(schematic, pos);
        this.schematics.add(schematicWrapper);

        Minecraft.getMinecraft().world.markBlockRangeForRenderUpdate(
                schematicWrapper.pos.x,
                schematicWrapper.pos.y,
                schematicWrapper.pos.z,
                schematicWrapper.pos.x + schematicWrapper.schematic.getWidth(),
                schematicWrapper.pos.y + schematicWrapper.schematic.getHeight(),
                schematicWrapper.pos.z + schematicWrapper.schematic.getLength()
        );
    }

    public void renderOutlines() {
        /*
        for(SchematicWrapper schematicWrapper: this.schematics) {
            ISchematic schematic = schematicWrapper.schematic;
            BlockPos pos1 = schematicWrapper.getPosition();
            BlockPos pos2 = pos1.add(schematic.getWidth(), schematic.getHeight(), schematic.getLength());

            Entity cam = Minecraft.getMinecraft().getRenderViewEntity();
            Vec3d camPos = new Vec3d(cam.posX, cam.posY, cam.posZ);

            GlStateManager.glLineWidth(2.0f);
            Tessellator.getInstance().getBuffer().begin(3, DefaultVertexFormats.POSITION_COLOR);

            this.putVertex(camPos, pos1);
            this.putVertex(camPos, pos2);

            Tessellator.getInstance().draw();
        }*/
    }

    protected void putVertex(Vec3d camPos, BlockPos pos) {
        for(int i = 0; i < 2; i++) {
            Tessellator.getInstance().getBuffer().pos(
                    pos.getX() - camPos.x,
                    pos.getY() - camPos.y,
                    pos.getZ() - camPos.z
            ).color(
                    1.0f,
                    1.0f,
                    1.0f,
                    1.0f
            ).endVertex();
        }
    }

    public static class SchematicWrapper {
        private final ISchematic schematic;
        private final BlockPos pos;

        public SchematicWrapper(ISchematic schematic, BlockPos pos) {
            this.schematic = schematic;
            this.pos = pos;
        }

        public IBlockState getBlockState(BlockPos pos) {
            return this.schematic.getBlockState(pos.subtract(this.pos));
        }

        public ISchematic getSchematic() {
            return this.schematic;
        }

        public boolean setBlockState(BlockPos pos, IBlockState newState) {
            return this.schematic.setBlockState(pos.subtract(this.pos), newState);
        }

        public BlockPos getPosition() {
            return this.pos;
        }
    }


    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState) {
        pos = pos.add(ClientProxy.schematic.position);
        boolean flag = false;

        for(SchematicWrapper schematic: this.schematics) {
            flag |= schematic.setBlockState(pos, newState);
        }

        return flag;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        pos = pos.add(ClientProxy.schematic.position);

        for(SchematicWrapper schematic: this.schematics) {
            IBlockState state = schematic.getBlockState(pos);

            if(state != Blocks.AIR.getDefaultState()) {
                return state;
            }
        }

        return Minecraft.getMinecraft().world.getBlockState(pos);
    }

    @Override
    public TileEntity getTileEntity(BlockPos blockPos) {
        return null;
    }

    @Override
    public List<TileEntity> getTileEntities() {
        return new ArrayList<>();
    }

    @Override
    public void setTileEntity(BlockPos blockPos, TileEntity tileEntity) {

    }

    @Override
    public void removeTileEntity(BlockPos blockPos) {

    }

    @Override
    public List<Entity> getEntities() {
        return new ArrayList<>();
    }

    @Override
    public void addEntity(Entity entity) {

    }

    @Override
    public void removeEntity(Entity entity) {

    }

    @Override
    public ItemStack getIcon() {
        return ItemStack.EMPTY;
    }

    @Override
    public void setIcon(ItemStack itemStack) {

    }

    @Override
    public int getWidth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getHeight() {
        return Integer.MAX_VALUE;
    }

    @Nonnull
    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public void setAuthor(@Nonnull String s) {

    }

}

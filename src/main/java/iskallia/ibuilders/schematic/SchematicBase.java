package iskallia.ibuilders.schematic;

import com.github.lunatrius.schematica.api.ISchematic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SchematicBase implements ISchematic {

    final short[][][] blocks;
    final byte[][][] metadata;
    final int width;
    final int height;
    final int length;
    private String author;

    public SchematicBase(final int width, final int height, final int length) {
        this(width, height, length, "");
    }

    public SchematicBase(final int width, final int height, final int length, @Nonnull final String author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        this.blocks = new short[width][height][length];
        this.metadata = new byte[width][height][length];

        this.width = width;
        this.height = height;
        this.length = length;

        this.author = author;
    }

    @Override
    public IBlockState getBlockState(final BlockPos pos) {
        if (!isValid(pos)) {
            return Blocks.AIR.getDefaultState();
        }

        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        final Block block = Block.REGISTRY.getObjectById(this.blocks[x][y][z]);

        return block.getStateFromMeta(this.metadata[x][y][z]);
    }

    @Override
    public boolean setBlockState(final BlockPos pos, final IBlockState blockState) {
        if (!isValid(pos)) {
            return false;
        }

        final Block block = blockState.getBlock();
        final int id = Block.REGISTRY.getIDForObject(block);
        if (id == -1) {
            return false;
        }

        final int meta = block.getMetaFromState(blockState);
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();

        this.blocks[x][y][z] = (short) id;
        this.metadata[x][y][z] = (byte) meta;
        return true;
    }

    @Override
    public TileEntity getTileEntity(final BlockPos pos) {
        return null;
    }

    @Override
    public List<TileEntity> getTileEntities() {
        return new ArrayList<>();
    }

    @Override
    public void setTileEntity(final BlockPos pos, final TileEntity tileEntity) {
    }

    @Override
    public void removeTileEntity(final BlockPos pos) {

    }

    @Override
    public List<Entity> getEntities() {
        return new ArrayList<>();
    }

    @Override
    public void addEntity(final Entity entity) {
    }

    @Override
    public void removeEntity(final Entity entity) {
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Blocks.ANVIL); // Won't be saved, generating just in case.
    }

    @Override
    public void setIcon(ItemStack itemStack) { } // Not saving any icon..

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    private boolean isValid(final BlockPos pos) {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();

        return !(x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.height || z >= this.length);
    }

    @Override
    @Nonnull
    public String getAuthor() {
        return this.author;
    }

    @Override
    public void setAuthor(@Nonnull final String author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        this.author = author;
    }

}
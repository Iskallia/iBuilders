package iskallia.ibuilders.schematic;

import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.api.event.PreSchematicSaveEvent;
import com.github.lunatrius.schematica.nbt.NBTHelper;
import com.github.lunatrius.schematica.reference.Names;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.schematic.SchematicFormat;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SchematicFormatBase extends SchematicFormat {

    protected abstract String getMaterialsName();

    @Override
    public BuildersSchematic readFromNBT(final NBTTagCompound tagCompound) {
        final byte[] localBlocks = tagCompound.getByteArray(Names.NBT.BLOCKS);
        final byte[] localMetadata = tagCompound.getByteArray(Names.NBT.DATA);

        boolean extra = false;
        byte extraBlocks[] = null;
        byte extraBlocksNibble[] = null;
        if (tagCompound.hasKey(Names.NBT.ADD_BLOCKS)) {
            extra = true;
            extraBlocksNibble = tagCompound.getByteArray(Names.NBT.ADD_BLOCKS);
            extraBlocks = new byte[extraBlocksNibble.length * 2];
            for (int i = 0; i < extraBlocksNibble.length; i++) {
                extraBlocks[i * 2 + 0] = (byte) ((extraBlocksNibble[i] >> 4) & 0xF);
                extraBlocks[i * 2 + 1] = (byte) (extraBlocksNibble[i] & 0xF);
            }
        } else if (tagCompound.hasKey(Names.NBT.ADD_BLOCKS_SCHEMATICA)) {
            extra = true;
            extraBlocks = tagCompound.getByteArray(Names.NBT.ADD_BLOCKS_SCHEMATICA);
        }

        final short width = tagCompound.getShort(Names.NBT.WIDTH);
        final short length = tagCompound.getShort(Names.NBT.LENGTH);
        final short height = tagCompound.getShort(Names.NBT.HEIGHT);

        Short id = null;
        final Map<Short, Short> oldToNew = new HashMap<Short, Short>();
        if (tagCompound.hasKey(Names.NBT.MAPPING_SCHEMATICA)) {
            final NBTTagCompound mapping = tagCompound.getCompoundTag(Names.NBT.MAPPING_SCHEMATICA);
            final Set<String> names = mapping.getKeySet();
            for (final String name : names) {
                oldToNew.put(mapping.getShort(name), (short) Block.REGISTRY.getIDForObject(Block.REGISTRY.getObject(new ResourceLocation(name))));
            }
        }

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final BuildersSchematic schematic = new BuildersSchematic(width, height, length);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    final int index = x + (y * length + z) * width;
                    int blockID = (localBlocks[index] & 0xFF) | (extra ? ((extraBlocks[index] & 0xFF) << 8) : 0);
                    final int meta = localMetadata[index] & 0xFF;

                    if ((id = oldToNew.get((short) blockID)) != null) {
                        blockID = id;
                    }

                    final Block block = Block.REGISTRY.getObjectById(blockID);
                    pos.setPos(x, y, z);
                    try {
                        final IBlockState blockState = block.getStateFromMeta(meta);
                        schematic.setBlockState(pos, blockState);
                    } catch (final Exception e) {
                        Reference.logger.error("Could not set block state at {} to {} with metadata {}", pos, Block.REGISTRY.getNameForObject(block), meta, e);
                    }
                }
            }
        }

        final NBTTagList tileEntitiesList = tagCompound.getTagList(Names.NBT.TILE_ENTITIES, Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tileEntitiesList.tagCount(); i++) {
            try {
                final TileEntity tileEntity = NBTHelper.readTileEntityFromCompound(tileEntitiesList.getCompoundTagAt(i));
                if (tileEntity != null) {
                    schematic.setTileEntity(tileEntity.getPos(), tileEntity);
                }
            } catch (final Exception e) {
                Reference.logger.error("TileEntity failed to load properly!", e);
            }
        }

        return schematic;
    }

    @Override
    public boolean writeToNBT(final NBTTagCompound tagCompound, final ISchematic schematic) {
        tagCompound.setShort(Names.NBT.WIDTH, (short) schematic.getWidth());
        tagCompound.setShort(Names.NBT.LENGTH, (short) schematic.getLength());
        tagCompound.setShort(Names.NBT.HEIGHT, (short) schematic.getHeight());

        final int size = schematic.getWidth() * schematic.getLength() * schematic.getHeight();
        final byte[] localBlocks = new byte[size];
        final byte[] localMetadata = new byte[size];
        final byte[] extraBlocks = new byte[size];
        final byte[] extraBlocksNibble = new byte[(int) Math.ceil(size / 2.0)];
        boolean extra = false;

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final Map<String, Short> mappings = new HashMap<String, Short>();
        for (int x = 0; x < schematic.getWidth(); x++) {
            for (int y = 0; y < schematic.getHeight(); y++) {
                for (int z = 0; z < schematic.getLength(); z++) {
                    final int index = x + (y * schematic.getLength() + z) * schematic.getWidth();
                    final IBlockState blockState = schematic.getBlockState(pos.setPos(x, y, z));
                    final Block block = blockState.getBlock();
                    final int blockId = Block.REGISTRY.getIDForObject(block);
                    localBlocks[index] = (byte) blockId;
                    localMetadata[index] = (byte) block.getMetaFromState(blockState);
                    if ((extraBlocks[index] = (byte) (blockId >> 8)) > 0) {
                        extra = true;
                    }

                    final String name = String.valueOf(Block.REGISTRY.getNameForObject(block));
                    if (!mappings.containsKey(name)) {
                        mappings.put(name, (short) blockId);
                    }
                }
            }
        }

        int count = 20;
        final NBTTagList tileEntitiesList = new NBTTagList();
        for (final TileEntity tileEntity : schematic.getTileEntities()) {
            try {
                final NBTTagCompound tileEntityTagCompound = NBTHelper.writeTileEntityToCompound(tileEntity);
                tileEntitiesList.appendTag(tileEntityTagCompound);
            } catch (final Exception e) {
                final BlockPos tePos = tileEntity.getPos();
                final int index = tePos.getX() + (tePos.getY() * schematic.getLength() + tePos.getZ()) * schematic.getWidth();
                if (--count > 0) {
                    final IBlockState blockState = schematic.getBlockState(tePos);
                    final Block block = blockState.getBlock();
                    Reference.logger.error("Block {}[{}] with TileEntity {} failed to save! Replacing with bedrock...", block, block != null ? Block.REGISTRY.getNameForObject(block) : "?", tileEntity.getClass().getName(), e);
                }
                localBlocks[index] = (byte) Block.REGISTRY.getIDForObject(Blocks.BEDROCK);
                localMetadata[index] = 0;
                extraBlocks[index] = 0;
            }
        }

        for (int i = 0; i < extraBlocksNibble.length; i++) {
            if (i * 2 + 1 < extraBlocks.length) {
                extraBlocksNibble[i] = (byte) ((extraBlocks[i * 2 + 0] << 4) | extraBlocks[i * 2 + 1]);
            } else {
                extraBlocksNibble[i] = (byte) (extraBlocks[i * 2 + 0] << 4);
            }
        }

        final NBTTagList entityList = new NBTTagList();
        final List<Entity> entities = schematic.getEntities();
        for (final Entity entity : entities) {
            try {
                final NBTTagCompound entityCompound = NBTHelper.writeEntityToCompound(entity);
                if (entityCompound != null) {
                    entityList.appendTag(entityCompound);
                }
            } catch (final Throwable t) {
                Reference.logger.error("Entity {} failed to save, skipping!", entity, t);
            }
        }

        final PreSchematicSaveEvent event = new PreSchematicSaveEvent(schematic, mappings);
        MinecraftForge.EVENT_BUS.post(event);

        final NBTTagCompound nbtMapping = new NBTTagCompound();
        for (final Map.Entry<String, Short> entry : mappings.entrySet()) {
            nbtMapping.setShort(entry.getKey(), entry.getValue());
        }

        tagCompound.setString(Names.NBT.MATERIALS, getMaterialsName());
        tagCompound.setByteArray(Names.NBT.BLOCKS, localBlocks);
        tagCompound.setByteArray(Names.NBT.DATA, localMetadata);
        if (extra) {
            tagCompound.setByteArray(Names.NBT.ADD_BLOCKS, extraBlocksNibble);
        }
        tagCompound.setTag(Names.NBT.ENTITIES, entityList);
        tagCompound.setTag(Names.NBT.TILE_ENTITIES, tileEntitiesList);
        tagCompound.setTag(Names.NBT.MAPPING_SCHEMATICA, nbtMapping);
        final NBTTagCompound extendedMetadata = event.extendedMetadata;
        if (!extendedMetadata.hasNoTags()) {
            tagCompound.setTag(Names.NBT.EXTENDED_METADATA, extendedMetadata);
        }

        return true;
    }

    public abstract long hash(NBTTagCompound nbt);

}

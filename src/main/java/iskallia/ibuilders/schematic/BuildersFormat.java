package iskallia.ibuilders.schematic;

import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.nbt.NBTHelper;
import com.github.lunatrius.schematica.reference.Names;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.schematic.SchematicAlpha;
import com.github.lunatrius.schematica.world.schematic.SchematicUtil;
import com.github.lunatrius.schematica.world.storage.Schematic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BuildersFormat extends SchematicAlpha {

    public static BuildersFormat INSTANCE = new BuildersFormat();

    @Override
    public String getName() {
        return "ibuilders.format.builders";
    }

    @Override
    public String getExtension() {
        return ".schematic";
    }

    @Override
    public boolean writeToNBT(NBTTagCompound tagCompound, ISchematic schematic) {
        boolean result = super.writeToNBT(tagCompound, schematic);

        if (schematic instanceof BuildersSchematic) {
            BuildersSchematic buildersSchematic = (BuildersSchematic) schematic;

            if (buildersSchematic.getName() != null)
                tagCompound.setString("Name", buildersSchematic.getName());

            if (buildersSchematic.getDescription() != null)
                tagCompound.setString("Description", buildersSchematic.getDescription());
        }

        return result;
    }

    @Override
    public ISchematic readFromNBT(NBTTagCompound tagCompound) {
        BuildersSchematic buildersSchematic = superReadFromNBT(tagCompound);

        if (tagCompound.hasKey("Name", Constants.NBT.TAG_STRING))
            buildersSchematic.setName(tagCompound.getString("Name"));

        if (tagCompound.hasKey("Description", Constants.NBT.TAG_STRING))
            buildersSchematic.setDescription(tagCompound.getString("Description"));

        return buildersSchematic;
    }

    // How to inject stuff by actually not injecting 101
    private BuildersSchematic superReadFromNBT(NBTTagCompound tagCompound) {
        final ItemStack icon = SchematicUtil.getIconFromNBT(tagCompound);

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

        final MBlockPos pos = new MBlockPos();
        final BuildersSchematic schematic = new BuildersSchematic(icon, width, height, length);
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
                    pos.set(x, y, z);
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

}

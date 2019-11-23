package iskallia.ibuilders.world.data;

import com.github.lunatrius.schematica.api.ISchematic;
import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.item.ItemBlueprint;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SchematicTracker extends WorldSavedData {

    protected static final String DATA_NAME = "ibuilders_SchematicsTracker";

    protected boolean isChanged = false;

    public SchematicTracker() {
        this(DATA_NAME);
    }

    public SchematicTracker(String name) {
        super(name);
    }

    public void updateCreator(TileEntityCreator creator) {
        this.isChanged = true;
    }

    public Map<BlockPos, BuildersSchematic> getClientSchematics(World world) {
        Map<BlockPos, BuildersSchematic> schematicMap = new HashMap<>();

        world.loadedTileEntityList.forEach(tileEntity -> {
            if(tileEntity instanceof TileEntityCreator) {
                TileEntityCreator creator = (TileEntityCreator)tileEntity;
                if(creator.getSchematic() != null) {
                    schematicMap.put(creator.getPos().add(creator.getOffset()), creator.getSchematic());
                }
            }
        });

        world.playerEntities.forEach(entityPlayer -> {
            ItemStack heldStack = entityPlayer.getHeldItemMainhand();
            NBTTagCompound stackNbt = heldStack.getTagCompound();

            if(heldStack.getItem() == InitItem.BLUEPRINT && stackNbt != null
                    && stackNbt.hasKey("FirstCorner", Constants.NBT.TAG_LONG)) {
                BlockPos firstCorner = BlockPos.fromLong(stackNbt.getLong("FirstCorner"));

                Pair<BlockPos, BlockPos> posData = ItemBlueprint.getCenterAndDimensions(firstCorner, firstCorner);

                if(stackNbt.hasKey("SecondCorner", Constants.NBT.TAG_LONG)) {
                    BlockPos secondCorner = BlockPos.fromLong(stackNbt.getLong("SecondCorner"));
                    posData = ItemBlueprint.getCenterAndDimensions(firstCorner, secondCorner);
                }

                schematicMap.put(posData.getKey(), new BuildersSchematic(posData.getValue().x, posData.getValue().y, posData.getValue().z));
            }
        });

        return schematicMap;
    }

    public boolean getAndSetChanged(boolean flag) {
        if(flag == this.isChanged)return this.isChanged;
        this.isChanged = flag;
        return !this.isChanged;
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        /*
        NBTTagList schematicEntries = nbt.getTagList("SchematicEntries", Constants.NBT.TAG_COMPOUND);

        schematicEntries.forEach(rawSchematicEntry -> {
            NBTTagCompound schematicEntry = (NBTTagCompound)rawSchematicEntry;

            this.schematicMap.put(
                    BlockPos.fromLong(schematicEntry.getLong("Key")),
                    BuildersFormat.INSTANCE.readFromNBT(schematicEntry.getCompoundTag("Value"))
            );
        });

        this.isChanged = true;
        */
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        /*
        NBTTagList schematicEntries = new NBTTagList();

        this.schematicMap.forEach((key, value) -> {
            NBTTagCompound schematicEntry = new NBTTagCompound();
            NBTTagCompound schematic = new NBTTagCompound();
            BuildersFormat.INSTANCE.writeToNBT(schematic, value);

            schematicEntry.setLong("Key", key.toLong());
            schematicEntry.setTag("Value", schematic);
            schematicEntries.appendTag(schematicEntry);
        });

        compound.setTag("SchematicEntries", schematicEntries);
         */
        return compound;
    }


    public static SchematicTracker get(World world) {
        SchematicTracker data = (SchematicTracker)world
                .getPerWorldStorage()
                .getOrLoadData(SchematicTracker.class, DATA_NAME);

        if(data == null) {
            data = new SchematicTracker();
            world.getPerWorldStorage().setData(DATA_NAME, data);
        }

        return data;
    }

}

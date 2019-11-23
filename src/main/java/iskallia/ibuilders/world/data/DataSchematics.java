package iskallia.ibuilders.world.data;

import com.google.common.collect.Lists;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSchematics extends WorldSavedData {

    protected static final String DATA_NAME = "ibuilders_Schematics";

    public final Map<String, List<BuildersSchematic>> schematicsMap = new HashMap<>();

    public DataSchematics() {
        this(DATA_NAME);
    }

    public DataSchematics(String name) {
        super(name);
    }

    public List<BuildersSchematic.Info> getAllInfo() {
        List<BuildersSchematic.Info> infoList = new ArrayList<>();
        this.schematicsMap.values().forEach(schematics -> schematics.forEach(schematic -> infoList.add(schematic.getInfo())));
        return infoList;
    }

    public List<BuildersSchematic.Info> getInfoFor(String playerUuid) {
        List<BuildersSchematic.Info> infoList = new ArrayList<>();

        if(this.schematicsMap.containsKey(playerUuid)) {
            this.schematicsMap.get(playerUuid).forEach(schematic -> infoList.add(schematic.getInfo()));
        }

        return infoList;
    }

    public List<BuildersSchematic> getSchematicsFor(String playerUuid) {
        if(!this.schematicsMap.containsKey(playerUuid))return Lists.newArrayList();
        return this.schematicsMap.get(playerUuid);
    }

    @Nullable
    public BuildersSchematic getSchematic(String playerUuid, String name) {
        if(!this.schematicsMap.containsKey(playerUuid))return null;

        List<BuildersSchematic> schematics = this.schematicsMap.get(playerUuid);

        if(schematics == null || schematics.isEmpty()) {
            return null;
        }

        for (BuildersSchematic schematic : schematics) {
            if(schematic.getInfo().getName().equals(name)) {
                return schematic;
            }
        }

        return null;
    }

    public void removeSchematic(String playerUuid, String name) {
        if(!this.schematicsMap.containsKey(playerUuid))return;
        List<BuildersSchematic> schematics = this.schematicsMap.get(playerUuid);

        if(schematics == null || schematics.isEmpty()) {
            return;
        }

        schematics.removeIf(schematic -> schematic.getInfo().getName().equals(name));
    }

    public boolean hasSchematic(String playerUuid, String name) {
        return this.getSchematic(playerUuid, name) != null;
    }

    public void addSchematic(String playerUuid, BuildersSchematic schematic) {
        if(schematic == null)return;

        if(!this.schematicsMap.containsKey(playerUuid)) {
            this.schematicsMap.put(playerUuid, Lists.newArrayList());
        }

        this.schematicsMap.get(playerUuid).add(schematic);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList schematicEntries = nbt.getTagList("SchematicEntries", Constants.NBT.TAG_COMPOUND);

        schematicEntries.forEach(rawSchematicEntry -> {
            NBTTagCompound schematicEntry = (NBTTagCompound)rawSchematicEntry;

            String playerUuid = schematicEntry.getString("PlayerUuid");
            NBTTagList playerSchematics = schematicEntry.getTagList("PlayerSchematics", Constants.NBT.TAG_COMPOUND);
            List<BuildersSchematic> schematics = new ArrayList<>();

            playerSchematics.forEach(rawPlayerSchematic -> {
                NBTTagCompound playerSchematic = (NBTTagCompound)rawPlayerSchematic;
                BuildersSchematic schematic = BuildersFormat.INSTANCE.readFromNBT(playerSchematic);
                if(schematic != null)schematics.add(schematic);
            });

            this.schematicsMap.put(playerUuid, schematics);
        });
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList schematicEntries = new NBTTagList();

        this.schematicsMap.forEach((key, value) -> {
            NBTTagCompound schematicEntry = new NBTTagCompound();
            schematicEntry.setString("PlayerUuid", key);

            NBTTagList playerSchematics = new NBTTagList();

            value.forEach(s -> {
                NBTTagCompound playerSchematic = new NBTTagCompound();
                BuildersFormat.INSTANCE.writeToNBT(playerSchematic, s);
                playerSchematics.appendTag(playerSchematic);
            });

            schematicEntry.setTag("PlayerSchematics", playerSchematics);
            schematicEntries.appendTag(schematicEntry);
        });

        compound.setTag("SchematicEntries", schematicEntries);
        return compound;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public static DataSchematics get(World world) {
        DataSchematics data = (DataSchematics)world
                .getMapStorage()
                .getOrLoadData(DataSchematics.class, DATA_NAME);

        if(data == null) {
            data = new DataSchematics();
            world.getMapStorage().setData(DATA_NAME, data);
        }

        return data;
    }

}

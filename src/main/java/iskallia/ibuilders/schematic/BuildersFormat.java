package iskallia.ibuilders.schematic;

import com.github.lunatrius.schematica.api.ISchematic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;

public class BuildersFormat extends SchematicFormatBase {

    public static BuildersFormat INSTANCE = new BuildersFormat();
    public static String MATERIALS_VALUE = "iBuilders";

    @Override
    public String getName() {
        return "ibuilders.format.builders";
    }

    @Override
    protected String getMaterialsName() {
        return MATERIALS_VALUE;
    }

    @Override
    public String getExtension() {
        return ".schematic";
    }

    /*
    {
        Schematic:{
            Width:1s,
            Height:3s,
            Length:2s,
            Materials:"Alpha",
            Blocks:[B;20B,54B,20B,20B,20B,20B],
            Data:[B;0B,4B,0B,0B,0B,0B],
            SchematicaMapping:{"minecraft:glass":20s,"minecraft:chest":54s},
            TileEntities:[{ForgeData:{},x:0,y:0,z:1,Items:[],id:"minecraft:chest",Lock:""}],
            Entities:[],

            Hash:-304185190,
            Info:{
                  Description:"TEST DESCRIPTION",
                  Author:"FakeGuy",
                  Name:"TEST BUILD SCHEME"
           },
     }
     */
    @Override
    public long hash(NBTTagCompound  nbt) {
        long hash = 0;

        short[] saltData = {
            nbt.getShort("Width"),
            nbt.getShort("Height"),
            nbt.getShort("Length")
        };

        long salt = 0;

        for (int i = 0; i < saltData.length; i++) {
            salt = 805306457L * salt + saltData[i];
        }

        //Because of the nature of such LCGs, an even salt offers a maximum period of m / 4.
        salt |= 1;
        hash = salt;

        for(int i = 0; i < 5; i++) {
            hash = hash * 2862933555777941757L + salt;
        }

        byte[] blocks = nbt.getByteArray("Blocks");
        byte[] data = nbt.getByteArray("Data");
        int blocksHash = Arrays.hashCode(blocks) ^ Arrays.hashCode(data);
        hash *= blocksHash;

        NBTTagList tileEntities = nbt.getTagList("TileEntities", Constants.NBT.TAG_COMPOUND);
        NBTTagList entities = nbt.getTagList("Entities", Constants.NBT.TAG_COMPOUND);
        int entityHash = tileEntities.hashCode() ^ entities.hashCode();
        hash *= entityHash;

        return hash * 2862933555777941757L + salt;
    }

    @Override
    public boolean writeToNBT(NBTTagCompound tagCompound, ISchematic schematic) {
        boolean result = super.writeToNBT(tagCompound, schematic);

        if (schematic instanceof BuildersSchematic) {
            BuildersSchematic buildersSchematic = (BuildersSchematic) schematic;
            BuildersSchematic.Info info = buildersSchematic.getInfo();
            NBTTagCompound infoNBT = new NBTTagCompound();

            // Evaluate and put hash even before the input derived from user.
            tagCompound.setLong("Hash", hash(tagCompound));

            if (info.getUuid() != null)
                infoNBT.setString("Uuid", info.getUuid());

            if (info.getName() != null)
                infoNBT.setString("Name", info.getName());

            if (info.getDescription() != null)
                infoNBT.setString("Description", info.getDescription());

            if (info.getAuthor() != null)
                infoNBT.setString("Author", info.getAuthor());

            tagCompound.setTag("Info", infoNBT);
        }

        return result;
    }

    @Override
    public BuildersSchematic readFromNBT(NBTTagCompound tagCompound) {
        BuildersSchematic buildersSchematic = super.readFromNBT(tagCompound);

        buildersSchematic.setHash(tagCompound.getLong("Hash"));

        if (tagCompound.hasKey("Info", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound infoNBT = tagCompound.getCompoundTag("Info");

            if (infoNBT.hasKey("Uuid", Constants.NBT.TAG_STRING))
                buildersSchematic.getInfo().setUuid(infoNBT.getString("Uuid"));

            if (infoNBT.hasKey("Name", Constants.NBT.TAG_STRING))
                buildersSchematic.getInfo().setName(infoNBT.getString("Name"));

            if (infoNBT.hasKey("Description", Constants.NBT.TAG_STRING))
                buildersSchematic.getInfo().setDescription(infoNBT.getString("Description"));

            if (infoNBT.hasKey("Author", Constants.NBT.TAG_STRING))
                buildersSchematic.getInfo().setAuthor(infoNBT.getString("Author"));
        }

        return buildersSchematic;
    }

}

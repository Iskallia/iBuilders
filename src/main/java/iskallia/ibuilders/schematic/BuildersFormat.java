package iskallia.ibuilders.schematic;

import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.reference.Names;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

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

    @Override
    public boolean writeToNBT(NBTTagCompound tagCompound, ISchematic schematic) {
        boolean result = super.writeToNBT(tagCompound, schematic);

        if (schematic instanceof BuildersSchematic) {
            BuildersSchematic buildersSchematic = (BuildersSchematic) schematic;
            BuildersSchematic.Info info = buildersSchematic.getInfo();
            NBTTagCompound infoNBT = new NBTTagCompound();

            // Evaluate and put hash even before the input derived from user.
            tagCompound.setInteger("Hash", tagCompound.hashCode());

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

        buildersSchematic.setHash(tagCompound.getInteger("Hash"));

        if (tagCompound.hasKey("Info", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound infoNBT = tagCompound.getCompoundTag("Info");

            if (infoNBT.hasKey("Name", Constants.NBT.TAG_STRING))
                buildersSchematic.getInfo().setName(infoNBT.getString("Name"));

            if (infoNBT.hasKey("Description", Constants.NBT.TAG_STRING))
                buildersSchematic.getInfo().setDescription(infoNBT.getString("Description"));

            if (infoNBT.hasKey("Author", Constants.NBT.TAG_STRING))
                buildersSchematic.getInfo().setDescription(infoNBT.getString("Author"));
        }

        return buildersSchematic;
    }

}

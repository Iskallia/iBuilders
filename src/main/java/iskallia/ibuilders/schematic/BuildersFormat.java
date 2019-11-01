package iskallia.ibuilders.schematic;

import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.world.schematic.SchematicAlpha;
import net.minecraft.nbt.NBTTagCompound;

public class BuildersFormat extends SchematicAlpha {

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
        return super.writeToNBT(tagCompound, schematic);
    }

    @Override
    public ISchematic readFromNBT(NBTTagCompound tagCompound) {
        return super.readFromNBT(tagCompound);
    }

}

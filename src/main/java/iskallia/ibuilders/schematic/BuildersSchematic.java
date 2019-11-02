package iskallia.ibuilders.schematic;

import com.github.lunatrius.schematica.nbt.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class BuildersSchematic extends SchematicBase {

    private BuildersSchematic.Info info = new BuildersSchematic.Info();
    private int hash;

    public BuildersSchematic(int width, int height, int length) {
        super(width, height, length);
    }

    public Info getInfo() {
        return info;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    @Override
    public void setTileEntity(BlockPos pos, TileEntity tileEntity) {
        if (tileEntity == null) {
            super.setTileEntity(pos, null);
            return;
        }

        NBTTagCompound tileEntityNBT = tileEntity.writeToNBT(new NBTTagCompound());
        TileEntity schematicTE = NBTHelper.readTileEntityFromCompound(tileEntityNBT);
        schematicTE.setPos(pos);
        super.setTileEntity(pos, schematicTE);
    }

    @Nonnull
    @Override
    public String getAuthor() {
        return info.getAuthor();
    }

    @Override
    public void setAuthor(@Nonnull String author) {
        this.info.setAuthor(author);
    }

    public static class Info {
        private String name;
        private String description;
        private String author;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getAuthor() {
            return author;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

}

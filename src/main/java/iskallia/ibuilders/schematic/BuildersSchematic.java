package iskallia.ibuilders.schematic;

import com.github.lunatrius.schematica.nbt.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class BuildersSchematic extends SchematicBase {

    // TODO: private int workload (in months)
    // TODO: private List<ItemFighterEgg> workerList (?)
    private BuildersSchematic.Info info = new BuildersSchematic.Info();
    private long hash;

    public BuildersSchematic(int width, int height, int length) {
        super(width, height, length);
    }

    public Info getInfo() {
        return info;
    }

    public long getHash() {
        return hash;
    }

    public void setHash(long hash) {
        this.hash = hash;
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

    public static class Info {
        // TODO: private long dateUnix
        private String uuid = "";
        private String name = "";
        private String description = "";
        private String author = "";

        public String getUuid() {
            return this.uuid;
        }

        public String getName() {
            return this.name;
        }

        public String getDescription() {
            return this.description;
        }

        public String getAuthor() {
            return this.author;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
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

        @Override
        public String toString() {
            String s = this.name;

            if(!this.author.isEmpty()) {
                s += " by " + this.author;
            }

            return s;
        }
    }

}

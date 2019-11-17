package iskallia.ibuilders.schematic;

import com.github.lunatrius.schematica.nbt.NBTHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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

    public static BuildersSchematic fromBytes(ByteBuf buf) {
        int width = buf.readInt();
        int height = buf.readInt();
        int length = buf.readInt();

        BuildersSchematic schematic = new BuildersSchematic(width, height, length);

        for(int i = 0; i < schematic.getWidth(); i++) {
            for(int j = 0; j < schematic.getHeight(); j++) {
                for(int k = 0; k < schematic.getLength(); k++) {
                    schematic.blocks[i][j][k] = buf.readShort();
                    schematic.metadata[i][j][k] = buf.readByte();
                }
            }
        }

        schematic.setHash(buf.readLong());
        schematic.info.setUuid(ByteBufUtils.readUTF8String(buf));
        schematic.info.setAuthor(ByteBufUtils.readUTF8String(buf));
        schematic.info.setName(ByteBufUtils.readUTF8String(buf));
        schematic.info.setDescription(ByteBufUtils.readUTF8String(buf));
        return schematic;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.getWidth());
        buf.writeInt(this.getHeight());
        buf.writeInt(this.getLength());

        for(int i = 0; i < this.getWidth(); i++) {
            for(int j = 0; j < this.getHeight(); j++) {
                for(int k = 0; k < this.getLength(); k++) {
                    buf.writeShort(this.blocks[i][j][k]);
                    buf.writeByte(this.metadata[i][j][k]);
                }
            }
        }

        buf.writeLong(this.getHash());
        ByteBufUtils.writeUTF8String(buf, this.info.getUuid());
        ByteBufUtils.writeUTF8String(buf, this.info.getAuthor());
        ByteBufUtils.writeUTF8String(buf, this.info.getName());
        ByteBufUtils.writeUTF8String(buf, this.info.getDescription());
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

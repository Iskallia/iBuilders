package iskallia.ibuilders.net.packet.mc;

import io.netty.buffer.ByteBuf;
import iskallia.ibuilders.gui.container.ISchemaInfo;
import iskallia.ibuilders.schematic.BuildersSchematic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class S2CUserUploads implements IMessage {

    private List<BuildersSchematic.Info> infoList = new ArrayList<>();

    public S2CUserUploads() {
    }

    public S2CUserUploads(List<BuildersSchematic.Info> infoList) {
        this.infoList = infoList;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readShort();

        for(int i = 0; i < size; i++) {
            BuildersSchematic.Info info = new BuildersSchematic.Info();
            info.setName(ByteBufUtils.readUTF8String(buf));
            info.setDescription(ByteBufUtils.readUTF8String(buf));
            info.setAuthor(ByteBufUtils.readUTF8String(buf));
            this.infoList.add(info);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeShort(this.infoList.size());

        this.infoList.forEach(info -> {
            ByteBufUtils.writeUTF8String(buf, info.getName());
            ByteBufUtils.writeUTF8String(buf, info.getDescription());
            ByteBufUtils.writeUTF8String(buf, info.getAuthor());
        });
    }

    public static class Handler implements IMessageHandler<S2CUserUploads, IMessage> {
        @Override
        public IMessage onMessage(S2CUserUploads message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                GuiScreen gui = Minecraft.getMinecraft().currentScreen;

                if(gui instanceof ISchemaInfo) {
                    ((ISchemaInfo)gui).setInfoList(message.infoList);
                }
            });

            return null;
        }
    }

}

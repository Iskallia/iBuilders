package iskallia.ibuilders.net.packet.mc;

import io.netty.buffer.ByteBuf;
import iskallia.ibuilders.gui.container.GuiContainerCreator;
import iskallia.ibuilders.gui.container.GuiContainerSchemaInfo;
import iskallia.ibuilders.schematic.BuildersSchematic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class S2CSchemaInfo implements IMessage {

    private Action action;
    private List<BuildersSchematic.Info> infoList = new ArrayList<>();

    public S2CSchemaInfo() {
    }

    public S2CSchemaInfo(Action action, List<BuildersSchematic.Info> infoList) {
        this.action = action;
        this.infoList = infoList;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.action = Action.values()[buf.readInt()];
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
        buf.writeInt(this.action.ordinal());
        buf.writeShort(this.infoList.size());

        this.infoList.forEach(info -> {
            ByteBufUtils.writeUTF8String(buf, info.getName());
            ByteBufUtils.writeUTF8String(buf, info.getDescription());
            ByteBufUtils.writeUTF8String(buf, info.getAuthor());
        });
    }

    public enum Action {
        OVERWRITE, ADD
    }

    public static class Handler implements IMessageHandler<S2CSchemaInfo, IMessage> {
        @Override
        public IMessage onMessage(S2CSchemaInfo message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                GuiScreen gui = Minecraft.getMinecraft().currentScreen;

                if(gui instanceof GuiContainerSchemaInfo) {
                    GuiContainerSchemaInfo schemaGui = (GuiContainerSchemaInfo)gui;
                    if(message.action == Action.OVERWRITE) {
                        schemaGui.setInfoList(message.infoList);
                    } else if(message.action == Action.ADD) {
                        schemaGui.getInfoList().addAll(message.infoList);
                    }
                }
            });

            return null;
        }
    }

}

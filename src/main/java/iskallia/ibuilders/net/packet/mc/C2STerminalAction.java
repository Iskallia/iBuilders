package iskallia.ibuilders.net.packet.mc;

import io.netty.buffer.ByteBuf;
import iskallia.ibuilders.container.ContainerSchematicTerminal;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.world.data.DataSchematics;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2STerminalAction implements IMessage {

    private Action action;
    private String[] args;

    public C2STerminalAction() {
    }

    public C2STerminalAction(Action action, String... args) {
        this.action = action;
        this.args = args;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.action = Action.values()[buf.readInt()];

        int size = buf.readShort();
        this.args = new String[size];

        for(int i = 0; i < size; i++) {
            this.args[i] = ByteBufUtils.readUTF8String(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.action.ordinal());

        buf.writeShort(this.args.length);

        for(int i = 0; i < this.args.length; i++) {
            ByteBufUtils.writeUTF8String(buf, this.args[i]);
        }
    }

    public enum Action {
        GET_INFO, UPLOAD, DELETE
    }

    public static class Handler implements IMessageHandler<C2STerminalAction, IMessage> {
        @Override
        public IMessage onMessage(C2STerminalAction message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Container container = player.openContainer;

            if(container instanceof ContainerSchematicTerminal) {
                DataSchematics dataSchematics = DataSchematics.get(player.world);
                String playerUuid = player.getUniqueID().toString();

                if(message.action == Action.GET_INFO) {
                    return new S2CSchemaInfo(S2CSchemaInfo.Action.OVERWRITE, dataSchematics.getInfoFor(playerUuid));
                } else if(message.action == Action.UPLOAD) {
                    ContainerSchematicTerminal schematicTerminal = (ContainerSchematicTerminal)container;
                    ItemStack blueprint = schematicTerminal.inventorySlots.get(0).getStack();

                    NBTTagCompound schematicNBT = blueprint.getSubCompound("Schematic");

                    if(schematicNBT != null) {
                        BuildersSchematic schematic = BuildersFormat.INSTANCE.readFromNBT(schematicNBT);

                        if(!dataSchematics.hasSchematic(playerUuid, schematic.getInfo().getName())) {
                            dataSchematics.addSchematic(playerUuid, schematic);
                            blueprint.shrink(1);
                        }
                    }

                    return new S2CSchemaInfo(S2CSchemaInfo.Action.OVERWRITE, dataSchematics.getInfoFor(playerUuid));
                } else if(message.action == Action.DELETE) {
                    dataSchematics.removeSchematic(message.args[0], message.args[1]);
                    return new S2CSchemaInfo(S2CSchemaInfo.Action.OVERWRITE, dataSchematics.getInfoFor(playerUuid));
                }
            }

            return null;
        }
    }

}

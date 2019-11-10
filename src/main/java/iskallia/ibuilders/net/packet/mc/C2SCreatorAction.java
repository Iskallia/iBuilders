package iskallia.ibuilders.net.packet.mc;

import io.netty.buffer.ByteBuf;
import iskallia.ibuilders.Builders;
import iskallia.ibuilders.container.ContainerCreator;
import iskallia.ibuilders.container.ContainerSchematicTerminal;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.item.ItemBlueprint;
import iskallia.ibuilders.net.packet.PacketRequestSchemaInfo;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.world.data.DataSchematics;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.ItemStackHandler;

public class C2SCreatorAction implements IMessage {

    private Action action;
    private String[] args;

    public C2SCreatorAction() {
    }

    public C2SCreatorAction(Action action, String... args) {
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
        GET_INFO, PRINT
    }

    public static class Handler implements IMessageHandler<C2SCreatorAction, IMessage> {
        @Override
        public IMessage onMessage(C2SCreatorAction message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Container container = player.openContainer;

            if(container instanceof ContainerCreator) {
                DataSchematics dataSchematics = DataSchematics.get(player.world);
                String playerUuid = player.getUniqueID().toString();

                if(message.action == Action.GET_INFO) {
                    Builders.NETWORK.sendToAllPlotServers(new PacketRequestSchemaInfo());
                    return new S2CSchemaInfo(S2CSchemaInfo.Action.OVERWRITE, dataSchematics.getAllInfo());
                } else if(message.action == Action.PRINT) {
                    ContainerCreator schematicTerminal = (ContainerCreator)container;
                    ItemStack paper = schematicTerminal.inventorySlots.get(1).getStack();

                    if(!paper.isEmpty() && paper.getItem() == Items.PAPER) {
                        ItemStack currentBlueprint = schematicTerminal.inventorySlots.get(2).getStack();
                        BuildersSchematic schematic = dataSchematics.getSchematic(playerUuid, message.args[0]);

                        if(currentBlueprint.isEmpty() && schematic != null) {
                            ItemStack blueprint = new ItemStack(InitItem.BLUEPRINT, 1);
                            ItemBlueprint.setSchematicNBT(blueprint, schematic);
                            schematicTerminal.putStackInSlot(2, blueprint);
                            paper.shrink(1);
                            schematicTerminal.putStackInSlot(1, paper);
                            schematicTerminal.detectAndSendChanges();
                        }
                    }
                }
            }

            return null;
        }
    }

}

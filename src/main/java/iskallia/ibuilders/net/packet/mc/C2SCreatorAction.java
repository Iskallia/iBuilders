package iskallia.ibuilders.net.packet.mc;

import io.netty.buffer.ByteBuf;
import iskallia.ibuilders.Builders;
import iskallia.ibuilders.net.packet.PacketRequestSchemaInfo;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SCreatorAction implements IMessage {

    private C2STerminalAction.Action action;
    private String[] args;

    public C2SCreatorAction() {
    }

    public C2SCreatorAction(C2STerminalAction.Action action, String... args) {
        this.action = action;
        this.args = args;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.action = C2STerminalAction.Action.values()[buf.readInt()];

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
        GET_INFO
    }

    public static class Handler implements IMessageHandler<C2SCreatorAction, IMessage> {
        @Override
        public IMessage onMessage(C2SCreatorAction message, MessageContext ctx) {
            Builders.NETWORK.sendToAllPlotServers(new PacketRequestSchemaInfo());
            return null;
        }
    }

}

package iskallia.ibuilders.net.packet.mc;

import io.netty.buffer.ByteBuf;
import iskallia.ibuilders.client.SchematicWorlds;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.world.data.SchematicTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class S2CRenderSchematics implements IMessage {

    private Map<BlockPos, BuildersSchematic> schematicMap = new HashMap<>();

    public S2CRenderSchematics() {
    }

    public S2CRenderSchematics(World world) {
        SchematicTracker schematicTracker = SchematicTracker.get(world);
        this.schematicMap = schematicTracker.getClientSchematics(world);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        for(int i = 0; i < size; i++) {
            this.schematicMap.put(BlockPos.fromLong(buf.readLong()), BuildersSchematic.fromBytes(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.schematicMap.size());

        this.schematicMap.forEach((pos, schematic) -> {
            buf.writeLong(pos.toLong());
            schematic.toBytes(buf);
        });
    }

    public static class Handler implements IMessageHandler<S2CRenderSchematics, IMessage> {
        @Override
        public IMessage onMessage(S2CRenderSchematics message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                SchematicWorlds.get().clear();
                message.schematicMap.forEach(SchematicWorlds.get()::addSchematic);
            });

            return null;
        }
    }

}

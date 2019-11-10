package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.container.ContainerCreator;
import iskallia.ibuilders.init.InitPacket;
import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.mc.S2CSchemaInfo;
import iskallia.ibuilders.net.packet.util.C2SMessage;
import iskallia.ibuilders.net.packet.util.S2CMessage;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.world.data.DataSchematics;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.List;

public class PacketRequestSchemaInfo extends Packet implements C2SMessage, S2CMessage {

    private List<BuildersSchematic.Info> infoList;

    //Use this constructor on the live server, to request a list of all schematic names, authors and descriptions.
    public PacketRequestSchemaInfo() {
        this.infoList = null;
    }

    //Use this constructor on the plot server, to send a list of all schematic names, authors and descriptions.
    private PacketRequestSchemaInfo(List<BuildersSchematic.Info> infoList) {
        this.infoList = infoList;
    }

    //On received on the plot server.
    @Override
    public Packet onPacketReceived(ServerContext context) {
        World overworld = context.minecraftServer.worlds[DimensionType.OVERWORLD.getId()];
        DataSchematics dataSchematics = DataSchematics.get(overworld);
        return new PacketRequestSchemaInfo(dataSchematics.getAllInfo());
    }

    //On received on the live server.
    @Override
    public Packet onPacketReceived(ClientContext context) {
        context.minecraftServer.addScheduledTask(() -> {
            context.minecraftServer.getPlayerList().getPlayers().forEach(entityPlayerMP -> {
                if(entityPlayerMP.openContainer instanceof ContainerCreator) {
                    InitPacket.PIPELINE.sendTo(new S2CSchemaInfo(S2CSchemaInfo.Action.ADD, this.infoList), entityPlayerMP);
                }
            });
        });

        return null;
    }
}

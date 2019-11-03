package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.util.C2SMessage;
import iskallia.ibuilders.net.packet.util.S2CMessage;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.world.data.DataSchematics;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class PacketRequestSchematic extends Packet implements C2SMessage, S2CMessage {

    private String playerUuid;
    private String name;
    private BuildersSchematic schematic;

    public PacketRequestSchematic(String playerUuid, String name) {
        this.playerUuid = playerUuid;
        this.name = name;
        this.schematic = null;
    }

    private PacketRequestSchematic(String playerUuid, String name, BuildersSchematic schematic) {
        this.playerUuid = playerUuid;
        this.name = name;
        this.schematic = schematic;
    }

    //On received on the plot server.
    @Override
    public Packet onPacketReceived(ServerContext context) {
        World overworld = context.minecraftServer.worlds[DimensionType.OVERWORLD.getId()];
        DataSchematics dataSchematics = DataSchematics.get(overworld);
        BuildersSchematic schematic = dataSchematics.getSchematic(this.playerUuid, this.name);
        return new PacketRequestSchematic(this.playerUuid, this.name, schematic);
    }

    //On received on the live server.
    @Override
    public Packet onPacketReceived(ClientContext context) {
        //Sync schematic to container?
        return null;
    }
}

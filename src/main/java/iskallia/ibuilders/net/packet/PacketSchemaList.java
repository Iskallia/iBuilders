package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.util.C2SMessage;
import iskallia.ibuilders.net.packet.util.S2CMessage;
import iskallia.ibuilders.schematic.BuildersSchematic;

import java.util.List;

public class PacketSchemaList extends Packet implements C2SMessage, S2CMessage {

    private List<BuildersSchematic.Info> schemaList;

    //Use this constructor on the live server, to request a list of all schematic names, authors and descriptions.
    public PacketSchemaList() {
        this.schemaList = null;
    }

    //Use this constructor on the plot server, to send a list of all schematic names, authors and descriptions.
    public PacketSchemaList(List<BuildersSchematic.Info> schemaList) {
        this.schemaList = schemaList;
    }

    //On received on the plot server.
    @Override
    public Packet onPacketReceived(ServerContext context) {
        return new PacketSchemaList();
    }

    //On received on the live server.
    @Override
    public Packet onPacketReceived(ClientContext context) {
        //Sync schemaList to GUI?
        return null;
    }
}

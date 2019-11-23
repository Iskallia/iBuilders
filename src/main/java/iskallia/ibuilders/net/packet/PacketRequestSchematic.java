package iskallia.ibuilders.net.packet;

import iskallia.ibuilders.container.ContainerCreator;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.item.ItemBlueprint;
import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.net.packet.util.C2SMessage;
import iskallia.ibuilders.net.packet.util.S2CMessage;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.world.data.DataSchematics;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.UUID;

public class PacketRequestSchematic extends Packet implements C2SMessage, S2CMessage {

    private String client;
    private String playerUuid;
    private String name;
    private BuildersSchematic schematic;

    private PacketRequestSchematic() {

    }

    public PacketRequestSchematic(String client, String playerUuid, String name) {
        this.client = client;
        this.playerUuid = playerUuid;
        this.name = name;
        this.schematic = null;
    }

    private PacketRequestSchematic(String client, String playerUuid, String name, BuildersSchematic schematic) {
        this.client = client;
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
        return new PacketRequestSchematic(this.client, this.playerUuid, this.name, schematic);
    }

    //On received on the live server.
    @Override
    public Packet onPacketReceived(ClientContext context) {
        if(this.schematic != null) {
            context.minecraftServer.addScheduledTask(() -> {
                EntityPlayerMP player = context.minecraftServer.getPlayerList().getPlayerByUUID(UUID.fromString(this.client));
                if(player == null)return;

                Container container = player.openContainer;

                if(container instanceof ContainerCreator) {
                    ContainerCreator creator = (ContainerCreator)container;

                    ItemStack paper = creator.inventorySlots.get(1).getStack();
                    paper.shrink(1);
                    creator.putStackInSlot(1, paper);

                    ItemStack blueprint = new ItemStack(InitItem.BLUEPRINT, 1);
                    ItemBlueprint.setSchematicNBT(blueprint, this.schematic);
                    creator.putStackInSlot(2, blueprint);

                    creator.detectAndSendChanges();
                }
            });
        }

        return null;
    }
}

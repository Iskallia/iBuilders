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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SUploadSchematic implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<C2SUploadSchematic, S2CUserUploads> {
        @Override
        public S2CUserUploads onMessage(C2SUploadSchematic message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Container container = player.openContainer;

            if(container instanceof ContainerSchematicTerminal) {
                ContainerSchematicTerminal schematicTerminal = (ContainerSchematicTerminal)container;
                ItemStack blueprint = schematicTerminal.inventorySlots.get(0).getStack();

                NBTTagCompound schematicNBT = blueprint.getSubCompound("Schematic");

                DataSchematics dataSchematics = DataSchematics.get(player.world);
                String playerUuid = player.getUniqueID().toString();

                if(schematicNBT != null) {
                    BuildersSchematic schematic = BuildersFormat.INSTANCE.readFromNBT(schematicNBT);

                    if(!dataSchematics.hasSchematic(playerUuid, schematic.getInfo().getName())) {
                        dataSchematics.addSchematic(playerUuid, schematic);
                        blueprint.shrink(1);
                    }
                }

                return new S2CUserUploads(dataSchematics.getInfoFor(playerUuid));
            }

            return null;
        }
    }

}

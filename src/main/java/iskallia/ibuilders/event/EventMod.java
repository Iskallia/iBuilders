package iskallia.ibuilders.event;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.command.CommandDebugBuilders;
import iskallia.ibuilders.gui.GuiHandler;
import iskallia.ibuilders.init.*;
import iskallia.ibuilders.net.context.ClientContext;
import iskallia.ibuilders.net.context.ServerContext;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class EventMod {

    public static void onConstruction(FMLConstructionEvent event) {
        //Stops a crash bug, WTF?
        if(event.getSide() == Side.CLIENT) {
           new BuildersFormat().writeToNBT(new NBTTagCompound(), new BuildersSchematic(0, 0, 0));
        }
    }

    public static void onPreInitialization(FMLPreInitializationEvent event) {
        if(event.getSide() == Side.CLIENT) {
            InitEntity.registerEntityRenderers();
            InitModel.registerTileEntityRenderers();
        }

        InitEntity.registerEntities();
    }

    public static void onInitialization(FMLInitializationEvent event) {
        if(event.getSide() == Side.SERVER) {
            InitPacket.registerPackets();
        }

        InitPacket.registerMCPackets();
        NetworkRegistry.INSTANCE.registerGuiHandler(Builders.getInstance(), new GuiHandler());
    }

    public static void onPostInitialization(FMLPostInitializationEvent event) {
        InitConfig.registerConfigs();
        InitSchematic.registerSchematics();
    }

    public static void onServerStart(FMLServerStartingEvent event) {
        if(event.getSide().isServer()) {
            event.getServer().setGameType(GameType.CREATIVE);
            event.getServer().setForceGamemode(true);
            Builders.NETWORK.start();

            if(Builders.NETWORK.hasServerListener()) {
                Builders.NETWORK.getServerListener().onContextCreated(context -> {
                    if(context instanceof ServerContext) {
                        ((ServerContext)context).minecraftServer = event.getServer();
                    } else if(context instanceof ClientContext) {
                        ((ClientContext)context).minecraftServer = event.getServer();
                    }
                });
            }
        }

        event.registerServerCommand(new CommandDebugBuilders());
    }

}

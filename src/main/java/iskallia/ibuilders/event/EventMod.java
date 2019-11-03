package iskallia.ibuilders.event;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.command.CommandDebugBuilders;
import iskallia.ibuilders.init.InitConfig;
import iskallia.ibuilders.init.InitModel;
import iskallia.ibuilders.init.InitSchematic;
import iskallia.ibuilders.net.context.ServerContext;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;

public class EventMod {

    public static void onConstruction(FMLConstructionEvent event) { }

    public static void onPreInitialization(FMLPreInitializationEvent event) {
        if(event.getSide() == Side.CLIENT) {
            InitModel.registerTileEntityRenderers();
        }
    }

    public static void onInitialization(FMLInitializationEvent event) { }

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
                    }
                });
            }
        }

        event.registerServerCommand(new CommandDebugBuilders());
    }

}

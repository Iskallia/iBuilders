package iskallia.ibuilders.event;

import iskallia.ibuilders.init.InitConfig;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.event.*;

public class EventMod {

    public static void onConstruction(FMLConstructionEvent event) { }

    public static void onPreInitialization(FMLPreInitializationEvent event) { }

    public static void onInitialization(FMLInitializationEvent event) { }

    public static void onPostInitialization(FMLPostInitializationEvent event) {
        InitConfig.registerConfigs();
    }

    public static void onServerStart(FMLServerStartingEvent event) {
        event.getServer().setGameType(GameType.CREATIVE);
        event.getServer().setForceGamemode(true);
    }

}

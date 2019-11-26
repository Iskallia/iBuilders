package iskallia.ibuilders;

import iskallia.ibuilders.event.EventMod;
import iskallia.ibuilders.net.NetworkHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Builders.MOD_ID, name = Builders.MOD_NAME, version = Builders.MOD_VERSION,
        dependencies = "required-before:schematica")
public class Builders {

    public static final Logger LOG = LogManager.getLogger(Builders.MOD_NAME);

    @Mod.Instance
    private static Builders INSTANCE;
    public static NetworkHandler NETWORK = new NetworkHandler();

    public static final String MOD_ID = "ibuilders";
    public static final String MOD_NAME = "iBuilders";
    public static final String MOD_VERSION = "0.0.2";

    public static Builders getInstance() {
        return INSTANCE;
    }

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        EventMod.onConstruction(event);
    }

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        EventMod.onPreInitialization(event);
    }

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        EventMod.onInitialization(event);
    }

    @Mod.EventHandler
    public void onPostInitialization(FMLPostInitializationEvent event) {
        EventMod.onPostInitialization(event);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        EventMod.onServerStart(event);
    }

    public static ResourceLocation getResource(String name) {
        return new ResourceLocation(Builders.MOD_ID, name);
    }

}

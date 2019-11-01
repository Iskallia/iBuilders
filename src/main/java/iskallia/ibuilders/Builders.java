package iskallia.ibuilders;

import iskallia.ibuilders.event.EventMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Builders.MOD_ID, name = Builders.MOD_NAME, version = Builders.MOD_VERSION)
public class Builders {

    public static final Logger LOG = LogManager.getLogger(Builders.MOD_NAME);

    @Mod.Instance
    private static Builders INSTANCE;
    public boolean isOnDedicatedServer = false;

    public static final String MOD_ID = "ibuilders";
    public static final String MOD_NAME = "iBuilders";
    public static final String MOD_VERSION = "${version}";

    public static Builders getInstance() {
        return INSTANCE;
    }

    public static boolean isOnDedicatedServer() {
        return getInstance().isOnDedicatedServer;
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
        this.isOnDedicatedServer = true;
        EventMod.onServerStart(event);
    }

    public static ResourceLocation getResource(String name) {
        return new ResourceLocation(Builders.MOD_ID, name);
    }

}

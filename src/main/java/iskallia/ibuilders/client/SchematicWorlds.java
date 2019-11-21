package iskallia.ibuilders.client;

import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import iskallia.ibuilders.Builders;
import iskallia.ibuilders.schematic.BuildersSchematic;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Builders.MOD_ID)
public class SchematicWorlds {

    private static final SchematicWorlds INSTANCE = new SchematicWorlds();

    private World parentWorld;
    private SchematicWorld schematicWorld;
    public RenderSchematic renderSchematic = new RenderSchematic(Minecraft.getMinecraft());

    public static SchematicWorlds get() {
        INSTANCE.updateWorld();
        return INSTANCE;
    }

    public void addSchematic(BlockPos pos, BuildersSchematic schematic) {
        ((MegaSchematic)this.schematicWorld.getSchematic()).addSchematic(schematic, pos);
    }

    public void clear() {
        ((MegaSchematic)this.schematicWorld.getSchematic()).clear();
    }

    public void updateWorld() {
        World world = Minecraft.getMinecraft().world;

        if(world != this.parentWorld) {
            this.parentWorld = world;
            this.schematicWorld = new SchematicWorld(new MegaSchematic());
            this.schematicWorld.isRendering = true;
            this.schematicWorld.position.set(BlockPos.ORIGIN.add(-(Integer.MAX_VALUE >> 1), 0, -(Integer.MAX_VALUE >> 1)));
            this.renderSchematic.setWorldAndLoadRenderers(this.schematicWorld);
            this.parentWorld.addEventListener(this.renderSchematic);
            ClientProxy.schematic = this.schematicWorld;

        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        get().renderSchematic.onRenderWorldLast(event);
        ((MegaSchematic)get().schematicWorld.getSchematic()).renderOutlines();
    }

}

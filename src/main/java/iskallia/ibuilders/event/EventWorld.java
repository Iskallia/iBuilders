package iskallia.ibuilders.event;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.init.InitPacket;
import iskallia.ibuilders.net.packet.mc.S2CRenderSchematics;
import iskallia.ibuilders.world.data.SchematicTracker;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Builders.MOD_ID)
public class EventWorld {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if(event.phase == TickEvent.Phase.END && !event.world.isRemote) {
            if(SchematicTracker.get(event.world).getAndSetChanged(false) || event.world.getWorldTime() % 40 == 0) {
                event.world.playerEntities.forEach(player -> {
                    InitPacket.PIPELINE.sendTo(new S2CRenderSchematics(event.world), (EntityPlayerMP)player);
                });
            }
        }
    }

}


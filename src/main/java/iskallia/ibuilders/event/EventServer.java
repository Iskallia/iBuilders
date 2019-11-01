package iskallia.ibuilders.event;

import iskallia.ibuilders.Builders;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Builders.MOD_ID)
public class EventServer {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.START) {
            Builders.NETWORK.tick();
        }
    }

}

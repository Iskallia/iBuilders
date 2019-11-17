package iskallia.ibuilders.event;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.init.InitPacket;
import iskallia.ibuilders.net.packet.mc.S2CRenderSchematics;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = Builders.MOD_ID)
public class EventPlayer {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        InitPacket.PIPELINE.sendTo(new S2CRenderSchematics(event.player.world), (EntityPlayerMP)event.player);
    }

}

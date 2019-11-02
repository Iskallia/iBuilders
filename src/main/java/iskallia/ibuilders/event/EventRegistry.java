package iskallia.ibuilders.event;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.init.InitBlock;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.init.InitModel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Builders.MOD_ID)
public class EventRegistry {

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        InitBlock.registerBlocks(event.getRegistry());
        InitBlock.registerTileEntities();
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        InitItem.registerItems(event.getRegistry());
        InitBlock.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        InitModel.registerItemModels();
        InitModel.registerBlockModels();
    }

}

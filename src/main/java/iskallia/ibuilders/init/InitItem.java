package iskallia.ibuilders.init;

import iskallia.ibuilders.item.ItemBlueprint;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class InitItem {

    public static ItemBlueprint BLUEPRINT = new ItemBlueprint("build_blueprint");

    public static void registerItems(IForgeRegistry<Item> registry) {
        registerItem(BLUEPRINT, registry);
    }

    /* --------------------------------- */

    private static void registerItem(Item item, IForgeRegistry<Item> registry) {
        registry.register(item);
    }

}

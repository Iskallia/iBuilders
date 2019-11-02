package iskallia.ibuilders.init;

import iskallia.ibuilders.item.ItemSchema;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class InitItem {

    public static ItemSchema SCHEMA = new ItemSchema("item_schema");

    public static void registerItems(IForgeRegistry<Item> registry) {
        registerItem(SCHEMA, registry);
    }

    /* --------------------------------- */

    private static void registerItem(Item item, IForgeRegistry<Item> registry) {
        registry.register(item);
    }

}

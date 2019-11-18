package iskallia.ibuilders.init;

import iskallia.ibuilders.item.ItemBlueprint;
import iskallia.ibuilders.item.ItemSchematicRelocator;
import iskallia.ibuilders.item.ItemSpawnEggBuilder;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class InitItem {

    public static ItemBlueprint BLUEPRINT = new ItemBlueprint("build_blueprint");
    public static ItemSchematicRelocator SCHEMATIC_RELOCATOR = new ItemSchematicRelocator("schematic_relocator");
    public static ItemSpawnEggBuilder SPAWN_EGG_BUILDER = new ItemSpawnEggBuilder("spawn_egg_builder");

    public static void registerItems(IForgeRegistry<Item> registry) {
        registerItem(BLUEPRINT, registry);
        registerItem(SCHEMATIC_RELOCATOR, registry);
        registerItem(SPAWN_EGG_BUILDER, registry);
    }

    /* --------------------------------- */

    private static void registerItem(Item item, IForgeRegistry<Item> registry) {
        registry.register(item);
    }

}

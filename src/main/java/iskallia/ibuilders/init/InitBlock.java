package iskallia.ibuilders.init;

import iskallia.ibuilders.block.BlockMarker;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistry;

public class InitBlock {

    public static BlockMarker MARKER = new BlockMarker("schema_marker");
    public static ItemBlock ITEM_MARKER = getItemBlock(MARKER);

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registerBlock(MARKER, registry);
    }

    public static void registerTileEntities() { }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(ITEM_MARKER);
    }

    /* ------------------------------------- */

    private static void registerBlock(Block block, IForgeRegistry<Block> registry) {
        registry.register(block);
    }

    private static ItemBlock getItemBlock(Block block) {
        ItemBlock itemBlock = new ItemBlock(block);
        String resourceName = block.getRegistryName().getResourcePath();
        itemBlock.setUnlocalizedName(resourceName);
        itemBlock.setRegistryName(resourceName);
        return itemBlock;
    }

}

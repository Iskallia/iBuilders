package iskallia.ibuilders.init;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.BlockMarker;
import iskallia.ibuilders.block.entity.TileEntityMarker;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class InitBlock {

    public static BlockMarker MARKER = new BlockMarker("schema_marker");
    public static ItemBlock ITEM_MARKER = getItemBlock(MARKER);

    public static BlockMarker SCHEMATIC_TERMINAL = new BlockMarker("schematic_terminal");
    public static ItemBlock ITEM_SCHEMATIC_TERMINAL = getItemBlock(SCHEMATIC_TERMINAL);

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registerBlock(MARKER, registry);
        registerBlock(SCHEMATIC_TERMINAL, registry);
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityMarker.class, Builders.getResource("schema_marker"));
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(ITEM_MARKER);
        registry.register(ITEM_SCHEMATIC_TERMINAL);
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

package iskallia.ibuilders.init;

import iskallia.ibuilders.Builders;
import iskallia.itraders.Traders;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class InitModel {

    public static void registerItemModels() {
        registerSimpleItemModel(InitBlock.ITEM_MARKER, 0);
    }

    public static void registerBlockModels() {
        registerBlockModel(InitBlock.MARKER, 0);
    }

    public static void registerTileEntityRenderers() {}

    /* ---------------------------------- */

    private static void registerSimpleItemModel(Item item, int metadata) {
        ModelLoader.setCustomModelResourceLocation(item, metadata,
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void registerBlockModel(Block block, int metadata) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), metadata,
                new ModelResourceLocation(Builders.getResource(block.getUnlocalizedName().substring(5)), "inventory"));
    }

}

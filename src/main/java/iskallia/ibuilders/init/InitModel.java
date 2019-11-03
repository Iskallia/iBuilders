package iskallia.ibuilders.init;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.entity.TileEntityMarker;
import iskallia.ibuilders.block.render.TESRMarker;
import iskallia.itraders.Traders;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class InitModel {

    public static void registerItemModels() {
        registerSimpleItemModel(InitItem.SCHEMA, 0);

        registerSimpleItemModel(InitBlock.ITEM_MARKER, 0);
    }

    public static void registerBlockModels() {
        registerBlockModel(InitBlock.MARKER, 0);
    }

    public static void registerTileEntityRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMarker.class, new TESRMarker());
    }

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

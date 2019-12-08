package iskallia.ibuilders.item;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.item.Item;

public class ItemBuilderUpgrade extends Item {

    public ItemBuilderUpgrade(String name) {
        this.setUnlocalizedName(name);
        this.setRegistryName(Builders.getResource(name));
        this.setCreativeTab(CreativeTabsIBuilders.INSTANCE);
    }

}

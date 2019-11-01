package iskallia.ibuilders.tab;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.init.InitBlock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CreativeTabsIBuilders extends CreativeTabs {

    public static final CreativeTabs INSTANCE = new CreativeTabsIBuilders(Builders.MOD_ID);

    public CreativeTabsIBuilders(String label) {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(InitBlock.MARKER);
    }

    @Override
    public boolean hasSearchBar() {
        return true;
    }

}

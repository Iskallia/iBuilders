package iskallia.ibuilders.item;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.entity.EntityBuilder;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSpawnEggBuilder extends ItemSpawnEgg<EntityBuilder> {

    public ItemSpawnEggBuilder(String name) {
        super(name, Builders.getResource("builder"), false);
    }

    @Override
    protected EntityBuilder onPlayerSpawn(World world, EntityPlayer entityPlayer, BlockPos pos, ItemStack stack) {
        EntityBuilder builder = this.spawnCreature(world, this.getNamedIdFrom(stack), pos.getX() + 0.5D, pos.getY(), pos.getZ());

        if(stack.hasDisplayName()) {
            builder.setCustomNameTag(stack.getDisplayName());
        }

        ItemMonsterPlacer.applyItemEntityDataToEntity(world, null, stack, builder);
        return builder;
    }

    @Override
    protected EntityBuilder onDispenserSpawn(World world, EnumFacing enumFacing, BlockPos blockPos, ItemStack itemStack) {
        return null;
    }

}

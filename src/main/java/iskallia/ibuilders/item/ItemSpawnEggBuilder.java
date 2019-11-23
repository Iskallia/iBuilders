package iskallia.ibuilders.item;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.entity.EntityBuilder;
import iskallia.ibuilders.util.MaterialList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSpawnEggBuilder extends ItemSpawnEgg<EntityBuilder> {

    public ItemSpawnEggBuilder(String name) {
        super(name, Builders.getResource("builder"), false);
    }

    @Override
    protected EntityBuilder onPlayerSpawn(World world, EntityPlayer entityPlayer, BlockPos pos, ItemStack stack) {
        return null;
    }

    @Override
    protected EntityBuilder onDispenserSpawn(World world, EnumFacing enumFacing, BlockPos blockPos, ItemStack itemStack) {
        return null;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        //TODO: Repurposed this for testing the material list.
        if(hand == EnumHand.MAIN_HAND) {
            Builders.LOG.warn(MaterialList.getItem(world, world.getBlockState(pos), pos));
        }

        return EnumActionResult.FAIL;
    }

}

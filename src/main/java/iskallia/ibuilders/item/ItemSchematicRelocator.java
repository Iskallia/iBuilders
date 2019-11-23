package iskallia.ibuilders.item;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ItemSchematicRelocator extends Item {

    public ItemSchematicRelocator(String name) {
        this.setUnlocalizedName(name);
        this.setRegistryName(Builders.getResource(name));
        this.setCreativeTab(CreativeTabsIBuilders.INSTANCE);
    }


    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldStack = player.getHeldItem(hand);
        TileEntity tileEntity = world.getTileEntity(pos);

        if(!player.isSneaking()) {
            if(tileEntity instanceof TileEntityCreator) {
                heldStack.setTagInfo("Creator", new NBTTagLong(pos.toLong()));
                player.sendStatusMessage(new TextComponentTranslation("use.schematic_relocator.bound.success"), true);
            } else {
                player.sendStatusMessage(new TextComponentTranslation("use.schematic_relocator.bound.fail"), true);
            }
        } else {
            if(!heldStack.hasTagCompound() || !heldStack.getTagCompound().hasKey("Creator", Constants.NBT.TAG_LONG)) {
                player.sendStatusMessage(new TextComponentTranslation("use.schematic_relocator.move.fail"), true);
            } else {
                BlockPos creatorPos = BlockPos.fromLong(heldStack.getTagCompound().getLong("Creator"));
                TileEntity boundTe = world.getTileEntity(creatorPos);

                if(boundTe instanceof TileEntityCreator) {
                    TileEntityCreator creator = (TileEntityCreator)boundTe;
                    double distance = pos.getDistance(creatorPos.getX(), creatorPos.getY(), creatorPos.getZ());

                    if(distance >= 16.0d) {
                        player.sendStatusMessage(new TextComponentTranslation("use.schematic_relocator.move.far"), true);
                    } else {
                        player.sendStatusMessage(new TextComponentTranslation("use.schematic_relocator.move.successful"), true);
                        creator.setTransform(pos.subtract(creatorPos), player.getHorizontalFacing());
                        return EnumActionResult.SUCCESS;
                    }
                } else {
                    heldStack.setTagCompound(null);
                    player.sendStatusMessage(new TextComponentTranslation("use.schematic_relocator.move.lost"), true);
                }
            }
        }

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
}

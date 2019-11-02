package iskallia.ibuilders.item;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.BlockMarker;
import iskallia.ibuilders.init.InitBlock;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSchema extends Item {

    public static ItemStack setSchematicNBT(ItemStack schemaStack, BuildersSchematic schematic) {
        NBTTagCompound stackNBT = schemaStack.getTagCompound();
        NBTTagCompound schematicNBT = new NBTTagCompound();

        BuildersFormat.INSTANCE.writeToNBT(schematicNBT, schematic);

        if (stackNBT == null) {
            stackNBT = new NBTTagCompound();
            schemaStack.setTagCompound(stackNBT);
        }

        stackNBT.setTag("Schematic", schematicNBT);

        return schemaStack;
    }

    public ItemSchema(String name) {
        this.setUnlocalizedName(name);
        this.setRegistryName(Builders.getResource(name));
        this.setCreativeTab(CreativeTabsIBuilders.INSTANCE);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

        ItemStack heldStack = player.getHeldItem(hand);

        Block clickedBlock = world.getBlockState(pos).getBlock();

        if (clickedBlock != InitBlock.MARKER) {
            player.sendStatusMessage(new TextComponentTranslation("use.item_schema.fail.not_marker"), true);
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }

        int closestX = BlockMarker.closestMarkerX(world, pos);
        int closestY = BlockMarker.closestMarkerY(world, pos);
        int closestZ = BlockMarker.closestMarkerZ(world, pos);

        if (closestX == pos.getX()) {
            player.sendStatusMessage(new TextComponentTranslation("use.item_schema.fail.invalid_pos", "X"), true);
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }

        if (closestY == pos.getY()) {
            player.sendStatusMessage(new TextComponentTranslation("use.item_schema.fail.invalid_pos", "Y"), true);
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }

        if (closestZ == pos.getZ()) {
            player.sendStatusMessage(new TextComponentTranslation("use.item_schema.fail.invalid_pos", "Z"), true);
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }

        BuildersSchematic schematic = BlockMarker.getSchematic(world, pos, closestX, closestY, closestZ);
        schematic.setAuthor(player.getName());
        schematic.getInfo().setName("TEST BUILD SCHEME"); // TODO
        schematic.getInfo().setDescription("TEST DESCRIPTION"); // TODO

        ItemSchema.setSchematicNBT(heldStack, schematic);
        player.sendStatusMessage(new TextComponentTranslation("use.item_schema.success"), true);

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound stackNBT = stack.getTagCompound();

        if (stackNBT != null && stackNBT.hasKey("Schematic", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound schematicNBT = stackNBT.getCompoundTag("Schematic");
            NBTTagCompound infoNBT = schematicNBT.getCompoundTag("Info");

            int width = schematicNBT.getShort("Width");
            int height = schematicNBT.getShort("Height");
            int length = schematicNBT.getShort("Length");

            String name = infoNBT.getString("Name");
            String description = infoNBT.getString("Description");
            String author = schematicNBT.getString("Author");

            // TODO: i18n-izify + colorize those values
            tooltip.add("Name: " + name);
            tooltip.add("By: " + author);
            tooltip.add("Dimensions: " + width + "x" + height + "x" + length);
            tooltip.add("");
            tooltip.add("Description: " + description);

        } else {
            tooltip.add(new TextComponentTranslation("tooltip.item_schema.empty").getFormattedText());
        }

        super.addInformation(stack, world, tooltip, flagIn);
    }

}

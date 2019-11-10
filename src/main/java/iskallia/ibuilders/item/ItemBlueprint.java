package iskallia.ibuilders.item;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.block.BlockMarker;
import iskallia.ibuilders.block.entity.TileEntityMarker;
import iskallia.ibuilders.init.InitBlock;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBlueprint extends Item {

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

    public ItemBlueprint(String name) {
        this.setUnlocalizedName(name);
        this.setRegistryName(Builders.getResource(name));
        this.setCreativeTab(CreativeTabsIBuilders.INSTANCE);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

        ItemStack heldStack = player.getHeldItem(hand);
        Block clickedBlock = world.getBlockState(pos).getBlock();
        TileEntityMarker markerTileEntity = TileEntityMarker.getMarkerTileEntity(world, pos);

        if (clickedBlock != InitBlock.MARKER) {
            player.sendStatusMessage(new TextComponentTranslation("use.build_blueprint.fail.not_marker"), true);
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }

        if (markerTileEntity == null || markerTileEntity.isUnset()) {
            player.sendStatusMessage(new TextComponentTranslation("use.build_blueprint.fail.not_connected"), true);
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }

        TileEntityMarker masterTileEntity = markerTileEntity.getMaster();
        int closestX = masterTileEntity.getExtensionX().getX();
        int closestY = masterTileEntity.getExtensionY().getY();
        int closestZ = masterTileEntity.getExtensionZ().getZ();

        BuildersSchematic schematic = BlockMarker.getSchematic(world,
                masterTileEntity.getPos(),
                closestX, closestY, closestZ);
        schematic.setAuthor(player.getName());

        if(!heldStack.hasDisplayName()) {
            schematic.getInfo().setName("TEST BUILD SCHEME"); // TODO
        } else {
            schematic.getInfo().setName(heldStack.getDisplayName());
        }

        schematic.getInfo().setDescription("TEST DESCRIPTION"); // TODO

        ItemBlueprint.setSchematicNBT(heldStack, schematic);
        player.sendStatusMessage(new TextComponentTranslation("use.build_blueprint.success"), true);

        return EnumActionResult.SUCCESS;
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
            String author = infoNBT.getString("Author");

            // TODO: i18n-izify + colorize those values
            tooltip.add("Name: " + name);
            tooltip.add("Hash: " + Long.toHexString(schematicNBT.getLong("Hash")).toUpperCase());
            tooltip.add("By: " + author);
            tooltip.add("Dimensions: " + width + "x" + height + "x" + length);
            tooltip.add("");

            tooltip.add("Contents:");
            byte[] blocks = schematicNBT.getByteArray("Blocks");
            NBTTagCompound schematicaMapping = schematicNBT.getCompoundTag("SchematicaMapping");
            int i = 0;
            for (String mapping : schematicaMapping.getKeySet()) {
                if (i >= 3 && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    tooltip.add("... Press shift for more content");
                    break;
                }

                short id = schematicaMapping.getShort(mapping);

                if (id == 0) continue;

                tooltip.add(countOf(blocks, id) + " x " + mapping);
                i++;
            }

            tooltip.add("");
            tooltip.add("Description: " + description);

        } else {
            tooltip.add(new TextComponentTranslation("tooltip.build_blueprint.empty").getFormattedText());
        }

        super.addInformation(stack, world, tooltip, flagIn);
    }

    // TODO: Extract to other place, will be used by the Creator block as well!
    private int countOf(byte[] list, short id) {
        int count = 0;
        for (byte item : list) {
            if (new Byte(item).shortValue() == new Short(id).byteValue()) // ? :c
                count++;
        }
        return count;
    }

}

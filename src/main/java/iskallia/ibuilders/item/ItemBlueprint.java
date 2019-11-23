package iskallia.ibuilders.item;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.init.InitItem;
import iskallia.ibuilders.schematic.BuildersFormat;
import iskallia.ibuilders.schematic.BuildersSchematic;
import iskallia.ibuilders.tab.CreativeTabsIBuilders;
import iskallia.ibuilders.util.Pair;
import iskallia.ibuilders.world.data.SchematicTracker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.input.Keyboard;

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
        schemaStack.setStackDisplayName(schematic.getInfo().getName());
        return schemaStack;
    }

    public ItemBlueprint(String name) {
        this.setUnlocalizedName(name);
        this.setRegistryName(Builders.getResource(name));
        this.setCreativeTab(CreativeTabsIBuilders.INSTANCE);
    }

    /*
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

        schematic.getInfo().setUuid(player.getUniqueID().toString());
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
    }*/

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldStack = player.getHeldItem(hand);
        boolean changed = false;

        if(hand == EnumHand.MAIN_HAND && heldStack.getItem() == InitItem.BLUEPRINT) {
            NBTTagCompound stackNbt = heldStack.getTagCompound();

            if(stackNbt == null || !stackNbt.hasKey("FirstCorner", Constants.NBT.TAG_LONG)) {
                heldStack.setTagInfo("FirstCorner", new NBTTagLong(pos.toLong()));
                changed = true;
            } else {
                BlockPos firstCorner = BlockPos.fromLong(stackNbt.getLong("FirstCorner"));

                if(firstCorner.equals(pos)) {
                    stackNbt.removeTag("FirstCorner");
                    stackNbt.removeTag("SecondCorner");
                    heldStack.setTagCompound(stackNbt);
                    changed = true;
                } else if(Math.abs(firstCorner.getX() - pos.getX()) > 64
                    || Math.abs(firstCorner.getY() - pos.getY()) > 256
                    || Math.abs(firstCorner.getZ() - pos.getZ()) > 64) {
                    player.sendStatusMessage(new TextComponentTranslation("use.blueprint.corner.far"), true);
                } else {
                    heldStack.setTagInfo("SecondCorner", new NBTTagLong(pos.toLong()));
                    Pair<BlockPos, BlockPos> posData = getCenterAndDimensions(firstCorner, pos);

                    BuildersSchematic schematic = getSchematic(world, posData.getKey(), posData.getValue());

                    schematic.getInfo().setUuid(player.getUniqueID().toString());
                    schematic.setAuthor(player.getName());
                    schematic.getInfo().setName(heldStack.hasDisplayName() ? heldStack.getDisplayName() : "My Build");
                    schematic.getInfo().setDescription("This is my cool build!");

                    setSchematicNBT(heldStack, schematic);
                    changed = true;
                }
            }
        }

        if(changed)SchematicTracker.get(world).getAndSetChanged(true);
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    public static Pair<BlockPos, BlockPos> getCenterAndDimensions(BlockPos firstCorner, BlockPos secondCorner) {
        int x1 = firstCorner.getX() + (secondCorner.getX() > firstCorner.getX() ? 0 : 1);
        int y1 = firstCorner.getY() + (secondCorner.getY() > firstCorner.getY() ? 0 : 1);
        int z1 = firstCorner.getZ() + (secondCorner.getZ() > firstCorner.getZ() ? 0 : 1);
        int x2 = secondCorner.getX() + (secondCorner.getX() > firstCorner.getX() ? 1 : 0);
        int y2 = secondCorner.getY() + (secondCorner.getY() > firstCorner.getY() ? 1 : 0);
        int z2 = secondCorner.getZ() + (secondCorner.getZ() > firstCorner.getZ() ? 1 : 0);

        BlockPos center = new BlockPos(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        BlockPos dimensions = new BlockPos(Math.abs(x1 - x2), Math.abs(y1 - y2), Math.abs(z1 - z2));

        if(!firstCorner.equals(secondCorner)) {
            center = center.add(1, 1, 1);
            dimensions = dimensions.add(-2, -2, -2);
        }

        return new Pair<>(center, dimensions);
    }

    public static BuildersSchematic getSchematic(World world, BlockPos pos, BlockPos dimensions) {
        BuildersSchematic schematic = new BuildersSchematic(dimensions.getX(), dimensions.getY(), dimensions.getZ());

        for(int x = 0; x < dimensions.getX(); x++) {
            for(int y = 0; y <= dimensions.getY(); y++) {
                for(int z = 0; z < dimensions.getZ(); z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    IBlockState blockState = world.getBlockState(blockPos.add(pos));
                    schematic.setBlockState(blockPos, blockState);
                }
            }
        }

        return schematic;
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

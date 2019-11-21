package iskallia.ibuilders.entity;

import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.ibuilders.util.MaterialList;
import iskallia.itraders.util.profile.SkinProfile;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityBuilder extends EntityCreature {

    public SkinProfile skin;
    private String lastName = "Builder";
    protected BuilderPathFinder pathFinder;

    private TileEntityCreator creator;
    private int placeBlockDelay;

    public EntityBuilder(World world) {
        super(world);

        if(this.world.isRemote) {
            this.skin = new SkinProfile();
        } else {
            this.pathFinder = new BuilderPathFinder(this);
        }

        this.setCustomNameTag(this.lastName);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound) {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(this.isDead)return;

        if(this.world.isRemote) {
            String name = this.getCustomNameTag();

            if(!this.lastName.equals(name)) {
                this.skin.updateSkin(name);
                this.lastName = name;
            }
        } else {
            this.checkHasCreator();
            if(this.isDead)return;
            this.pathFinder.update();
            this.placeBlocks();
        }

        this.updateArmSwingProgress();
    }

    private void placeBlocks() {
        if(this.getBuildTarget() != null) {
            double d0 = this.getBuildTarget().getX() - this.posX;
            double d1 = this.getBuildTarget().getY() - (this.posY + (double)this.getEyeHeight());
            double d2 = this.getBuildTarget().getZ() - this.posZ;
            double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
            this.rotationYaw = f;
            this.rotationPitch = f1;
        }

        this.placeBlockDelay--;

        if(this.placeBlockDelay > 0)return;
        BlockPos pos = this.getBuildTarget();
        if(pos == null)return;

        if(this.getBuildState() != null) {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, MaterialList.getItem(this.world, this.getBuildState(), this.getBuildTarget()));
        }

        if(pos.distanceSq(this.getPosition()) > 3 * 3)return;

        IBlockState state = this.world.getBlockState(pos);

        if(this.isAirOrLiquid(state) && this.getBuildState() != null) {
            this.world.setBlockState(pos, this.getBuildState());
            SoundType type = this.getBuildState().getBlock().getSoundType(state, this.world, pos, this);
            this.world.playSound(null, pos, type.getPlaceSound(), SoundCategory.BLOCKS, type.getVolume(), type.getPitch());
            this.swingArm(EnumHand.MAIN_HAND);
            this.pathFinder.reset();
            this.placeBlockDelay = 5;
        }
    }

    @Override
    protected boolean isMovementBlocked() {
        return false;
    }

    @Override
    protected void dealFireDamage(int amount) {
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    public boolean isAirOrLiquid(IBlockState state) {
        return state.getMaterial() == Material.AIR
                || state.getMaterial() == Material.WATER
                || state.getMaterial() == Material.LAVA;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;

        double d3 = this.motionY;
        float f = this.jumpMovementFactor;
        this.jumpMovementFactor = 0.05F;
        this.collidedHorizontally = false;
        this.collidedVertically = false;
        this.collided = false;
        super.travel(strafe, vertical, forward);
        this.motionY = d3 * 0.6D;
        this.jumpMovementFactor = f;
        this.fallDistance = 0.0F;
        this.setFlag(7, false);
    }

    @Override
    public void setInWeb() {
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    public void setCreator(TileEntityCreator creator) {
        this.creator = creator;
    }

    public BlockPos getBuildTarget() {
        if(this.creator == null)return null;
        return this.creator.getPendingBlock(this);
    }

    private IBlockState getBuildState() {
        if(this.creator == null)return null;
        return this.creator.getExpectedState(this);
    }

    private void checkHasCreator() {
        if(this.creator != null) {
            boolean areaLoaded = this.world.isBlockLoaded(this.creator.getPos());

            if (areaLoaded) {
                boolean teExists = this.world.getTileEntity(this.creator.getPos()) != null;
                if (teExists)return;
            }
        }

        this.setDead();
    }

}

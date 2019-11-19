package iskallia.ibuilders.entity;

import iskallia.ibuilders.block.entity.TileEntityCreator;
import iskallia.itraders.util.profile.SkinProfile;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityBuilder extends EntityCreature {

    public SkinProfile skin;
    private String lastName = "Builder";
    protected BuilderPathFinder pathFinder;

    private TileEntityCreator creator;
    private BlockPos buildTarget;

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
        BlockPos pos = this.getBuildTarget();
        if(pos == null)return;

        if(pos.distanceSq(this.getPosition()) > 2.5 * 2.5)return;

        IBlockState state = this.world.getBlockState(pos);

        if(this.isAirOrLiquid(state) && this.getBuildState() != null) {
            this.world.setBlockState(pos, this.getBuildState());
            SoundType type = state.getBlock().getSoundType();
            this.world.playSound(null, pos, type.getPlaceSound(), SoundCategory.BLOCKS, type.getVolume(), type.getPitch());
            this.swingArm(EnumHand.MAIN_HAND);
        }
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
        this.jumpMovementFactor = 0.05F * (float)(this.isSprinting() ? 2 : 1);
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
        return this.creator.getPendingBlock();
    }

    private IBlockState getBuildState() {
        if(this.creator == null)return null;
        return this.creator.getExpectedState();
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

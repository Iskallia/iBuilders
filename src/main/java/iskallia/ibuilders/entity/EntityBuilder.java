package iskallia.ibuilders.entity;

import iskallia.itraders.util.profile.SkinProfile;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;

public class EntityBuilder extends EntityCreature {

    public SkinProfile skin;
    private String lastName = "Builder";

    public EntityBuilder(World world) {
        super(world);

        if(this.world.isRemote) {
            this.skin = new SkinProfile();
        }

        this.setCustomNameTag(this.lastName);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(this.world.isRemote) {
            String name = this.getCustomNameTag();

            if(!this.lastName.equals(name)) {
                this.skin.updateSkin(name);
                this.lastName = name;
            }
        } else {
            this.noClip = true;
            this.setNoGravity(true);
            this.travel(0.0f, 0.2f, 0.2f);
        }
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

}

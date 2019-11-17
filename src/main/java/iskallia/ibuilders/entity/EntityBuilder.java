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
        }
    }

}

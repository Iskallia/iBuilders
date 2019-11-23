package iskallia.ibuilders.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class RenderUtils {

    public static Vec3d getCameraView(float partialTicks) {
        return interpolatePosition(Minecraft.getMinecraft().getRenderViewEntity(), partialTicks);
    }

    public static Vec3d interpolatePosition(Entity e, float partialTicks) {
        if(e == null)return Vec3d.ZERO;

        return new Vec3d(
                RenderUtils.interpolate(e.lastTickPosX, e.posX, partialTicks),
                RenderUtils.interpolate(e.lastTickPosY, e.posY, partialTicks),
                RenderUtils.interpolate(e.lastTickPosZ, e.posZ, partialTicks)
        );
    }

    //Thanks HellFirePvP!
    public static double interpolate(double oldP, double newP, float partialTicks) {
        if(oldP == newP)return oldP;
        return oldP + ((newP - oldP) * partialTicks);
    }

}

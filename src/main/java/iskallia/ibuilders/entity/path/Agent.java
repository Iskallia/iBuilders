package iskallia.ibuilders.entity.path;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class Agent<T extends EntityLivingBase> {

    public abstract List<Node> getNodes(World world, Node currentNode);
    public abstract PathResult pathTo(T entity, BlockPos pos);

    public enum PathResult {
        IN_PROGRESS, COMPLETED, ERRORED
    }

}

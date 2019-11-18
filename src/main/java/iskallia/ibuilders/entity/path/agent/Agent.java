package iskallia.ibuilders.entity.path.agent;

import iskallia.ibuilders.entity.path.Node;
import net.minecraft.world.World;

import java.util.List;

public abstract class Agent {

    public abstract List<Node> getNodes(World world, Node currentNode);

}

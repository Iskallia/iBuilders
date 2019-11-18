package iskallia.ibuilders.entity.path;

import iskallia.ibuilders.entity.EntityBuilder;
import iskallia.ibuilders.entity.path.agent.Agent;
import iskallia.ibuilders.init.InitPath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathController {

    private EntityBuilder builder;
    private World world;

    public BlockPos start;
    public BlockPos end;

    private Set<Node> OPEN = new HashSet<Node>();
    private Set<Node> CLOSED = new HashSet<Node>();

    private List<Node> PATH = new ArrayList<Node>();

    public PathController(EntityBuilder builder) {
        this.builder = builder;
        this.world = this.builder.world;
    }

    public void tick() {

    }

    private static Vec3d toVec3f(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
    }

    public void findPath() {
        if(start != null && end != null)this.findPath(start.add(0, 0, 0), end.add(0, 0, 0));
    }

    public void findPath(BlockPos start, BlockPos end) {
        Node TARGET = new Node(end, null);
        Node START = new Node(start, null);
        OPEN.add(START);

        while(!OPEN.isEmpty()) {
            Node currentNode = null;

            for(Node node: OPEN) {
                if(currentNode == null || node.totalCost < currentNode.totalCost)currentNode = node;
            }

            OPEN.remove(currentNode);
            CLOSED.add(currentNode);

            if(currentNode.getPos().equals(TARGET.getPos())) {
                PATH.add(currentNode);

                while(currentNode.parent != null) {
                    PATH.add(currentNode.parent);
                    currentNode = currentNode.parent;
                }

                return;
            }

            for(Agent agent: InitPath.AGENTS) {
                for(Node child: agent.getNodes(world, currentNode)) {
                    child.parent = currentNode;
                    child.heuristic = (float)Math.sqrt(TARGET.getPos().distanceSq(child.getPos()));
                    child.totalCost = child.pathCost + child.heuristic;

                    if(CLOSED.contains(child))continue;

                    Node existingNode;

                    if(!OPEN.contains(child)) {
                        OPEN.add(child);
                    } else if((existingNode = getNodeWithPos(OPEN, child.getPos())) != null && child.pathCost < existingNode.pathCost) {
                        OPEN.remove(existingNode);
                        OPEN.add(child);
                    }
                }
            }

        }

    }

    private Node getNodeWithPos(Set<Node> set, BlockPos pos) {
        for(Node n: set) {
            if(n.getPos().getX() == pos.getX() &&
                    n.getPos().getY() == pos.getY() &&
                    n.getPos().getZ() == pos.getZ())return n;
        }

        return null;
    }

}

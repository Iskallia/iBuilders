package iskallia.ibuilders.entity.path;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class PathFinder {

    protected World world;
    protected List<Agent<?>> agents;
    private final Type type;

    protected Set<Node> openNodes = new HashSet<>();
    protected Set<Node> closedNodes = new HashSet<>();
    protected Queue<Node> finalPath = new ConcurrentLinkedQueue<>();

    public PathFinder(World world, List<Agent<?>> agents, Type type) {
        this.world = world;
        this.agents = agents;
        this.type = type;
    }

    public abstract void update();

    private static Vec3d toVec3f(BlockPos pos) {
        return new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
    }

    public void findPath(BlockPos start, BlockPos end) {
        Node endNode = new Node(end, null);
        Node startNode = new Node(start, null);
        this.openNodes.clear();
        this.closedNodes.clear();
        this.finalPath.clear();

        this.openNodes.add(startNode);

        while(!this.openNodes.isEmpty()) {
            Node currentNode = null;

            for(Node node: this.openNodes) {
                if(currentNode == null || node.totalCost < currentNode.totalCost)currentNode = node;
            }

            this.openNodes.remove(currentNode);
            this.closedNodes.add(currentNode);

            if(currentNode.getPos().equals(endNode.getPos())) {
                List<Node> reversePath = new ArrayList<>();
                reversePath.add(currentNode);

                while(currentNode.parent != null) {
                    reversePath.add(currentNode.parent);
                    currentNode = currentNode.parent;
                }

                Collections.reverse(reversePath);
                this.finalPath.addAll(reversePath);
                return;
            }

            for(Agent<?> agent: this.agents) {
                for(Node child: agent.getNodes(this.world, currentNode)) {
                    child.parent = currentNode;
                    child.heuristic = (float)endNode.getPos().distanceSq(child.getPos());
                    child.totalCost = child.pathCost + child.heuristic;
                    child.agent = agent;

                    if(this.closedNodes.contains(child))continue;

                    Node existingNode;

                    if(!this.openNodes.contains(child)) {
                        this.openNodes.add(child);
                    } else if((existingNode = getNodeWithPos(this.openNodes, child.getPos())) != null && child.totalCost < existingNode.totalCost) {
                        this.openNodes.remove(existingNode);
                        this.openNodes.add(child);
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

    public enum Type {
        A_STAR, GREEDY
    }

}

package iskallia.ibuilders.entity;

import iskallia.ibuilders.entity.path.Agent;
import iskallia.ibuilders.entity.path.Node;
import iskallia.ibuilders.entity.path.PathFinder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class BuilderPathFinder extends PathFinder {

    protected static final ExecutorService SERVICE = Executors.newFixedThreadPool(2);
    protected static List<Agent<?>> AGENTS = new ArrayList<>();
    protected EntityBuilder builder;

    protected AtomicReference<State> state = new AtomicReference<>(State.IDLE);
    protected Node node = null;

    public BuilderPathFinder(EntityBuilder builder) {
        super(builder.world, AGENTS, PathFinder.Type.GREEDY);
        this.builder = builder;
    }

    @Override
    public void update() {
        this.builder.setNoGravity(true);
        this.builder.noClip = true;

        if(this.state.get() == State.IDLE && this.builder.getBuildTarget() != null) {
            this.state.set(State.COMPUTING);
            this.findPath(this.builder.getPosition(), this.closerPos(this.builder.getPosition(), this.builder.getBuildTarget().up(1)));
            this.state.set(State.PATHFINDING);
            this.node = null;
        } else if(this.state.get() == State.PATHFINDING) {
            if(this.node == null || this.node.agent == null) {
                if(this.finalPath.isEmpty()) {
                    this.state.set(State.IDLE);
                    return;
                }

                this.node = this.finalPath.poll();
            } else {
                Agent.PathResult result = this.node.agent.pathTo(this.builder, node.getPos());

                if(result == Agent.PathResult.ERRORED) {
                    this.state.set(State.IDLE);
                    this.node = null;
                } else if(result == Agent.PathResult.COMPLETED) {
                    this.node = this.finalPath.poll();
                }
            }
        }
    }

    public BlockPos closerPos(BlockPos start, BlockPos target) {
        double distance = start.distanceSq(target);
        distance = Math.sqrt(distance);

        if(distance < 30.0d)return target;

        double r = 30.0d / distance;

        return new BlockPos(
                start.getX() + r * (target.getX() - start.getX()),
                start.getY() + r * (target.getY() - start.getY()),
                start.getZ() + r * (target.getZ() - start.getZ())
        );
    }

    public void reset() {
        this.node = null;
        this.finalPath.clear();
    }

    public enum State {
        IDLE, COMPUTING, PATHFINDING
    }

    static {
        AGENTS.add(new Agent<EntityBuilder>() {
            @Override
            public List<Node> getNodes(World world, Node currentNode) {
                List<Node> nodes = new ArrayList<>();

                for(int x = -1; x <= 1; x++) {
                    for(int y = -1; y <= 1; y++) {
                        for(int z = -1; z <= 1; z++) {
                            BlockPos offset = new BlockPos(x, y, z);
                            if(offset.equals(BlockPos.ORIGIN))continue;
                            Node node = new Node(currentNode.getPos().add(offset), this);
                            node.pathCost = currentNode.pathCost + (float)BlockPos.ORIGIN.distanceSq(offset);
                            nodes.add(node);
                        }
                    }
                }

                return nodes;
            }

            @Override
            public PathResult pathTo(EntityBuilder builder, BlockPos pos) {
                //Builders.LOG.warn("Starting at " + builder.getPosition() + " and pathing to " + pos + ".");

                if(builder.getPosition().equals(pos)) {
                    //Builders.LOG.warn("Moving to " + pos + " succeeded cause " + builder.getPosition().distanceSq(pos));
                    return PathResult.COMPLETED;
                }

                if(builder.getPosition().distanceSq(pos) > 3) {
                    //Builders.LOG.warn("Moving to " + pos + " failed cause " + builder.getPosition().distanceSq(pos));
                    return PathResult.ERRORED;
                }

                BlockPos offset = pos.subtract(builder.getPosition());
                builder.posX += offset.getX() * 0.8f;
                builder.posY += offset.getY() * 0.8f;
                builder.posZ += offset.getZ() * 0.8f;
                builder.setPositionAndUpdate(builder.posX, builder.posY, builder.posZ);
                builder.motionX += offset.getX() * 0.03f;
                builder.motionY += offset.getY() * 0.03f;
                builder.motionZ += offset.getZ() * 0.03f;

                return PathResult.IN_PROGRESS;
            }
        });
    }

}

package iskallia.ibuilders.entity;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.entity.path.Node;
import iskallia.ibuilders.entity.path.PathFinder;
import iskallia.ibuilders.entity.path.Agent;
import iskallia.ibuilders.init.InitPath;
import net.minecraft.util.EnumFacing;
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
        super(builder.world, AGENTS);
        this.builder = builder;
    }

    @Override
    public void update() {
        this.builder.setNoGravity(true);
        this.builder.noClip = true;

        if(this.state.get() == State.IDLE && this.builder.getBuildTarget() != null) {
            this.state.set(State.COMPUTING);
            Builders.LOG.warn("Computing...");
            this.findPath(this.builder.getPosition(), this.builder.getBuildTarget().up());
            Builders.LOG.warn("Found path with " + this.finalPath.size() + " nodes. " + this.finalPath);
            this.state.set(State.PATHFINDING);
            this.node = null;
        } else if(this.state.get() == State.PATHFINDING) {
            if(this.node == null || this.node.agent == null) {
                if(this.finalPath.isEmpty()) {
                    this.state.set(State.IDLE);
                    return;
                }

                this.node = this.finalPath.poll();
                Builders.LOG.warn("First node is " + this.node + ".");
            } else {
                Agent.PathResult result = this.node.agent.pathTo(this.builder, node.getPos());

                if(result == Agent.PathResult.ERRORED) {
                    Builders.LOG.warn("Agent " + node.getPos() + " has errored!");
                    this.state.set(State.IDLE);
                    this.node = null;
                } else if(result == Agent.PathResult.COMPLETED) {
                    Builders.LOG.warn("Agent " + node.getPos() + " has been completed!");
                    this.node = this.finalPath.poll();
                    Builders.LOG.warn("Next node is " + this.node + ".");
                }
            }
        }
    }

    public enum State {
        IDLE, COMPUTING, PATHFINDING
    }

    static {
        AGENTS.add(new Agent<EntityBuilder>() {
            private EnumFacing[] directions = new EnumFacing[] {
                EnumFacing.UP,
                EnumFacing.DOWN
            };

            @Override
            public List<Node> getNodes(World world, Node currentNode) {
                List<Node> nodes = new ArrayList<>();

                for(EnumFacing direction: this.directions) {
                    BlockPos pos = currentNode.getPos().offset(direction);

                    float cost = 1.0f;
                    if(!InitPath.GO_THROUGH_BLOCKS.contains(world.getBlockState(pos).getBlock()))cost += 2.0f;
                    if(!InitPath.GO_THROUGH_BLOCKS.contains(world.getBlockState(pos.up()).getBlock()))cost += 2.0f;

                    Node node = new Node(pos, this);
                    node.pathCost = currentNode.pathCost + cost;
                    nodes.add(node);
                }

                return nodes;
            }

            @Override
            public PathResult pathTo(EntityBuilder builder, BlockPos pos) {
                if(builder.getPosition().equals(pos)) {
                    return PathResult.COMPLETED;
                }

                if(builder.getPosition().distanceSq(pos) > 1) {
                    return PathResult.ERRORED;
                }

                int signum = pos.subtract(builder.getPosition()).getY();
                builder.travel(0.0f, signum * 2.0f, 0.0f);

                return PathResult.IN_PROGRESS;
            }
        });

        AGENTS.add(new Agent<EntityBuilder>() {
            private EnumFacing[] directions = EnumFacing.HORIZONTALS;

            @Override
            public List<Node> getNodes(World world, Node currentNode) {
                List<Node> nodes = new ArrayList<>();

                for(EnumFacing direction: this.directions) {
                    BlockPos pos = currentNode.getPos().offset(direction);

                    float cost = 1.0f;
                    if(!InitPath.GO_THROUGH_BLOCKS.contains(world.getBlockState(pos).getBlock()))cost += 2.0f;
                    if(!InitPath.GO_THROUGH_BLOCKS.contains(world.getBlockState(pos.up()).getBlock()))cost += 2.0f;

                    Node node = new Node(pos, this);
                    node.pathCost = currentNode.pathCost + cost;
                    nodes.add(node);
                }

                return nodes;
            }

            @Override
            public PathResult pathTo(EntityBuilder builder, BlockPos pos) {
                if(builder.getPosition().equals(pos)) {
                    return PathResult.COMPLETED;
                }

                if(builder.getPosition().distanceSq(pos) > 1) {
                    return PathResult.ERRORED;
                }

                BlockPos direction = pos.subtract(builder.getPosition());
                EnumFacing facing = EnumFacing.getFacingFromVector(direction.getX(), direction.getY(), direction.getZ());
                builder.rotationYaw = facing.getHorizontalAngle();

                builder.travel(0.0f, 0.0f, 0.5f);

                return PathResult.IN_PROGRESS;
            }
        });
    }

}

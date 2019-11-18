package iskallia.ibuilders.entity.path.agent;

import iskallia.ibuilders.entity.path.Node;
import iskallia.ibuilders.init.InitPath;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AgentLinear extends Agent {

    private EnumFacing[] directions;

    public AgentLinear() {
        this.directions = EnumFacing.values();
    }

    @Override
    public List<Node> getNodes(World world, Node currentNode) {
        List<Node> nodes = new ArrayList<>();

        for(EnumFacing direction: this.directions) {
            BlockPos pos = currentNode.getPos().offset(direction);

            if(this.isValidPos(world, pos)) {
                Node node = new Node(pos, this);
                node.pathCost = currentNode.pathCost + 1;
                nodes.add(node);
            }
        }

        return nodes;
    }

    private boolean isValidPos(World world, BlockPos pos) {
        if(!InitPath.GO_THROUGH_BLOCKS.contains(world.getBlockState(pos).getBlock()))return false;
        if(!InitPath.GO_THROUGH_BLOCKS.contains(world.getBlockState(pos.up()).getBlock()))return false;
        if(!InitPath.STEP_ON_BLOCKS.contains(world.getBlockState(pos.down()).getBlock()))return false;
        return true;
    }

}
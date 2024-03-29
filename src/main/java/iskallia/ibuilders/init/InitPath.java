package iskallia.ibuilders.init;

import net.minecraft.block.Block;

import java.util.HashSet;
import java.util.Set;

public class InitPath {

    public static Set<Block> GO_THROUGH_BLOCKS = new HashSet<Block>();
    public static Set<Block> STEP_ON_BLOCKS = new HashSet<Block>();

    static {
        for(Block block: Block.REGISTRY) {
            if(block.canSpawnInBlock()) {
                GO_THROUGH_BLOCKS.add(block);
            } else if(block.getDefaultState().getMaterial().isSolid()) {
                STEP_ON_BLOCKS.add(block);
            }
        }
    }

}

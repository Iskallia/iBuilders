package iskallia.ibuilders.schematic;

import com.github.lunatrius.schematica.world.storage.Schematic;
import net.minecraft.item.ItemStack;

public class BuildersSchematic extends Schematic {

    private BuildersSchematic.Info info = new BuildersSchematic.Info();

    public BuildersSchematic(ItemStack icon, int width, int height, int length) {
        super(icon, width, height, length);
    }

    public class Info {
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}

package iskallia.ibuilders.init;

import com.github.lunatrius.schematica.world.schematic.SchematicFormat;
import iskallia.ibuilders.schematic.BuildersFormat;

public class InitSchematic {

    public static void registerSchematics() {
        SchematicFormat.FORMATS.put("Builders", BuildersFormat.INSTANCE);
    }

}

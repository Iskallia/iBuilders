package iskallia.ibuilders.init;

import iskallia.ibuilders.Builders;
import iskallia.ibuilders.entity.EntityBuilder;
import iskallia.ibuilders.entity.render.RenderBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class InitEntity {
    private static int ID = 0;

    public InitEntity() {
    }

    public static void registerEntities() {
        registerEntity("builder", EntityBuilder.class);
    }

    public static void registerEntityRenderers() {
        registerEntityRenderer(EntityBuilder.class, RenderBuilder.getRenderFactory());
    }

    private static void registerEntity(String name, Class<? extends Entity> entityClass) {
        EntityRegistry.registerModEntity(Builders.getResource(name), entityClass, name, nextId(), Builders.getInstance(), 64, 1, true);
    }

    private static void registerEntityAndEgg(String name, Class<? extends Entity> entityClass, int primaryEggColor, int secondaryEggColor) {
        EntityRegistry.registerModEntity(Builders.getResource(name), entityClass, name, nextId(), Builders.getInstance(), 64, 1, true, primaryEggColor, secondaryEggColor);
    }

    private static void registerEntityRenderer(Class<? extends Entity> entityClass, IRenderFactory renderFactory) {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
    }

    private static int nextId() {
        return ID++;
    }
}
package cn.ussshenzhou.tellmewhere.blockentity.renderer;

import cn.ussshenzhou.tellmewhere.blockentity.ModBlockEntityTypeRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModBlockEntityRendererRegistry {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypeRegistry.TEST_SIGN.get(), TestSignBlockEntityRenderer::new);
    }
}

package cn.ussshenzhou.tellmewhere;

import cn.ussshenzhou.t88.render.event.T88RenderChunkBufferTypePrepareEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModRenderTypesListener {

    @SubscribeEvent
    public static void prepareRenderFillColor(T88RenderChunkBufferTypePrepareEvent event) {
        if (event.renderType == ModRenderTypes.FILL_COLOR) {
            var cPos = event.camera.getPosition();
            event.poseStack.translate(-cPos.x, -cPos.y, -cPos.z);
        }
    }
}

package cn.ussshenzhou.tellmewhere;

import cn.ussshenzhou.t88.render.ChunkBufferRenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

/**
 * @author USS_Shenzhou
 */
public class ModRenderTypes extends RenderStateShard {

    @Deprecated
    public ModRenderTypes(String pName, Runnable pSetupState, Runnable pClearState) {
        super(pName, pSetupState, pClearState);
    }

    @ChunkBufferRenderType
    public static final RenderType FILL_COLOR = RenderType.create("fill_color",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            2097152,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(POSITION_COLOR_LIGHTMAP_SHADER)
                    .createCompositeState(true)
    );
}

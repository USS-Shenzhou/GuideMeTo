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

    @ChunkBufferRenderType
    public static final RenderType TEXTa1 = RenderType.text(new ResourceLocation("minecraft:missing/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTb1 = RenderType.textSeeThrough(new ResourceLocation("minecraft:missing/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTc1 = RenderType.textPolygonOffset(new ResourceLocation("minecraft:missing/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTa2 = RenderType.text(new ResourceLocation("minecraft:uniform/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTb2 = RenderType.textSeeThrough(new ResourceLocation("minecraft:uniform/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTc2 = RenderType.textPolygonOffset(new ResourceLocation("minecraft:uniform/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTa3 = RenderType.text(new ResourceLocation("minecraft:default/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTb3 = RenderType.textSeeThrough(new ResourceLocation("minecraft:default/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTc3 = RenderType.textPolygonOffset(new ResourceLocation("minecraft:default/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTa4 = RenderType.text(new ResourceLocation("minecraft:illageralt/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTb4 = RenderType.textSeeThrough(new ResourceLocation("minecraft:illageralt/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTc4 = RenderType.textPolygonOffset(new ResourceLocation("minecraft:illageralt/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTa5 = RenderType.text(new ResourceLocation("minecraft:include/unifont/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTb5 = RenderType.textSeeThrough(new ResourceLocation("minecraft:include/unifont/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTc5 = RenderType.textPolygonOffset(new ResourceLocation("minecraft:include/unifont/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTa6 = RenderType.text(new ResourceLocation("minecraft:include/default/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTb6 = RenderType.textSeeThrough(new ResourceLocation("minecraft:include/default/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTc6 = RenderType.textPolygonOffset(new ResourceLocation("minecraft:include/default/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTa7 = RenderType.text(new ResourceLocation("minecraft:include/space/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTb7 = RenderType.textSeeThrough(new ResourceLocation("minecraft:include/space/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTc7 = RenderType.textPolygonOffset(new ResourceLocation("minecraft:include/space/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTa8 = RenderType.text(new ResourceLocation("minecraft:alt/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTb8 = RenderType.textSeeThrough(new ResourceLocation("minecraft:alt/0"));
    @ChunkBufferRenderType
    public static final RenderType TEXTc8 = RenderType.textPolygonOffset(new ResourceLocation("minecraft:alt/0"));

}

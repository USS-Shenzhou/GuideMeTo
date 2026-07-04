package cn.ussshenzhou.tellmewhere.blockentity;

import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.SectionPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class SignBlockEntitySectionRenderer {

    @SubscribeEvent
    public static void addDisguiseToSection(AddSectionGeometryEvent event) {
        var sectionOrigin = event.getSectionOrigin();
        var sectionPos = SectionPos.of(sectionOrigin);
        var level = event.getLevel();
        var chunk = level.getChunk(sectionOrigin);
        chunk.blockEntities.forEach((blockPos, blockEntity) -> {
            if (blockPos.getY() < sectionPos.minBlockY() || blockPos.getY() > sectionPos.maxBlockY() || !(blockEntity instanceof SignBlockEntity signBlockEntity) || signBlockEntity.disguiseModel == null) {
                return;
            }
            event.addRenderer(context -> {
                context.getBlockRenderer().tesselateBlock((x, y, z, quad, instance) -> context.getOrCreateChunkBuffer(ChunkSectionLayer.CUTOUT).putBlockBakedQuad(x, y, z, quad, instance),
                        blockPos.getX() - sectionOrigin.getX(), blockPos.getY() - sectionOrigin.getY(), blockPos.getZ() - sectionOrigin.getZ(),
                        (BlockAndTintGetter) level, blockPos, signBlockEntity.getDisguiseBlockState(), signBlockEntity.disguiseModel, 42);
            });
        });
    }
}

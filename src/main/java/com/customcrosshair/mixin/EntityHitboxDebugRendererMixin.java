package com.customcrosshair.mixin;

import com.customcrosshair.CrosshairConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.ArrowGizmo;
import net.minecraft.world.debug.gizmo.Gizmo;
import net.minecraft.world.debug.gizmo.GizmoCollector;
import net.minecraft.world.debug.gizmo.VisibilityConfigurable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Lets the menu hide the blue "looking direction" line on Minecraft's own F3+B hitboxes.
 * Every hook is optional (require = 0): if Minecraft draws the line some other way,
 * the toggle simply does nothing instead of crashing the game.
 */
@Mixin(targets = "net.minecraft.client.render.debug.EntityHitboxDebugRenderer")
public abstract class EntityHitboxDebugRendererMixin {

    private static boolean customcrosshair$hide(int color) {
        if (CrosshairConfig.get().vanillaHitboxDirection) return false;
        int r = (color >> 16) & 0xFF, g = (color >> 8) & 0xFF, b = color & 0xFF;
        return b > 150 && r < 100 && g < 100;   // the direction line is blue
    }

    @WrapOperation(method = "drawHitbox", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/debug/gizmo/GizmoDrawing;arrow(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;I)Lnet/minecraft/world/debug/gizmo/VisibilityConfigurable;"))
    private VisibilityConfigurable customcrosshair$arrow(Vec3d start, Vec3d end, int color, Operation<VisibilityConfigurable> original) {
        return customcrosshair$hide(color) ? GizmoCollector.NOOP_CONFIGURABLE : original.call(start, end, color);
    }

    @WrapOperation(method = "drawHitbox", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/debug/gizmo/GizmoDrawing;arrow(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;IF)Lnet/minecraft/world/debug/gizmo/VisibilityConfigurable;"))
    private VisibilityConfigurable customcrosshair$arrowW(Vec3d start, Vec3d end, int color, float width, Operation<VisibilityConfigurable> original) {
        return customcrosshair$hide(color) ? GizmoCollector.NOOP_CONFIGURABLE : original.call(start, end, color, width);
    }

    @WrapOperation(method = "drawHitbox", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/debug/gizmo/GizmoDrawing;line(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;I)Lnet/minecraft/world/debug/gizmo/VisibilityConfigurable;"))
    private VisibilityConfigurable customcrosshair$line(Vec3d start, Vec3d end, int color, Operation<VisibilityConfigurable> original) {
        return customcrosshair$hide(color) ? GizmoCollector.NOOP_CONFIGURABLE : original.call(start, end, color);
    }

    @WrapOperation(method = "drawHitbox", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/debug/gizmo/GizmoDrawing;line(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;IF)Lnet/minecraft/world/debug/gizmo/VisibilityConfigurable;"))
    private VisibilityConfigurable customcrosshair$lineW(Vec3d start, Vec3d end, int color, float width, Operation<VisibilityConfigurable> original) {
        return customcrosshair$hide(color) ? GizmoCollector.NOOP_CONFIGURABLE : original.call(start, end, color, width);
    }

    /** Fallback in case the arrow is built directly and handed to collect(). */
    @WrapOperation(method = "drawHitbox", require = 0, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/debug/gizmo/GizmoDrawing;collect(Lnet/minecraft/world/debug/gizmo/Gizmo;)Lnet/minecraft/world/debug/gizmo/VisibilityConfigurable;"))
    private VisibilityConfigurable customcrosshair$collect(Gizmo gizmo, Operation<VisibilityConfigurable> original) {
        if (gizmo instanceof ArrowGizmo && !CrosshairConfig.get().vanillaHitboxDirection) return GizmoCollector.NOOP_CONFIGURABLE;
        return original.call(gizmo);
    }
}

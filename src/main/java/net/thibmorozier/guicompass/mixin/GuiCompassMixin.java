package net.thibmorozier.guicompass.mixin;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.thibmorozier.guicompass.config.CompassConfig;
import net.thibmorozier.guicompass.util.CompassVector2i;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class GuiCompassMixin {
	private static final String[] direction = new String[] { "S", "SW", "W", "NW", "N", "NE", "E", "SE" };

	private TextRenderer textRenderer = null;

    @Accessor("client")
    protected abstract MinecraftClient getClient();

	@Inject(
		method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/util/math/MatrixStack;)V",
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
	private void renderCompass(MatrixStack matrices, float tickDelta, CallbackInfo info) {
		MinecraftClient client = getClient();
		PlayerEntity playerEntity = client.player;

		if (CompassConfig.MUST_HAVE_COMPASS_IN_INVENTORY.getValue())
			if (!playerEntity.getInventory().contains(Items.COMPASS.getDefaultStack()))
				return;

		if (textRenderer == null)
			textRenderer = client.textRenderer;

		int x      = playerEntity.getBlockX();
		int y      = playerEntity.getBlockY();
		int z      = playerEntity.getBlockZ();
		int facing = MathHelper.floor(playerEntity.getYaw() / 45.0D + 0.5D) & 7;

		String compassStr = String.format("%s: %d, %d, %d", direction[facing], x, y, z);

		int textWidth = textRenderer.getWidth(compassStr);
		CompassVector2i screenPos = getCompassScreenPos(client, textWidth);

		Color foreColor = new Color(CompassConfig.RGB_R.getValue(), CompassConfig.RGB_G.getValue(), CompassConfig.RGB_B.getValue(), 255);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		DrawableHelper.fill(matrices, screenPos.getX() - 2, screenPos.getY() - 2, screenPos.getX() + textWidth + 2, screenPos.getY() + 11, client.options.getTextBackgroundColor(0));
		textRenderer.drawWithShadow(matrices, compassStr, (float)screenPos.getX(), (float)screenPos.getY(), foreColor.getRGB());
		RenderSystem.disableBlend();
	}

	private CompassVector2i getCompassScreenPos(MinecraftClient client, int textWidth) {
		switch (CompassConfig.POSITION.getValue()) {
			case TOP_LEFT:      return new CompassVector2i(3, 2);
			case TOP_CENTER:    return new CompassVector2i((client.getWindow().getScaledWidth() / 2) - (textWidth / 2), 2);
			case TOP_RIGHT:     return new CompassVector2i(client.getWindow().getScaledWidth() - (textWidth + 3), 2);

			case CENTER_LEFT:   return new CompassVector2i(3, (client.getWindow().getScaledHeight() / 2) - 5);
			case CENTER_CENTER: return new CompassVector2i((client.getWindow().getScaledWidth() / 2) - (textWidth / 2), (client.getWindow().getScaledHeight() / 2) - 5);
			case CENTER_RIGHT:  return new CompassVector2i(client.getWindow().getScaledWidth() - (textWidth + 3), (client.getWindow().getScaledHeight() / 2) - 5);

			case BOTTOM_LEFT:   return new CompassVector2i(3, client.getWindow().getScaledHeight() - 36);
			case BOTTOM_CENTER: return new CompassVector2i((client.getWindow().getScaledWidth() / 2) - (textWidth / 2), client.getWindow().getScaledHeight() - 36);
			case BOTTOM_RIGHT:  return new CompassVector2i(client.getWindow().getScaledWidth() - (textWidth + 3), client.getWindow().getScaledHeight() - 36);

			default:            return new CompassVector2i(3, client.getWindow().getScaledHeight() - 36);
		}
	}
}

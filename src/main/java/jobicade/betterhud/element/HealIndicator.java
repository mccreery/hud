package jobicade.betterhud.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

import static jobicade.betterhud.BetterHud.SPACER;

public class HealIndicator extends HudElement {
    private SettingChoose mode;

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        position.setPreset(Direction.NORTH_WEST);
        mode.setIndex(1);
    }

    public HealIndicator() {
        super("healIndicator");
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(new Legend("misc"));
        settings.add(mode = new SettingChoose(2));
    }

    @Override
    public Rect render(Event event) {
            String healIndicator = I18n.get("betterHud.hud.healIndicator");
            Rect bounds = mode.getIndex() == 0 ? new Rect(Minecraft.getInstance().font.width(healIndicator), Minecraft.getInstance().font.lineHeight) : new Rect(9, 9);

            if(position.isCustom()) {
                bounds = position.applyTo(bounds);
            } else {
                Direction side = HudElement.HEALTH.getIndicatorSide();
                bounds = bounds.align(HudElement.HEALTH.getLastBounds().grow(SPACER, 0, SPACER, 0).getAnchor(side), side.mirrorCol());
            }

            if(mode.getIndex() == 0) {
                GlUtil.drawString(((RenderGameOverlayEvent)event).getMatrixStack(), healIndicator, bounds.getPosition(), Direction.NORTH_WEST, Color.GREEN);
            } else {
                Minecraft.getInstance().getTextureManager().bind(Textures.HUD_ICONS);
                MatrixStack matrixStack = ((RenderGameOverlayEvent)event).getMatrixStack();
                Minecraft.getInstance().gui.blit(matrixStack, bounds.getX(), bounds.getY(), 0, 80, 9, 9);
            }
            return bounds;
    }

    /** @see net.minecraft.util.FoodStats#tick(PlayerEntity) */
    @Override
    public boolean shouldRender(Event event) {
        return super.shouldRender(event)
            && Minecraft.getInstance().gameMode.hasExperience()
            && Minecraft.getInstance().level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)
            && Minecraft.getInstance().player.getFoodData().getFoodLevel() >= 18
            && Minecraft.getInstance().player.isHurt();
    }
}

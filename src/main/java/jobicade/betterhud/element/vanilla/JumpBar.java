package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.ICONS;
import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.GlUtil;

public class JumpBar extends OverrideElement {
	public JumpBar() {
		super("jumpBar", new SettingPosition("position", DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		settings.priority.set(2);
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.player.isRidingHorse() && super.shouldRender(event);
	}

	@Override
	protected Rect render(Event event) {
		MC.getTextureManager().bindTexture(ICONS);

		Rect bounds = new Rect(182, 5);
		if(!position.isCustom() && position.getDirection() == Direction.SOUTH) {
			bounds = MANAGER.position(Direction.SOUTH, bounds, false, 1);
		} else {
			bounds = position.applyTo(bounds);
		}

		float charge = MC.player.getHorseJumpPower();
		int filled = (int)(charge * bounds.getWidth());

		GlUtil.drawTexturedModalRect(bounds.getPosition(), new Rect(0, 84, bounds.getWidth(), bounds.getHeight()));

		if(filled > 0) {
			GlUtil.drawTexturedModalRect(bounds.getPosition(), new Rect(0, 89, filled, bounds.getHeight()));
		}
		return bounds;
	}

	@Override
	protected ElementType getType() {
		return ElementType.JUMPBAR;
	}
}

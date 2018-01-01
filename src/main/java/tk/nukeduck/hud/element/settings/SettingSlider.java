package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiOptionSliderA;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class SettingSlider extends SettingAlignable {
	private final double min;
	private final double max;
	public final double accuracy;
	public double value = 0;

	public SettingSlider(String name, double min, double max) {
		this(name, min, max, -1, Direction.CENTER);
	}
	public SettingSlider(String name, double min, double max, double accuracy) {
		this(name, min, max, accuracy, Direction.CENTER);
	}
	public SettingSlider(String name, double min, double max, double accuracy, Direction alignment) {
		super(name, alignment);
		this.min = min;
		this.max = max;
		this.accuracy = accuracy;
		this.value = MathHelper.clamp(value, min, max);
	}

	public double normalize(double value) {
		return (value - min) / (max - min);
	}

	public double denormalize(double normalized) {
		double toRound = normalized * (max - min) + min;
		return accuracy == -1 ? toRound : MathHelper.clamp(Math.round(toRound / accuracy) * accuracy, min, max);
	}

	public String getSliderText() {
		return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), value);
	}

	@Override
	public int getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Bounds bounds) {
		parts.add(new GuiOptionSliderA(0, bounds.x(), bounds.y(), bounds.width(), bounds.height(), this));
		return bounds.bottom() + SPACER;
	}

	@Override public void actionPerformed(GuiElementSettings gui, GuiButton button) {}
	@Override public void keyTyped(char typedChar, int keyCode) throws IOException {}
	@Override public void otherAction(Collection<Setting> settings) {}

	@Override
	public String save() {
		return String.valueOf(value);
	}

	@Override
	public void load(String val) {
		value = MathHelper.clamp(Double.parseDouble(val), min, max);
	}
}

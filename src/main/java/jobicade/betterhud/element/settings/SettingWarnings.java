package jobicade.betterhud.element.settings;

import java.util.Collection;

import jobicade.betterhud.geom.Direction;
import net.minecraft.client.resources.I18n;

public class SettingWarnings extends SettingStub {
	private final SettingSlider[] sliders;

	public SettingWarnings(String name) {
		this(name, 3);
	}

	public SettingWarnings(String name, int warnings) {
		super(name);

		addChild(new Legend("damageWarning"));

		sliders = new SettingSlider[warnings];
		for(int i = 0; i < sliders.length; i++) {
			final int index = i;

			addChild(sliders[i] = new SettingSlider("warning." + String.valueOf(i + 1), 0, 1) {
				@Override
				public String getDisplayValue(double value) {
					SettingSlider next = next();

					if(next == null || next.getValue() < getValue()) {
						return super.getDisplayValue(value);
					} else {
						return I18n.format("betterHud.value.disabled");
					}
				}

				@Override
				public void setValue(float value) {
					SettingSlider next = next();
					super.setValue(next != null ? Math.max(value, next.getValue()) : value);

					for(int i = index - 1; i >= 0; i--) {
						SettingSlider slider = sliders[i];
						if(slider != null) slider.setValue(Math.max(slider.getValue(), getValue()));
					}
				}

				private SettingSlider next() {
					return index == sliders.length - 1 ? null : sliders[index + 1];
				}

				@Override
				public void updateGuiParts(Collection<Setting> settings) {
					guiSlider.updateDisplayString();
				}
			}.setDisplayPercent().setAlignment((i & 1) == 1 ? Direction.EAST : Direction.WEST));
		}

		if((sliders.length & 1) == 1) {
			sliders[sliders.length - 1].setAlignment(Direction.CENTER);
		}
	}

	public float[] get() {
		float[] values = new float[sliders.length];

		for(int i = 0; i < sliders.length; i++) {
			values[i] = sliders[i].getValue();
		}
		return values;
	}

	public void set(float... values) {
		for(int i = 0; i < sliders.length; i++) {
			if(values[i] >= 0) {
				sliders[i].setValue(values[i]);
			}
		}
	}

	public int getWarning(float value) {
		for(int i = sliders.length - 1; i >= 0; i--) {
			if(value <= sliders[i].getValue()) return i + 1;
		}
		return 0;
	}
}

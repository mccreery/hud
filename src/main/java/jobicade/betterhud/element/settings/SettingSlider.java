package jobicade.betterhud.element.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.GuiSlider;
import jobicade.betterhud.util.ISlider;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.Map;

public class SettingSlider extends SettingAlignable<Double> implements ISlider {
    protected GuiSlider slider;
    private final double min, max, interval;

    private int displayPlaces;
    private String unlocalizedValue;

    private double value;
    private double displayScale = 1;

    public SettingSlider(String name, double min, double max) {
        this(name, min, max, -1);
    }

    public SettingSlider(String name, double min, double max, double interval) {
        super(name, Direction.CENTER);
        this.min = min;
        this.max = max;
        this.interval = interval;

        updateDisplayPlaces();
        set(getMinimum());
    }

    private void updateDisplayPlaces() {
        int places = interval != -1
            && interval * displayScale == (int)(interval * displayScale) ? 0 : 1;
        setDisplayPlaces(places);
    }

    public SettingSlider setAlignment(Direction alignment) {
        this.alignment = alignment;
        return this;
    }

    public SettingSlider setDisplayScale(double displayScale) {
        this.displayScale = displayScale;
        updateDisplayPlaces();

        return this;
    }

    public SettingSlider setDisplayPlaces(int displayPlaces) {
        this.displayPlaces = displayPlaces;
        return this;
    }

    public SettingSlider setUnlocalizedValue(String unlocalizedValue) {
        this.unlocalizedValue = unlocalizedValue;
        return this;
    }

    @Override
    public String getDisplayString() {
        return I18n.get("betterHud.setting." + name) + ": " + getDisplayValue(get() * displayScale);
    }

    public String getDisplayValue(double scaledValue) {
        String displayValue = MathUtil.formatToPlaces(scaledValue, displayPlaces);

        if(unlocalizedValue != null) {
            displayValue = I18n.get(unlocalizedValue, displayValue);
        }
        return displayValue;
    }

    @Override
    public void getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting<?>> callbacks, Rect bounds) {
        slider = new GuiSlider(0, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), this);

        parts.add(slider);
        callbacks.put(slider, this);
    }

    @Override public void actionPerformed(GuiElementSettings gui, Button button) {}
    @Override public Double get() {return value;}

    public int getInt() {
        return get().intValue();
    }

    @Override
    public void set(Double value) {
        this.value = normalize(value);
        if(slider != null) slider.updateDisplayString();
    }

    public void set(int value) {
        set((double)value);
    }

    @Override
    public JsonElement save() {
        return new JsonPrimitive(get());
    }

    @Override
    public void load(JsonElement save) {
        set(save.getAsDouble());

        if(slider != null) {
            slider.updateDisplayString();
        }
    }

    @Override public Double getMinimum() {return min;}
    @Override public Double getMaximum() {return max;}
    @Override public Double getInterval() {return interval;}
}

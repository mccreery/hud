package jobicade.betterhud.element.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.GuiOffsetChooser;
import jobicade.betterhud.gui.GuiUpDownButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static jobicade.betterhud.BetterHud.SPACER;

public class SettingAbsolutePosition extends Setting<Point> {
    public TextFieldWidget xBox, yBox;
    public Button pick;
    private Button xUp, xDown, yUp, yDown;

    private final SettingPosition position;

    protected int x, y, cancelX, cancelY;
    protected boolean isPicking = false;

    public boolean isPicking() {
        return isPicking;
    }

    public SettingAbsolutePosition(String name) {
        this(name, null);
    }

    public SettingAbsolutePosition(String name, SettingPosition position) {
        super(name);
        this.position = position;
    }

    @Override
    public Point getGuiParts(List<AbstractGui> parts, Map<AbstractGui, Setting<?>> callbacks, Point origin) {
        parts.add(xBox = new TextFieldWidget(Minecraft.getInstance().font, origin.getX() - 106, origin.getY() + 1, 80, 18, StringTextComponent.EMPTY.copy()));
        xBox.setValue(String.valueOf(x));
        parts.add(yBox = new TextFieldWidget(Minecraft.getInstance().font, origin.getX() + 2, origin.getY() + 1, 80, 18, StringTextComponent.EMPTY.copy()));
        yBox.setValue(String.valueOf(y));

        parts.add(xUp   = new GuiUpDownButton(true ).setBounds(new Rect(origin.getX() - 22, origin.getY(),      0, 0)).setId(0).setRepeat());
        parts.add(xDown = new GuiUpDownButton(false).setBounds(new Rect(origin.getX() - 22, origin.getY() + 10, 0, 0)).setId(1).setRepeat());
        parts.add(yUp   = new GuiUpDownButton(true ).setBounds(new Rect(origin.getX() + 86, origin.getY(),      0, 0)).setId(2).setRepeat());
        parts.add(yDown = new GuiUpDownButton(false).setBounds(new Rect(origin.getX() + 86, origin.getY() + 10, 0, 0)).setId(3).setRepeat());

        if(position != null) {
            parts.add(pick = new Button(origin.getX() - 100, origin.getY() + 22, 200, 20, new TranslationTextComponent("betterHud.menu.pick"), null));
            callbacks.put(pick, this);
        }

        callbacks.put(xBox, this);
        callbacks.put(yBox, this);
        callbacks.put(xUp, this);
        callbacks.put(xDown, this);
        callbacks.put(yUp, this);
        callbacks.put(yDown, this);

        return origin.add(0, 42 + SPACER);
    }

    public void updateText() {
        if(xBox != null && yBox != null) {
            xBox.setValue(String.valueOf(x));
            yBox.setValue(String.valueOf(y));
        }
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, Button button) {
        switch(0) {
            case 0: xBox.setValue(String.valueOf(++x)); break;
            case 1: xBox.setValue(String.valueOf(--x)); break;
            case 2: yBox.setValue(String.valueOf(++y)); break;
            case 3: yBox.setValue(String.valueOf(--y)); break;
            case 4: Minecraft.getInstance().setScreen(new GuiOffsetChooser(gui, position)); break;
        }
    }

    /** Forgets the original position and keeps the current picked position */
    public void finishPicking() {
        isPicking = false;
        //pick.displayString = I18n.format("betterHud.menu.pick");
    }

    @Override
    public void set(Point value) {
        x = value.getX();
        y = value.getY();
        updateText();
    }

    @Override
    public Point get() {
        return new Point(x, y);
    }

    @Override
    public JsonElement save() {
        JsonObject object = new JsonObject();
        object.add("x", new JsonPrimitive(x));
        object.add("y", new JsonPrimitive(y));
        return object;
    }

    @Override
    public void load(JsonElement val) {
        JsonObject object = val.getAsJsonObject();
        set(new Point(object.get("x").getAsInt(), object.get("y").getAsInt()));
    }

    @Override
    public void updateGuiParts(Collection<Setting<?>> settings) {
        super.updateGuiParts(settings);

        boolean enabled = enabled();
        xBox.setEditable(enabled);
        yBox.setEditable(enabled);

        if(pick != null) pick.active = enabled;

        if(enabled) {
            try {
                x = Integer.parseInt(xBox.getValue());
                xUp.active = xDown.active = true;
            } catch(NumberFormatException e) {
                x = 0;
                xUp.active = xDown.active = false;
            }

            try {
                y = Integer.parseInt(yBox.getValue());
                yUp.active = yDown.active = true;
            } catch(NumberFormatException e) {
                y = 0;
                yUp.active = yDown.active = false;
            }
        } else {
            xUp.active = xDown.active = yUp.active = yDown.active = false;
        }
    }
}

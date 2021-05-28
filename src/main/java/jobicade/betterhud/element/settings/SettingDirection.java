package jobicade.betterhud.element.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiActionButton;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

import java.util.Collection;
import java.util.Map;

import static jobicade.betterhud.BetterHud.SPACER;

public class SettingDirection extends SettingAlignable<Direction> {
    private GuiActionButton[] toggles = new GuiActionButton[9];
    private Rect bounds;

    private boolean horizontal = false;

    private final DirectionOptions options;
    private Direction value;

    public SettingDirection(String name, Direction alignment) {
        this(name, alignment, DirectionOptions.ALL);
    }

    public SettingDirection(String name, Direction alignment, DirectionOptions options) {
        super(name, alignment);
        this.options = options;
    }

    public SettingDirection setHorizontal() {
        horizontal = true;
        setAlignment(Direction.WEST);

        return this;
    }

    @Override
    protected int getAlignmentWidth() {
        return horizontal ? 150 : 240;
    }

    @Override
    public SettingAlignable<Direction> setAlignment(Direction alignment) {
        if(!horizontal) {
            return super.setAlignment(alignment);
        } else {
            return this;
        }
    }

    @Override
    public void getGuiParts(java.util.List<AbstractGui> parts, Map<AbstractGui,Setting<?>> callbacks, Rect bounds) {
        this.bounds = bounds;

        Rect radios = new Rect(60, 60).anchor(bounds, horizontal ? Direction.WEST : Direction.SOUTH);
        Rect radio = new Rect(20, 20);

        for(Direction direction : Direction.values()) {
            GuiActionButton button = new GuiActionButton("")
                .setId(direction.ordinal())
                .setBounds(radio.anchor(radios, direction));

            parts.add(button);
            callbacks.put(button, this);
            toggles[direction.ordinal()] = button;
        }
    }

    @Override
    protected Point getSize() {
        return horizontal ? new Point(150, 60) : new Point(60, 60 + SPACER + Minecraft.getInstance().font.lineHeight);
    }

    private String getText() {
        return horizontal ? getLocalizedName() + ": " + localizeDirection(value) : getLocalizedName();
    }

    @Override
    public void actionPerformed(GuiElementSettings gui, Button button) {
        value = Direction.values()[button.field_146127_k];
    }

    @Override
    public void updateGuiParts(Collection<Setting<?>> settings) {
        super.updateGuiParts(settings);
        boolean enabled = enabled();

        for(GuiActionButton button : toggles) {
            button.glowing = value != null && button.field_146127_k == value.ordinal();
            button.field_146124_l = button.glowing || enabled && options.isValid(Direction.values()[button.field_146127_k]);
        }
    }

    @Override
    public void draw() {
        String text = getText();

        if(horizontal) {
            GlUtil.drawString(text, bounds.withWidth(60 + SPACER).getAnchor(Direction.EAST), Direction.WEST, Color.WHITE);
        } else {
            GlUtil.drawString(text, bounds.getAnchor(Direction.NORTH), Direction.NORTH, Color.WHITE);
        }
    }

    @Override
    public JsonElement save() {
        return value != null ? new JsonPrimitive(value.name()) : JsonNull.INSTANCE;
    }

    @Override
    public void load(JsonElement save) {
        try {
            set(Direction.valueOf(save.getAsString()));
        } catch(IllegalArgumentException e) {
            set(null);
        }
    }

    @Override
    public Direction get() {
        return value;
    }

    @Override
    public void set(Direction value) {
        value = options.apply(value);

        if(options.isValid(value)) {
            this.value = value;
        }
    }

    @Override
    protected boolean shouldBreak() {
        return horizontal || alignment == Direction.EAST;
    }

    public DirectionOptions getOptions() {
        return options;
    }

    public static String localizeDirection(Direction direction) {
        String name = "none";

        if (direction != null) {
            switch(direction) {
                case NORTH_WEST: name = "northWest"; break;
                case NORTH:      name = "north"; break;
                case NORTH_EAST: name = "northEast"; break;
                case WEST:       name = "west"; break;
                case CENTER:     name = "center"; break;
                case EAST:       name = "east"; break;
                case SOUTH_WEST: name = "southWest"; break;
                case SOUTH:      name = "south"; break;
                case SOUTH_EAST: name = "southEast"; break;
            }
        }
        return I18n.get("betterHud.value." + name);
    }
}

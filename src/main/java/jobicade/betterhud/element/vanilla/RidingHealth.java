package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.util.bars.StatBarMount;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.Event;

public class RidingHealth extends Bar {
    public RidingHealth() {
        super("mountHealth", new StatBarMount());
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        settings.priority.set(5);
        side.setIndex(1);
    }

    @Override
    protected ElementType getType() {
        return ElementType.HEALTHMOUNT;
    }

    @Override
    public boolean shouldRender(Event event) {
        return Minecraft.getInstance().gameMode.canHurtPlayer() && super.shouldRender(event);
    }
}

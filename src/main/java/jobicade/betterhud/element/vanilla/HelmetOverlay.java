package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.Event;

import static jobicade.betterhud.BetterHud.MANAGER;

public class HelmetOverlay extends OverrideElement {
    private static final ResourceLocation PUMPKIN_BLUR_TEX_PATH = new ResourceLocation("textures/misc/pumpkinblur.png");

    public HelmetOverlay() {
        super("helmetOverlay");
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        settings.priority.set(Integer.MIN_VALUE);
    }

    @Override
    protected ElementType getType() {
        return ElementType.HELMET;
    }

    @Override
    public boolean shouldRender(Event event) {
        return super.shouldRender(event) && Minecraft.getInstance().options.getCameraType() == PointOfView.FIRST_PERSON && !Minecraft.getInstance().player.inventory.getArmor(3).isEmpty();
    }

    @Override
    protected Rect render(Event event) {
        ItemStack stack = Minecraft.getInstance().player.inventory.getArmor(3);
        Item item = stack.getItem();

        if(item == Item.byBlock(Blocks.PUMPKIN)) {
            Minecraft.getInstance().getTextureManager().bind(PUMPKIN_BLUR_TEX_PATH);
            GlUtil.drawRect(MANAGER.getScreen(), new Rect(256, 256), Color.RED);
            Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
        } else {
            MainWindow window = ((RenderGameOverlayEvent)event).getWindow();
            item.renderHelmetOverlay(stack, Minecraft.getInstance().player, window.getGuiScaledWidth(), window.getGuiScaledHeight(), getPartialTicks(event));
        }
        return MANAGER.getScreen();
    }
}

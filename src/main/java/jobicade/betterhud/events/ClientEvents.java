package jobicade.betterhud.events;

import org.lwjgl.glfw.GLFW;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.gui.GuiElementList;
import jobicade.betterhud.registry.OverlayElements;
import net.java.games.input.Keyboard;
import net.java.games.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = BetterHud.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static final KeyBinding menuKey = new KeyBinding("key.betterHud.open", GLFW.GLFW_KEY_U, "key.categories.misc");
    private static final ConfigManager configManager = new ConfigManager();

    public static void setupClient(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(menuKey);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (menuKey.isPressed()) {
            Minecraft.getInstance().displayGuiScreen(new GuiElementList(configManager));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityDamage(LivingDamageEvent event) {
        if (!event.isCanceled() && event.getEntity().equals(Minecraft.getMinecraft().player)) {
            OverlayElements.BLOOD_SPLATTERS.onDamaged((int)event.getAmount());
        }
    }

    @SubscribeEvent
    public static void onConnect(ClientConnectedToServerEvent event) {
        if(event.isLocal()) {
            OverlayElements.CONNECTION.setLocal();
        } else {
            OverlayElements.CONNECTION.setRemote(event.getManager().getRemoteAddress());
        }
    }

    @SubscribeEvent
    public static void onClick(MouseInputEvent event) {
        if(Mouse.getEventButton() != -1 && Mouse.getEventButtonState()) {
            OverlayElements.CPS.onClick();
        }
    }

    @SubscribeEvent
    public static void onPlayerDisconnected(ClientDisconnectionFromServerEvent event) {
        OverlayElements.BLOCK_VIEWER.onChangeWorld();
        BetterHud.setServerVersion(null);
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
        OverlayElements.BLOCK_VIEWER.onChangeWorld();
    }
}

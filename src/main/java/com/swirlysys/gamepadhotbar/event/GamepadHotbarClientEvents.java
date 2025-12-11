package com.swirlysys.gamepadhotbar.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.swirlysys.gamepadhotbar.GamepadHotbar;
import com.swirlysys.gamepadhotbar.config.GamepadHotbarClientConfig;
import com.swirlysys.gamepadhotbar.util.HotbarPos;
import com.swirlysys.gamepadhotbar.util.HotbarScale;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.joml.Vector2i;

@EventBusSubscriber(modid = GamepadHotbar.MODID, value = Dist.CLIENT)
public class GamepadHotbarClientEvents {
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
    private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");
    private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_right");
    private static final ResourceLocation HOTBAR_0 = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "item/hotbar_0");
    private static final ResourceLocation HOTBAR_1 = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "item/hotbar_1");
    private static final ResourceLocation HOTBAR_2 = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "item/hotbar_2");
    private static final ResourceLocation HOTBAR_3 = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "item/hotbar_3");
    private static final ResourceLocation HOTBAR_4 = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "item/hotbar_4");
    private static final ResourceLocation HOTBAR_5 = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "item/hotbar_5");
    private static final ResourceLocation HOTBAR_6 = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "item/hotbar_6");
    private static final ResourceLocation HOTBAR_7 = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "item/hotbar_7");
    private static final ResourceLocation TAP_HOTBAR_LEFT = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "hud/tap_hotbar_left");
    private static final ResourceLocation TAP_HOTBAR_UP = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "hud/tap_hotbar_up");
    private static final ResourceLocation TAP_HOTBAR_RIGHT = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "hud/tap_hotbar_right");
    private static final ResourceLocation TAP_HOTBAR_DOWN = ResourceLocation.fromNamespaceAndPath(GamepadHotbar.MODID, "hud/tap_hotbar_down");
    private static final ResourceLocation HOTBAR_8 = ResourceLocation.withDefaultNamespace("item/empty_slot_sword");
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace(
            "hud/hotbar_attack_indicator_background"
    );
    private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace(
            "hud/hotbar_attack_indicator_progress"
    );
    private static Vector2i iteratePos(int index, int scale, int arm) {
        Vector2i vec = new Vector2i(0, 0);
        switch (index) {
            case 1 -> {return vec.add(-41 + scale, -31);}
            case 2 -> {return vec.add(-20 + scale, -41);}
            case 3 -> {return vec.add(scale, -41);}
            case 4 -> {return vec.add(21 + scale, -31);}
            case 5 -> {return vec.add(21 + scale, -11);}
            case 6 -> {return vec.add(scale, 0);}
            case 7 -> {return vec.add(-20 + scale, 0);}
            case 8 -> {return vec.add(4 - arm - scale, -20);}
            default -> {return vec.add(-41 + scale, -11);}
        }
    }
    private static Pair<ResourceLocation, ResourceLocation> iterateSlotIcons(int index) {
        switch (index) {
            case 1 -> {return Pair.of(InventoryMenu.BLOCK_ATLAS, HOTBAR_1);}
            case 2 -> {return Pair.of(InventoryMenu.BLOCK_ATLAS, HOTBAR_2);}
            case 3 -> {return Pair.of(InventoryMenu.BLOCK_ATLAS, HOTBAR_3);}
            case 4 -> {return Pair.of(InventoryMenu.BLOCK_ATLAS, HOTBAR_4);}
            case 5 -> {return Pair.of(InventoryMenu.BLOCK_ATLAS, HOTBAR_5);}
            case 6 -> {return Pair.of(InventoryMenu.BLOCK_ATLAS, HOTBAR_6);}
            case 7 -> {return Pair.of(InventoryMenu.BLOCK_ATLAS, HOTBAR_7);}
            case 8 -> {return Pair.of(InventoryMenu.BLOCK_ATLAS, HOTBAR_8);}
            default -> {return Pair.of(InventoryMenu.BLOCK_ATLAS, HOTBAR_0);}
        }
    }
    private static void renderSlot(GuiGraphics guiGraphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            float f = (float)stack.getPopTime() - deltaTracker.getGameTimeDeltaPartialTick(false);
            if (f > 0.0F) {
                float f1 = 1.0F + f / 5.0F;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate((float)(x + 8), (float)(y + 12), 0.0F);
                guiGraphics.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                guiGraphics.pose().translate((float)(-(x + 8)), (float)(-(y + 12)), 0.0F);
            }

            guiGraphics.renderItem(player, stack, x, y, seed);
            if (f > 0.0F) {
                guiGraphics.pose().popPose();
            }

            guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
        }
    }
    private static boolean isHovering(Slot slot, int mouseX, int mouseY, int guiLeft, int guiTop) {
        int x = guiLeft + slot.x;
        int y = guiTop + slot.y;
        return mouseX >= x - 1 && mouseX < x + 16 + 1 &&
                mouseY >= y - 1 && mouseY < y + 16 + 1;
    }

    @SubscribeEvent
    public static void gamepadHotbarTicks(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if (GamepadHotbarClientConfig.GAMEPAD_HOTBAR_TOGGLE.isFalse() || player == null) return;

        int current = player.getInventory().selected;

        while (GamepadHotbarModBusEvents.HOTBAR_LEFT.consumeClick())
            player.getInventory().selected = current == 0 ? 1 : 0;
        while (GamepadHotbarModBusEvents.HOTBAR_UP.consumeClick())
            player.getInventory().selected = current == 2 ? 3 : 2;
        while (GamepadHotbarModBusEvents.HOTBAR_RIGHT.consumeClick())
            player.getInventory().selected = current == 5 ? 4 : 5;
        while (GamepadHotbarModBusEvents.HOTBAR_DOWN.consumeClick())
            player.getInventory().selected = current == 7 ? 6 : 7;
        while (GamepadHotbarModBusEvents.HOTBAR_WEAPON.consumeClick()) player.getInventory().selected = 8;
    }

    @SubscribeEvent
    public static void onRenderHotbar(RenderGuiLayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (GamepadHotbarClientConfig.GAMEPAD_HOTBAR_TOGGLE.isFalse() || mc.options.hideGui) return;

        Entity entity = mc.getCameraEntity();
        if (event.getName() == VanillaGuiLayers.HOTBAR && entity instanceof Player player) {
            // Vanilla hotbar is not rendered
            event.setCanceled(true);

            ItemStack offHand = player.getOffhandItem();
            HumanoidArm offArm = player.getMainArm().getOpposite();
            GuiGraphics guiGfx = event.getGuiGraphics();
            DeltaTracker parTick = event.getPartialTick();

            // Base variables
            int screenLeft = 43;
            int screenRight = guiGfx.guiWidth() - screenLeft;
            int flipVar1 = 1;
            int flipVar2 = 0;
            int scaleVar = GamepadHotbarClientConfig.PAD_X.get();

            // Configuration adjustments
            if (GamepadHotbarClientConfig.MIRROR_MODE.getAsBoolean()) {
                screenRight = screenLeft;
                screenLeft = guiGfx.guiWidth() - screenRight;
                scaleVar *= -1;
                flipVar1 *= -1;
                flipVar2 = 100;
            }
            if (GamepadHotbarClientConfig.SCALE_X.get() == HotbarScale.TYPE2) {
                screenLeft = (guiGfx.guiWidth() / 2) - (140 * flipVar1);
                screenRight = (guiGfx.guiWidth() / 2) + (140 * flipVar1);
            }
            if (GamepadHotbarClientConfig.SCALE_X.get() == HotbarScale.TYPE3) {
                screenLeft = (guiGfx.guiWidth() / 2) - (50 * flipVar1);
                screenRight = (guiGfx.guiWidth() / 2) + (50 * flipVar1);
            }
            if (GamepadHotbarClientConfig.SCALE_X.get() == HotbarScale.TYPE4) {
                screenLeft = 43 + flipVar2;
                screenRight = 143 - flipVar2;
            }
            if (GamepadHotbarClientConfig.SCALE_X.get() == HotbarScale.TYPE5) {
                screenLeft = guiGfx.guiWidth() - 143 + flipVar2;
                screenRight = guiGfx.guiWidth() - 43 - flipVar2;
            }
            int baseY = GamepadHotbarClientConfig.POS_Y.get() == HotbarPos.TOP ? 64 + GamepadHotbarClientConfig.PAD_Y.get() : guiGfx.guiHeight() - GamepadHotbarClientConfig.PAD_Y.get();

            // Adjustment variables
            int uWidth;
            int uPos;
            int xPos;
            int yPos;
            int slotY = baseY - 22;
            int selectY = slotY - 1;
            int itemY = slotY + 3;
            int baseX;
            int armVar = offArm == HumanoidArm.RIGHT ? 28 : 0;

            RenderSystem.enableBlend();
            guiGfx.pose().pushPose();
            guiGfx.pose().translate(0.0F, 0.0F, -90.0F);

            // Hotbar slots 1-9
            // Extra blitSprite calls are made here to fill out each slot pair with the outline portion of the hotbar texture
            for (int i = 0; i < 5; i++) {
                switch (i) {
                    case 1 -> {
                        //  X X
                        // .   .
                        // .   .         .
                        //  . .
                        uWidth = 40;
                        uPos = 41;
                        xPos = -20 + scaleVar;
                        yPos = -41;
                        baseX = screenLeft;
                        guiGfx.pose().translate(baseX, slotY, 0);
                        guiGfx.pose().mulPose(Axis.ZP.rotationDegrees(90));
                        guiGfx.pose().translate(-baseX, -slotY, 0);
                        guiGfx.blitSprite(HOTBAR_SPRITE, 182, 22, 0, 0, baseX + xPos - 1, slotY + yPos, 1, 22);
                        guiGfx.blitSprite(HOTBAR_SPRITE, 182, 22, 181, 0, baseX + xPos + uWidth, slotY + yPos, 1, 22);
                    }
                    case 2 -> {
                        //  . .
                        // .   X
                        // .   X         .
                        //  . .
                        uWidth = 40;
                        uPos = 81;
                        xPos = -30;
                        yPos = -42 - scaleVar;
                        baseX = screenLeft;
                        guiGfx.pose().translate(baseX, slotY, 0);
                        guiGfx.pose().mulPose(Axis.ZP.rotationDegrees(90));
                        guiGfx.pose().translate(-baseX, -slotY, 0);
                        guiGfx.blitSprite(HOTBAR_SPRITE, 182, 22, 0, 0, baseX + xPos - 1, slotY + yPos, 1, 22);
                        guiGfx.blitSprite(HOTBAR_SPRITE, 182, 22, 181, 0, baseX + xPos + uWidth, slotY + yPos, 1, 22);
                    }
                    case 3 -> {
                        //  . .
                        // .   .
                        // .   .         .
                        //  X X
                        uWidth = 40;
                        uPos = 121;
                        xPos = -20 + scaleVar;
                        yPos = 0;
                        baseX = screenLeft;
                        guiGfx.pose().translate(baseX, slotY, 0);
                        guiGfx.pose().mulPose(Axis.ZP.rotationDegrees(-90));
                        guiGfx.pose().translate(-baseX, -slotY, 0);
                        guiGfx.blitSprite(HOTBAR_SPRITE, 182, 22, 0, 0, baseX + xPos - 1, slotY + yPos, 1, 22);
                        guiGfx.blitSprite(HOTBAR_SPRITE, 182, 22, 181, 0, baseX + xPos + uWidth, slotY + yPos, 1, 22);
                    }
                    case 4 -> {
                        //  . .
                        // .   .
                        // .   .         X
                        //  . .
                        uWidth = 21;
                        uPos = 161;
                        xPos = 4 - armVar - scaleVar;
                        yPos = -20;
                        baseX = screenRight;
                        guiGfx.blitSprite(HOTBAR_SPRITE, 182, 22, 0, 0, baseX + xPos - 1, slotY + yPos, 1, 22);
                    }
                    default -> {
                        //  . .
                        // X   .
                        // X   .         .
                        //  . .
                        uPos = 0;
                        uWidth = 41;
                        xPos = -11;
                        yPos = -42 + scaleVar;
                        baseX = screenLeft;
                        guiGfx.pose().translate(baseX, slotY, 0);
                        guiGfx.pose().mulPose(Axis.ZP.rotationDegrees(-90));
                        guiGfx.pose().translate(-baseX, -slotY, 0);
                        guiGfx.blitSprite(HOTBAR_SPRITE, 182, 22, 181, 0, baseX + xPos + uWidth, slotY + yPos, 1, 22);
                    }
                }
                guiGfx.blitSprite(HOTBAR_SPRITE, 182, 22,
                        uPos, 0, baseX + xPos, slotY + yPos, uWidth, 22
                );
            }

            // Selected hotbar slot
            int selected = player.getInventory().selected;
            if (selected >= 0 && selected < 8) baseX = screenLeft;
            else baseX = screenRight;

            int selectX = baseX - 2;
            xPos = iteratePos(selected, scaleVar, armVar).x;
            yPos = iteratePos(selected, scaleVar, armVar).y;
            guiGfx.blitSprite(HOTBAR_SELECTION_SPRITE, selectX + xPos, selectY + yPos, 24, 23);

            // Off-hand hotbar slot
            if (!offHand.isEmpty()) {
                if (offArm == HumanoidArm.LEFT) {
                    guiGfx.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, screenRight + 4 - 29 - scaleVar, slotY - 21, 29, 24);
                } else {
                    guiGfx.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, screenRight + 4 - armVar + 20 - scaleVar, slotY - 21, 29, 24);
                }
            }

            guiGfx.pose().popPose();
            RenderSystem.disableBlend();

            // Custom tap button indicator
            guiGfx.pose().pushPose();
            ResourceLocation tap = null;
            int tapOffset = 8;
            boolean keyFlag = false;
            if (GamepadHotbarModBusEvents.HOTBAR_LEFT.isDown() || GamepadHotbarModBusEvents.HOTBAR_RIGHT.isDown() || GamepadHotbarModBusEvents.HOTBAR_UP.isDown() || GamepadHotbarModBusEvents.HOTBAR_DOWN.isDown()) {
                guiGfx.setColor(1.0F, 0.8F, 0.0F, 1.0F);
                keyFlag = true;
            }
            if (selected == 0 || selected == 1) {
                tap = TAP_HOTBAR_LEFT;
                tapOffset = 17;
            }
            if (selected == 2 || selected == 3) {
                tap = TAP_HOTBAR_UP;
            }
            if (selected == 4 || selected == 5) {
                tap = TAP_HOTBAR_RIGHT;
                tapOffset = -1;
            }
            if (selected == 6 || selected == 7) {
                tap = TAP_HOTBAR_DOWN;
            }
            if (selected >= 0 && selected < 8) {
                guiGfx.blitSprite(tap, baseX - tapOffset + scaleVar, slotY - 18, 16, 16);
            }
            if (keyFlag) guiGfx.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGfx.pose().popPose();

            // Items
            int itemX;
            int l = 1;
            for (int j = 0; j < 9; j++) {
                xPos = iteratePos(j, scaleVar, armVar).x;
                yPos = iteratePos(j, scaleVar, armVar).y;
                if (j < 8) {
                    itemX = screenLeft + 2;
                } else itemX = screenRight + 2;
                renderSlot(guiGfx, itemX + xPos, itemY + yPos, parTick, player, player.getInventory().items.get(j), l++);
            }

            // Off-hand slot
            if (!offHand.isEmpty()) {
                itemX = screenRight + 2;
                renderSlot(guiGfx, itemX - 24 + armVar - scaleVar, itemY - 20, parTick, player, offHand, l);
            }

            // Hotbar attack indicator
            if (mc.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                RenderSystem.enableBlend();
                float f = player.getAttackStrengthScale(0.0F);
                if (f < 1.0F) {
                    int j2 = baseY - 62;
                    int k2 = screenRight + 5 - armVar - scaleVar;

                    int l1 = (int)(f * 19.0F);
                    guiGfx.blitSprite(HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, k2, j2, 18, 18);
                    guiGfx.blitSprite(HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - l1, k2, j2 + 18 - l1, 18, l1);
                }

                RenderSystem.disableBlend();
            }
        }
    }

    @SubscribeEvent
    public static void inventoryScreenIcons(ContainerScreenEvent.Render.Foreground event) {
        AbstractContainerScreen<?> screen = event.getContainerScreen();
        if (GamepadHotbarClientConfig.GAMEPAD_HOTBAR_TOGGLE.isFalse() || screen.getMenu().getCarried().isEmpty()) return;

        GuiGraphics guiGfx = event.getGuiGraphics();
        Vector2i vec = new Vector2i(event.getMouseX(), event.getMouseY());

        // Ensures slot icons are always occupying the same slots on each screen
        int adjuster = 0;
        if (screen instanceof InventoryScreen) adjuster = 1;
        if (screen instanceof CreativeModeInventoryScreen creativeScreen)
            if (creativeScreen.isInventoryOpen()) adjuster = 2;

        guiGfx.pose().pushPose();
        guiGfx.pose().translate(0.0F, 0.0F, 100.0F);
        int slotCount = screen.getMenu().slots.size();

        for (int i = 0; i < 9; i++) {
            Slot slot = screen.getMenu().getSlot(i + slotCount - 9 - adjuster);
            if (slot.hasItem()) continue;

            Pair<ResourceLocation, ResourceLocation> pair = iterateSlotIcons(i);
            TextureAtlasSprite texAtlas = Minecraft.getInstance().getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
            if (isHovering(slot, vec.x, vec.y, screen.getGuiLeft(), screen.getGuiTop()))
                guiGfx.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, FastColor.ABGR32.fromArgb32(1090519040));

            guiGfx.blit(slot.x, slot.y, 0, 16, 16, texAtlas);
            if (isHovering(slot, vec.x, vec.y, screen.getGuiLeft(), screen.getGuiTop()))
                guiGfx.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, FastColor.ABGR32.fromArgb32(-2130706433));
        }
        guiGfx.pose().popPose();
    }
}

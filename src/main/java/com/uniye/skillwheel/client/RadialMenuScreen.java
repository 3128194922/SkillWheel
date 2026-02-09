package com.uniye.skillwheel.client;

import com.uniye.skillwheel.Network;
import com.uniye.skillwheel.util.ItemSources;
import com.uniye.skillwheel.util.SelectableItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class RadialMenuScreen extends Screen {
    private final List<SelectableItem> all = new ArrayList<>();
    private int page = 0;
    private long openTime;
    private int hovered = -1;
    private int selecting = -1;
    private long selectingTime = 0L;
    private boolean sent = false;
    private int lastHovered = -1;
    private long hoverStart = 0L;

    public RadialMenuScreen() {
        super(Component.translatable("screen.skillwheel.radial"));
    }

    @Override
    protected void init() {
        Player p = Minecraft.getInstance().player;
        all.clear();
        if (p != null) all.addAll(ItemSources.getDisplayEntries(p));
        openTime = System.nanoTime();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private List<SelectableItem> pageItems() {
        int start = page * 6;
        int end = Math.min(start + 6, all.size());
        return all.subList(start, end);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
        int cx = this.width / 2;
        int cy = this.height / 2;
        float t = Math.min(1f, (System.nanoTime() - openTime) / 5_000_000f);
        float radius = 60f + 40f * t;
        List<SelectableItem> items = pageItems();
        hovered = -1;
        float inner = radius - 24f;
        float outer = radius + 24f;
        float per = (float) (Math.PI * 2 / 6);
        float gap = 0.035f;
        for (int i = 0; i < 6; i++) {
            float a0 = per * i - (float) Math.PI / 2 + gap;
            float a1 = per * (i + 1) - (float) Math.PI / 2 - gap;
            int base = 0x55000000;
            int hi = 0x88FFFFFF;
            drawRingSegment(g, cx, cy, inner, outer, a0, a1, base);
            if (i < items.size()) {
                boolean h = inSector(mx - cx, my - cy, a0, a1, inner, outer);
                if (h) {
                    hovered = i;
                    drawRingSegment(g, cx, cy, inner, outer, a0, a1, hi);
                }
            }
        }
        if (hovered != lastHovered) {
            hoverStart = System.nanoTime();
            lastHovered = hovered;
        }
        for (int i = 0; i < 6; i++) {
            float a0 = per * i - (float) Math.PI / 2 + gap;
            float a1 = per * (i + 1) - (float) Math.PI / 2 - gap;
            float mid = (a0 + a1) * 0.5f;
            float ir = (inner + outer) * 0.5f;
            int ix = cx + (int) (Math.cos(mid) * ir);
            int iy = cy + (int) (Math.sin(mid) * ir);
            if (i < items.size()) {
                ItemStack s = items.get(i).stack;
                float scale = 1.0f;
                if (i == hovered) {
                    float dt = Math.min(1f, (System.nanoTime() - hoverStart) / 150_000_000f);
                    scale = 1.0f + 0.15f * dt;
                }
                if (i == selecting) {
                    float dt = Math.min(1f, (System.nanoTime() - selectingTime) / 150_000_000f);
                    scale = 1.0f + dt * 0.5f;
                    if (dt >= 1f && sent) onClose();
                }
                g.pose().pushPose();
                g.pose().translate(ix, iy, 0);
                g.pose().scale(scale, scale, 1f);
                g.renderItem(s, -8, -8);
                g.pose().popPose();
                String name = s.getHoverName().getString();
                float pct = cooldownPercent(s, pt);
                String pctText = Mth.clamp((int) ((1f - pct) * 100f), 0, 100) + "%";
                g.drawString(this.font, pctText, ix - this.font.width(pctText) / 2, iy + 12, 0xFFFFFF, false);
                g.drawString(this.font, name, ix - this.font.width(name) / 2, iy - 22, 0xFFFFFF, true);
            }
        }
        String pageText = (all.isEmpty() ? "0/0" : ((page + 1) + "/" + ((all.size() + 5) / 6)));
        g.drawString(this.font, pageText, cx - this.font.width(pageText) / 2, cy + (int) (radius + 20), 0xFFFFFF, false);
    }

    private float cooldownPercent(ItemStack s, float pt) {
        Player p = Minecraft.getInstance().player;
        if (p == null) return 0f;
        return p.getCooldowns().getCooldownPercent(s.getItem(), pt);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 1) {
            if (all.size() > 6) {
                page = (page + 1) % ((all.size() + 5) / 6);
                return true;
            }
            return false;
        }
        if (button == 0) {
            List<SelectableItem> items = pageItems();
            if (hovered >= 0 && hovered < items.size()) {
                Player p = Minecraft.getInstance().player;
                if (p != null) {
                    Network.sendSelect(p, items.get(hovered));
                    sent = true;
                    selecting = hovered;
                    selectingTime = System.nanoTime();
                }
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    private void drawRingSegment(GuiGraphics g, int cx, int cy, float inner, float outer, float a0, float a1, int color) {
        Matrix4f pose = g.pose().last().pose();
        Tesselator t = Tesselator.getInstance();
        BufferBuilder bb = t.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bb.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        int steps = 40;
        float da = (a1 - a0) / steps;
        int r = (color >> 16) & 0xFF;
        int gcol = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        for (int i = 0; i <= steps; i++) {
            float ang = a0 + da * i;
            float cos = (float) Math.cos(ang);
            float sin = (float) Math.sin(ang);
            float ox = cx + cos * outer;
            float oy = cy + sin * outer;
            float ix = cx + cos * inner;
            float iy = cy + sin * inner;
            bb.vertex(pose, ox, oy, 0).color(r, gcol, b, a).endVertex();
            bb.vertex(pose, ix, iy, 0).color(r, gcol, b, a).endVertex();
        }
        t.end();
        RenderSystem.disableBlend();
    }

    private boolean inSector(double dx, double dy, float a0, float a1, float inner, float outer) {
        double ang = Math.atan2(dy, dx);
        if (ang < -Math.PI) ang += Math.PI * 2;
        if (ang > Math.PI) ang -= Math.PI * 2;
        float s0 = a0;
        float s1 = a1;
        if (s1 < s0) {
            boolean inAng = ang >= s0 || ang <= s1;
            double r = Math.sqrt(dx * dx + dy * dy);
            return inAng && r >= inner && r <= outer;
        } else {
            boolean inAng = ang >= s0 && ang <= s1;
            double r = Math.sqrt(dx * dx + dy * dy);
            return inAng && r >= inner && r <= outer;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyBindings.OPEN != null && KeyBindings.OPEN.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

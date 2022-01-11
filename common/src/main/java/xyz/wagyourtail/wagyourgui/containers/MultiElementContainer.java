package xyz.wagyourtail.wagyourgui.containers;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiElementContainer<T extends IContainerParent> extends Gui implements IContainerParent {
    protected List<GuiButton> buttons = new ArrayList<>();
    protected FontRenderer textRenderer;
    protected boolean visible = true;
    public final T parent;
    public int x;
    public int y;
    public int width;
    public int height;
    
    public MultiElementContainer(int x, int y, int width, int height, FontRenderer textRenderer, T parent) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
    }
    
    public void init() {
        buttons.clear();
    }
    

    public boolean getVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        for (GuiButton btn : buttons) {
            btn.visible = visible;
            btn.active = visible;
        }
        this.visible = visible;
    }
    
    @Override
    public <T extends GuiButton> T addButton(T btn) {
        buttons.add(btn);
        parent.addButton(btn);
        return btn;
    }
    
    public List<GuiButton> getButtons() {
        return buttons;
    }
    
    public void setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void openOverlay(OverlayContainer overlay) {
        parent.openOverlay(overlay);
    }
    
    @Override
    public void openOverlay(OverlayContainer overlay, boolean disableButtons) {
        parent.openOverlay(overlay, disableButtons);
    }
    
    @Override
    public void removeButton(GuiButton button) {
        this.buttons.remove(button);
        parent.removeButton(button);
    }
    
    @Override
    public IOverlayParent getFirstOverlayParent() {
        return parent.getFirstOverlayParent();
    }
    
    public abstract void render(int mouseX, int mouseY, float delta);
    
}

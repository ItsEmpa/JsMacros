package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Draw2D} is cool
 *
 *   @author Wagyourtail
 *
 * @since 1.0.6
 *
 */
 @SuppressWarnings("unused")
public class Draw3D {
    private final List<Box> boxes = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();

    /**
     * @since 1.0.6
     *
     * @return
     */
    public List<Box> getBoxes() {
        return ImmutableList.copyOf(boxes);
    }

    /**
     * @since 1.0.6
     *
     * @return
     */
    public List<Line> getLines() {
        return ImmutableList.copyOf(lines);
    }

    /**
     * @since 1.0.6
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param fillColor
     * @param fill
     * @return The {@link Box} you added.
     */
    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill) {
        return addBox(x1, y1, z1, x2, y2, z2, color, fillColor, fill, false);
    }

    /**
    * @since 1.3.1
    *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param fillColor
     * @param fill
     * @param cull
     *
     * @return
     */
    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill, boolean cull) {
        Box b = new Box(x1, y1, z1, x2, y2, z2, color, fillColor, fill, cull);
        synchronized (boxes) {
            boxes.add(b);
        }
        return b;
    }

    /**
     * @since 1.1.8
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param alpha
     * @param fillColor
     * @param fillAlpha
     * @param fill
     * @return the {@link Box} you added.
     */
    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill) {
        return addBox(x1, y1, z1, x2, y2, z2, color, alpha, fillColor, fillAlpha, fill, false);
    }


    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill, boolean cull) {
        Box b = new Box(x1, y1, z1, x2, y2, z2, color, alpha, fillColor, fillAlpha, fill, cull);
        synchronized (boxes) {
            boxes.add(b);
        }
        return b;
    }

    /**
     * @since 1.0.6
     *
     * @param b
     * @return
     */
    public Draw3D removeBox(Box b) {
        synchronized (boxes) {
            boxes.remove(b);
        }
        return this;
    }


    /**
     * @since 1.0.6
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @return the {@link Line} you added.
     */
    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        return addLine(x1, y1, z1, x2, y2, z2, color, false);
    }


    /**
    *
    * @since 1.3.1
    *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param cull
     *
     * @return
     */
    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, boolean cull) {
        Line l = new Line(x1, y1, z1, x2, y2, z2, color, cull);
        synchronized (lines) {
            lines.add(l);
        }
        return l;
    }

    /**
     * @since 1.1.8
     *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param alpha
     * @return the {@link Line} you added.
     */

    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha) {
        return addLine(x1, y1, z1, x2, y2, z2, color, alpha, false);
    }

    /**
    *
    * @since 1.3.1
    *
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param alpha
     * @param cull
     *
     * @return
     */
    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, boolean cull) {
        Line l = new Line(x1, y1, z1, x2, y2, z2, color, alpha, cull);
        synchronized (lines) {
            lines.add(l);
        }
        return l;
    }

    /**
     * @since 1.0.6
     *
     * @param l
     * @return
     */
    public Draw3D removeLine(Line l) {
        synchronized (lines) {
            lines.remove(l);
        }
        return this;
    }

    /**
     * Draws a cube({@link Box}) with a specific radius({@code side length = 2*radius})
     *
     * @param point  the center point
     * @param radius 1/2 of the side length of the cube
     * @param color  point color
     * @return the {@link Box} generated, and visualized
     * @see Draw3D.Box
     * @since 1.4.0
     */
    public Box addPoint(PositionCommon.Pos3D point, double radius, int color) {
        return addPoint(point.getX(), point.getY(), point.getZ(), radius, color);
    }

    /**
     * Draws a cube({@link Box}) with a specific radius({@code side length = 2*radius})
     *
     * @param x      x value of the center point
     * @param y      y value of the center point
     * @param z      z value of the center point
     * @param radius 1/2 of the side length of the cube
     * @param color  point color
     * @return the {@link Box} generated, and visualized
     * @see Draw3D.Box
     * @since 1.4.0
     */
    public Box addPoint(double x, double y, double z, double radius, int color) {
        return addBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius, color, color, true, false);
    }

    /**
     * Draws a cube({@link Box}) with a specific radius({@code side length = 2*radius})
     *
     * @param x      x value of the center point
     * @param y      y value of the center point
     * @param z      z value of the center point
     * @param radius 1/2 of the side length of the cube
     * @param color  point color
     * @param alpha  alpha of the point
     * @param cull   whether to cull the point or not
     * @return the {@link Box} generated, and visualized
     * @see Draw3D.Box
     * @since 1.4.0
     */
    public Box addPoint(double x, double y, double z, double radius, int color, int alpha, boolean cull) {
        return addBox(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius, color, color, alpha, alpha, true, cull);
    }


    /**
     * register so it actually shows up
     * @since 1.6.5
     * @return self for chaining
     */
    public Draw3D register() {
        FHud.renders.add(this);
        return this;
    }

    public Draw3D unregister() {
        FHud.renders.remove(this);
        return this;
    }


    public void render() {
        Minecraft mc  = Minecraft.getInstance();

        // setup
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glLineWidth(2.5F);
        GlStateManager.disableTexture();
        GlStateManager.matrixMode(5889);

        GlStateManager.pushMatrix();

        // offsetRender
        RenderManager camera = mc.getEntityRenderManager();
        PositionCommon.Pos3D camPos = new PositionCommon.Pos3D(camera.field_78730_l, camera.field_78731_m, camera.field_78728_n);

        //render
        synchronized (boxes) {
            for (Box b : boxes) {
                b.render(camPos);
            }
        }

        synchronized (lines) {
            for (Line l : lines) {
                l.render(camPos);
            }
        }

        GlStateManager.popMatrix();

        // reset
        GlStateManager.matrixMode(5888);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public static class Box {
        public PositionCommon.Vec3D pos;
        public int color;
        public int fillColor;
        public boolean fill;
        public boolean cull;

        public Box(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill, boolean cull) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color);
            setFillColor(fillColor);
            this.fill = fill;
            this.cull = cull;
        }

        public Box(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill, boolean cull) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color, alpha);
            setFillColor(fillColor, fillAlpha);
            this.fill = fill;
            this.cull = cull;
        }

        /**
         * @since 1.0.6
         *
         * @param x1
         * @param y1
         * @param z1
         * @param x2
         * @param y2
         * @param z2
         */
        public void setPos(double x1, double y1, double z1, double x2, double y2, double z2) {
            pos = new PositionCommon.Vec3D(x1, y1, z1, x2, y2, z2);
        }



        /**
         * @since 1.0.6
         *
         * @param color
         */
        public void setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
        }

        /**
         * @since 1.0.6
         *
         * @param fillColor
         */
        public void setFillColor(int fillColor) {
            this.fillColor = fillColor;
        }

        /**
         * @since 1.1.8
         *
         * @param color
         * @param alpha
         */
        public void setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
        }

        /**
         * @since 1.1.8
         *
         * @param alpha
         */
        public void setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
        }

        /**
         * @since 1.1.8
         *
         * @param fillColor
         * @param alpha
         */
        public void setFillColor(int fillColor, int alpha) {
            this.fillColor = fillColor | (alpha << 24);
        }

        /**
         * @since 1.1.8
         *
         * @param alpha
         */
        public void setFillAlpha(int alpha) {
            this.fillColor = (fillColor & 0xFFFFFF) | (alpha << 24);
        }

        /**
         * @since 1.0.6
         *
         * @param fill
         */
        public void setFill(boolean fill) {
            this.fill = fill;
        }

        public void render(PositionCommon.Pos3D camPos) {
            final boolean cull = !this.cull;
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            float x1 = (float) (pos.x1 - camPos.x);
            float y1 = (float) (pos.y1 - camPos.y);
            float z1 = (float) (pos.z1 - camPos.z);
            float x2 = (float) (pos.x2 - camPos.x);
            float y2 = (float) (pos.y2 - camPos.y);
            float z2 = (float) (pos.z2 - camPos.z);

            if (cull) GlStateManager.disableDepthTest();
            
            Tessellator tess = Tessellator.getInstance();
            WorldRenderer buf = tess.getBuffer();
        
            if (this.fill) {
                float fa = ((fillColor >> 24) & 0xFF)/255F;
                float fr = ((fillColor >> 16) & 0xFF)/255F;
                float fg = ((fillColor >> 8) & 0xFF)/255F;
                float fb = (fillColor & 0xFF)/255F;
                
                //1.15+ culls insides
                GlStateManager.disableCull();

                buf.begin(GL11.GL_TRIANGLE_STRIP,  DefaultVertexFormats.POSITION_COLOR);

                //draw a cube using triangle strips
                buf.vertex(x1, y2, z2).color(fr, fg, fb, fa).next(); // Front-top-left
                buf.vertex(x2, y2, z2).color(fr, fg, fb, fa).next(); // Front-top-right
                buf.vertex(x1, y1, z2).color(fr, fg, fb, fa).next(); // Front-bottom-left
                buf.vertex(x2, y1, z2).color(fr, fg, fb, fa).next(); // Front-bottom-right
                buf.vertex(x2, y1, z1).color(fr, fg, fb, fa).next(); // Front-bottom-left
                buf.vertex(x2, y2, z2).color(fr, fg, fb, fa).next(); // Front-top-right
                buf.vertex(x2, y2, z1).color(fr, fg, fb, fa).next(); // Back-top-right
                buf.vertex(x1, y2, z2).color(fr, fg, fb, fa).next(); // Front-top-left
                buf.vertex(x1, y2, z1).color(fr, fg, fb, fa).next(); // Back-top-left
                buf.vertex(x1, y1, z2).color(fr, fg, fb, fa).next(); // Front-bottom-left
                buf.vertex(x1, y1, z1).color(fr, fg, fb, fa).next(); // Back-bottom-left
                buf.vertex(x2, y1, z1).color(fr, fg, fb, fa).next(); // Back-bottom-right
                buf.vertex(x1, y2, z1).color(fr, fg, fb, fa).next(); // Back-top-left
                buf.vertex(x2, y2, z1).color(fr, fg, fb, fa).next(); // Back-top-right

                tess.draw();
                
                GlStateManager.enableCull();
            }

            GL11.glLineWidth(2.5F);
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

            buf.vertex(x1, y1, z1).color(r, g, b, a).next();
            buf.vertex(x1, y1, z2).color(r, g, b, a).next();

            buf.vertex(x2, y1, z2).color(r, g, b, a).next();

            buf.vertex(x2, y1, z1).color(r, g, b, a).next();

            buf.vertex(x1, y1, z1).color(r, g, b, a).next();

            buf.vertex(x1, y2, z1).color(r, g, b, a).next();

            buf.vertex(x1, y2, z2).color(r, g, b, a).next();

            buf.vertex(x2, y2, z2).color(r, g, b, a).next();

            buf.vertex(x2, y2, z1).color(r, g, b, a).next();

            buf.vertex(x1, y2, z1).color(r, g, b, a).next();

            buf.vertex(x1, y2, z1).color(r, g, b, 0).next();
            buf.vertex(x2, y1, z1).color(r, g, b, 0).next();

            buf.vertex(x2, y1, z1).color(r, g, b, a).next();
            buf.vertex(x2, y2, z1).color(r, g, b, a).next();

            buf.vertex(x2, y2, z1).color(r, g, b, 0).next();
            buf.vertex(x1, y1, z2).color(r, g, b, 0).next();

            buf.vertex(x1, y1, z2).color(r, g, b, a).next();
            buf.vertex(x1, y2, z2).color(r, g, b, a).next();

            buf.vertex(x1, y2, z2).color(r, g, b, 0).next();
            buf.vertex(x2, y1, z2).color(r, g, b, 0).next();

            buf.vertex(x2, y1, z2).color(r, g, b, a).next();
            buf.vertex(x2, y2, z2).color(r, g, b, a).next();

            tess.draw();

            if (cull) GlStateManager.enableDepthTest();
        }
        
        private static void drawBox(WorldRenderer buffer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
            buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
            buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        }
        
    }
    
    public static class Line {
        public PositionCommon.Vec3D pos;
        public int color;
        public boolean cull;
        public Line(double x1, double y1, double z1, double x2, double y2, double z2, int color, boolean cull) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color);
            this.cull = cull;
        }
        
        public Line(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, boolean cull) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color, alpha);
            this.cull = cull;
        }
                
        /**
         * @since 1.0.6
         * 
         * @param x1
         * @param y1
         * @param z1
         * @param x2
         * @param y2
         * @param z2
         */
        public void setPos(double x1, double y1, double z1, double x2, double y2, double z2) {
            pos = new PositionCommon.Vec3D(x1, y1, z1, x2, y2, z2);
        }
        
        /**
         * @since 1.0.6
         * 
         * @param color
         */
        public void setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
        }
        
        /**
         * @since 1.1.8
         * 
         * @param color
         * @param alpha
         */
        public void setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
        }
        
        /**
         * @since 1.1.8
         * 
         * @param alpha
         */
        public void setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
        }
    
        public void render(PositionCommon.Pos3D camPos) {
            final boolean cull = !this.cull;
            if (cull) GlStateManager.disableDepthTest();
        
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            Tessellator tess = Tessellator.getInstance();
            WorldRenderer buf = tess.getBuffer();
            buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            buf.vertex(pos.x1 - camPos.x, pos.y1 - camPos.y, pos.z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(pos.x1 - camPos.x, pos.y1 - camPos.y, pos.z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(pos.x2 - camPos.x, pos.y2 - camPos.y, pos.z2 - camPos.z).color(r, g, b, a).next();
            tess.draw();
            
            if (cull) GlStateManager.enableDepthTest();
        }
    }
}

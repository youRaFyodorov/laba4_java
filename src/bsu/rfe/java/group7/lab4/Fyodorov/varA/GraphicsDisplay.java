package bsu.rfe.java.group7.lab4.Fyodorov.varA;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import javax.swing.*;

import static java.lang.Math.abs;

public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;

    private boolean showAxis = true;
    private boolean showMarkers = true;
    // Границы диапазона пространства,
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double scale;
    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;

    private Font axisFont;

    public GraphicsDisplay() {
        setBackground(Color.WHITE);
// Сконструировать необходимые объекты, используемые в рисовании
        graphicsStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, new float[]{9,6,6,9,3}, 0.0f);

        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    public void showGraphics(Double[][] graphicsData) {

        this.graphicsData = graphicsData;

        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (graphicsData == null || graphicsData.length == 0) return;

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }

        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);

        if (scale == scaleX) {

            double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;

            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {

            double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;

            maxX += xIncrement;
            minX -= xIncrement;
        }

        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis) paintAxis(canvas);
        paintGraphics(canvas);
        if (showMarkers) paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintGraphics(Graphics2D canvas) {

        canvas.setStroke(graphicsStroke);

        canvas.setColor(Color.RED);

        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {

            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]  + 2);
            if (i > 0) {

                graphics.lineTo(point.getX(), point.getY());
            } else {

                graphics.moveTo(point.getX(), point.getY());
            }
        }

        canvas.draw(graphics);
    }


    protected void paintMarkers(Graphics2D canvas) {
        double res = 0;
        int k = 0;

        for (Double[] point : graphicsData) {
            res += point[1];
            k++;
        }
        for (Double[] point : graphicsData) {

            double znachenie = point[1];

            if (znachenie < (res * 2 / k)) {
                canvas.setColor(Color.RED);
                canvas.setPaint(Color.RED);
            } else {
                canvas.setColor(Color.BLUE);
                canvas.setPaint(Color.BLUE);
            }
            canvas.setStroke(markerStroke);
            GeneralPath path = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0], point[1] + 2);
            canvas.draw(new Line2D.Double(shiftPoint(center, -8, 0), shiftPoint(center, 8, 0)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, 8), shiftPoint(center, 0, -8)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 8, 8), shiftPoint(center, -8, -8)));
            canvas.draw(new Line2D.Double(shiftPoint(center, -8, 8), shiftPoint(center, 8, -8)));
            Point2D.Double corner = shiftPoint(center, 3, 3);
        }
    }

    protected void paintAxis(Graphics2D canvas) {

        canvas.setStroke(axisStroke);

        canvas.setColor(Color.BLACK);

        canvas.setPaint(Color.BLACK);

        canvas.setFont(axisFont);

        FontRenderContext context = canvas.getFontRenderContext();

        if (minX <= 0.0 && maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY  - 2), xyToPoint(0, minY)));

            GeneralPath arrow = new GeneralPath();

            Point2D.Double lineEnd = xyToPoint(0, maxY - 2);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());

            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);

            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());


            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);

            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));

        }

        if (maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 2), xyToPoint(maxX,  2)));

            GeneralPath arrow = new GeneralPath();

            Point2D.Double lineEnd = xyToPoint(maxX, 2);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());

            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);

            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);


            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);

            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));

        }
    }
    // смещение X Y
    protected Point2D.Double xyToPoint(double x, double y) {

        double deltaX = x - minX;
        double deltaY = maxY - y;

        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {

        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);

        return dest;
    }
}
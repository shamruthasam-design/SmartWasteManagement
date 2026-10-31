package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.util.Map;

/** Creates JFreeChart charts that match the dark theme. */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class ChartStyler {
    private ChartStyler() { }

    public static ChartPanel pie(Map<String, Double> data) {
        DefaultPieDataset ds = new DefaultPieDataset();
        for (Map.Entry<String, Double> e : data.entrySet()) ds.setValue(e.getKey(), e.getValue());
        JFreeChart chart = ChartFactory.createPieChart(null, ds, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Theme.CARD);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setLabelGenerator(null);
        plot.setSectionOutlinesVisible(false);
        plot.setInteriorGap(0.04);
        for (String k : data.keySet()) plot.setSectionPaint(k, Theme.categoryColor(k));
        return wrap(chart);
    }

    public static ChartPanel bar(Map<String, ? extends Number> data, String axisLabel, Color color) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (Map.Entry<String, ? extends Number> e : data.entrySet()) ds.addValue(e.getValue(), "Series", e.getKey());
        JFreeChart chart = ChartFactory.createBarChart(null, null, axisLabel, ds, PlotOrientation.VERTICAL, false, true, false);
        CategoryPlot plot = chart.getCategoryPlot();
        stylePlot(plot);
        BarRenderer r = (BarRenderer) plot.getRenderer();
        r.setBarPainter(new StandardBarPainter());
        r.setShadowVisible(false);
        r.setSeriesPaint(0, color);
        r.setMaximumBarWidth(0.12);
        return wrap(chart);
    }

    public static ChartPanel line(Map<String, Double> data, String axisLabel, Color color) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> e : data.entrySet()) ds.addValue(e.getValue(), "Series", e.getKey());
        JFreeChart chart = ChartFactory.createLineChart(null, null, axisLabel, ds, PlotOrientation.VERTICAL, false, true, false);
        CategoryPlot plot = chart.getCategoryPlot();
        stylePlot(plot);
        LineAndShapeRenderer r = (LineAndShapeRenderer) plot.getRenderer();
        r.setSeriesPaint(0, color);
        r.setSeriesStroke(0, new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        r.setSeriesShapesVisible(0, true);
        return wrap(chart);
    }

    private static void stylePlot(CategoryPlot plot) {
        plot.setBackgroundPaint(Theme.CARD);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(Theme.BORDER);
        plot.setDomainGridlinesVisible(false);
        styleAxis(plot.getDomainAxis());
        styleAxis(plot.getRangeAxis());
    }

    private static void styleAxis(Axis a) {
        a.setTickLabelPaint(Theme.MUTED);
        a.setLabelPaint(Theme.MUTED);
        a.setAxisLinePaint(Theme.BORDER);
        a.setTickMarkPaint(Theme.BORDER);
        a.setTickLabelFont(Theme.font(Font.PLAIN, 11f));
    }

    private static ChartPanel wrap(JFreeChart chart) {
        chart.setBackgroundPaint(Theme.CARD);
        chart.setBorderVisible(false);
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(Theme.CARD);
            chart.getLegend().setItemPaint(Theme.TEXT);
            chart.getLegend().setFrame(BlockBorder.NONE);
            chart.getLegend().setItemFont(Theme.font(Font.PLAIN, 12f));
        }
        ChartPanel panel = new ChartPanel(chart);
        panel.setBackground(Theme.CARD);
        panel.setBorder(null);
        panel.setPopupMenu(null);
        panel.setMouseZoomable(false);
        panel.setMinimumDrawWidth(50);
        panel.setMinimumDrawHeight(50);
        panel.setMaximumDrawWidth(4000);
        panel.setMaximumDrawHeight(3000);
        return panel;
    }
}

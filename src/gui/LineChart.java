package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart extends JFrame{
	public enum FitnessType{AVG, MAX, NONNORMALIZED};	
	private int currentIndex;
	private JFreeChart chart;
	private XYSeriesCollection  dataset;
	private XYSeries seriesAvg;
	private XYSeries seriesMax;
    private XYSeries seriesNonNormalized;
    private ChartPanel panel;
	
	public LineChart() {
		super("Learning Progress Chart");
		currentIndex = 1;
		seriesAvg = new XYSeries("Avg");
		seriesMax = new XYSeries("Max");
		seriesNonNormalized = new XYSeries("Non Normalized");
		dataset = new XYSeriesCollection ();
		dataset.addSeries(seriesAvg);
		dataset.addSeries(seriesMax);
		dataset.addSeries(seriesNonNormalized);
	    chart = ChartFactory.createXYLineChart("Learning progress", "Epochs", "Fitness", dataset,
			 	PlotOrientation.VERTICAL, true, false, false);	
	    chart.setTextAntiAlias(true);
	    chart.setAntiAlias(true);
	    chart.setBackgroundPaint(Color.WHITE);
	    panel = new ChartPanel(chart);
	    panel.setBackground(Color.WHITE);
	    panel.setPreferredSize(new Dimension(600,400));
	    add(panel);
	    pack();
	    setDefaultCloseOperation(HIDE_ON_CLOSE);
	    setVisible(true);
	}
	//json should save this values in order to reload them in the next run with reloadChart
	public double[][] getSeries(FitnessType type) {
		switch(type) {
		case AVG:
			return seriesAvg.toArray();
		case MAX:
			return seriesMax.toArray();
		case NONNORMALIZED:
			return seriesNonNormalized.toArray();
		}
		//TODO fail
		return null;
	}
	//maybe needed
	public double getBiggestFitness(FitnessType type) {
		switch(type) {
		case AVG:
			return seriesAvg.getMaxY();
		case MAX:
			return seriesMax.getMaxY();
		case NONNORMALIZED:
			return seriesNonNormalized.getMaxY();
		}
		//TODO fail
		return 0;
	}
	public void clearDataSet() {
		seriesAvg.clear();
		seriesMax.clear();
		seriesNonNormalized.clear();
		currentIndex = 1;
	}
	public void reloadChart(double[] avg, double[] max, double[] normalized, int epochs) {
		clearDataSet();
		for(int i = 0; i < avg.length; i++) {
			addFitness(avg[i], FitnessType.AVG);			
		}
		for(int i = 0; i < max.length; i++) {
			addFitness(max[i], FitnessType.MAX);			
		}
		for(int i = 0; i < normalized.length; i++) {
			addFitness(normalized[i], FitnessType.NONNORMALIZED);			
		}
		currentIndex = epochs;
	}
	public void addFitness(double fitness, FitnessType type) {
		switch(type) {
		case AVG:
			seriesAvg.add(currentIndex,fitness);
			break;
		case MAX:
			seriesMax.add(currentIndex,fitness);
			break;
		case NONNORMALIZED:
			seriesNonNormalized.add(currentIndex,fitness);
			break;
		}		
		repaint();
	}
	public void increaseIndex(int number) {
		currentIndex += number;
	}
	public void createJpeg(String name, String directory,int width, int heigth) throws IOException {
		File chartFile = new File(directory + "/" + name + ".png" );
		ChartUtils.saveChartAsPNG(chartFile, chart, width, heigth);		
	}
}

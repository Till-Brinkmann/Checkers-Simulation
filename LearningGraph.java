package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import datastructs.List;

public class LearningGraph extends JFrame {
	private XYSeriesCollection dataset;
	List<XYSeries> seriesSet = new List<XYSeries>(); 
	private ChartPanel panel;
	public LearningGraph() {
		setResizable(true);
	    setBackground(Color.WHITE);
	    dataset = new XYSeriesCollection ();
		panel =new ChartPanel(ChartFactory.createXYLineChart("Learning progress", "x", "y", dataset,
			 	PlotOrientation.VERTICAL, true, false, false));
		panel.setSize(new Dimension(400,600));
	    add(panel);
	    pack();
	    setDefaultCloseOperation(HIDE_ON_CLOSE);
	    setVisible(true);
	    
	}
	public void updateChart() {
		remove(panel);
		panel =new ChartPanel(ChartFactory.createXYLineChart("Learning progress", "x", "y", dataset,
			 	PlotOrientation.VERTICAL, true, false, false));
		panel.setSize(new Dimension(400,600));
	    add(panel);
	    pack();
	}
	public void addSeries(String name, double data[]) {
		XYSeries series = new XYSeries(name);
		for(int i = 0; i<data.length; i++) {
			series.add(i,data[i]);
		}
		dataset.addSeries(series);
	  	updateChart();
		seriesSet.append(series);
	}
	public void updateSeries(String name,double data) {
		for(seriesSet.toFirst(); seriesSet.hasAccess(); seriesSet.next()) {
			if(seriesSet.get().getKey() == name) {
				seriesSet.get().add(new XYDataItem(seriesSet.get().getItemCount(), data));
			}
		}
		repaint();
	}
	public double[][] getSeries(String name) {
		for(seriesSet.toFirst(); seriesSet.hasAccess(); seriesSet.next()) {
			if(seriesSet.get().getKey() == name) {
				return seriesSet.get().toArray();
			}
		}
		return new double[1][1];
	}
	public void removeSeries(String name) {
		for(seriesSet.toFirst(); seriesSet.hasAccess(); seriesSet.next()) {
			if(seriesSet.get().getKey() == name) {
				dataset.removeSeries(seriesSet.get());
				updateChart();
				seriesSet.remove();
			}
		}
	}
}

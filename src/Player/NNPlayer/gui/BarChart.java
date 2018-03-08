package gui;



import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DFactory;
import com.orsoncharts.data.DefaultKeyedValues;
import com.orsoncharts.data.category.StandardCategoryDataset3D;
import com.orsoncharts.graphics3d.swing.Panel3D;

public class BarChart extends JFrame{

	
	private Panel3D panel;
	private Chart3D chart;
	private StandardCategoryDataset3D dataset;
	private DefaultKeyedValues<Number> y1;
	private DefaultKeyedValues<Number> y2;
	private DefaultKeyedValues<Number> y3;
	private DefaultKeyedValues<Number> y4;
	private DefaultKeyedValues<Number> y5;
	private DefaultKeyedValues<Number> y6;
	private DefaultKeyedValues<Number> y7;
	private DefaultKeyedValues<Number> y8;
	public BarChart() {
		super("Output visualization barchart");
		dataset = new StandardCategoryDataset3D();
		y1 = new DefaultKeyedValues<Number>();
		y2 = new DefaultKeyedValues<Number>();
		y3 = new DefaultKeyedValues<Number>();
		y4 = new DefaultKeyedValues<Number>();
		y5 = new DefaultKeyedValues<Number>();
		y6 = new DefaultKeyedValues<Number>();
		y7 = new DefaultKeyedValues<Number>();
		y8 = new DefaultKeyedValues<Number>();
		for(int i = 1; i <= 8; i++) {
			y1.put(i,0);
			y2.put(i,0);
			y3.put(i,0);
			y4.put(i,0);
			y5.put(i,0);
			y6.put(i,0);
			y7.put(i,0);
			y8.put(i,0);
		}
		dataset.addSeriesAsRow("1",y1);
		dataset.addSeriesAsRow("2",y2);
		dataset.addSeriesAsRow("3",y3);
		dataset.addSeriesAsRow("4",y4);
		dataset.addSeriesAsRow("5",y5);
		dataset.addSeriesAsRow("6",y6);
		dataset.addSeriesAsRow("7",y7);
		dataset.addSeriesAsRow("8",y8);
		
		chart = Chart3DFactory.createBarChart("NN Output",
												null,
												dataset,
												"x",
												"y",
												"Output value");
		chart.setAntiAlias(true);
		panel = new Panel3D(chart);
		panel.setBackground(Color.WHITE);
	    panel.setPreferredSize(new Dimension(1000,800));
		add(panel);
		pack();
		setVisible(true);
	}

	public void updateChart(double[] outputVector) {
		int size = 8;
		if(outputVector.length == 64){
			size = 16;
		}
		int index1 = 0;
		int index2 = 0;
		for(int i = 1; i <= size; i++) {	
			if(i%2 == 0) {
				y1.put(i,outputVector[index1]);
				y2.put(i,0);
				y3.put(i,outputVector[index1+8]);
				y4.put(i,0);
				y5.put(i,outputVector[index1+16]);
				y6.put(i,0);
				y7.put(i,outputVector[index1+24]);
				y8.put(i,0);
				index1++;
				
			}
			else {
				y1.put(i,0);
				y2.put(i,outputVector[index2+3]);
				y3.put(i,0);
				y4.put(i,outputVector[index2+11]);
				y5.put(i,0);
				y6.put(i,outputVector[index2+19]);
				y7.put(i,0);
				y8.put(i,outputVector[index2+27]);
				index2++;
			}
		}
		dataset = new StandardCategoryDataset3D();
		dataset.addSeriesAsRow("1",y1);
		dataset.addSeriesAsRow("2",y2);
		dataset.addSeriesAsRow("3",y3);
		dataset.addSeriesAsRow("4",y4);
		dataset.addSeriesAsRow("5",y5);
		dataset.addSeriesAsRow("6",y6);
		dataset.addSeriesAsRow("7",y7);
		dataset.addSeriesAsRow("8",y8);
		remove(panel);
		validate();
		add(new Panel3D(Chart3DFactory.createBarChart("NN Output",
				null,
				dataset,
				"x",
				"y",
				"Output value")));
		validate();
		//chart.plotChanged(new Plot3DChangeEvent(new Dataset3DChangeEvent(dataset, dataset),chart.getPlot(), true));
	}
}

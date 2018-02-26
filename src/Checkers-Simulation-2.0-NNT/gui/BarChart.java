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
	
	public BarChart() {
		super("Output visualization barchart");
		dataset = new StandardCategoryDataset3D();
		DefaultKeyedValues<Number> y1 = new DefaultKeyedValues<Number>();
		DefaultKeyedValues<Number> y2 = new DefaultKeyedValues<Number>();
		DefaultKeyedValues<Number> y3 = new DefaultKeyedValues<Number>();
		DefaultKeyedValues<Number> y4 = new DefaultKeyedValues<Number>();
		DefaultKeyedValues<Number> y5 = new DefaultKeyedValues<Number>();
		DefaultKeyedValues<Number> y6 = new DefaultKeyedValues<Number>();
		DefaultKeyedValues<Number> y7 = new DefaultKeyedValues<Number>();
		DefaultKeyedValues<Number> y8 = new DefaultKeyedValues<Number>();
		for(int i = 1; i <= 8; i++) {
			y1.put(i,1);
			y2.put(i,1);
			y3.put(i,1);
			y4.put(i,1);
			y5.put(i,1);
			y6.put(i,1);
			y7.put(i,1);
			y8.put(i,1);
		}
		dataset.addSeriesAsRow("1",y1);
		dataset.addSeriesAsRow("2",y1);
		dataset.addSeriesAsRow("3",y1);
		dataset.addSeriesAsRow("4",y1);
		dataset.addSeriesAsRow("5",y1);
		dataset.addSeriesAsRow("6",y1);
		dataset.addSeriesAsRow("7",y1);
		dataset.addSeriesAsRow("8",y1);
		
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
}

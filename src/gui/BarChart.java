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
		DefaultKeyedValues<Number> s1 = new DefaultKeyedValues<Number>();
		s1.put(1,1);
		s1.put(2,1);
		s1.put(3,1);
		s1.put(4,1);
		s1.put(5,1);
		s1.put(6,1);
		
		dataset.addSeriesAsRow("1",s1);
		chart = Chart3DFactory.createBarChart("NN Output",
												null,
												dataset,
												"x",
												"y",
												"Output value");
		chart.setAntiAlias(true);
		panel = new Panel3D(chart);
		panel.setBackground(Color.WHITE);
	    panel.setPreferredSize(new Dimension(600,400));
		add(panel);
		pack();
		setVisible(true);
	}
}

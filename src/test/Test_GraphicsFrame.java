package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import view.GraphicsFrame;

public class Test_GraphicsFrame {
	public static void main(String[] args) {
		JPanel p = new JPanel() {
			public String getName(){
				return "Scatterplot";
			}
			public void paintComponent(final Graphics g) {
			    super.paintComponent(g);
			    
			    setBackground(Color.WHITE);
			    g.setColor(Color.BLACK);
			    g.drawRect(100, 100, getWidth()-200, getHeight()-200);
			    g.drawLine(100, getHeight()-100, getWidth()-100, 150);
			    g.setColor(Color.RED);
			    g.fillOval(150, 350, 15, 15);
			    g.fillOval(275, 200, 15, 15);
			    g.setColor(Color.BLUE);
			    g.fillOval(255, 330, 15, 15);
			    g.fillOval(330, 200, 15, 15);
			}
		};

		p.setSize(new Dimension(600, 600));
		p.setPreferredSize(p.getSize());
		
		GraphicsFrame f = new GraphicsFrame(p);
		f.start();
	}

}

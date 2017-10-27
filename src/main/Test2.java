package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test2 extends JPanel {
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(Color.red);
		
		g2.fillRect(0, 300, 200, 200);
	}
	
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.getContentPane().add(new Test2());
		
		jf.setMinimumSize(new Dimension(500, 500));
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
	}

}

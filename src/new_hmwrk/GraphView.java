package new_hmwrk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import main.Fft;
import util.Util;
import wav.WavFile;

public class GraphView extends JPanel {
	// private WavFile wavFile;
	// private int width = 800;
	// private int heigth = 400;
	private int padding = 25;
	private int labelPadding = 25;
	private Color lineColor = new Color(44, 102, 230, 180);
	private Color pointColor = new Color(100, 100, 100, 180);
	private Color gridColor = new Color(200, 200, 200, 200);
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private int pointWidth = 4;
	private int numberYDivisions = 10;
	private int windowSize;
	private List<Double> scores;
	private int first_element;
	private int last_element;
	private int step;
	private double[] arr;
	private int sampleRate;
	
	

	public GraphView(List<Double> scores) {
		this.scores = scores;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (scores.size() - 1);
		double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore() - getMinScore());

		List<Point> graphPoints = new ArrayList<>();
		for (int i = 0; i < scores.size(); i++) {
			int x1 = (int) (i * xScale + padding + labelPadding);
			int y1 = (int) ((getMaxScore() - scores.get(i)) * yScale + padding);
			graphPoints.add(new Point(x1, y1));
		}

		// draw white background
		g2.setColor(Color.WHITE);
		g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding,
				getHeight() - 2 * padding - labelPadding);
		g2.setColor(Color.BLACK);

		// create hatch marks and grid lines for y axis.
		for (int i = 0; i < numberYDivisions + 1; i++) {
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if (scores.size() > 0) {
				g2.setColor(gridColor);
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
				g2.setColor(Color.BLACK);
				String yLabel = ((int) ((getMinScore()
						+ (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";

				FontMetrics metrics = g2.getFontMetrics();
				int labelWidth = metrics.stringWidth(yLabel);
				g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
			}
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		// 1000 / windowSize (50) = hertz = 20Hz -> najsporiji
		// 22 * windowSize = najbrzi = 22000
		// 
		for (int i = 0; i < scores.size() - 1; i++) {
			if (scores.size() > 1) {
				int x0 = i * (getWidth() - padding * 2 - labelPadding) / (scores.size() - 1) + padding + labelPadding;
				int x2 = (i+1) * (getWidth() - padding * 2 - labelPadding) / (scores.size() - 1) + padding + labelPadding;
				int x1 = x0;
				int y0 = getHeight() - padding - labelPadding;
				int y1 = y0 - pointWidth;
				if ((i % ((int) ((scores.size() / 20.0)) + 1)) == 0) {
					g2.setColor(gridColor);
					g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
					g2.setColor(Color.BLACK);
					String xLabel;
					
					if (i < 1000)
						xLabel = i+1 + "Hz";
					else
						xLabel = i / 1000 + "kHz";
					FontMetrics metrics = g2.getFontMetrics();
					int labelWidth = metrics.stringWidth(xLabel);
//					g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
					g2.drawString(xLabel, x0 + (x2-x0)/2 - labelWidth, y0 + metrics.getHeight() + 3);
				}
				g2.drawLine(x0, y0, x1, y1);
			}
		}

		// create x and y axes
		
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
		g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding,
				getHeight() - padding - labelPadding);

		Stroke oldStroke = g2.getStroke();
		g2.setColor(lineColor);
		g2.setStroke(GRAPH_STROKE);
//		for (int i = 0; i < graphPoints.size() - 1; i++) {
//			int x1 = graphPoints.get(i).x;
//			int y1 = graphPoints.get(i).y;
//			// int x1 = getHeight() - padding - labelPadding
//			int x2 = graphPoints.get(i + 1).x;
//			int y2 = graphPoints.get(i + 1).y;
//			g2.drawLine(x1, y1, x2, y2);
//		}

		for (int i = 0; i < graphPoints.size() - 1; i++) {
			int x1 = graphPoints.get(i).x;
			int y1 = graphPoints.get(i).y;
			// int x1 = getHeight() - padding - labelPadding
			int x2 = graphPoints.get(i + 1).x;
			g2.fillRect(x1, y1, x2-x1, getHeight() - padding - labelPadding - y1);
		}

		g2.setStroke(oldStroke);
		g2.setColor(pointColor);
		for (int i = 0; i < graphPoints.size(); i++) {
			int x = graphPoints.get(i).x - pointWidth / 2;
			int y = graphPoints.get(i).y - pointWidth / 2;
			int ovalW = pointWidth;
			int ovalH = pointWidth;
			// i : 100 = x : 255
			// int proportion = (int) ((scores.get(i) * 255.0) / 100);
			// g2.setColor(new Color(proportion, proportion, proportion));
			g2.fillOval(x, y, ovalW, ovalH);
		}
	}

	// @Override
	// public Dimension getPreferredSize() {
	// return new Dimension(width, heigth);
	// }
	private double getMinScore() {
		double minScore = Double.MAX_VALUE;
		for (Double score : scores) {
			minScore = Math.min(minScore, score);
		}
		return minScore;
	}
	
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	private double getMaxScore() {
		double maxScore = Double.MIN_VALUE;
		for (Double score : scores) {
			maxScore = Math.max(maxScore, score);
		}
		return maxScore;
	}

	public void setScores(ArrayList<Double> scores) {
		this.scores = scores;
		invalidate();
		this.repaint();
	}

	public List<Double> getScores() {
		return scores;
	}

	public void setPointWidth(int pointWidth) {
		this.pointWidth = pointWidth;
	}

	public void setFirst_element(int first_element) {
		this.first_element = first_element;
	}

	public void setLast_element(int last_element) {
		this.last_element = last_element;
	}

	public void setArr(double[] arr) {
		this.arr = arr;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

}
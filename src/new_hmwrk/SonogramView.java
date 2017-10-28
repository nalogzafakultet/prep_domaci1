package new_hmwrk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class SonogramView extends JPanel {
	private int padding = 25;
	private int labelPadding = 25;
	private Color lineColor = new Color(44, 102, 230, 180);
	private Color pointColor = new Color(100, 100, 100, 180);
	private Color gridColor = new Color(200, 200, 200, 200);
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private int pointWidth = 10;
	private int numberYDivisions = 10;
	private ArrayList<ArrayList<Pair>> scores;
	private int windowSize;
	private double minScore = Double.MAX_VALUE;
	private double maxScore = Double.MIN_VALUE;

	private ArrayList<String> yLabels;

	public SonogramView(ArrayList<ArrayList<Pair>> scores) {
		this.scores = scores;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (scores.size() - 1);
		double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore() - getMinScore());
		
		System.out.println("YSCALE" + yScale);
		

		List<List<Point>> graphPoints = new ArrayList<>();
		for (int i = 0; i < scores.size(); i++) {
			graphPoints.add(new ArrayList<>());
			for (int j = 0; j < scores.get(i).size(); j++) {
				int x1 = (int) (i * xScale + padding + labelPadding);
				int y1 = (int) (getMaxScore()*yScale - scores.get(i).get(j).frequency * yScale + padding);
				graphPoints.get(i).add(new Point(x1, y1));
			}
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
			int y0 = getHeight()
					- ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if (scores.size() > 0) {
				g2.setColor(gridColor);
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
				g2.setColor(Color.BLACK);

				String yLabel = ((int) ((getMinScore() + (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100 + "";
				

//				String yLabel = ((int) (((i * 1.0) / numberYDivisions)) * 100) / 100 + "";

				FontMetrics metrics = g2.getFontMetrics();
				int labelWidth = metrics.stringWidth(yLabel);
				g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
			}
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 0; i < scores.size(); i++) {
			if (scores.size() > 1) {
				int x0 = i * (getWidth() - padding * 2 - labelPadding) / (scores.size() - 1) + padding + labelPadding;
				int x1 = x0;
				int y0 = getHeight() - padding - labelPadding;
				int y1 = y0 - pointWidth;
				if ((i % ((int) ((scores.size() / 20.0)) + 1)) == 0) {
					g2.setColor(gridColor);
					g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
					g2.setColor(Color.BLACK);
					String xLabel;
					xLabel = (i * windowSize) + "ms";
					
					FontMetrics metrics = g2.getFontMetrics();
					int labelWidth = metrics.stringWidth(xLabel);
					g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
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

		g2.setStroke(oldStroke);
		g2.setColor(pointColor);
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("output-colors.log"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < graphPoints.size(); i++) {
			for (int j = 0; j < graphPoints.get(i).size(); j++) {
				int x = graphPoints.get(i).get(j).x - pointWidth / 2;
				int y = graphPoints.get(i).get(j).y - pointWidth / 2;
				int ovalW = pointWidth;
				int ovalH = pointWidth;
				int proportion = 255 - ((int) (scores.get(i).get(j).magnitude * 255 / 100));
				pw.print("[" + x + "," + y + "]");
				g2.setColor(new Color(proportion, proportion, proportion));
				g2.fillOval(x, y, ovalW, ovalH);
			}
			pw.println();
		}
		pw.close();

	}

	// @Override
	// public Dimension getPreferredSize() {
	// return new Dimension(width, heigth);
	// }
	
	public void setMinScore(double minScore) {
		this.minScore = minScore;
	}
	
	public void setMaxScore(double maxScore) {
		this.maxScore = maxScore;
	}

	public double getMinScore() {
		return minScore;
	}

	public double getMaxScore() {
		return maxScore;
	}

	public void setScores(ArrayList<ArrayList<Pair>> scores) {
		this.scores = scores;
		invalidate();
		this.repaint();
	}

	public ArrayList<ArrayList<Pair>> getScores() {
		return scores;
	}
	
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	// private static void createAndShowGui() {
	// ArrayList<Double> scores = new ArrayList<>();
	// try {
	// WavFile wavFile = WavFile.openWavFile(new File("primer.wav"));
	// double[] buffer = new double[100 * wavFile.getNumChannels()];
	// int bytesRead;
	//
	// double min = Double.MAX_VALUE;
	// double max = Double.MIN_VALUE;
	//
	// do {
	// bytesRead = wavFile.readFrames(buffer, 100);
	// for (int s = 0; s < bytesRead * wavFile.getNumChannels(); s++) {
	// scores.add(buffer[s] * 100);
	// if (buffer[s] > max)
	// max = buffer[s] * 100;
	// if (buffer[s] < min)
	// min = buffer[s] * 100;
	// }
	//
	// } while (bytesRead > 0);
	//
	// System.out.printf("Min: %f, Max: %f\n", min, max);
	// wavFile.close();
	//
	// } catch (IOException e) {
	//
	// System.out.println("nije ucitan fajl");
	// } catch (WavFileException e) {
	// // TODO Auto-generated catch block
	// System.err.println("neki wav xptn");
	// }
	//
	// SonogramView mainPanel = new SonogramView(scores);
	// mainPanel.setPreferredSize(new Dimension(1200, 600));
	// JFrame frame = new JFrame("DrawGraph");
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// frame.getContentPane().add(mainPanel);
	// frame.pack();
	// frame.setLocationRelativeTo(null);
	// frame.setVisible(true);
	//
	// }

}
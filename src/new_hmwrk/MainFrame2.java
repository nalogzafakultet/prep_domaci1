package new_hmwrk;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.Fft;
import util.Util;
import util.WindowFunctions;
import wav.WavFile;
import wav.WavFileException;

public class MainFrame2 extends JFrame {

	private static MainFrame2 instance;
	private SonogramView gv;

	private ArrayList<ArrayList<Pair>> scores = new ArrayList<>();
	private Random random;

	private JLabel fLabel;
	private File file = new File("assets/please-male.wav");
	private JLabel wsLabel;
	// No window - 0, Hamming - 1, Hanning - 2
	private int windowFunction = 0;
	private JTextField wsTextField;
	private static int sampleRate;
	private int windowSize;
	private int number_of_windows;
	private int numOfChannels;
	private int total_chunks;
	private int samples_per_window;
	private int minFreq;
	private int total_windows;

	private double arr[];

	private MainFrame2() {
		setTitle("Prepoznavanje Govora");
		JPanel jp = new JPanel();
		jp.setAlignmentX(Component.CENTER_ALIGNMENT);
		jp.setMaximumSize(new Dimension(800, 800));
		jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));

		JPanel wavForm = new JPanel();
		wavForm.setLayout(new FlowLayout());
		// wavForm.setMaximumSize(new Dimension(600, 400));

		GridBagConstraints cs = new GridBagConstraints();

		cs.fill = GridBagConstraints.HORIZONTAL;

		fLabel = new JLabel("No file selected.");
		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		wavForm.add(fLabel, cs);

		JButton fOpen = new JButton("Open file...");

		fOpen.addActionListener(e -> {
			JFileChooser jfc = new JFileChooser();
			FileFilter ff = new FileNameExtensionFilter("Wav Files (*.wav)", "wav");
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setFileFilter(ff);
			int success = jfc.showOpenDialog(null);

			if (success == JFileChooser.APPROVE_OPTION) {
				file = jfc.getSelectedFile();
				fLabel.setText(file.getName());
				System.out.println(file.getAbsolutePath());
			} else {
				System.out.println("Nista");
				return;
			}
		});

		fOpen.setPreferredSize(new Dimension(83, 29));
		cs.gridx = 1;
		cs.gridy = 0;
		cs.gridwidth = 1;
		wavForm.add(fOpen, cs);

		JLabel wLabel = new JLabel("Select window function:");
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		wavForm.add(wLabel, cs);

		JComboBox<String> combo = new JComboBox<>();
		combo.addItem("No window function");
		combo.addItem("Hamming");
		combo.addItem("Hanning");
		combo.setSelectedItem(0);
		combo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				windowFunction = combo.getSelectedIndex();
			}
		});
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 1;
		wavForm.add(combo, cs);

		wsLabel = new JLabel("Window size (ms):");
		cs.gridx = 0;
		cs.gridy = 2;
		cs.gridwidth = 1;
		wavForm.add(wsLabel, cs);

		wsTextField = new JTextField(4);
		wsTextField.setText("5");
		cs.gridx = 1;
		cs.gridy = 2;
		cs.gridwidth = 1;
		wavForm.add(wsTextField, cs);

		JButton submit = new JButton("Submit");
		cs.gridx = 1;
		cs.gridy = 4;
		cs.gridwidth = 1;
		wavForm.add(submit, cs);

		submit.addActionListener(e -> {
			windowFunction = combo.getSelectedIndex();
			if (file == null) {
				JOptionPane.showMessageDialog(wavForm, "No file selected", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (wsTextField.getText().isEmpty() || wsTextField.getText() == null) {
				JOptionPane.showMessageDialog(wavForm, "Select window size and number!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				WavFile wav = WavFile.openWavFile(file);
				numOfChannels = wav.getNumChannels();
				sampleRate = (int) wav.getSampleRate();
				total_chunks = (int) wav.getNumFrames();
				windowSize = Integer.parseInt(wsTextField.getText());

				samples_per_window = (sampleRate / 1000 * windowSize * numOfChannels);
				minFreq = 1000 / windowSize;

				gv.setMaxScore(sampleRate / 2);
				gv.setMinScore(minFreq);

				total_windows = (int) Math.ceil((double) total_chunks / (sampleRate / 1000 * windowSize));
				double[] arr = new double[samples_per_window];

				scores = new ArrayList<>();

				for (int i = 0; i < total_windows; i++) {

					arr = Util.readWindow(file.getAbsolutePath(), i + 1, windowSize);
					double[] imag = new double[arr.length];
					int arrSize;

					switch (windowFunction) {
					case 0:
						break;
					case 1:
						arrSize = arr.length;
						for (int k = 0; k < arrSize; k++) {
							arr[k] *= WindowFunctions.hamming(k, arrSize);
						}
						break;
					case 2:
						arrSize = arr.length;
						for (int k = 0; k < arrSize; k++) {
							arr[k] *= WindowFunctions.hanning(k, arrSize);
						}
						break;
					default:
						break;
					}
					// arr[i], img[i]
					// Math.sqrt(arr[i]^2 + imag[i]^2)

					Fft.getFourier(arr, imag);
					double minVal = Double.MAX_VALUE;
					double maxVal = Double.MIN_VALUE;

					for (int j = 2; j < arr.length / 2; j += numOfChannels) {
						if (arr[j] < minVal)
							minVal = arr[j];
						if (arr[j] > maxVal)
							maxVal = arr[j];
					}

					scores.add(new ArrayList<Pair>());
					for (int j = 2; j < arr.length / 2; j += numOfChannels) {
						double freq = (j * minFreq) / numOfChannels;
						double magn = Math.round(((arr[j] - minVal) * 100) / (maxVal - minVal));
						Pair p = new Pair(freq, magn);
						scores.get(i).add(p);
					}
				}
				gv.setScores(scores);
				gv.setWindowSize(windowSize);

			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(wavForm, "Please insert integer value", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (WavFileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			jp.revalidate();
			jp.repaint();
			gv.revalidate();
			gv.repaint();
		});

		jp.add(wavForm);

		// fillScores();
		gv = new SonogramView(scores);
		gv.setPreferredSize(new Dimension(800, 800));
		jp.add(gv);

		JPanel searchPane = new JPanel();
		searchPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel lFrom = new JLabel("From (freq):");
		JTextField tfFrom = new JTextField(10);
		JLabel lTo = new JLabel("To (freq):");
		JTextField tfTo = new JTextField(10);
		JButton bSearch = new JButton("Search");

		bSearch.addActionListener(e -> {

			int fromTime, toTime;
			try {
				fromTime = Integer.parseInt(tfFrom.getText());
				toTime = Integer.parseInt(tfTo.getText());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(wavForm, "Please insert integer value", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			int fromPtr = fromTime / windowSize;
			int toPtr = (int) Math.ceil(toTime * 1.0 / windowSize);
			
			double[] arr = new double[samples_per_window];
			scores = new ArrayList<>();

			
			for (int i = 0; i < (toPtr - fromPtr + 1); i++) {
				arr = Util.readWindow(file.getAbsolutePath(), i + 1 + fromPtr, windowSize);
				double[] imag = new double[arr.length];
				int arrSize;

				switch (windowFunction) {
				case 0:
					break;
				case 1:
					arrSize = arr.length;
					for (int k = 0; k < arrSize; k++) {
						arr[k] *= WindowFunctions.hamming(k, arrSize);
					}
					break;
				case 2:
					arrSize = arr.length;
					for (int k = 0; k < arrSize; k++) {
						arr[k] *= WindowFunctions.hanning(k, arrSize);
					}
					break;
				default:
					break;
				}
				// arr[i], img[i]
				// Math.sqrt(arr[i]^2 + imag[i]^2)

				Fft.getFourier(arr, imag);
				double minVal = Double.MAX_VALUE;
				double maxVal = Double.MIN_VALUE;

				for (int j = 2; j < arr.length / 2; j += numOfChannels) {
					if (arr[j] < minVal)
						minVal = arr[j];
					if (arr[j] > maxVal)
						maxVal = arr[j];
				}

				scores.add(new ArrayList<Pair>());
				for (int j = 2; j < arr.length / 2; j += numOfChannels) {
					double freq = (j * minFreq) / numOfChannels;
					double magn = Math.round(((arr[j] - minVal) * 100) / (maxVal - minVal));
					Pair p = new Pair(freq, magn);
					scores.get(i).add(p);
				}
				
			}
			gv.setScores(scores);
			
			jp.revalidate();
			jp.repaint();
			gv.revalidate();
			gv.repaint();
			

		});

		searchPane.add(lFrom);
		searchPane.add(tfFrom);
		searchPane.add(lTo);
		searchPane.add(tfTo);
		searchPane.add(bSearch);

		jp.add(searchPane);
		JScrollPane jsp = new JScrollPane(jp);
		getContentPane().add(jsp);

		setMinimumSize(new Dimension(1200, 1200));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void setSampleRate(int sampleRate) {
		MainFrame2.sampleRate = sampleRate;
	}

	public static MainFrame2 getInstance() {
		if (instance == null)
			instance = new MainFrame2();
		return instance;
	}

	public static void main(String[] args) {
		MainFrame2.getInstance();
	}

}

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

public class MainFrame extends JFrame {

	private static MainFrame instance;
	private HistogramView gv;

	private ArrayList<Double> scores = new ArrayList<>();
	private Random random;

	private JLabel fLabel;
	private File file = new File("primer.wav");
	private JLabel wsLabel;
	// No window - 0, Hamming - 1, Hanning - 2
	private int windowFunction = 0;
	private JTextField wnTextField;
	private JTextField wsTextField;
	private static int sampleRate;
	private int windowSize;
	private int windowNumber;
	private int numOfChannels;
	
	private double arr[];

	private void fillScores(double arr[]) {
		this.scores = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			scores.add(arr[i]);
		}
	}

	private MainFrame() {
		setTitle("Prepoznavanje Govora");
		JPanel jp = new JPanel();
		jp.setAlignmentX(Component.CENTER_ALIGNMENT);
		jp.setMaximumSize(new Dimension(400, 100));
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

		JLabel wnLabel = new JLabel("Window number:");
		cs.gridx = 0;
		cs.gridy = 3;
		cs.gridwidth = 1;
		wavForm.add(wnLabel, cs);

		wnTextField = new JTextField(4);
		wnTextField.setText("2");
		cs.gridx = 1;
		cs.gridy = 3;
		cs.gridwidth = 1;
		wavForm.add(wnTextField, cs);

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
			if (wnTextField.getText().isEmpty() || wsTextField.getText().isEmpty() || wnTextField.getText() == null
					|| wsTextField.getText() == null) {
				JOptionPane.showMessageDialog(wavForm, "Select window size and number!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			windowNumber = Integer.parseInt(wnTextField.getText());
			windowSize = Integer.parseInt(wsTextField.getText());

			arr = Util.readWindow(file.getAbsolutePath(), windowNumber, windowSize);
			
			try {
				WavFile wav = WavFile.openWavFile(file);
				numOfChannels = wav.getNumChannels();
				System.out.println("CHANNEL NUM: " + numOfChannels);
				sampleRate = (int) wav.getSampleRate();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (WavFileException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

			double[] imag = new double[arr.length];
			int arrSize;

			switch (windowFunction) {
			case 0:
				break;
			case 1:
				arrSize = arr.length;
				for (int i = 0; i < arrSize; i++) {
					arr[i] *= WindowFunctions.hamming(i, arrSize);
				}
				break;
			case 2:
				arrSize = arr.length;
				for (int i = 0; i < arrSize; i++) {
					arr[i] *= WindowFunctions.hanning(i, arrSize);
				}
				break;
			default:
				break;
			}
			// arr[i], img[i]
			// Math.sqrt(arr[i]^2 + imag[i]^2)
			Fft.getFourier(arr, imag);
			int len = arr.length;



			// Scaling ampls
			double[] copy = new double[arr.length];
			double minVal = Double.MAX_VALUE;
			double maxVal = Double.MIN_VALUE;

			for (int i = 1; i < copy.length; i++) {
				copy[i] = arr[i];
			}
			ArrayList<Double> scoresCopy = new ArrayList<>();
			ArrayList<String> xLabels = new ArrayList<>();
			scoresCopy = new ArrayList<>();
			scoresCopy.add(copy[1]);
			int startingFreq = 1000/windowSize;
			xLabels.add(startingFreq + "Hz");
			if (copy[1] < minVal) minVal = copy[1];
			if (copy[1] > maxVal) maxVal = copy[1];
			
			int step = (int) Math.ceil((len / 16));
			System.out.println("ARR SIZE: " + arr.length);
			System.out.println("step: " + step);
			
			for (int i = 1 + len / 16; i < (arr.length / 2); i += step) {
				
				if (copy[i] < minVal) minVal = copy[i];
				if (copy[i] > maxVal) maxVal = copy[i];
				scoresCopy.add(copy[i]);
				
				if (i * startingFreq > 1000) {
					xLabels.add(((((i / numOfChannels)* startingFreq) / 100.0) / 10) + "kHz");
				} else {
					xLabels.add(((i / numOfChannels) * startingFreq) + "Hz");
				}
			}
			xLabels.add((((len/2 - 1) * startingFreq / numOfChannels) / 1000) + "kHz");
			scoresCopy.add(copy[len / 2 - 1]);
			if (copy[len / 2 - 1] < minVal) minVal = copy[len / 2 - 1];
			if (copy[len / 2 - 1] > maxVal) maxVal = copy[len / 2 - 1];
			
			gv.setxLabels(xLabels);
			
			scores = new ArrayList<>();
			for (int i = 0; i < scoresCopy.size(); i++) {
				scores.add(Math.floor(((scoresCopy.get(i) - minVal) * 100.0) / (maxVal - minVal)));
			}
			
			System.out.println("SCORES: " + scores);
			System.out.println("XLABELS: " + xLabels);
			
			gv.setSampleRate(sampleRate);
			gv.setArr(arr);
			gv.setWindowSize(windowSize);
			gv.setStep(len / 16);
			gv.setFirst_element(1);
			gv.setLast_element(len / 2 - 1);
			
			
			
			gv.setScores(scores);
			jp.revalidate();
			jp.repaint();
			gv.revalidate();
			gv.repaint();
		});

		jp.add(wavForm);

		// fillScores();
		gv = new HistogramView(scores);
		gv.setPreferredSize(new Dimension(600, 600));
		jp.add(gv);

		JPanel searchPane = new JPanel();
		searchPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel lFrom = new JLabel("From (freq):");
		JTextField tfFrom = new JTextField(10);
		JLabel lTo = new JLabel("To (freq):");
		JTextField tfTo = new JTextField(10);
		JButton bSearch = new JButton("Search");

		bSearch.addActionListener(e -> {
			
			double minVal = Double.MAX_VALUE;
			double maxVal = Double.MIN_VALUE;
			int fromFreq = Integer.parseInt(tfFrom.getText());
			int toFreq = Integer.parseInt(tfTo.getText());
			int lowest_freq = 1000 / windowSize;
			
			
			
			int fromPtr = fromFreq * numOfChannels / lowest_freq;
			int toPtr = toFreq * numOfChannels / lowest_freq;

			ArrayList<String> xLabels = new ArrayList<>();
			ArrayList<Double> scoresCopy = new ArrayList<>();
			scores = new ArrayList<>();
			scoresCopy.add(arr[fromPtr]);
			
			if (arr[fromPtr] > maxVal) maxVal = arr[fromPtr];
			if (arr[fromPtr] < minVal) minVal = arr[fromPtr];
			
			if (lowest_freq * fromPtr > 1000) {
				xLabels.add((((lowest_freq * fromPtr / numOfChannels) / 100.0) / 10) + "kHz");
			} else {
				xLabels.add((lowest_freq * fromPtr / numOfChannels) + "Hz");
			}
			
			int num_of_samples = toPtr - fromPtr - 1;
			int step = (int) Math.ceil(num_of_samples / 8);
			
			System.out.println("ARR SIZE: " + arr.length);
			System.out.println("NUM OF SAMPLES: " + num_of_samples);
			
			System.out.println("FROM: " + fromPtr + ", TO: " + toPtr);
			
			
			for (int i = fromPtr + step; i < toPtr; i += step) {
				scoresCopy.add(arr[i]);
				if (arr[i] > maxVal) maxVal = arr[i];
				if (arr[i] < minVal) minVal = arr[i];
				
				if (lowest_freq * fromPtr > 1000) {
					xLabels.add(((lowest_freq / numOfChannels * i) / 1000) + "kHz");
				} else {
					xLabels.add((lowest_freq / numOfChannels * i) + "Hz");
				}
			}
			scoresCopy.add(arr[toPtr]);
			if (arr[toPtr] > maxVal) maxVal = arr[toPtr];
			if (arr[toPtr] < minVal) minVal = arr[toPtr];
			if (lowest_freq * toPtr > 1000) {
				xLabels.add(((lowest_freq / numOfChannels* (toPtr)) / 1000) + "kHz");
			} else {
				xLabels.add((lowest_freq / numOfChannels * (toPtr)) + "Hz");
			}
			
			
			scores = new ArrayList<>();
			for (int i = 0; i < scoresCopy.size(); i++) {
				scores.add(Math.floor(((scoresCopy.get(i) - minVal) * 100.0) / (maxVal - minVal)));
			}
			System.out.println("MAX: " + maxVal);
			System.out.println("MIN: " + minVal);
			
			
			gv.setxLabels(xLabels);
			
			gv.setStep(step);
			gv.setFirst_element(fromPtr);
			gv.setLast_element(toPtr);
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

		setMinimumSize(new Dimension(1024, 568));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void setSampleRate(int sampleRate) {
		MainFrame.sampleRate = sampleRate;
	}
	
	public static MainFrame getInstance() {
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}

	public static void main(String[] args) {
		MainFrame.getInstance();
	}

}

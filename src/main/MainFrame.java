package main;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame {
	private static MainFrame instance;
	private JLabel fLabel;
	private File file;
	private JLabel wsLabel;
	// No window - 0, Hamming - 1, Hanning - 2
	private int windowFunction = 0;
	
	private MainFrame() {
		setTitle("Speech Recognition");
		JPanel root = new JPanel(new GridBagLayout());
		GridBagConstraints cs = new GridBagConstraints();
		
		cs.fill = GridBagConstraints.HORIZONTAL;
		
		fLabel = new JLabel("No file selected.");
		cs.gridx = 0;
		cs.gridy = 0;
		cs.gridwidth = 1;
		root.add(fLabel, cs);
		
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
		root.add(fOpen, cs);
		
		JLabel wLabel = new JLabel("Select window function:");
		cs.gridx = 0;
		cs.gridy = 1;
		cs.gridwidth = 1;
		root.add(wLabel, cs);
		
		JComboBox<String> combo = new JComboBox<>();
		combo.addItem("No window function");
		combo.addItem("Hamming");
		combo.addItem("Hanning");
		combo.setSelectedItem(0);
		cs.gridx = 1;
		cs.gridy = 1;
		cs.gridwidth = 1;
		root.add(combo, cs);
		
		wsLabel = new JLabel("Window size (ms):");
		cs.gridx = 0;
		cs.gridy = 2;
		cs.gridwidth = 1;
		root.add(wsLabel, cs);
		
		JTextField wsTextField = new JTextField();
		cs.gridx = 1;
		cs.gridy = 2;
		cs.gridwidth = 1;
		root.add(wsTextField, cs);
		 
		JLabel wnLabel = new JLabel("Window number:");
		cs.gridx = 0;
		cs.gridy = 3;
		cs.gridwidth = 1;
		root.add(wnLabel, cs);
		
		JTextField wnTextField = new JTextField();
		cs.gridx = 1;
		cs.gridy = 3;
		cs.gridwidth = 1;
		root.add(wnTextField, cs);
		
		JButton submit = new JButton("Submit");
		cs.gridx = 1;
		cs.gridy = 4;
		cs.gridwidth = 1;
		root.add(submit, cs);
		
		submit.addActionListener(e -> {
			windowFunction = combo.getSelectedIndex();
			if (file == null) {
				JOptionPane.showMessageDialog(root, "No file selected", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			System.out.println("Yoohoo");
		});
		
		this.add(root);
		
		
		setPreferredSize(new Dimension(400, 300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
	}
	
	public static MainFrame getInstance() {
		if (instance == null) instance = new MainFrame();
		return instance;
	}
	
}

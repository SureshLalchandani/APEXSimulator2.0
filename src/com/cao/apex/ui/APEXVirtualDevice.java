package com.cao.apex.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.cao.apex.Initializer;
import com.cao.apex.models.ICache;
import com.cao.apex.models.IQueue;
import com.cao.apex.models.ROB;
import com.cao.apex.models.RRAT;
import com.cao.apex.models.RenameTable;
import com.cao.apex.models.SharedData;
import com.cao.apex.pipeline.APEXPipelineHandler;
import com.cao.apex.pipeline.APEXPipelineHandlerV2;
import com.cao.apex.utility.Constants;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class APEXVirtualDevice {

	private JFrame frame;
	private JTextField textField;
	private JFileChooser chooser;
	private JTextField textField_2;
	JButton btnNewButton_1;
	JButton btnNewButton_2 ;
	JButton btnNewButton;
	private JTextField textField_3;
	private JTextField textField_4;
	JTextArea textArea_1;
	JTextArea textArea;
	private JScrollPane scrollPane_1;
	private JTextField textField_1;
	private JTextField textField_5;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					APEXVirtualDevice window = new APEXVirtualDevice();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public APEXVirtualDevice() {
		initialize();
	}

	private void enableButtons(boolean enable) {
		if(chooser.getSelectedFile() != null) {
			btnNewButton_1.setEnabled(enable);
			btnNewButton_2.setEnabled(enable);
			btnNewButton.setEnabled(enable);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 917, 532);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		chooser= new JFileChooser();

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panel.setBounds(6, 78, 905, 42);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		btnNewButton = new JButton("Initialize");
		btnNewButton.setEnabled(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initializeSimulator(chooser.getSelectedFile());
			}
		});
		btnNewButton.setBounds(185, 6, 98, 29);
		panel.add(btnNewButton);

		btnNewButton_1 = new JButton("Simulate");
		btnNewButton_1.setEnabled(false);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String num = APEXVirtualDevice.this.textField_1.getText();
				//num = "0";
				if(num == null || num.trim().length() == 0) {

					JOptionPane.showMessageDialog(frame,
							"Please enter the number of cycles.",
							"Required Field",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				try {
					simulate(Integer.parseInt(num));
				} catch(NumberFormatException nfex) {
					JOptionPane.showMessageDialog(frame,  "Please enter correct number of cycles.", "Required Field", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		btnNewButton_1.setBounds(284, 6, 106, 29);
		panel.add(btnNewButton_1);

		textField = new JTextField();


		btnNewButton_2 = new JButton("Display");
		btnNewButton_2.setEnabled(false);
		btnNewButton_2.setBounds(823, 6, 76, 29);
		btnNewButton_2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				display();

			}
		});
		panel.add(btnNewButton_2);

		textField_1 = new JTextField();
		textField_1.setText("0");
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setBounds(385, 5, 69, 28);
		panel.add(textField_1);
		textField_1.setColumns(10);

		JButton btnSetUrfSize = new JButton("Set URF Size");
		btnSetUrfSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String num = APEXVirtualDevice.this.textField_5.getText();
				//num = "0";
				if(num == null || num.trim().length() == 0) {

					JOptionPane.showMessageDialog(frame,
							"Please enter the URF Size.",
							"Required Field",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				try {
					setURFSize(Integer.parseInt(num));
				} catch(NumberFormatException nfex) {
					JOptionPane.showMessageDialog(frame,  "Please enter correct number of cycles.", "Required Field", JOptionPane.WARNING_MESSAGE);
				}	
			}
		});
		btnSetUrfSize.setBounds(6, 6, 117, 29);
		panel.add(btnSetUrfSize);

		textField_5 = new JTextField();

		textField_5.setHorizontalAlignment(SwingConstants.CENTER);
		textField_5.setText("32");
		textField_5.setBounds(119, 5, 54, 28);
		panel.add(textField_5);
		textField_5.setColumns(10);

		JButton btnMapTables = new JButton("Prn RAT/RRAT");
		btnMapTables.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayRename();
			}
		});
		btnMapTables.setBounds(451, 6, 117, 29);
		panel.add(btnMapTables);

		JButton btnNewButton_5 = new JButton("Prn IQ");
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayIQ();
			}
		});
		btnNewButton_5.setBounds(566, 6, 82, 29);
		panel.add(btnNewButton_5);

		JButton btnPrnRob = new JButton("Prn ROB");
		btnPrnRob.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayROB();
			}
		});
		btnPrnRob.setBounds(647, 6, 76, 29);
		panel.add(btnPrnRob);

		JButton btnPrnStats = new JButton("Prn Stats");
		btnPrnStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				display();
			}
		});
		btnPrnStats.setBounds(726, 6, 98, 29);
		panel.add(btnPrnStats);

		chooser.setCurrentDirectory(new File("/My Data/BU Coursework/CS520-COA/Project/Deployment"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		chooser.setFileFilter(filter);
		chooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				updateUI();
			}
		});

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(6, 125, 313, 379);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JPanel panel_3 = new JPanel();
		panel_3.setBounds(6, 27, 301, 41);
		panel_1.add(panel_3);
		panel_3.setLayout(null);

		textField_4 = new JTextField();
		textField_4.setHorizontalAlignment(SwingConstants.CENTER);
		textField_4.setText("0");
		textField_4.setBounds(47, 6, 60, 28);
		panel_3.add(textField_4);
		textField_4.setColumns(10);

		textField_3 = new JTextField();
		textField_3.setText("100");
		textField_3.setHorizontalAlignment(SwingConstants.CENTER);
		textField_3.setBounds(136, 6, 60, 28);
		panel_3.add(textField_3);
		textField_3.setColumns(10);

		JLabel lblTo = new JLabel("To");
		lblTo.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTo.setBounds(119, 12, 16, 16);
		panel_3.add(lblTo);

		JLabel lblNewMemorylabel = new JLabel("From");
		lblNewMemorylabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewMemorylabel.setBounds(6, 12, 40, 16);
		panel_3.add(lblNewMemorylabel);

		JButton btnNewButton_3 = new JButton("GO");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMemory();
			}
		});
		btnNewButton_3.setBounds(228, 7, 60, 29);
		panel_3.add(btnNewButton_3);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(6, 69, 301, 304);
		panel_1.add(scrollPane_1);

		textArea_1 = new JTextArea();
		scrollPane_1.setViewportView(textArea_1);
		textArea_1.setBackground(UIManager.getColor("Button.background"));

		JLabel lblMemory = new JLabel("Memory");
		lblMemory.setBounds(16, 6, 61, 16);
		panel_1.add(lblMemory);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(6, 16, 905, 42);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(null);

		textField_2 = new JTextField();
		textField_2.setEditable(false);
		textField_2.setBounds(122, 6, 579, 28);
		panel_2.add(textField_2);
		textField_2.setColumns(10);


		JButton btnChooseFile = new JButton("Choose File");
		btnChooseFile.setBounds(6, 7, 117, 29);
		panel_2.add(btnChooseFile);

		JButton btnNewButton_4 = new JButton("Reset Simulator");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				APEXPipelineHandlerV2.newInstance();
				SharedData.newInstance().setPc(4000);
				textField_2.setText("");
				textArea_1.setText("");
				textArea.setText("");
				textField_3.setText("100");
				textField_4.setText("0");
				textField.setText("");

				updateUI();
			}
		});
		btnNewButton_4.setBounds(726, 7, 173, 29);
		panel_2.add(btnNewButton_4);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(331, 146, 580, 358);
		frame.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JLabel lblLog = new JLabel("Console");
		lblLog.setBounds(335, 125, 61, 16);
		frame.getContentPane().add(lblLog);
		btnChooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.showOpenDialog(frame);
			}
		});

		updateUI();
	}

	private void updateUI() {
		// TODO Auto-generated method stub
		if(chooser.getSelectedFile() != null)
		{
			textField_2.setText(chooser.getSelectedFile().getAbsolutePath());
			enableButtons(true);
		} else {
			enableButtons(false);
		}
	}



	private static void initializeSimulator(File file) {
		Initializer initializer = new Initializer();
		ICache cache = ICache.getInstance();
		cache.size();
		try {
			initializer.readInstructionsFromFile(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			cache.size();
		}
	}

	private static void simulate(int numOfCycle) {
		APEXPipelineHandlerV2 apexPipelineHandler = APEXPipelineHandlerV2.getInstance();
		try {
			apexPipelineHandler.simulate(numOfCycle);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private static void setURFSize(int size) {
		SharedData.getInstance().setURFSize(size);
	}


	private void display() {
		if(APEXPipelineHandlerV2.getInstance().log != null)
			textArea.setText("");
			textArea.setText(APEXPipelineHandlerV2.getInstance().log.toString());

	}


	private void displayIQ() {
		String iq = IQueue.getIsntance().toString();
		textArea.setText("");
		textArea.setText(iq);

	}
	
	private void displayRename() {
		String rat = RenameTable.getInstance().toString();
		String rrat = RRAT.getInstance().toString();
		textArea.setText("");
		textArea.setText("RAT \n" + rat + "\n RRAT \n" + rrat);

	}
	
	private void displayROB() {
		String rat = ROB.getInstance().toString();
		textArea.setText("");
		textArea.setText(rat);
	}


	private void showMemory() {
		int start = Integer.parseInt(textField_4.getText().trim());
		int to = Integer.parseInt(textField_3.getText().trim());
		StringBuilder builder = new StringBuilder();


		for (int i = start ; i < to; i++) {
			if(SharedData.getInstance().readMemoryForLocation(i) == Constants.INVALID) continue;

			builder.append("\nMemory [" + i + "] = " + SharedData.getInstance().readMemoryForLocation(i) + " \t" );
		}

		textArea_1.setText(builder.toString());


	}
}

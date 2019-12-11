package com.youtube.gui;

import javax.swing.JFrame;
import org.json.simple.parser.ParseException;

import com.youtube.controller.Controller;
import com.youtube.utils.Constants;

import javax.swing.JLabel;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.AbstractListModel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class DescriptionFrame extends JFrame implements ActionListener ,ListSelectionListener{
	
	private static final long serialVersionUID = -4137850100812229621L;
	
	private ButtonListener btnListener;
	
	private JList<String> list ;
	private JTextArea textArea;
	/**
	 * @param btnListener the btnListener to set
	 */
	public void setBtnListener(ButtonListener btnListener) {
		this.btnListener = btnListener;
	}

	public DescriptionFrame() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException {
		setTitle("Set Description");
		getContentPane().setLayout(null);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(125, 209, 89, 23);
		btnOk.addActionListener(this);
		getContentPane().add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(219, 209, 89, 23);
		btnCancel.addActionListener(this);
		getContentPane().add(btnCancel);
		
		JLabel lblPleaseEnterThe = new JLabel("Please Select The desired Stream and edit it's description");
		lblPleaseEnterThe.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPleaseEnterThe.setBounds(10, 20, 361, 14);
		getContentPane().add(lblPleaseEnterThe);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(124, 45, 280, 121);
		getContentPane().add(scrollPane_1);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		
		scrollPane_1.setViewportView(textArea);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(13, 45, 101, 164);
		getContentPane().add(scrollPane);
		
		list = new JList<>();
		scrollPane.setViewportView(list);
		list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String titles[] = Controller.getInstance().getStreamTitles();
			public int getSize() {
				return titles.length;
			}
			public String getElementAt(int index) {
				return titles[index];
			}
		});
		list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JLabel lblChooseStream = new JLabel("Choose Stream");
		lblChooseStream.setFont(new Font("Tahoma", Font.PLAIN, 9));
		scrollPane.setColumnHeaderView(lblChooseStream);
		
		JButton SetDescription = new JButton("Set Description");
		SetDescription.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					String newDescription = textArea.getText(); 
					int desLen = newDescription.getBytes("UTF-8").length;
					System.out.println("deslen: "+ desLen);
					if(desLen>5000) {
						//bad input
						JOptionPane.showMessageDialog(null,
								" Description is too long",
				                "Not Completed",
				                JOptionPane.ERROR_MESSAGE);
						return;
					}
					String ID = Controller.getInstance().getID(list.getSelectedValue());
					Constants.StreamDescription.replace(ID, newDescription);
					JOptionPane.showMessageDialog(null,
							list.getSelectedValue() + " Description succssesfully changed.",
			                "Completed",
			                JOptionPane.PLAIN_MESSAGE);
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}
		});
		SetDescription.setBounds(260, 175, 144, 23);
		getContentPane().add(SetDescription);
		
		JLabel lblCharactersMax = new JLabel("Max 5000 Characters ");
		lblCharactersMax.setBounds(125, 177, 155, 14);
		getContentPane().add(lblCharactersMax);
		
		list.addListSelectionListener(this);
		
		this.addWindowListener(new WindowAdapter(){		//on closing act as Cancel was pressed
            public void windowClosing(WindowEvent e){
            	btnListener.ButtonPressed("Cancel");
            }
        });
		
		setSize(447,282);
		setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		btnListener.ButtonPressed(e.getActionCommand());
	}

	public String getText() {
		return textArea.getText();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		try {
		
		String ID = Controller.getInstance().getID(list.getSelectedValue());
		//System.out.println(ID);
		
		//System.out.println(idDes);
		String description = Constants.StreamDescription.get(ID);
		//System.out.println(description);
		if (!arg0.getValueIsAdjusting()) {
			   textArea.setText(description);
			}
			
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

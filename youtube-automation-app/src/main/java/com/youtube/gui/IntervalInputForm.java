package com.youtube.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class IntervalInputForm extends JFrame implements ActionListener {
	


	private static final long serialVersionUID = 1L;
	
	private ButtonListener btnlistener;
	private String selected;
	
	private String[] intervals = {"00:01","00:05","00:10","00:15","00:30","00:45","01:00","01:30","02:00","03:00","04:00"
			,"05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00"};
	
	JComboBox<Object> box;
	
	public IntervalInputForm() {
		setSize(462, 145);
		getContentPane().setLayout(null);
		JPanel panel = new JPanel();
		panel.setBounds(26, 11, 410, 83);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		selected="00:01";	//set default 5 min 
		
		JLabel lblPleaseEnterInterval = new JLabel("Please Select Interval time in HH:MM format");
		lblPleaseEnterInterval.setBounds(10, 11, 262, 29);
		panel.add(lblPleaseEnterInterval);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(115, 51, 74, 23);
		panel.add(btnOk);
		btnOk.addActionListener(this);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.setBounds(199, 51, 88, 23);
		panel.add(btnCancel);
		
		
		
		box = new JComboBox<Object>(intervals);
		box.setBounds(300, 14, 88, 23);
		box.setSelectedIndex(1);
		box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selected = intervals[box.getSelectedIndex()];
			}
		} );
		panel.add(box);
		
	}
	

	
	public String[] getIntervals() {
		return intervals;
	}

	public void setBtnListener(ButtonListener btnlstener) {
		this.btnlistener=btnlstener;
	}
	
	
	public String getSelected() {
		return selected;
	}

	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent ev) {
		JButton jb = (JButton) ev.getSource();
		btnlistener.ButtonPressed(jb.getLabel());
	}
}

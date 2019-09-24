package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.google.api.services.youtube.model.LiveStream;
import com.youtube.app.ListStreams;

public class StatusPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTable streamstbl;
	
	private StatusTableModel stm;
	
	private JButton refreshbtn;
	
	private ButtonListener btnlitsener;
	
	public StatusPanel() {
		
		refreshbtn = new JButton("Refresh");
		StatusTableModel stm = new StatusTableModel();
		stm.setData(ListStreams.run(null));
		streamstbl = new JTable(stm);
		streamstbl.setPreferredScrollableViewportSize(new Dimension(350, 70));
		streamstbl.setFillsViewportHeight(true);
		streamstbl.setEnabled(false);
		refreshbtn.addActionListener(this);
		add(refreshbtn,BorderLayout.EAST);	
		add(new JScrollPane(streamstbl),BorderLayout.CENTER);
		
	}

	public void setBtnListener(ButtonListener listener) {
		this.btnlitsener=listener;
	}
	
	public void refresh() { //refresh not working removed invoke from main
		
		List<LiveStream> updtae = ListStreams.run(null);
		stm.setData(updtae);   //get stream list from server
		
	}

	public void actionPerformed(ActionEvent ev) {
		JButton jb = (JButton) ev.getSource();
		btnlitsener.ButtonPressed(jb.getLabel());
	}
	
}

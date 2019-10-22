package com.youtube.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.google.api.services.youtube.model.LiveStream;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class IntervalInputForm extends JFrame implements ActionListener {
	
	/**
	 * @return the btnRefresh
	 */
	public JButton getBtnRefresh() {
		return btnRefresh;
	}

	private static final long serialVersionUID = 1L;
	
	private ButtonListener btnlistener;
	
	private String selected;
	
	private String[] intervals = {"00:01","00:05","00:10","00:15","00:30","00:45","01:00","01:30","02:00","03:00","04:00"
			,"05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00"};
	
	private JComboBox<Object> box;
	
	private JTable streamTable;
	
	private StreamTableModel stm;
	
	private Boolean[] checked;
	
	private JLabel lblThereAreNo;
	
	private JLabel lblPleaseEnterInterval;
	
	private static IntervalInputForm instance;
	
	private JButton btnRefresh;
	
	private JScrollPane jsp;
	
	public JTable getStreamTable() {
		return streamTable;
	}


	public static IntervalInputForm getInstance() {
		if(instance == null)
			instance=new IntervalInputForm();
		return instance;
	}

	public IntervalInputForm() {
		super("Starting Live Broadcast");
		setSize(462, 307);
		getContentPane().setLayout(null);
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 426, 246);
		getContentPane().add(panel);
		panel.setLayout(null);
		stm = new StreamTableModel();
		streamTable = new JTable(stm);
		int widths[]= {50,50,50,200};
		setColumnWidths(streamTable,widths);
		streamTable.setEditingColumn(0);
		streamTable.setPreferredScrollableViewportSize(new Dimension(350, 100));
		streamTable.setFillsViewportHeight(true);
		jsp = new JScrollPane(streamTable);
		jsp.setBounds(20,51,352,117);
		panel.add(jsp,BorderLayout.SOUTH);
		selected="00:05";	//set default 5 min 
		
		lblPleaseEnterInterval = new JLabel("Please Select Interval time in HH:MM format");
		lblPleaseEnterInterval.setBounds(10, 11, 262, 29);
		panel.add(lblPleaseEnterInterval);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(20, 212, 74, 23);
		panel.add(btnOk);
		btnOk.addActionListener(this);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.setBounds(104, 212, 88, 23);
		panel.add(btnCancel);
		
		
		
		box = new JComboBox<Object>(intervals);
		box.setBounds(284, 14, 88, 23);
		box.setSelectedIndex(1);
		box.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selected = intervals[box.getSelectedIndex()];
			}
		} );
		panel.add(box);
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(202, 212, 89, 23);
		btnRefresh.addActionListener(this);
		panel.add(btnRefresh);
		
		lblThereAreNo = new JLabel("There are no active streams available please refresh and try again");
		lblThereAreNo.setBounds(20, 179, 382, 14);
		lblThereAreNo.setVisible(false);
		panel.add(lblThereAreNo);
		
	}
	
	/**
	 * @return the jsp
	 */
	public JScrollPane getJsp() {
		return jsp;
	}


	/**
	 * @param lblPleaseEnterInterval the lblPleaseEnterInterval to set
	 */
	public void setLblPleaseEnterInterval(JLabel lblPleaseEnterInterval) {
		this.lblPleaseEnterInterval = lblPleaseEnterInterval;
	}


	/**
	 * @return the box
	 */
	public JComboBox<Object> getBox() {
		return box;
	}


	/**
	 * @return the lblPleaseEnterInterval
	 */
	public JLabel getLblPleaseEnterInterval() {
		return lblPleaseEnterInterval;
	}


	public Boolean[] getChecked() {
		for(int i = 0 ; i<streamTable.getRowCount() ;i++) {
			checked[i]=Boolean.valueOf((boolean) streamTable.getValueAt(i, 0));
		}
		return checked;
	}
	
	public void setData(List<LiveStream> data) { 
		
		stm.setData(data); 
		checked = new Boolean[data.size()];
		if(!data.isEmpty()) {
			System.out.println("data  not empty");
			lblThereAreNo.setVisible(false);
			return;
		}
		System.out.println("data empty");
		lblThereAreNo.setVisible(true);
	}
	
	public void refresh() {
		stm.fireTableDataChanged();
		System.out.println("refreshed");
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
	
	public static void setColumnWidths(JTable table, int... widths) {
	    TableColumnModel columnModel = table.getColumnModel();
	    for (int i = 0; i < widths.length; i++) {
	        if (i < columnModel.getColumnCount()) {
	            columnModel.getColumn(i).setMaxWidth(widths[i]);
	        }
	        else break;
	    }
	    
	}
}

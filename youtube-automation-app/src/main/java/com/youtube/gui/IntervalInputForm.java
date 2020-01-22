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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.SystemColor;

/**
 * This class represents an Interval input form , which will appear when setting an interval,
 * or Starting a Live Broadcast
 * @author Evgeny Geyfman
 *
 */
public class IntervalInputForm extends JFrame implements ActionListener {
	
	
	private static final long serialVersionUID = 424851761694278102L;

	private ButtonListener btnlistener;
	
	private String selected;
	
	private String[] intervals = {"Non-Stop","00:05","00:10","00:15","00:20","00:25","00:30","00:35","00:40","00:45"
			,"00:50","00:55","01:00","01:15","01:30","02:00","03:00","04:00"
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
	
	private JButton btnOk;
	
	private JButton btnCancel;
	
	/**
	 * @return the btnRefresh
	 */
	public JButton getBtnRefresh() {
		return btnRefresh;
	}
	
	public JTable getStreamTable() {
		return streamTable;
	}

	public static IntervalInputForm getInstance() {
		if(instance == null)
			instance = new IntervalInputForm();
		return instance;
	}

	public IntervalInputForm() {
		super("Starting Live Broadcast");
		getContentPane().setBackground(SystemColor.textHighlightText);
		setSize(462, 307);
		getContentPane().setLayout(null);
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.textHighlightText);
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
		selected = "Non-Stop";	//set default 5 min 
		
		lblPleaseEnterInterval = new JLabel("Please Select Interval time in HH:MM format");
		lblPleaseEnterInterval.setBounds(10, 11, 262, 29);
		panel.add(lblPleaseEnterInterval);
		
		btnOk = new JButton("Start");
		btnOk.setBounds(20, 212, 89, 23);
		panel.add(btnOk);
		btnOk.addActionListener(this);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.setBounds(123, 212, 89, 23);
		panel.add(btnCancel);
		
		
		box = new JComboBox<Object>(intervals);
		box.setBounds(284, 14, 88, 23);
		box.setSelectedItem(selected);
		panel.add(box);
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(226, 212, 89, 23);
		btnRefresh.addActionListener(this);
		panel.add(btnRefresh);
		
		lblThereAreNo = new JLabel("<html>There are no active streams available,<br>\r\nPlease start streaming on your Encoder, then press refresh</html>");
		lblThereAreNo.setForeground(Color.RED);
		lblThereAreNo.setBounds(20, 175, 382, 29);
		lblThereAreNo.setVisible(false);
		panel.add(lblThereAreNo);
		
		
		this.addWindowListener(new WindowAdapter(){		//on closing act as Cancel was pressed
            public void windowClosing(WindowEvent e){
            	btnlistener.ButtonPressed("Cancel");
            }
        });
		
		setLocationRelativeTo(null);
	}
	
	/**
	 * @return the btnCancel
	 */
	public JButton getBtnCancel() {
		return btnCancel;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(String selected) {
		this.selected = selected;
	}

	/**
	 * @return the btnOk
	 */
	public JButton getBtnOk() {
		return btnOk;
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


	public String[] getChecked() {
		ArrayList<String> streamID = new ArrayList<String>();
		for(int i = 0 ; i<streamTable.getRowCount() ;i++) {
			checked[i]=Boolean.valueOf((boolean) streamTable.getValueAt(i, 0));
			if((boolean) streamTable.getValueAt(i, 0)){
				streamID.add(stm.getDataId()[i]);
			}
		}
		String[] streamIds = new String[streamID.size()];
		streamID.toArray(streamIds);
		return streamIds;
	}
	
	/**
	 * sets checked table according to live streams
	 * @param checked the checked to set
	 */
	public void setChecked(String[] ids) {
		System.out.println("in set checked");
		for(int i = 0 ; i<streamTable.getRowCount() ;i++) {
			System.out.println("in set checked for");
			for(int j = 0 ;j<ids.length;j++) {
				if(stm.getDataId()[i].equals(ids[j]))
					streamTable.setValueAt(true,i,0);
				else
					streamTable.setValueAt(false,i, 0);
			}
			System.out.println("bool val at row "+i+ "is "+streamTable.getValueAt(i, 0));
		}
	}

	/**
	 * @return the btnlistener
	 */
	public ButtonListener getBtnlistener() {
		return btnlistener;
	}

	public void setData(List<LiveStream> data) { 
		
		stm.setData(data); 
		checked = new Boolean[data.size()];
		if(!data.isEmpty()) {
			lblThereAreNo.setVisible(false);
			btnOk.setEnabled(true);
			return;
		}
		btnOk.setEnabled(false);
		lblThereAreNo.setVisible(true);
	}
	
	
	public JLabel getLblThereAreNo() {
		return lblThereAreNo;
	}

	public void refresh() {
		stm.fireTableDataChanged();
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

	public void actionPerformed(ActionEvent ev) {
		btnlistener.ButtonPressed(ev.getActionCommand());
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

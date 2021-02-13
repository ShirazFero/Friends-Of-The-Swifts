package com.youtube.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.google.api.services.youtube.model.LiveStream;
import com.youtube.controller.StreamTableModel;

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
import java.awt.Toolkit;

/**
 * This class represents an Interval input form , which will appear when setting an interval,
 * or Starting a Live Broadcast
 * @author Evgeny Geyfman
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
	
	private JPanel jpanel;
	
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

	private void initPanel()
	{
		jpanel = new JPanel();
		jpanel.setBackground(SystemColor.textHighlightText);
		jpanel.setBounds(10, 11, 426, 246);
		getContentPane().add(jpanel);
		jpanel.setLayout(null);
	}
	
	private void initStreamTable()
	{
		stm = new StreamTableModel();
		streamTable = new JTable(stm);
		int widths[]= {50,50,50,200};
		setColumnWidths(streamTable,widths);
		streamTable.setEditingColumn(0);
		streamTable.setPreferredScrollableViewportSize(new Dimension(350, 100));
		streamTable.setFillsViewportHeight(true);
		
	}
	
	private void initScrollPane()
	{
		jsp = new JScrollPane(streamTable);
		jsp.setBounds(20,51,352,117);
		jpanel.add(jsp,BorderLayout.SOUTH);
	}
	
	private void initRequestIntervalLbl()
	{
		lblPleaseEnterInterval = new JLabel("Please Select Interval time in HH:MM format");
		lblPleaseEnterInterval.setBounds(10, 11, 262, 29);
		jpanel.add(lblPleaseEnterInterval);
	}
	
	private void initOkBtn()
	{
		btnOk = new JButton("Start");
		btnOk.setBounds(20, 212, 89, 23);
		btnOk.addActionListener(this);
		jpanel.add(btnOk);
	}
	
	private void initCancelBtn()
	{
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.setBounds(123, 212, 89, 23);
		jpanel.add(btnCancel);
	}
	
	private void initComboBox()
	{
		box = new JComboBox<Object>(intervals);
		box.setBounds(284, 14, 88, 23);
		box.setSelectedItem(selected);
		jpanel.add(box);
	}
	
	private void initRefreshBtn()
	{
		btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(226, 212, 89, 23);
		btnRefresh.addActionListener(this);
		jpanel.add(btnRefresh);
	}
	
	private void initWariningLbl()
	{
		lblThereAreNo = new JLabel("<html>There are no active streams available,<br>\r\nPlease start streaming on your Encoder, then press refresh</html>");
		lblThereAreNo.setForeground(Color.RED);
		lblThereAreNo.setBounds(20, 175, 382, 29);
		lblThereAreNo.setVisible(false);
		jpanel.add(lblThereAreNo);
	}
	
	public IntervalInputForm() {
		super("Starting Live Broadcast");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/YABAWB.png")));
		getContentPane().setBackground(SystemColor.textHighlightText);
		setSize(462, 307);
		getContentPane().setLayout(null);
		
		selected = "Non-Stop";	//set default non - stop
		
		initPanel();
		initStreamTable();
		initScrollPane();
		initRequestIntervalLbl();
		initOkBtn();
		initCancelBtn();
		initComboBox();
		initRefreshBtn();
		initWariningLbl();
		
		this.addWindowListener(new WindowAdapter(){		//on closing act as Cancel was pressed
            public void windowClosing(WindowEvent e){
            	btnlistener.ButtonPressed("Cancel");
            }
        });
		
		setLocationRelativeTo(null);
	}
	
	public JButton getBtnCancel() {
		return btnCancel;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public JButton getBtnOk() {
		return btnOk;
	}

	public JScrollPane getJsp() {
		return jsp;
	}

	public void setLblPleaseEnterInterval(JLabel lblPleaseEnterInterval) {
		this.lblPleaseEnterInterval = lblPleaseEnterInterval;
	}

	public JComboBox<Object> getBox() {
		return box;
	}

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

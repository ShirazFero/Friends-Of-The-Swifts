package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableColumnModel;

import com.google.api.services.youtube.model.LiveStream;

public class StreamPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JTable streamsTbl;
	
	private StreamTableModel stm;
	
	private ButtonListener btnlitsener;
	
	private Boolean checked[];
	
	private StatusBtnPanel stp;
	
	private JScrollPane jsp;
	
	public StreamPanel() {
		stm = new StreamTableModel();
		streamsTbl = new JTable(stm);
		int widths[]= {50,100,100,200};
		setColumnWidths(streamsTbl,widths);
		streamsTbl.setPreferredScrollableViewportSize(new Dimension(350, 100));
		streamsTbl.setFillsViewportHeight(true);
		streamsTbl.setEditingColumn(0);
		Border outerborder = BorderFactory.createTitledBorder("Streams Table");
		Border innerborder = BorderFactory.createEmptyBorder(5,5,5,5);
		setBorder(BorderFactory.createCompoundBorder(outerborder, innerborder));
		setLayout(null);
		jsp = new JScrollPane(streamsTbl);
		jsp.setLocation(10, 59);
		jsp.setSize(350, 140);
		add(jsp,BorderLayout.CENTER);
		stp = new StatusBtnPanel();
		stp.setSize(350, 37);
		stp.setLocation(10, 22);
		stp.getRefreshbtn().addActionListener(this);
		stp.getAddStreambtn().addActionListener(this);
		stp.getReomveStreambtn().addActionListener(this);
		add(stp,BorderLayout.SOUTH);
		
		
	}

	
	public JScrollPane getJsp() {
		return jsp;
	}

	/**
	 * @return the checked
	 */
	public Boolean[] getChecked() {
		for(int i = 0 ; i<streamsTbl.getRowCount() ;i++) {
			checked[i]=Boolean.valueOf((boolean) streamsTbl.getValueAt(i, 0));
		}
		return checked;
	}

	public void setBtnListener(ButtonListener listener) {
		this.btnlitsener=listener;
	}
	
	public void setData(List<LiveStream> data) { 
		stm.setData(data);   
		checked = new Boolean[data.size()];
	}
	
	public void refresh() {
		stm.fireTableDataChanged();
	}
	
	public StreamTableModel getStm() {
		return stm;
	}
	
	public void actionPerformed(ActionEvent ev) {
		btnlitsener.ButtonPressed( ev.getActionCommand());
		
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

package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
		stp = new StatusBtnPanel();
		stm = new StreamTableModel();
		streamsTbl = new JTable(stm);
		int widths[]= {50,50,50,200};
		setColumnWidths(streamsTbl,widths);
		streamsTbl.setPreferredScrollableViewportSize(new Dimension(350, 100));
		streamsTbl.setFillsViewportHeight(true);
		streamsTbl.setEditingColumn(0);
		stp.getRefreshbtn().addActionListener(this);
		stp.getAddStreambtn().addActionListener(this);
		stp.getReomveStreambtn().addActionListener(this);
		Border outerborder = BorderFactory.createTitledBorder("Streams Table");
		Border innerborder = BorderFactory.createEmptyBorder(5,5,5,5);
		setBorder(BorderFactory.createCompoundBorder(outerborder, innerborder));
		setLayout(new BorderLayout());
		jsp =new JScrollPane(streamsTbl);
		add(jsp,BorderLayout.CENTER);
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
	
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent ev) {
		JButton jb = (JButton) ev.getSource();
		btnlitsener.ButtonPressed(jb.getLabel());
		
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

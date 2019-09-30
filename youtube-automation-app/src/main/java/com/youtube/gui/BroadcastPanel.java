package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableColumnModel;

import com.google.api.services.youtube.model.LiveBroadcast;

public class BroadcastPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JTable broadcastTbl;
	
	private BroadcastTableModel btm;
	
	private Boolean[] checked;

	
	public BroadcastPanel(){
		btm = new BroadcastTableModel();
		broadcastTbl = new JTable(btm);
		int widths[]= {50,100,100,200};
		setColumnWidths(broadcastTbl,widths);
		broadcastTbl.setPreferredScrollableViewportSize(new Dimension(350, 100));
		broadcastTbl.setFillsViewportHeight(true);
		broadcastTbl.setEditingColumn(0);
		Border outerborder = BorderFactory.createTitledBorder("Broadcasts Table");
		Border innerborder = BorderFactory.createEmptyBorder(5,5,5,5);
		setBorder(BorderFactory.createCompoundBorder(outerborder, innerborder));
		setLayout(new BorderLayout());
		add(new JScrollPane(broadcastTbl),BorderLayout.CENTER);
	}

	public void setData(List<LiveBroadcast> data) { 
		btm.setData(data);   
		checked = new Boolean[data.size()];
	}
	
	public void refresh() {
		btm.fireTableDataChanged();
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

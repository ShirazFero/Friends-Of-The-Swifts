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
import javax.swing.table.TableColumnModel;

import com.google.api.services.youtube.model.LiveStream;

public class StatusPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTable streamstbl;
	
	private StatusTableModel stm;
	
	private JButton refreshbtn;
	private JButton getSelectedbtn;
	private ButtonListener btnlitsener;
	private Boolean checked[];
	public StatusPanel() {
		
		refreshbtn = new JButton("Refresh");
		getSelectedbtn = new JButton("Get Selected");
		stm = new StatusTableModel();
		streamstbl = new JTable(stm);
		int widths[]= {50,50,50,200};
		setColumnWidths(streamstbl,widths);
		streamstbl.setPreferredScrollableViewportSize(new Dimension(350, 100));
		streamstbl.setFillsViewportHeight(true);
		streamstbl.setEditingColumn(0);
		refreshbtn.addActionListener(this);
		getSelectedbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(int i = 0 ; i<streamstbl.getRowCount() ;i++) {
					checked[i]=Boolean.valueOf((boolean) streamstbl.getValueAt(i, 0));
					
				}
				btnlitsener.StreamsSelected(checked);
			}
			
		});
		add(new JScrollPane(streamstbl),BorderLayout.CENTER);
		JPanel btnpnl = new JPanel();
		add(btnpnl,BorderLayout.SOUTH);
		btnpnl.setLayout(new FlowLayout());
		btnpnl.add(refreshbtn);	
		btnpnl.add(getSelectedbtn);	
		
	}

	public void setBtnListener(ButtonListener listener) {
		this.btnlitsener=listener;
	}
	
	public void setData(List<LiveStream> data) { 
		this.stm.setData(data);   
		checked = new Boolean[data.size()];
	}
	
	public void refresh() {
		stm.fireTableDataChanged();
	}
	
	public StatusTableModel getStm() {
		return stm;
	}
	
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

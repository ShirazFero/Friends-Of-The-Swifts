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

import com.google.api.services.youtube.model.LiveBroadcast;
import javax.swing.JComboBox;
import javax.swing.JButton;

public class BroadcastPanel extends JPanel  {

	private static final long serialVersionUID = 1L;
	
	private JTable broadcastTbl;
	
	private BroadcastTableModel btm;
	
	private Boolean[] checked;

	private String selected;
	
	private ButtonListener btnlistener;
	
	private static BroadcastPanel instance;

	public BroadcastPanel(){
		btm = new BroadcastTableModel();
		broadcastTbl = new JTable(btm);
		int widths[]= {50,100,100,200};
		setColumnWidths(broadcastTbl,widths);
		broadcastTbl.setPreferredScrollableViewportSize(new Dimension(550, 100));
		broadcastTbl.setFillsViewportHeight(true);
		broadcastTbl.setEditingColumn(0);
		Border outerborder = BorderFactory.createTitledBorder("Broadcasts Table");
		Border innerborder = BorderFactory.createEmptyBorder(5,5,5,5);
		setBorder(BorderFactory.createCompoundBorder(outerborder, innerborder));
		setLayout(null);
		JScrollPane scrollPane = new JScrollPane(broadcastTbl);
		scrollPane.setBounds(10, 49, 350, 140);
		add(scrollPane,BorderLayout.CENTER);
		
		selected="all";
		
		String[] filter = {"all","active","completed","upcoming"};
		JComboBox<?> comboBox = new JComboBox<Object>(filter);
		comboBox.setBounds(261, 15, 99, 23);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected = filter[comboBox.getSelectedIndex()];
			}
		} );
		add(comboBox);
		
		JButton btnFilter = new JButton("Filter");
		btnFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				btnlistener.ButtonPressed(event.getActionCommand());
			}
		});
		btnFilter.setBounds(165, 15, 86, 23);
		add(btnFilter);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				btnlistener.ButtonPressed(event.getActionCommand());
			}
		});
		btnSelect.setBounds(10, 15, 89, 23);
		add(btnSelect);
	}
	
	public Boolean[] getChecked() {
		for(int i = 0 ; i<broadcastTbl.getRowCount() ;i++) {
			checked[i]=Boolean.valueOf((boolean) broadcastTbl.getValueAt(i, 0));
		}
		return checked;
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
	
	public void setBtnlistener(ButtonListener btnlistener) {
		this.btnlistener = btnlistener;
	}

	public String getSelected() {
		return selected;
	}
	
	public static BroadcastPanel getInstance() {
		if(instance==null)
			instance = new BroadcastPanel();
		return instance;
	}
}

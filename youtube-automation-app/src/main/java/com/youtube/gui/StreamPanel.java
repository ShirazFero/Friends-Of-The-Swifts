package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.google.api.services.youtube.model.LiveStream;
import com.youtube.utils.Constants;

import javax.swing.SwingConstants;

public class StreamPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JTable streamsTbl;
	
	private StreamTableModel stm;
	
	private ButtonListener btnlitsener;
	
	private Boolean checked[];
	
	private JScrollPane jsp;
	
	private JButton refreshbtn = new JButton("Refresh");
	
	private JButton AddStreambtn = new JButton("Add Stream");
	
	private JButton ReomveStreambtn = new JButton("Remove Streams");
	
	private JButton btnSetDescription; 
	
	private static StreamPanel instance;
	
	public static StreamPanel getInstance() {
		if(instance==null)
			instance = new StreamPanel();
		return instance;
	}


	public StreamPanel() {
		stm = new StreamTableModel();
		streamsTbl = new JTable(stm);
		int widths[]= {50,100,100,200};
		setColumnWidths(streamsTbl,widths);
		streamsTbl.setPreferredScrollableViewportSize(new Dimension(350, 100));
		streamsTbl.setFillsViewportHeight(true);
		streamsTbl.setEditingColumn(0);
		streamsTbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
		setLayout(null);
		jsp = new JScrollPane(streamsTbl);
		jsp.setBounds(10, 73, 378, 140);
		add(jsp,BorderLayout.CENTER);
		
		
		refreshbtn = new JButton("Refresh");
		refreshbtn.addActionListener(this);
		refreshbtn.setBounds(296, 22, 91, 37);
		
		AddStreambtn = new JButton(Constants.addStream);
		AddStreambtn.addActionListener(this);
		AddStreambtn.setSize(91, 37);
		AddStreambtn.setLocation(105, 22);
		
		
		ReomveStreambtn = new JButton(Constants.removeStream);
		ReomveStreambtn.setToolTipText("Select the streams to and press here to remove them");
		ReomveStreambtn.addActionListener(this);
		ReomveStreambtn.setSize(92, 37);
		ReomveStreambtn.setLocation(200, 22);
		ReomveStreambtn.setContentAreaFilled(true);
		
		add(refreshbtn);
		add(AddStreambtn);
		add(ReomveStreambtn);
		
		btnSetDescription = new JButton(Constants.setDescription);
		btnSetDescription.setHorizontalAlignment(SwingConstants.LEFT);
		btnSetDescription.setToolTipText("");
		btnSetDescription.setBounds(10, 22, 91, 37);
		btnSetDescription.addActionListener(this);
		add(btnSetDescription);
		
		instance=this;
		
	}

	
	/**
	 * @return the btnSetDescription
	 */
	public JButton getBtnSetDescription() {
		return btnSetDescription;
	}
	


	public JScrollPane getJsp() {
		return jsp;
	}

	/**
	 * @return the checked
	 */
	public String[] getChecked() {
		ArrayList<String> streamID = new ArrayList<String>();
		for(int i = 0 ; i<streamsTbl.getRowCount() ;i++) {
			checked[i]=Boolean.valueOf((boolean) streamsTbl.getValueAt(i, 0));
			if((boolean) streamsTbl.getValueAt(i, 0)){
				streamID.add(stm.getDataId()[i]);
			}
		}
		String[] streamIds = new String[streamID.size()];
		streamID.toArray(streamIds);
		return streamIds;
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
		resizeColumnWidth(streamsTbl);
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
	
	public void resizeColumnWidth(JTable table) {
	    final TableColumnModel columnModel = table.getColumnModel();
	    for (int column = 0; column < table.getColumnCount(); column++) {
	        int width = 15; // Min width
	        for (int row = 0; row < table.getRowCount(); row++) {
	            TableCellRenderer renderer = table.getCellRenderer(row, column);
	            Component comp = table.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        if(width > 300)
	            width=300;
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}


	/**
	 * @return the streamsTbl
	 */
	public JTable getStreamsTbl() {
		return streamsTbl;
	}
}

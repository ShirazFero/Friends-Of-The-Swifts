package com.youtube.gui;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.google.api.services.youtube.model.LiveStream;

public class StreamTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private Object[][] data;
	
	private int datalen;
	
	private String[] columnNames = {"Select","Name","Status","Stream Key" };
	
	
	
	public void setData(List<LiveStream> data) {
		this.data = new Object[data.size()][4];
		int i=0;
		for(LiveStream stream : data) {
			this.data[i][0]= Boolean.FALSE;
			this.data[i][1]= stream.getSnippet().getTitle();
			this.data[i][2]= stream.getStatus().getStreamStatus();
			this.data[i][3]= stream.getCdn().getIngestionInfo().getStreamName();
			i++;
		}
		datalen=data.size();
	}
		
	public String getColumnName(int col) {
		return columnNames[col];
	}

	public int getColumnCount() {
		return 4;
	}

	public int getRowCount() {
			
			return datalen;
	}
	
	public Class<?> getColumnClass(int columnIndex) {
        return data[0][columnIndex].getClass();
    }
	
	public Object getValueAt(int row, int col) {
		
		return data[row][col];
		
	}

	@Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
   //     super.setValueAt(aValue, rowIndex, columnIndex); by default empty implementation is not necesary if direct parent is AbstractTableModel
        data[rowIndex][columnIndex] = aValue; 
        fireTableCellUpdated(rowIndex, columnIndex);// notify listeners
    }
}

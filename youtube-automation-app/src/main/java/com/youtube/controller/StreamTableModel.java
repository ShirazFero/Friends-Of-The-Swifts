package com.youtube.controller;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.google.api.services.youtube.model.LiveStream;

public class StreamTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private Object[][] data;
	
	private int datalen;
	
	private String[] dataId;
	
	private String[] columnNames = {"Select","Name","Status","Stream Key" };
	
	public void setData(List<LiveStream> data) {
		this.dataId= new String[data.size()];
		this.data =  new Object[data.size()][4];
		int i=0;
		for(LiveStream stream : data) {
			this.data[i][0]= Boolean.FALSE;
			this.data[i][1]= stream.getSnippet().getTitle();
			this.data[i][2]= stream.getStatus().getStreamStatus();
			this.data[i][3]= stream.getCdn().getIngestionInfo().getStreamName();
			this.dataId[i]=stream.getId();
			i++;
		}
		datalen=data.size();
	}
		
	
	public String[] getDataId() {
		return dataId;
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
        data[rowIndex][columnIndex] = aValue; 
        fireTableCellUpdated(rowIndex, columnIndex);// notify listeners
    }
}

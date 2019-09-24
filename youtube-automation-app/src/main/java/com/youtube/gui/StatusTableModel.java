package com.youtube.gui;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.api.services.youtube.model.LiveStream;

public class StatusTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1575702782803571819L;
	
	private List<LiveStream> data;
	
	private String[] columnNames = {"Name","Status","Stream Key" };
	
	public void setData(List<LiveStream> data) {
		if(this.data!=null)
			this.data.clear();
		this.data=data;
	}
		
	@Override
	public String getColumnName(int col) {
		// TODO Auto-generated method stub
		return columnNames[col];
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		LiveStream stream = data.get(row);
		
		switch(col) {
		case 0: return stream.getSnippet().getTitle(); 						
		case 1: return stream.getStatus().getStreamStatus(); 				
		case 2:	return stream.getCdn().getIngestionInfo().getStreamName();
		}
		return null;
	}

}

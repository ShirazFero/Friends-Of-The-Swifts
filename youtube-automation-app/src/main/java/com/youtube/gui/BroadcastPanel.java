package com.youtube.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.youtube.controller.BroadcastTableModel;
import com.youtube.utils.Constants;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.SystemColor;

public class BroadcastPanel extends JPanel implements ActionListener  {

	private static final long serialVersionUID = 1L;
	
	private JTable broadcastTbl;
	
	private BroadcastTableModel btm;
	
	private Boolean[] checked;

	private String selected;
	
	private ButtonListener btnlistener;
	
	private static BroadcastPanel instance;

	private JScrollPane scrollPane;
	
	private JButton btnPreviousPage;

	private JButton btnNextPage;
	
	private JComboBox<?> comboBox1;
	
	private JLabel lblSelectType;
	
	private void initBroadcastTbl()
	{
		btm = new BroadcastTableModel();
		broadcastTbl = new JTable(btm);
		int widths[]= {50,100,100,200};
		setColumnWidths(broadcastTbl,widths);
		broadcastTbl.setPreferredScrollableViewportSize(new Dimension(550, 100));
		broadcastTbl.setFillsViewportHeight(true);
		broadcastTbl.setEditingColumn(0);
		broadcastTbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
	}
	
	
	private void initScrollPane()
	{
		scrollPane = new JScrollPane(broadcastTbl);
		scrollPane.setBounds(10, 55,382,263);
		add(scrollPane);
	}
	
	private void initFilterComboBox()
	{
		String[] filter = {"all","active","completed","upcoming"};
		JComboBox<?> comboBox = new JComboBox<Object>(filter);
		comboBox.setBounds(302, 19, 90, 25);
		comboBox.setSelectedItem("active");
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected = (String) comboBox.getSelectedItem();
				comboBox.setSelectedItem(selected);
				btnlistener.ButtonPressed("Filter");
			}
		} );
		add(comboBox);
	}
	
	private void initResultNumComboBox()
	{
		String[] numberOfResulsts = {"15","10","20","30","40","50"};
		comboBox1 = new JComboBox<Object>(numberOfResulsts);
		comboBox1.setBounds(183, 329, 45, 23);
		comboBox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Constants.NumberOfResulsts = (String) comboBox1.getSelectedItem();
				btnlistener.ButtonPressed("Filter");
			}
		} );
		add(comboBox1);
	}
	
	private void initUpdateDescriptionBtn()
	{
		JButton btnSelect = new JButton("Update Description");
		btnSelect.setToolTipText("Select the Broadcasts to update their description");
		btnSelect.addActionListener(this);
		btnSelect.setBounds(10, 19, 152, 25);
		add(btnSelect);
	}
	
	private void initNextPageBtn()
	{
		btnNextPage = new JButton("Next Page");
		btnNextPage.setBounds(285, 329, 107, 23);
		btnNextPage.setEnabled(false);
		btnNextPage.setVisible(false);
		btnNextPage.addActionListener(this);
		add(btnNextPage);
	}
	
	private void initPrevPageBtn()
	{
		btnPreviousPage = new JButton("Previous Page");
		btnPreviousPage.setBounds(10, 329, 133, 23);
		btnPreviousPage.setEnabled(false);
		btnPreviousPage.setVisible(false);
		btnPreviousPage.addActionListener(this);
		add(btnPreviousPage);
	}
	
	private void initSelectTypeLbl()
	{
		lblSelectType = new JLabel("Broadcast Status:");
		lblSelectType.setBounds(194, 21, 107, 20);
		add(lblSelectType);
	}
	
	public BroadcastPanel(){
		setBackground(SystemColor.textHighlightText);
		
		setLayout(null);
		
		initBroadcastTbl();
		initScrollPane();
		
		selected = "all";
		
		initFilterComboBox();
		initResultNumComboBox();
		
		initUpdateDescriptionBtn();
		initNextPageBtn();
		initPrevPageBtn();
		initSelectTypeLbl();
	}
	
	/**
	 * @return the comboBox1
	 */
	public JComboBox<?> getComboBox1() {
		return comboBox1;
	}

	/**
	 * @param instance the instance to set
	 */
	public  void setInstance(BroadcastPanel instance) {
		BroadcastPanel.instance = instance;
	}

	/**
	 * @return the btnPreviousPage
	 */
	public JButton getBtnPreviousPage() {
		return btnPreviousPage;
	}

	/**
	 * @return the btnNextPage
	 */
	public JButton getBtnNextPage() {
		return btnNextPage;
	}

	/**
	 * @return the scrollPane
	 */
	public JScrollPane getScrollPane() {
		return scrollPane;
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
		if(data.size()<1)
			comboBox1.setVisible(false);
		else
			comboBox1.setVisible(true);
	}
	
	public void refresh() {
		btm.fireTableDataChanged();
		resizeColumnWidth(broadcastTbl);
	}
	
	/**
	 * @return the broadcastTbl
	 */
	public JTable getBroadcastTbl() {
		return broadcastTbl;
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

	@Override
	public void actionPerformed(ActionEvent event) {
		btnlistener.ButtonPressed(event.getActionCommand());
		
	}
}

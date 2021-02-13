package com.youtube.gui;

import javax.swing.JFrame;
import org.json.simple.parser.ParseException;

import com.google.api.services.youtube.model.LiveStream;
import com.youtube.api.YouTubeAPI;
import com.youtube.controller.LiveStreamsHandler;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.AbstractListModel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class DescriptionFrame extends JFrame implements ActionListener ,ListSelectionListener{
	
	private static final long serialVersionUID = -4137850100812229621L;
	
	private ButtonListener btnListener;
	
	private JList<String> list ;
	
	private JTextArea textArea;
	
	private LiveStreamsHandler m_streamHandler;
	
	public void setBtnListener(ButtonListener btnListener) {
		this.btnListener = btnListener;
	}

	public DescriptionFrame(LiveStreamsHandler streamsHandler) throws IOException ,ParseException
	{
		setTitle("Set Description");
		getContentPane().setLayout(null);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/YABAWB.png")));
		m_streamHandler = streamsHandler;
		
		initOkBtn();
		initCancelBtn();
		initRequestLbl();
		initTextArea();
		initList();
		initTextAreaScrollPane();
		initStreamsScrollPane();
		initSetDescriptionBtn();
		initMaxCharLbl();
		
		addWindowListener(new WindowAdapter(){		//on closing act as Cancel was pressed
            public void windowClosing(WindowEvent e){
            	btnListener.ButtonPressed("Cancel");
            }
        });
		
		setSize(447,282);
		setLocationRelativeTo(null);
	}

	public void refresh() throws IOException {
		list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String titles[] = m_streamHandler.getStreamTitles();
			public int getSize() {
				return titles.length;
			}
			public String getElementAt(int index) {
				return titles[index];
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		btnListener.ButtonPressed(e.getActionCommand());
	}

	public String getText() {
		return textArea.getText();
	}
	
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		try {
		
			String StreamName = list.getSelectedValue();
			String description=null;
			LiveStream stream = null;
			if(StreamName!=null) {
				stream  =  m_streamHandler.getStreamByName(StreamName);
			}
			if(stream!=null) {
				description = stream.getSnippet().getDescription();
			}
	
			if (!arg0.getValueIsAdjusting() && description!=null) {
				   textArea.setText(description);
			}
				
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	private void initOkBtn() 
	{
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(125, 209, 89, 23);
		btnOk.addActionListener(this);
		getContentPane().add(btnOk);
	}
	
	private void initCancelBtn()
	{
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(219, 209, 89, 23);
		btnCancel.addActionListener(this);
		getContentPane().add(btnCancel);
	}
	
	private void initRequestLbl()
	{
		JLabel lblPleaseEnterThe = new JLabel("Please Select The desired Stream and edit it's description");
		lblPleaseEnterThe.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPleaseEnterThe.setBounds(10, 20, 361, 14);
		getContentPane().add(lblPleaseEnterThe);
	}
	
	private void initTextAreaScrollPane()
	{
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(124, 45, 280, 121);
		scrollPane_1.setViewportView(textArea);
		getContentPane().add(scrollPane_1);
	}
	
	private void initTextArea()
	{
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setText("Click on Stream's name to edit it's description");
		textArea.setVisible(true);
	}
	
	private void initStreamsScrollPane()
	{
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(13, 45, 101, 164);
		scrollPane.setViewportView(list);
		getContentPane().add(scrollPane);
		JLabel lblChooseStream = new JLabel("Choose Stream");
		lblChooseStream.setFont(new Font("Tahoma", Font.PLAIN, 9));
		scrollPane.setColumnHeaderView(lblChooseStream);
	}
	
	private void initList() throws IOException
	{
		list = new JList<>();
		
		list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String titles[] =  m_streamHandler.getStreamTitles();
			public int getSize() {
				return titles.length;
			}
			public String getElementAt(int index) {
				return titles[index];
			}
		});
		list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		list.addListSelectionListener(this);
	}
	
	private void initSetDescriptionBtn()
	{
		JButton SetDescription = new JButton("Set Description");
		SetDescription.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String newDescription = textArea.getText(); 
					int desLen = newDescription.getBytes("UTF-8").length;
					System.out.println("deslen: "+ desLen);
					if(desLen>5000) {
						JOptionPane.showMessageDialog(null,
								" Description is too long",
				                "Not Completed",
				                JOptionPane.ERROR_MESSAGE);
						return;
					}
					String StreamName = list.getSelectedValue();
					LiveStream stream =null;
					YouTubeAPI instance = YouTubeAPI.getInstance();
					if(StreamName!=null) {
						stream  = instance.getStreamByName(StreamName);
					}
					if(stream!=null) {
						instance.updateStreamDescription(newDescription, stream);
						JOptionPane.showMessageDialog(null,
								list.getSelectedValue() + " Description succssesfully changed.",
				                "Completed",
				                JOptionPane.PLAIN_MESSAGE);
						 m_streamHandler.refreshStreams();
					}
				} catch (IOException | ParseException e1 ) {
					e1.printStackTrace();
				}
			}
		});
		SetDescription.setBounds(260, 175, 144, 23);
		getContentPane().add(SetDescription);
	}
	
	private void initMaxCharLbl()
	{
		JLabel lblCharactersMax = new JLabel("Max 5000 Characters ");
		lblCharactersMax.setBounds(125, 177, 155, 14);
		getContentPane().add(lblCharactersMax);
	}

}

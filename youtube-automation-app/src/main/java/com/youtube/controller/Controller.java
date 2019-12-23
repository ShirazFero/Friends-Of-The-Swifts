package com.youtube.controller;

import java.awt.HeadlessException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveStream;
import com.youtube.api.CompleteBroadcast;
import com.youtube.api.CreateBroadcast;
import com.youtube.api.ErrorHandler;
import com.youtube.api.YouTubeAPI;
import com.youtube.gui.BroadcastPanel;
import com.youtube.gui.ButtonPanel;
import com.youtube.gui.IntervalPanel;
import com.youtube.gui.ProgressFrame;
import com.youtube.utils.Constants;

/**
 * This class handles data flow from the API to the GUI and backwards,
 * all it's functions control API requests , also it temporarily holds
 * the data that is being transported to the GUI.  
 * @author Evgeny Geyfman
 *
 */
public class Controller {
	
	private List<LiveStream> streams;			//holds currently presented streams
	
	private List<LiveBroadcast> broadcasts;		//holds currently presented broadcasts
	
	private TimerRunner timerRunner;			//holds current timer runner
	
	private  Boolean[] checkedStreams;			//holds checked streams from inputform

	private  Boolean[] checkedBroadcasts;		//holds checked broadcasts from inputform
	
	private static Controller instance;			//singleton instance
	
	/**
	 * Gets checked stream
	 * @return
	 */
	public Boolean[] getCheckedStreams() {
		return checkedStreams;
	}

	/**
	 * Sets checked streams from Interval input form
	 * @param checkedStreams
	 */
	public void setCheckedStreams(Boolean[] checkedStreams) {
		this.checkedStreams = checkedStreams;
	}
	
	/**
	 * Sets checked broadcasts from broadcast panel
	 * 
	 */
	public void setCheckedBroadcasts(Boolean[] checkedBroadcasts) {
		this.checkedBroadcasts = checkedBroadcasts;
	}
	
	/**
	 * Check witch broadcast was chosen to update description and request an update
	 * @param decription
	 * @throws IOException
	 */
	public void updateDescription(String decription) throws IOException {
		
		Constants.BroadcastsToUpdate = new ArrayList<LiveBroadcast>();
		for(int i = 0;i<checkedBroadcasts.length ;i++) {
			if(checkedBroadcasts[i]) {
				Constants.BroadcastsToUpdate.add(broadcasts.get(i));
			}
		}
		if(Constants.BroadcastsToUpdate.size()>100) {
			JOptionPane.showMessageDialog(null,"Please select less then 100 broadcasts","Server request ERROR",JOptionPane.ERROR_MESSAGE);
			return;
		}
			
		Constants.Description = decription;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new ProgressFrame().updateTask();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
	}
	
	/**
	 * This method calculates interval stop time, it adds interval time to to current time instance 
	 * and converts it to Date object which is compatible with Timer Object
	 * @return finishDatetime
	 */
	public  Date calcStopTime() {
		Interval interval = Interval.getInstance();
		LocalDateTime now = LocalDateTime.now();		//set start time
		System.out.println("interval start time: " + now);
		now = now.plusHours(interval.getHours());		//calculate added interval hours
		now = now.plusMinutes(interval.getMinutes());	//calculate added interval minutes
		
		//convert to Date Object applicable with Timer.schedule(Task,Date)
		Date finishDatetime = Date.from( now.atZone( ZoneId.systemDefault()).toInstant());
		
		//System.out.println("interval finish time: " + now.toString());
		//System.out.println("interval finish Date object: "+finishDatetime.toString());
		interval.setCorrentInterval(finishDatetime);	//set current end time
		return finishDatetime;
	}
	
	/**
	 * Stream getter
	 * @return
	 */
	public List<LiveStream> getStreams() {
		return streams;
	}
	
	/**
	 * Broadcasts getter
	 * @return
	 */
	public List<LiveBroadcast> getBroadcasts(){
		return broadcasts;
	}
	
	/**
	 * Retrieves  new list of streams , delete old one if exists
	 * @throws ParseException 
	 */
	public boolean refreshStreams()  {
		if(streams!=null)
			streams.clear();
		streams = YouTubeAPI.listStreams(null);
		if(streams==null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Retrieves  new list of broadcasts and filter it depending on args , delete old one if exists ,
	 * @param args
	 * @throws ParseException 
	 */
	public boolean refreshBroadcasts(String[] args)  {
		if(broadcasts!=null)
			broadcasts.clear();
		broadcasts = YouTubeAPI.listBroadcasts(args);
		if(broadcasts==null) {
			return false;
		}
		
		//handle page tokens
		BroadcastPanel bpanel = BroadcastPanel.getInstance();
		if(Constants.NextPageToken!=null)
			bpanel.getBtnNextPage().setEnabled(true);
		else
			bpanel.getBtnNextPage().setEnabled(false);
		if(Constants.PrevPageToken!=null)
			bpanel.getBtnPreviousPage().setEnabled(true);
		else
			bpanel.getBtnPreviousPage().setEnabled(false);
		
		return true;
		
	}
	
	/**
	 * This method inserts a new live stream to the database
	 * @param checked array indicates whether a stream was chosen or not
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws HeadlessException 
	 */
	public boolean addStream() throws IOException, HeadlessException {
		String[] args = new String[1];
		args[0]=JOptionPane.showInputDialog("please enter stream name");
		if(args[0] == null) {
			System.out.println("requset cancelled");
			return false;
		}
		/*if("".equals(args[0])) {
			//bad input
			JOptionPane.showMessageDialog(null,"No stream name entered","Not Completed",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
			
		int desLen = args[0].getBytes("UTF-8").length;
		System.out.println("deslen: "+ desLen);
		if(desLen>128 || desLen < 1) {
			//bad input
			JOptionPane.showMessageDialog(null,"Stream name is too long or too short try again","Not Completed",
	                JOptionPane.ERROR_MESSAGE);
			return false;
		}*/
		if(args[0]!=null && YouTubeAPI.createStream(args)) {
				refreshStreams();
				JOptionPane.showMessageDialog(null,args[0] + " Stream Added Successfully","Completed",JOptionPane.INFORMATION_MESSAGE);	
				LiveStream stream =YouTubeAPI.getStreamByName(args[0]);
				Constants.StreamDescription.put(stream.getId(),Constants.Description); //set default description
				return true;
			}
		return false;
		
	}
	
	/**
	 * This method removes a live stream from database
	 * @param checked array indicates whether a stream was chosen or not
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public void removeStream(Boolean[] checked) throws IOException {
		String[] args = new String[1];
		ArrayList<String> Badresults = new ArrayList<String>();
		for(int i=0;i<streams.size();i++) {
			if(checked[i]) {
				args[0]=streams.get(i).getSnippet().getTitle();
				if(!YouTubeAPI.deleteStream(args)) { //if didn't succeed
					Badresults.add(args[0]);		//add stream title to error massage
				}
				else { //remove from  description map
					Constants.StreamDescription.remove(streams.get(i).getId()); 
				}
					
			}
		}
		if(!Badresults.isEmpty()) {
			String massage  = "Following Streams weren't deleted:  ";
			for(String title : Badresults) {
				massage += title +" ERROR code: " + Constants.ErrorArgs[0] + ", ERROR message : "+ Constants.ErrorArgs[1]+ ",\r\n ";
			}
			ErrorHandler.HandleMultipleError(massage);
			
		}
		refreshStreams();
		JOptionPane.showMessageDialog(null,"Finished Deleting Streams","Completed",JOptionPane.INFORMATION_MESSAGE);	

	}
	
	/**
	 * This method starts new live broadcast to every stream that was chosen on the GUI
	 * @param checked array indicates whether a stream was chosen or not
	 * Constants.isLive array delivers data to loading tasks regarding the progress of starting broadcast.
	 * @throws InterruptedException
	 * @throws ParseException 
	 */
	public void startBroadcast(Boolean[] checked) throws InterruptedException {
		
		Constants.badResults =  new ArrayList<String>();
		if(Constants.LiveId!= null && !Constants.LiveId.isEmpty())
			Constants.LiveId.clear();
		Constants.LiveId = new ArrayList<String>();
		List<LiveStream> streams = filterStreams("active");		
		if(streams==null) {
			System.out.println("error retrieving streams");
			System.exit(1);
		}
		CreateBroadcast brd =null;					//Pointer to currently created broadcast
		int checkedStreamsCount = 0;				//checked stream counter
		for(int i = 0;i<checked.length;i++) {		//count checked streams
			if(checked[i])
				checkedStreamsCount++;
		}
		if(checkedStreamsCount>10) {				//100> num of broadcasts
			JOptionPane.showMessageDialog(null,"Please select less then 10 broadcasts","Server request ERROR",JOptionPane.ERROR_MESSAGE);
			return;
		}
		Constants.isLive = checkedStreamsCount*2;	//init flag array to mark starting progress of broadcast
		/*for(int i=0;i<Constants.isLive.length;i++)				
			Constants.isLive[i]=false;	*/					

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new ProgressFrame().loadTask();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		int j=0; // index for setting queue numbers for Constants.isLive 
		for(int i = checked.length-1 ; i>=0 ; i--) {
			if(checked[i])	{
				String[] args = new String[2];		// args[0] = title , args[1] = end time
				args[0]= streams.get(i).getSnippet().getTitle();
				if(Constants.IntervalBroadcast) {	//calculate interval end time and set it as args 
					LocalDateTime finTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(calcStopTime().getTime())
							, ZoneId.systemDefault());
					args[1]= finTime.toString()	;
				}
				else { 								//if it's indefinite Broadcast no end time is set
					args[1]=null;
				}
				if(args[1]!=null)
					System.out.println("starting "+ args[0]+" end time: "+args[1]);
				else
					System.out.println("starting "+ args[0]);
				brd =  new CreateBroadcast(args);				// initiate new CreateBroadcast object
				brd.setQueueNum(j);   			// set broadcasts queue index for Constants.isLive array
				brd.start();									// start new thread
				Thread.sleep(1000);								// wait 1 second, better handles server requests
				j+=2;
			}
		}
		
	}
	
	/**
	 * This method stops all active live broadcasts if they were chosen
	 * @param checked
	 * @throws InterruptedException 
	 * @throws ParseException 
	 */
	public void stopBroadcast(Boolean[] checked) throws InterruptedException {
		String[] args = {"refresh","active",null,null};
		List<LiveBroadcast> returnedList =YouTubeAPI.listBroadcasts(args);
		if(returnedList==null) {
			System.out.println("error fetching broadcasts");
			return;
		}
		for(int i=returnedList.size()-1 ; i>=0 ;i--) {
			if(checked[i]) {
				args[0]=broadcasts.get(i).getSnippet().getTitle();
				CompleteBroadcast cmpBrd = new CompleteBroadcast(args);
				cmpBrd.start();
				System.out.println("stopped "+ args[0]);
				Thread.sleep(1000);								// wait 1 second, better handles server requests
			}
		}
	}
	
	/**
	 * This method stops all active live broadcasts
	 * @throws InterruptedException 
	 * @throws ParseException 
	 */
	public void stopBroadcasts() throws InterruptedException {
		CompleteBroadcast cmpBrd = null;
		String[] args = new String[1];
		for(String ID : Constants.LiveId) {
			args[0] =  ID;
			cmpBrd = new CompleteBroadcast(args);
			cmpBrd.start();
			System.out.println("stopped "+ args[0]);
			Thread.sleep(1000);	// wait 1 second, better handles server requests
		}
	}

	/**
	 * This method creates and start a timer runner object 
	 * @throws InterruptedException
	 */
	public void startTimerRunner() throws InterruptedException {
		timerRunner = new TimerRunner(calcStopTime());
	}
	
	/**
	 * This method stops timer runner object
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public void cancelTimerRunner() throws InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, InvalidAlgorithmParameterException {
		timerRunner.stopIntervalBroadcast();
	}

	/**
	 * This method updates Inerval Panel with ne parameters:
	 * @param newStartTime
	 * @param stopTime
	 */
	public  void updateIntervalPanel(String newStartTime, String stopTime) {
		IntervalPanel intervalPanel = IntervalPanel.getInstance();
		//prompt new start time to interval panel
		intervalPanel.getLblstime().setText(newStartTime);
		//prompt new end time to interval panel
		intervalPanel.getFtime().setText(stopTime);
	}

	/**
	 * This method filters streams by there status={active,ready,inactive}
	 * @param filter
	 * @return filterdList
	 * @throws ParseException 
	 */
	public List<LiveStream> filterStreams(String filter) {
		refreshStreams();											// get latest list of streams from server
		List<LiveStream> filterdList = new ArrayList<LiveStream>();	//init return list
		for(LiveStream stream : streams) {
			if(stream.getStatus().getStreamStatus().equals(filter))
				filterdList.add(stream);
		}
		return filterdList;
	}

	/**
	 * This method saves current broadcast data on window closing event
	 */
	@SuppressWarnings("unchecked")
	public void saveData() {
		JSONObject obj = new JSONObject();
    	
    	if(Constants.RegularBroadcast) {
    		System.out.println("closing regular broadcast");
    		
    		//save regular broadcast flag
    		obj.put("Regular Broadcast", "ON");
    		obj.put("Interval Broadcast", "OFF");
    		
    		//save checked streams
    		JSONArray checkedStreamList = new JSONArray();
    		for(Boolean checked: checkedStreams)
    			checkedStreamList.add(checked);
    		obj.put("Stream List", checkedStreamList);
    		
    		//save Live broadcasts id's
    		JSONArray LiveIdList = new JSONArray();
    		for(String ID: Constants.LiveId)
    			checkedStreamList.add(ID);
    		obj.put("Live ID List", LiveIdList);
    		
    	}
    	else if(Constants.IntervalBroadcast) {
    		System.out.println("closing inteval broadcast");
    	
    		//save interval broadcast flag
    		obj.put("Regular Broadcast", "OFF");
    		obj.put("Interval Broadcast", "ON");
    		
    		//save current interval stop and start time
    		obj.put("Stop Time", Interval.getInstance().getCorrentInterval().toString());
    		obj.put("Start Time", IntervalPanel.getInstance().getLblstime().getText());
    		
    		//save interval
    		obj.put("Interval", Interval.getInstance().getInterval());
    		
    		//save checked streams
    		JSONArray checkedStreamList = new JSONArray();
    		for(Boolean checked: checkedStreams)
    			checkedStreamList.add(checked);
    		obj.put("Stream List", checkedStreamList);
    		System.out.println("stream list size: "+ checkedStreamList.size());
    		
    		//save Live broadcasts id's
    		JSONArray LiveIdList = new JSONArray();
    		for(String ID: Constants.LiveId)
    			checkedStreamList.add(ID);
    		obj.put("Live ID List", LiveIdList);
    	}
    	else {
    		obj.put("Regular Broadcast", "OFF");
    		obj.put("Interval Broadcast", "OFF");
    	}
    	
    	JSONObject MapObject = new JSONObject(Constants.StreamDescription);
    	obj.put("Map" ,MapObject);
    	
    	//obj.put("Description", Constants.Description);
    	
    	try (FileWriter file = new FileWriter(Constants.UserDataPath + Constants.Username + ".json")) {
			file.write(obj.toJSONString());
			System.out.println("Successfully Saved JSON Object to File...");
			System.out.println("\nJSON Object: " + obj);
			} catch (IOException e1) {

				e1.printStackTrace();
			}
	}
	
	/**
	 * This method loads current broadcast data when window is opening
	 */
	@SuppressWarnings({ "unchecked" })
	public void loadData() {
		
		JSONParser parser = new JSONParser();
		 
        try {
 				
            Object obj = parser.parse(new FileReader(Constants.UserDataPath + Constants.Username + ".json"));
            		
            JSONObject jsonObject = (JSONObject) obj;
 			
            Constants.StreamDescription = (HashMap<String,String>) jsonObject.get("Map");
           
            if((ArrayList<String>) jsonObject.get("Live ID List")!=null)
            	Constants.LiveId = (ArrayList<String>) jsonObject.get("Live ID List");
           
            String RegularBroadcast = (String) jsonObject.get("Regular Broadcast");
            IntervalPanel intervalPanel = IntervalPanel.getInstance();
           
            //refresh broadcast to active panel
        	String[] args = {"refresh","active",null,null};
		    refreshBroadcasts(args);
		    BroadcastPanel broadcastPanel =BroadcastPanel.getInstance();
    		broadcastPanel.setData(broadcasts);			//set new data
		    broadcastPanel.refresh();
            
            if(RegularBroadcast.equals("ON")){
            	
            	//start regular flag
            	Constants.RegularBroadcast = true;
            	//refresh broadcast to active panel
            	
            	//set checked streams
            	JSONArray streamList = (JSONArray) jsonObject.get("Stream List");
            	checkedStreams = new Boolean[streamList.size()];
            	streamList.toArray(checkedStreams);
            	
            	//set non stop label
                intervalPanel.getLblNotSet().setText("Non-Stop");
				
            }
            String IntervalBroadcast = (String) jsonObject.get("Interval Broadcast");
            if(IntervalBroadcast.equals("ON")){

            	//set interval panel
            	Constants.IntervalBroadcast = true;
            	
    		    //set checked streams
    		    JSONArray streamList = (JSONArray) jsonObject.get("Stream List");
            	checkedStreams = new Boolean[streamList.size()];
            	streamList.toArray(checkedStreams);
            	
            	
            	//set data to interval panel
                Interval interval =Interval.getInstance(); 				//load saved interval
                interval.setInterval((String) jsonObject.get("Interval"));
                intervalPanel.getLblNotSet().setText(interval.getHours() +
						 " Hours and " + interval.getMinutes() +" minutes");

                //load and cast stored stop time
                SimpleDateFormat dateformat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                String stp = (String) jsonObject.get("Stop Time");
				Date stoptime =  dateformat.parse(stp);
				System.out.println("stoptime on load: "+stoptime.toString());
				
				Date now = Date.from(LocalDateTime.now().atZone( ZoneId.systemDefault()).toInstant());
				
                if(stoptime.after(now)) {//if stop time hasn't passed yet
            		//set interval panel
                	System.out.println("setting interval panel");
                	updateIntervalPanel((String) jsonObject.get("Start Time"),stoptime.toString());
                	intervalPanel.getFtime().setVisible(true);						
                	intervalPanel.getLblstime().setVisible(true);
                	interval.setCorrentInterval(stoptime);										//set it to current interval
                	
                	
                }
                else { /*else stop current broadcasts ,start them again ,
                			and start timer runner with new stop time */
                	stopBroadcasts();
                	startBroadcast(checkedStreams);
                	stoptime = calcStopTime();
                	intervalPanel = IntervalPanel.getInstance();
                	updateIntervalPanel(now.toString(),interval.getCorrentInterval().toString());
                	intervalPanel.getLblstime().setVisible(true);
    				intervalPanel.getFtime().setVisible(true);
                }
                //start timer run
            	if(timerRunner==null) //if program was closed
            		timerRunner = new TimerRunner(stoptime);
            	else	//if user was logged out
            		timerRunner.scheduleTimer(stoptime);
            }
        	//toggle buttons on GUI
            System.out.println("toggleing buttons");
        	ButtonPanel btnPnl =  ButtonPanel.getInstance();
        	btnPnl.getStartIntBrdbtn().setEnabled(false); 
			btnPnl.getStopIntbtn().setEnabled(true);
            
        } catch (Exception e) {
            e.printStackTrace();
        }		
	}
	
	/**
	 * Singleton instance retriever
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws ParseException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public static Controller getInstance() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, InvalidAlgorithmParameterException {
		if(instance==null)
			instance = new Controller();
		return instance;
	}

	/** 
	 * This method registers new user locally
	 * @param username
	 * @param password
	 * @param email
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void registerUser(String username, String password, String email) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException {
		// TODO Auto-generated method stub
		
		JSONParser parser = new JSONParser();
		
		String data = fileDecrypt();  //decrypt file
		
		Object readObject = parser.parse(data);
        JSONObject jsonObject = (JSONObject) readObject;
        JSONObject userObject = new JSONObject();
		JSONObject userDetailsObject = new JSONObject();
		
		userDetailsObject.put("username",username);
		
		//encrypt password
		
		userDetailsObject.put("password",password);
		userDetailsObject.put("email",email);
		userDetailsObject.put("rememberpass","false");
		
		userObject.put("User",userDetailsObject);
		
		//get user list
		JSONArray userArray = (JSONArray) jsonObject.get("User List");
		
		//add new user object
		userArray.add(userObject);
		
		jsonObject.put("User List", userArray);
		
		//save new list to file
		FileWriter file = new FileWriter(System.getProperty("user.home")+"\\Documents\\AppUsers.json");
		file.write(jsonObject.toJSONString());
		System.out.println("Successfully Registerd New User And Saved JSON Object to File...");
		//System.out.println("\nJSON Object: " + jsonObject);
		file.close();
		
		fileEncrypt(jsonObject.toString());
		JOptionPane.showMessageDialog(null,"User Registerd Successfully","Completed",JOptionPane.INFORMATION_MESSAGE);	

	}

	/**
 	 * This method checks if user exists
	 * @param username
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public boolean userExists(String username) throws FileNotFoundException, IOException, ParseException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		// TODO Auto-generated method stub
		if(username!=null && !"".equals(username)) {
			String data = fileDecrypt(); //decrypt file
			JSONParser parser = new JSONParser();
			Object readObject = parser.parse(data);
			JSONObject jsonObject = (JSONObject) readObject;
			JSONArray userArray = (JSONArray) jsonObject.get("User List");
			for(int i=0;i<userArray.size();i++) {
				JSONObject user = (JSONObject) userArray.get(i);
				user = (JSONObject) user.get("User");
				String userInList = (String) user.get("username");
				if(username.equals(userInList))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * This method validates the user to be logged in
	 * @param username
	 * @param password
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public boolean validateUser(String username, String password) throws FileNotFoundException, IOException, ParseException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		// TODO Auto-generated method stub
		final Path path = Paths.get(Constants.AppUserPath);
		
		JSONParser parser = new JSONParser();
		
		if(Files.exists(path)) {     	//check if there's a file of saved data
			//decrypt file
			String data = fileDecrypt();
			JSONArray userArray = (JSONArray) ((JSONObject) parser.parse(data)).get("User List");
			String decryptedPassword = null ,email =null; 	// to hold the encrypted password exported from the file
			for(int i=0;i<userArray.size();i++) {
				JSONObject	user = (JSONObject) ((JSONObject) userArray.get(i)).get("User");
				String userInList = (String) user.get("username");
				if(username!=null && username.equals(userInList)) {
					decryptedPassword = (String) user.get("password");
				    email = (String) user.get("email");
				}
			}
			if(password.equals(decryptedPassword)) {
				Constants.UserEmail = email;
				return true;
			}
				
            return false;
		}
		return true;
	}
	
	/**
	 * This method loads initial user details if they exist otherwise it generates files to hold the details
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws InvalidAlgorithmParameterException
	 */
	@SuppressWarnings({ "unchecked" })
	public void initData() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, IOException, ParseException, InvalidAlgorithmParameterException {
		
		Constants.StreamDescription = new HashMap<String,String>();
		
		final Path path = Paths.get(System.getProperty("user.home")+"\\Documents\\info.json");
		
		if(!Files.exists(path)) {
			
			Constants.SecretKey = KeyGenerator.getInstance("AES").generateKey();
			
			// get base64 encoded version of the key
			String encodedKey = Base64.getEncoder().encodeToString(Constants.SecretKey.getEncoded());
			
			JSONObject obj = new JSONObject();
			obj.put("info", encodedKey);
			try (FileWriter file = new FileWriter(Constants.InfoPath)) {
    			file.write(obj.toJSONString());
    			System.out.println("Successfully created first user list JSON Object File...");
    			//System.out.println("\nJSON Object: " + obj);
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
		}
		else {
			  JSONParser parser = new JSONParser();
			  JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(Constants.InfoPath));
			  String key =(String)jsonObject.get("info");
			  // decode the base64 encoded string
			  byte[] decodedKey = Base64.getDecoder().decode(key);
			  // rebuild key using SecretKeySpec
			  Constants.SecretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
			  
		}
		
		final Path path1 = Paths.get(Constants.AppUserPath);

		if(!Files.exists(path1)) {
			JSONObject obj = new JSONObject();
			JSONArray userArray = new JSONArray();
			obj.put("User List", userArray);
			try (FileWriter file = new FileWriter(Constants.AppUserPath)) {
    			file.write(obj.toJSONString());
    			System.out.println("Successfully created first user list JSON Object File...");
    			//System.out.println("\nJSON Object: " + obj);
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
			
			fileEncrypt(obj.toString());
		}
		else{
			
			String data = fileDecrypt();
			//System.out.println(data);
			JSONParser parser = new JSONParser();
			JSONArray userArray = (JSONArray) ((JSONObject) parser.parse(data)).get("User List");
			Iterator<JSONObject> Iterator = userArray.iterator();
			Constants.SavedUsers = new String[userArray.size()]; 
			int i=0;
			while (Iterator.hasNext()) {
				Constants.SavedUsers[i++] = (String) ((JSONObject) Iterator.next().get("User")).get("username");
			}
		}
	}
	
	/**
	 * Encrypt user file
	 * @param data
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	private void fileEncrypt(String data) throws InvalidKeyException, FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
		FileEncrypterDecrypter fileEncDec = new FileEncrypterDecrypter(Constants.SecretKey,"AES/CBC/PKCS5Padding");
		fileEncDec.encrypt(data,Constants.AppUserPath);
	}
	
	/**
	 * Decrypt user file
	 * @return
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	private String fileDecrypt() throws InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
		FileEncrypterDecrypter fileEncDec = new FileEncrypterDecrypter(Constants.SecretKey,"AES/CBC/PKCS5Padding");
		return fileEncDec.decrypt(Constants.AppUserPath);
	}

	/**
	 * 
	 * @return
	 */
	public String[] getStreamTitles() {
		// TODO Auto-generated method stub
		String[] titles = new String[streams.size()];
		int index = 0;
		for(LiveStream stream :streams) {
				titles[index++] = stream.getSnippet().getTitle();
			
		}
		return titles;
	}
	
	public TimerRunner getTimerRunner() {
		return timerRunner;
	}
	
	/**
	 * 
	 * @param title
	 * @return
	 */
	public String getID(String title){
		for(LiveStream stream :streams) {
			if(title.equals(stream.getSnippet().getTitle()))	 
				return stream.getId();
			
		}
		return null;
	}
}

package com.youtube.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.youtube.api.ErrorHandler;
import com.youtube.utils.Constants;

public class AppBootLoader {
	
	public void InitData() 
	{
		try {
			if(!FileExists(Constants.InfoPath)) {
				initBootData();
			}
			else {
				loadBootData();
			}
			if(!FileExists(Constants.AppUserPath)) {
				initUsersFile();
			}
			else {
				loadFromUsersFile();
			}
		} catch (NoSuchAlgorithmException | SecurityException | IOException | ParseException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			ErrorHandler.HandleError("Boot", e.toString());
		}
	}

	public boolean userExistsInFile(String username) throws FileNotFoundException, IOException, ParseException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		if(username != null && !"".equals(username)) {
			JSONArray userArray =  getUsers();
			for(int i=0 ; i < userArray.size() ; ++i) {
				JSONObject user = (JSONObject) ((JSONObject) userArray.get(i)).get("User");
				String userInList = (String) user.get("username");
				if(username.equals(userInList)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean userExists(String username) throws FileNotFoundException, IOException, ParseException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException 
	{
		if(Constants.SavedUsers != null) {
			for(String user : Constants.SavedUsers)  {
				if(username.equals(user)) {
					return true;
				}
			}
		}
        return false;
	}
	
	@SuppressWarnings("unchecked")
	public void addUserToFile(String username)  throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException
	{
		JSONObject newUser = createUserDetailsObject(username);
		JSONArray userArray = getUsers();
		userArray.add(newUser);
		Constants.SavedUsers.add(username);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("User List", userArray);
		fileEncrypt(jsonObject.toString());
	}

	public JSONArray getUsers() throws InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, ParseException 
	{
		String data = fileDecrypt(); 
		JSONObject jsonObject = (JSONObject) ((Object) new JSONParser().parse(data));
		return (JSONArray) jsonObject.get("User List");
	} 
	
	public void fileEncrypt(String data) throws InvalidKeyException, FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
		FileEncrypterDecrypter fileEncDec = new FileEncrypterDecrypter(Constants.SecretKey,"AES/CBC/PKCS5Padding");
		fileEncDec.encrypt(data,Constants.AppUserPath);
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject createUserDetailsObject(String username) 
	{
		JSONObject newUser = new JSONObject();
		JSONObject userDetailsObject = new JSONObject();
		userDetailsObject.put("username",username);
		newUser.put("User",userDetailsObject);
		return newUser;
	}

	private boolean FileExists(String fileName) {
		return Files.exists(Paths.get(fileName));
	}
	
	@SuppressWarnings("unchecked")
	private void initUsersFile() throws SecurityException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException 
	{
		JSONObject obj = new JSONObject();
		JSONArray userArray = new JSONArray();
		obj.put("User List", userArray);
		try (FileWriter file = new FileWriter(Constants.AppUserPath)) {
			file.write(obj.toJSONString());
			System.out.println("Successfully created first user list JSON Object File...");
		} catch (IOException e1) {
			e1.printStackTrace();
			ErrorHandler.HandleError("Boot", e1.toString());
		}
		fileEncrypt(obj.toString());
	}
	
	@SuppressWarnings("unchecked")
	private void loadFromUsersFile() throws InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		String data = fileDecrypt();
		JSONArray userArray = (JSONArray) ((JSONObject) parser.parse(data)).get("User List");
		Iterator<JSONObject> Iterator = userArray.iterator();
		Constants.SavedUsers = new ArrayList<String>(userArray.size()); 
		while (Iterator.hasNext()) {
			Constants.SavedUsers.add((String) ((JSONObject) Iterator.next().get("User")).get("username"));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initBootData() throws NoSuchAlgorithmException, SecurityException, IOException 
	{
		JSONObject obj = new JSONObject();
		Constants.SecretKey = KeyGenerator.getInstance("AES").generateKey();
		// get base64 encoded version of the key
		String encodedKey = Base64.getEncoder().encodeToString(Constants.SecretKey.getEncoded());
		
		obj.put("info", encodedKey);
		try (FileWriter file = new FileWriter(Constants.InfoPath)) {
			file.write(obj.toJSONString());
			Constants.DebugPrint("Successfully created first user list JSON Object File...");
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.HandleError("Boot", e.toString());
		}
	}
	
	private void loadBootData() throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(Constants.InfoPath));
		if(jsonObject != null) {
			String key =(String)jsonObject.get("info");
			// decode the base64 encoded string
			byte[] decodedKey = null;
			if(key != null) {
				decodedKey = Base64.getDecoder().decode(key);
				// rebuild key using SecretKeySpec
				Constants.SecretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
			}
		}
	}
	
	private String fileDecrypt() throws InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
		FileEncrypterDecrypter fileEncDec = new FileEncrypterDecrypter(Constants.SecretKey,"AES/CBC/PKCS5Padding");
		return fileEncDec.decrypt(Constants.AppUserPath);
	}

}

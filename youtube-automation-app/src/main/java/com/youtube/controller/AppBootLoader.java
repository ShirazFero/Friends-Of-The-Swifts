package com.youtube.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Iterator;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.youtube.api.ErrorHandler;
import com.youtube.utils.Constants;

public class AppBootLoader {
	
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
public void initData() 
{
	try {
		if(firstBoot()) {
			initBootData();
		}
		else {
			loadBootData();
		}
		if(firstUserBoot()) {
			initUsersFile();
		}
		else{
			loadFromUsersFile();
		}
		initModuleProperties();
	} catch (NoSuchAlgorithmException | SecurityException | IOException | ParseException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
		e.printStackTrace();
		ErrorHandler.HandleLoadError(e.toString());
	}
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

Iterator<JSONObject> Iterator = userArray.iterator();
Constants.SavedUsers = new String[userArray.size()]; 
int i=0;
while (Iterator.hasNext()) {
	Constants.SavedUsers[i++] = (String) ((JSONObject) Iterator.next().get("User")).get("username");
}
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
	
private boolean firstBoot() {
	final Path path = Paths.get(System.getProperty("user.home")+"\\Documents\\info.json");
	return !Files.exists(path);
}	

private boolean firstUserBoot() {
	final Path Userpath1 = Paths.get(Constants.AppUserPath);
	return !Files.exists(Userpath1);
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
		ErrorHandler.HandleLoadError(e1.toString());
	}
	fileEncrypt(obj.toString());
}

private void loadFromUsersFile() throws InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, ParseException {
	JSONParser parser = new JSONParser();
	String data = fileDecrypt();
	JSONArray userArray = (JSONArray) ((JSONObject) parser.parse(data)).get("User List");
	@SuppressWarnings("unchecked")
	Iterator<JSONObject> Iterator = userArray.iterator();
	Constants.SavedUsers = new String[userArray.size()]; 
	int i=0;
	while (Iterator.hasNext()) {
		Constants.SavedUsers[i++] = (String) ((JSONObject) Iterator.next().get("User")).get("username");
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
		if(Constants.Debug) {
			System.out.println("Successfully created first user list JSON Object File...");
		}
	} catch (IOException e) {
		e.printStackTrace();
		ErrorHandler.HandleLoadError(e.toString());
	}
}

private void initModuleProperties() throws IOException, ParseException {
	JSONParser parser = new JSONParser();
	Reader reader = new InputStreamReader(Controller.class.getResourceAsStream("/tmp.json"));
	JSONObject jsonObject = (JSONObject) parser.parse(reader);
	jsonObject = (JSONObject) jsonObject.get("installed");
	Constants.myBytes = (String) jsonObject.get("client_id");
	Constants.myBytes = (String) Constants.myBytes.subSequence(0, 12);
}

private void loadBootData() throws FileNotFoundException, IOException, ParseException {
	JSONParser parser = new JSONParser();
	JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(Constants.InfoPath));
	if(jsonObject!=null) {
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



}

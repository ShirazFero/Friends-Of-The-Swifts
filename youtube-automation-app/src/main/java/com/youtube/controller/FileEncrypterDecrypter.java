package com.youtube.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class FileEncrypterDecrypter {

	private SecretKey secretKey;
	private Cipher cipher;

	public FileEncrypterDecrypter(SecretKey secretKey, String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
	    this.secretKey = secretKey;
	    this.cipher = Cipher.getInstance(transformation);
	}
	
	public void encrypt(String content, String fileName) throws InvalidKeyException, FileNotFoundException, IOException {
	    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	    byte[] iv = cipher.getIV();
	 
	    try (FileOutputStream fileOut = new FileOutputStream(fileName);
	      CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {
	        fileOut.write(iv);
	        cipherOut.write(content.getBytes());
	    }
	}
	
	public String decrypt(String fileName) throws FileNotFoundException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
	    String content;
	 
	    try (FileInputStream fileIn = new FileInputStream(fileName)) {
	        byte[] fileIv = new byte[16];
	        fileIn.read(fileIv);
	        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));
	 
	        try (
	                CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
	                InputStreamReader inputReader = new InputStreamReader(cipherIn);
	                BufferedReader reader = new BufferedReader(inputReader)
	            ) {
	 
	            StringBuilder sb = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            content = sb.toString();
	        }
	 
	    }
	    return content;
	}
}

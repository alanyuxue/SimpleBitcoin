import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.lang.Math.*;
import java.net.*;
import java.io.*;
import java.io.PrintWriter;
import java.lang.*;
import java.util.*;
import java.security.*;
import java.nio.charset.*;

public class Miner3{
        static String message = "10ChrisCoinToBob"; //For testing
        static String date = "170512"; //For testing
        
        static int difficulty = 4;
        static String zeroString = computeZeroString(difficulty);
        static int nonce = 0;
        static int counter = 0;

		private static String PATH = ""; ///FILE Location
	
    
	// Completes a SHA-256 Hash of the string input
    private static String hash(String input) throws NoSuchAlgorithmException
    {
        String result = "";

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte hash[] = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuffer hexString = new StringBuffer();
            
            for(int i = 0; i < hash.length; i++){
                hexString.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }
            
            result = hexString.toString();
        }
        catch(NoSuchAlgorithmException e){
            throw new NoSuchAlgorithmException("Failure");
        }
        
        return result;
    }
    

	//initiates the proof of work
    private static void proof() throws NoSuchAlgorithmException
    {
        long startTime = System.currentTimeMillis();
        while(true){
            String testString =  date + message + nonce;
        
            try{ 
                String hashcode = hash(testString);
                
                if(hashcode.substring(0, difficulty).equals(zeroString)){
                    System.out.printf("\n\nNonce: "+ nonce + "\nFound after " + counter + " attempts");
                    break;
                }
            }
            catch(NoSuchAlgorithmException e){
                throw new NoSuchAlgorithmException("Failure");
            }
            
            counter++;
            nonce++;
        }
        long endTime = System.currentTimeMillis();
        
        System.out.println(" ");
        System.out.println("Duration : " + (endTime - startTime)+" milli seconds");
    }
    
	//Create a zero string to match difficulty
    private static String computeZeroString(int d)
    {
        String bits = "";
        
        for(int i = 0; i < d; i++){
            bits += "0";
        }
        
        return bits;
    }  
	//Save data to a file at PATH
	public static void saveData(int nonce){
		

		File file = new File(PATH);

        FileWriter writer = null;

		String contentToSave = Integer.toString(nonce);

	        try{
       			writer = new FileWriter(file, true);
        		writer.append(contentToSave+"\n");
			writer.close();
        	}
        	catch (Exception e){
        	    e.printStackTrace();
        	}	

	}

	//Read data from a file
	public static void readData(){

		File file = new File(PATH);

		FileReader reader = null;

		String line;

		try{
			reader = new FileReader(file);
			BufferedReader bufReader = new BufferedReader(reader);
			while((line = (bufReader.readLine())) != null){
				// 'line' will read file line by line				
			}
			
		}
		catch(Exception e){}
		
	}




	public static void main(String[] args){
			
		try{
			proof();
		}
		catch(NoSuchAlgorithmException e){}	
	}  
}

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.lang.*;

public class Miner{
    private static String message;
    private static String date;
    private static int difficulty = 4;
    private static String zeroString = computeZeroString(difficulty);
    private static int nonce = 0;
    private static int counter = 0;

    private static String PATH = ""; ///FILE Location

    public Miner(String msg, String time) {
        message = msg;
        date = time;
    }
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
    public void proof() throws NoSuchAlgorithmException
    {
        long startTime = System.currentTimeMillis();
        while(true){
            String testString =  date + message + nonce;

            try{
                String hashcode = hash(testString);

                if(hashcode.substring(0, difficulty).equals(zeroString)){
                    System.out.println("\nNonce: "+ nonce + "\nFound after " + counter + " attempts");
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

        System.out.println("\nDuration : " + (endTime - startTime)+" milli seconds" + "\nTransaction finished!");
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
}
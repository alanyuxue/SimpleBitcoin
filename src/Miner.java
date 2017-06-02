import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.lang.*;

public class Miner{
    public static String PATH = "";

    private static String message;
    private static String timeStamp;
    //Optimal Difficulty 16777216
    private static int diffDivisor = 16777216;
    private static String prevHash = "0"; // Probably should put this in for practical purposes but don't really need to.
    private static String currHash = "0";
    private static final BigInteger maxDiff = maxDiffCalc(); //max difficulty
    private static BigInteger targetDiff = maxDiff.divide(BigInteger.valueOf(diffDivisor)); //current difficulty

    private static int bits = 4;
    private static String zeroString = computeZeroString(bits);
    private static int maxNonce = Integer.MAX_VALUE;
    private static int nonce = 0;
    private static int counter = 0;

    public Miner(String msg, String time) {
        message = msg;
        timeStamp = time;
    }
    ///////////////////////// Proof Of Work Start /////////////////////////
    private static String hash(String input) throws NoSuchAlgorithmException
    {
        String result = "";

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte hash[] = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for(int i = 0; i < hash.length; i++){
                hexString.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }

            result = hexString.toString();
        }
        catch(NoSuchAlgorithmException e){
            throw new NoSuchAlgorithmException("Failure");
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Returns a double for mins taken - ONLY FOR TESTING using the DiffSetter class to get avg time.
     */
    public String proof() throws NoSuchAlgorithmException
    {
        long startTime = System.nanoTime();
        while(nonce <= maxNonce){
            String testString =  prevHash + timeStamp + message + nonce;

            try{
                String hashcode = hash(testString);

                if(hashcode.substring(0,bits).equals(zeroString) && checkDifficulty(hashcode)){
                    currHash = hashcode;
                    System.out.printf("\n\nNonce: "+ nonce + "\nFound after " + counter + " attempts");
                    break;
                }
            }
            catch(NoSuchAlgorithmException e){
                throw new NoSuchAlgorithmException("Failure");
            }

            if(nonce + 1 > maxNonce){
                incrementTimeStamp();
                nonce = -1; //resets nonce
            }

            counter++;
            nonce++;

        }
        long endTime = System.nanoTime();

        double duration = ((endTime - startTime)/1000000)/60000;

        System.out.println("\nDuration : " + duration + " mins.");

        return currHash;
    }

    static private String computeZeroString(int d)
    {
        String bits = "";

        for(int i = 0; i < d; i++){
            bits += "0";
        }

        return bits;
    }

    static private boolean checkDifficulty(String hash)
    {
        BigInteger hex1 = new BigInteger(hash, 16);

        if(hex1.compareTo(targetDiff) > 0){
            return false;
        }
        else{
            return true;
        }
    }

    static private BigInteger maxDiffCalc()
    {
        BigInteger i = BigInteger.valueOf((long)Math.pow(2, 32));
        i = i.pow(8);
        return i;
    }

    static private void incrementTimeStamp()
    {
        timeStamp = ""+(Integer.parseInt(timeStamp) + 1);
    }

    static public void getPreviousHash(String accountName)
    {
        File file = new File(PATH + accountName + ".txt");

        FileReader reader = null;

        String line;
        String tempLine = "";

        try{
            if (!file.exists()) file.createNewFile();
            reader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(reader);
            int lineCounter = 1;
            while((line = (bufReader.readLine())) != null){
                if((lineCounter%2)==0){

                    tempLine = "";

                    for(int i = 18; i < line.length(); i++){
                        tempLine = tempLine + line.charAt(i);
                    }
                }
                lineCounter++;
            }

        } catch(Exception e){e.printStackTrace();}

        if(tempLine.equals("")){return;}

        prevHash = tempLine;
    }
///////////////////////// Proof Of Work Finish /////////////////////////

///////////////////////// Verification Start /////////////////////////
    //UPDATES LOCAL SAVE OF CLIENT ACCOUNT
    public static void saveClientAccounts(String accountName, int balance, String tHash){

        File file = new File(PATH+accountName+".txt");
        FileWriter writer = null;
        try{
            if (!file.exists()) file.createNewFile();
            writer = new FileWriter(file, true);
            writer.append("Account Name: " + accountName + "\n");
            writer.append("Transaction Hash: " + tHash + "\n");
            writer.append("Nonce: " + nonce + "\n");
            writer.append("Balance: " + balance + "\n");
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    //RETURNS THE FINAL BALANCE OF AN ACCOUNT
    public static int readFinalBalance(String accountName){

        File file = new File(PATH + accountName + ".txt");

        FileReader reader = null;

        String line;
        String tempLine = "";

        try{
            if (!file.exists()) file.createNewFile();
            reader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(reader);
            int lineCounter = 1;
            while((line = (bufReader.readLine())) != null){
                if((lineCounter%4)==0){

                    tempLine = "";

                    for(int i = 9; i < line.length(); i++){
                        tempLine = tempLine + line.charAt(i);
                    }
                }
                lineCounter++;
            }

        } catch(Exception e){e.printStackTrace();}

        if(tempLine.equals("")){return 0;}

        return Integer.parseInt(tempLine);
    }

    //COMPARES THE BALANCE COMMUNICATED BY THE CLIENT TO WHAT IS SAVED BY THE MINER
    public static boolean accountBalanceVerification(String accountName, int allegedBalance){

        return (readFinalBalance(accountName) == allegedBalance);

    }
//////////////////// Verification Finished ////////////////////////////
}

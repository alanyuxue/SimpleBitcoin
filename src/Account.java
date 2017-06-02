import java.io.*;

public class Account {
    private static String PATH = "";

    private String accountName;
    private int balance = 0;

    //Initiate Account with Final Balance
    public Account(String accountName){
        this.accountName = accountName;
        this.balance = readFinalBalance();
    }

    //Initiate Account with Set Balance
    public Account(String accountName, int balance){
        this.accountName = accountName;
        this.balance = balance;
    }

    //Save data to a file for a transfer of cash
    public void sendMoney(int transferAmount){

        File file = new File(PATH + accountName + ".txt");
        FileWriter writer = null;

        try{
            writer = new FileWriter(file, true);
            writer.append("Account: " + accountName + "\n");
            writer.append("Opening Balance: " + balance + "\n");
            writer.append("Transaction Amount: " + transferAmount + "\n");
            balance = balance - transferAmount;
            writer.append("Closing Balance: " + balance  + "\n");
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void receiveMoney(int transferAmount){

        File file = new File(PATH + accountName + ".txt");
        FileWriter writer = null;

        try{
            if (!file.exists()) file.createNewFile();
            writer = new FileWriter(file, true);
            writer.append("Account: " + accountName + "\n");
            writer.append("Opening Balance: " + balance + "\n");
            writer.append("Transaction Amount: " + transferAmount + "\n");
            balance = balance + transferAmount;
            writer.append("Closing Balance: " + balance  + "\n");
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Read the final balance from an account
    public int readFinalBalance(){

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

                    for(int i = 17; i < line.length(); i++){
                        tempLine = tempLine + line.charAt(i);
                    }
                }
                lineCounter++;
            }
        }
        catch(Exception e){e.printStackTrace();}

        if(tempLine.equals("")){return 0;}

        return Integer.parseInt(tempLine);

    }
    public String getAccountName(){return accountName;}
    public int getBalance(){return balance;}
}

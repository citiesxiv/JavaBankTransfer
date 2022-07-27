
/**
 *
 * @author Anthony Bonasso
 */
import oracle.jdbc.pool.OracleDataSource;
import java.sql.SQLException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import BankDB.AccountDB;
public class AccountMng {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       String url =  "jdbc:oracle:thin:n01160956/oracle@calvin.humber.ca:1521:grok";
       String file = "Transactions.txt";
       try{
           OracleDataSource dbs = new OracleDataSource();
           dbs.setURL(url);
           AccountDB adb = new AccountDB(dbs);
           adb.CreateTable();
           adb.MakeAccounts(file);
           adb.CheckAccounts();
           CheckDailies(adb, file);
           System.out.println("Done Check..........\nPrinting Results..........");
           adb.CheckAccounts();
       }
       catch(IOException e){
           System.out.println(e.getMessage());
       }
       catch(SQLException ex){
           System.out.println(ex.getMessage());
       }
    }
   public static void CheckDailies(AccountDB adb, String inFile) throws IOException, SQLException{
       System.out.println("Checking Daily Transactions.....");
       try(BufferedReader read = new BufferedReader(new FileReader(inFile))){
           String line = "";
           while ((line = read.readLine()) != null){
               String[] inputs = line.split(" ");
               if(inputs[0].equalsIgnoreCase("transfer")){
                   adb.ManageTransactions(Integer.parseInt(inputs[3]),Integer.parseInt(inputs[5]),Integer.parseInt(inputs[1]));
               }
           }
           
       }
   } 
}

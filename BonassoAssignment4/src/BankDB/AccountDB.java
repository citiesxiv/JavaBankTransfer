
package BankDB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AccountDB implements AutoCloseable{
    private Connection connect;
    
    public AccountDB(DataSource datasource) throws SQLException{
        connect = datasource.getConnection();
        
        
    }

    @Override
    public void close() throws SQLException {
        connect.close();
    }
    
    public void CreateTable() throws SQLException{
        try(Statement stmt = connect.createStatement()){
            stmt.executeQuery("Drop TABLE N01160956_Accounts");
            stmt.executeUpdate("CREATE TABLE N01160956_ACCOUNTS(AccountID INT PRIMARY KEY,"
                    + "Balance INT , Status VARCHAR2(9) )");
        }
    }
    
    public void MakeAccounts(String readFile) throws IOException, SQLException{
        try (BufferedReader read = new BufferedReader(new FileReader(readFile))) {
            String query = "INSERT INTO N01160956_ACCOUNTS (AccountID,Balance,Status) VALUES (?,?,?)";
            String line = "";
            try (PreparedStatement prepStmt = connect.prepareStatement(query)) {
                while ((line = read.readLine()) != null) {
                    String[] inputs = line.split(" ");
                    if (inputs[0].equalsIgnoreCase("Account")) {
                        prepStmt.setInt(1, Integer.parseInt(inputs[1]));
                        prepStmt.setInt(2, Integer.parseInt(inputs[3]));
                        prepStmt.setString(3,inputs[4]);
                        prepStmt.executeUpdate();
                    }
                }
            }
        }
    }
    public void CheckAccounts() throws SQLException{
        try(Statement stmt = connect.createStatement() ){
            
            System.out.println("AccountID: \tBalance\t Status");
            try(ResultSet rs = stmt.executeQuery("SELECT * FROM N01160956_ACCOUNTS") ){
              while(rs.next()){
                  System.out.println(rs.getInt("AccountID")+"\t\t" + rs.getInt("Balance") +"\t"+ rs.getString("Status"));
                }  
            }
            
        }
    }
    public void ManageTransactions(int acct1, int acct2, int money) throws SQLException{
        try{
            connect.setAutoCommit(false);
            CheckAccountStatus(acct1);
            MoneySend(acct1,money);
            CheckAccountStatus(acct2);
            MoneyRecieve(acct2,money);
            connect.commit();
        }catch(Exception e){
            connect.rollback();
            System.out.println(e.getMessage());
        }finally{
            connect.setAutoCommit(true);
        }
    }

    public void CheckAccountStatus(int acct) throws SQLException {
        try(Statement stmt = connect.createStatement()){
           try(ResultSet rs = stmt.executeQuery("SELECT Status FROM N01160956_ACCOUNTS WHERE AccountID="+acct)){
               if(rs.next()){
                   String status = rs.getString("Status");
                   if(status.equalsIgnoreCase("locked")){
                       throw new RuntimeException("Cannot use locked Account: " + acct);
                   }
               }
           } 
        }

    }
    public void MoneySend(int acct, int money) throws SQLException{
         try(Statement stmt = connect.createStatement()){
          try(ResultSet rs = stmt.executeQuery("SELECT Balance FROM N01160956_ACCOUNTS WHERE AccountID="+acct)){
              if(rs.next()){
              money = rs.getInt("balance") - money;
              stmt.executeQuery("UPDATE N01160956_ACCOUNTS SET Balance= "+money+" WHERE AccountID="+acct);
              }
           } 
         }
    }
     public void MoneyRecieve(int acct, int money) throws SQLException{
         try(Statement stmt = connect.createStatement()){
          try(ResultSet rs = stmt.executeQuery("SELECT Balance FROM N01160956_ACCOUNTS WHERE AccountID="+acct)){
              if(rs.next()){
              money = rs.getInt("balance") + money;
              stmt.executeQuery("UPDATE N01160956_ACCOUNTS SET Balance= "+money+" WHERE AccountID="+acct);
              }
           } 
         }
    }

}

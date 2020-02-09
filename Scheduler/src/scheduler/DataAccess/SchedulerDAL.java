package scheduler.DataAccess;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import scheduler.Model.Address;
import scheduler.Model.Customer;


public class SchedulerDAL {
    
    private Connection db;
    private String userName;
    
    public SchedulerDAL() throws IOException, SQLException, ClassNotFoundException
    {
        String password = Files.readAllLines(Paths.get("../password.txt")).get(0);
        String dbAddress = "jdbc:mysql://3.227.166.251";
        String dbName = "U04SVR";
        String dbUser = "U04SVR";
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        
        db = (Connection) DriverManager.getConnection
            (dbAddress + "/" + dbName, dbUser, password);
        
        System.out.println("Connected to '" + dbName + "'.");
    }
    
    public void close() throws SQLException
    {
        db.close();
    }
    
    public boolean checkCredentials(String un, String pass) throws SQLException
    {
        //Vulnerable to SQL injection!
        String sql = "SELECT 1 FROM user u WHERE u.userName = ? AND u.password = ?;";
        ResultSet resultSet = parameterizedQuery(sql, un, pass);
        if (resultSet.next())
        {
            userName = un;
            return true;
        }
        return false;
    }
    
    public ResultSet parameterizedQuery(String queryString, String param1) throws SQLException
    {
        PreparedStatement prepared = db.prepareStatement(queryString);
        prepared.setString(0, param1);
        ResultSet result = prepared.executeQuery();
        return result;
    }
    
    public ResultSet parameterizedQuery(String queryString, String param1, String param2) 
            throws SQLException
    {
        PreparedStatement prepared = db.prepareStatement(queryString);
        prepared.setString(1, param1);
        prepared.setString(2, param2);
        ResultSet result = prepared.executeQuery();
        return result;
    }
    
    public ResultSet query(String queryString) throws SQLException
    {
        Statement statement = (Statement) db.createStatement();
        ResultSet result = statement.executeQuery(queryString);
        return result;
    }
    
    public ArrayList<Customer> getCustomers() throws SQLException
    {
        ResultSet results = query("SELECT\n" +
        "    C.customerId,\n" +
        "    C.customerName,\n" +
        "    C.active,\n" +
        "    C.lastUpdate,\n" +
        "    C.addressId,\n" +
        "    A.address,\n" +
        "    A.address2,\n" +
        "    Cit.city,\n" +
        "    A.postalCode,\n" +
        "    Coun.country,\n" +
        "    A.phone\n" +
        "FROM U04SVR.customer C\n" +
        "INNER JOIN address A ON C.addressId = A.addressId\n" +
        "INNER JOIN city Cit ON A.cityId = Cit.cityId\n" +
        "INNER JOIN country Coun ON Cit.countryId = Coun.countryId;");
        ArrayList<Customer> output = new ArrayList<>();
        while(results.next())
        {
            Address rowAddr = new Address(
                    results.getInt("addressId"),
                    results.getString("address"),
                    results.getString("address2"),
                    results.getString("city"),
                    results.getString("country"),
                    results.getString("postalCode"),
                    results.getString("phone")
            );
            Customer rowCust = new Customer(
                    results.getInt("customerId"),
                    results.getString("customerName"),
                    rowAddr,
                    results.getBoolean("active"),
                    results.getString("phone")
            );
            output.add(rowCust);
        }
        return output;
    }
}

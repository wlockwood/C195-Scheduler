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
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import scheduler.Model.Address;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


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
    
    //Adapted from https://stackoverflow.com/questions/696782/retrieve-column-names-from-java-sql-resultset
    public void printColumnNamesInResult(ResultSet input) throws SQLException
    {
        System.out.println("Showing column in results...");
        ResultSetMetaData rsmd = input.getMetaData();
        int columnCount = rsmd.getColumnCount();

        // The column count starts from 1
        for (int i = 1; i <= columnCount; i++ ) {
          System.out.println("Column " + i + "'s name: " + rsmd.getColumnName(i));
}
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
        
        printColumnNamesInResult(results);
        
        ArrayList<Customer> output = new ArrayList<>();
        while(results.next())
        {
            output.add(extractCustomer(results));
        }
        return output;
    }
    
    public Address extractAddress(ResultSet dbRow) throws SQLException
    {
        return new Address(
                    dbRow.getInt("addressId"),
                    dbRow.getString("address"),
                    dbRow.getString("address2"),
                    dbRow.getString("city"),
                    dbRow.getString("country"),
                    dbRow.getString("postalCode"),
                    dbRow.getString("phone")
            );
    }
    
    public Customer extractCustomer(ResultSet dbRow) throws SQLException
    {
        Customer output = new Customer(
                    dbRow.getInt("customerId"),
                    dbRow.getString("customerName"),
                    extractAddress(dbRow),
                    dbRow.getBoolean("active"),
                    dbRow.getString("phone")
            );
        return output;
    }
    
    /*
    Requires ridiculously large query result rows.
    */
    public Appointment extractAppoint(ResultSet dbRow) throws SQLException
    {
        return new Appointment(
            dbRow.getInt("appointmentId"),
            extractCustomer(dbRow),
            dbRow.getString("title"),
            dbRow.getString("description"),
            dbRow.getString("location"),
            dbRow.getString("contact"),
            dbRow.getString("type"),
            dbRow.getString("url"),
            dbRow.getTimestamp("start"),
            dbRow.getTimestamp("end")
            );
    }
    
    public void deleteCustomer(int customerId)
    {
        System.out.println("Let's pretend we just deleted a customer.");
    }

    public ArrayList<Appointment> getAppointments() throws SQLException {
        ResultSet results = query("SELECT\n" +
"	A.appointmentId,\n" +
"    A.title,\n" +
"    A.description,\n" +
"    A.location,\n" +
"    A.contact,\n" +
"    A.type,\n" +
"    A.url,\n" +
"    A.start,\n" +
"    A.end,\n" +
"    A.lastUpdate,\n" +
"	CU.customerId,\n" +
"    CU.customerName,\n" +
"    CU.addressId,\n" +
"    CU.active,\n" +
"    AD.address,\n" +
"    AD.address2,\n" +
"	city.city,\n" +
"    country.country,\n" +
"    AD.postalCode,\n" +
"    AD.phone\n" +
"FROM appointment A\n" +
"INNER JOIN customer CU ON A.customerId = CU.customerId\n" +
"INNER JOIN address AD ON CU.addressId = AD.addressId\n" +
"INNER JOIN city ON AD.cityId = city.cityId\n" +
"INNER JOIN country ON city.countryId = country.countryId");
        
        printColumnNamesInResult(results);
        
        ArrayList<Appointment> output = new ArrayList<>();
        while(results.next())
        {
            output.add(extractAppoint(results));
        }
        return output;
    }

    public void deleteAppointment(int appointmentId) {
        System.out.println("Let's pretend we just deleted an appointment.");
    }
}

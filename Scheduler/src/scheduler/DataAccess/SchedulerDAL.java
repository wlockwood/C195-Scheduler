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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.TimeZone;
import scheduler.Model.Address;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;
import scheduler.Model.User;


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
    
    //Database Helpers
    
    public ResultSet parameterizedQuery(String queryString, String param1) throws SQLException
    {
        PreparedStatement prepared = db.prepareStatement(queryString);
        prepared.setString(1, param1);
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
    
    public ResultSet parameterizedQuery(String queryString, int param1) throws SQLException
    {
        PreparedStatement prepared = db.prepareStatement(queryString);
        prepared.setInt(1, param1);
        ResultSet result = prepared.executeQuery();
        return result;
    }
    
    public ResultSet query(String queryString) throws SQLException
    {
        Statement statement = (Statement) db.createStatement();
        ResultSet result = statement.executeQuery(queryString);
        return result;
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
                    dbRow.getBoolean("active")
            );
        return output;
    }
    
    public Appointment extractAppoint(ResultSet dbRow) throws SQLException
    {
        long offsetMillis = TimeZone.getDefault().getOffset(Instant.now().toEpochMilli());
        return new Appointment(
            dbRow.getInt("appointmentId"),
            extractCustomer(dbRow),
            dbRow.getString("title"),
            dbRow.getString("description"),
            dbRow.getString("location"),
            dbRow.getString("contact"),
            dbRow.getString("type"),
            dbRow.getString("url"),
            dbRow.getTimestamp("start").toInstant().plusMillis(offsetMillis), //This is a silly approach, but there appears to be a bug in the JDBC driver.
            dbRow.getTimestamp("end").toInstant().plusMillis(offsetMillis)
            );
    }
    
    //Debugging
    public void printColumnNamesInResult(ResultSet input) throws SQLException
    {
        //Adapted from https://stackoverflow.com/questions/696782/retrieve-column-names-from-java-sql-resultset
        System.out.println("Showing column in results...");
        ResultSetMetaData rsmd = input.getMetaData();
        int columnCount = rsmd.getColumnCount();

        // The column count starts from 1
        for (int i = 1; i <= columnCount; i++ ) {
          System.out.println("Column " + i + "'s name: " + rsmd.getColumnName(i));
}
    }
    
    //Login
    
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
    
    //Customer CRUD
    public int addCustomer(Customer newCust) throws SQLException {
        try
        {
            db.setAutoCommit(false);    //Must be disabled start a transaction
            
            int addressId = getAddressIdWithInsert(newCust.getAddress());
            
            //Write customer
            String sql = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy)\n" +
                "Values (?,?,?,?,?,?)";
            PreparedStatement preps = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            
            preps.setString(1, newCust.getName());
            preps.setInt(2, addressId);
            preps.setBoolean(3, newCust.isActive());
            Instant now = Instant.now();
            preps.setTimestamp(4, Timestamp.from(now));
            preps.setString(5, userName);
            preps.setString(6, userName);
            preps.execute();
            
            ResultSet keys = preps.getGeneratedKeys();    
            keys.next();  
            int key = keys.getInt(1);
            
            db.commit();
            return key;
        }
        catch (SQLException se)
        {
            db.rollback();
            throw se;
        }
        finally
        {
            db.setAutoCommit(true);
        }
        
    }

    public void deleteCustomer(int customerId) throws SQLException
    {
        String sql = "DELETE FROM customer WHERE customerId = ?";
        PreparedStatement preps = db.prepareStatement(sql);
        preps.setInt(1, customerId);
        preps.execute();
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
        
        //printColumnNamesInResult(results);
        
        ArrayList<Customer> output = new ArrayList<>();
        while(results.next())
        {
            output.add(extractCustomer(results));
        }
        return output;
    }
    public int getAddressIdWithInsert(Address addr) throws SQLException
    {
        //See if addressId exists
        ResultSet results = parameterizedQuery("SELECT addressId\n" +
            "FROM address\n" +
            "WHERE address.addressId = ?\n" +
            "LIMIT 1;", addr.addressId);
        if(results.next())
        {
            return results.getInt("addressId");
        }

        //Make sure city exists and get code
        int cityId = getCityIdWithInsert(addr.city, addr.country);
        
        String sql = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy)\n" +
            "Values (?,?,?,?,?,?,?,?)";
        PreparedStatement preps = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preps.setString(1, addr.address);
        preps.setString(2, addr.address2);
        preps.setInt(3, cityId);
        preps.setString(4, addr.postalCode);
        preps.setString(5, addr.phone);
        Instant now = Instant.now();
        preps.setTimestamp(6, Timestamp.from(now));
        preps.setString(7, userName);
        preps.setString(8, userName);
        preps.execute();
        
        //Get new Id - from https://stackoverflow.com/questions/14170656/get-last-inserted-auto-increment-id-in-mysql
        ResultSet keys = preps.getGeneratedKeys();    
        keys.next();  
        int key = keys.getInt(1);
        return key;
        
    }
    
    public void updateCustomer(Customer cust) throws SQLException {
        //Check address
        int adId = getAddressIdWithInsert(cust.getAddress());

        //Write customer
        String sql = "UPDATE customer \n" +
            "SET \n" +
            "	customerName = ?, \n" +
            "	addressId = ?, \n" +
            "    active = ?, \n" +
            "    lastUpdate = ?\n" +
            "    lastUpdateBy = ?\n" +
            "WHERE\n" +
            "	customerId = ?";
        PreparedStatement preps = db.prepareStatement(sql);

        preps.setString(1, cust.getName());
        preps.setInt(2, adId);
        preps.setBoolean(3, cust.isActive());
        Instant now = Instant.now();
        preps.setTimestamp(4, Timestamp.from(now));
        preps.setString(5, userName);
        preps.setInt(6, cust.getCustomerId());
        preps.execute();
    }
    
    public int getCountryIdWithInsert(String countryName) throws SQLException
    {
        ResultSet results = parameterizedQuery("SELECT countryId\n" +
            "FROM country\n" +
            "WHERE country.country = ?\n" +
            "LIMIT 1;", countryName);
        if(results.next())
        {
            int output = results.getInt("countryId");
            System.out.println("Country '" + countryName + "' is Id " + output + ".");
            return output;
        }
        System.out.println("Couldn't find country '" + countryName + "', creating....");
        String sql = "INSERT INTO country (country, createDate, createdBy, lastUpdateBy)\n" +
            "VALUES (?,?,?,?)";

      
        PreparedStatement preps = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preps.setString(1, countryName);
        Instant now = Instant.now();
        preps.setTimestamp(2, Timestamp.from(now));
        preps.setString(3, userName);
        preps.setString(4, userName);
        preps.execute();
        System.out.println("Couldn't find country '" + countryName + "', created.");
        
        
        ResultSet keys = preps.getGeneratedKeys();    
        keys.next();  
        int key = keys.getInt(1);
        return key;
        
    }
   
    private int getCityIdWithInsert(String city, String country) throws SQLException {
        //Insufficient resolution for real-world use. Ex: there are many Portlands in the US.
        ResultSet results = parameterizedQuery("SELECT cityId FROM city\n" +
            "INNER JOIN country ON city.countryId = country.countryId\n" +
            "WHERE city.city = ? AND country.country = ?", city, country);
        if(results.next())
        {
            return results.getInt("cityId");
        }
        
        //Insert new city
        int countryId = getCountryIdWithInsert(country);

        String sql = "INSERT INTO city (city, countryId, createDate, createdBy, lastUpdateBy)\n" +
            "VALUES (?,?,?,?,?)";
        PreparedStatement preps = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        preps.setString(1, city);
        preps.setInt(2, countryId);
        Instant now = Instant.now();
        preps.setTimestamp(3, Timestamp.from(now));
        preps.setString(4, userName);
        preps.setString(5, userName);
        preps.execute();


       ResultSet keys = preps.getGeneratedKeys();    
        keys.next();  
        int key = keys.getInt(1);
        return key;
     }

    //Appointment CRUD
    
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
        
        //printColumnNamesInResult(results);
        
        ArrayList<Appointment> output = new ArrayList<>();
        while(results.next())
        {
            output.add(extractAppoint(results));
        }
        return output;
    }

    public void deleteAppointment(int appointmentId) throws SQLException {
        String sql = "DELETE FROM appointment WHERE appointmentId = ?";
        PreparedStatement preps = db.prepareStatement(sql);
        preps.setInt(1, appointmentId);
        preps.execute();
    }
   
    public void updateAppointment(Appointment updatedAppoint) throws SQLException {
        long offsetMillis = TimeZone.getDefault().getOffset(Instant.now().toEpochMilli());
        
        String sql = "UPDATE appointment\n" +
            "SET\n" +
            "	customerId = ?,\n" +
            "    title = ?,\n" +
            "    description = ?,\n" +
            "    location = ?,\n" +
            "    contact = ?,\n" +
            "    type = ?,\n" +
            "    url = ?,\n" +
            "    start = ?,\n" +
            "    end = ?,\n" +
            "    lastUpdate = ?,\n" +
            "    lastUpdateBy = ?\n" +
            "WHERE appointmentId = ?";
        System.out.println("Update appointment SQL:");
        System.out.println(sql);
        PreparedStatement preps = db.prepareStatement(sql);

        preps.setInt(1, updatedAppoint.getCustomer().getCustomerId());
        preps.setString(2, updatedAppoint.getTitle());
        preps.setString(3, updatedAppoint.getDescription());
        preps.setString(4, updatedAppoint.getLocation());
        preps.setString(5, updatedAppoint.getContact());
        preps.setString(6, updatedAppoint.getType());
        preps.setString(7, updatedAppoint.getUrl());
        preps.setTimestamp(8, Timestamp.from(updatedAppoint.getStart().minusMillis(offsetMillis)));
        preps.setTimestamp(9, Timestamp.from(updatedAppoint.getEnd().minusMillis(offsetMillis)));

        Instant now = Instant.now();
        preps.setTimestamp(10, Timestamp.from(now));
        preps.setString(11, userName);
        preps.setInt(12, updatedAppoint.getAppointmentId());
        preps.execute();
    }

    public int addAppointment(Appointment newAppoint) throws Exception {
        try
        {
            long offsetMillis = TimeZone.getDefault().getOffset(Instant.now().toEpochMilli());
            
            db.setAutoCommit(false);
            
            ResultSet userResult = parameterizedQuery("SELECT userId from user WHERE userName = ?", userName);
            int userId = 0;
            
            //(userResult); //DEBUG
            
            if(userResult.next())
            {
                userId = userResult.getInt("userId");
            }
            else
            {
                throw new Exception("User not found in database!");
            }
            
            //Write customer
            String sql = "INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdateBy)\n" +
                "Values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preps = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preps.setInt(1, newAppoint.getCustomer().getCustomerId());
            preps.setInt(2, userId);
            preps.setString(3, newAppoint.getTitle());
            preps.setString(4, newAppoint.getDescription());
            preps.setString(5, newAppoint.getLocation());
            preps.setString(6, newAppoint.getContact());
            preps.setString(7, newAppoint.getType());
            preps.setString(8, newAppoint.getUrl());
            preps.setTimestamp(9, Timestamp.from(newAppoint.getStart().minusMillis(offsetMillis)));
            preps.setTimestamp(10, Timestamp.from(newAppoint.getEnd().minusMillis(offsetMillis)));
            
            Instant now = Instant.now();
            preps.setTimestamp(11, Timestamp.from(now));
            preps.setString(12, userName);
            preps.setString(13, userName);
            preps.execute();
            
            ResultSet keys = preps.getGeneratedKeys();    
            keys.next();  
            int key = keys.getInt(1);
            
            db.commit();
            return key;
        }
        catch (SQLException se)
        {
            db.rollback();
            throw se;
        }
        finally
        {
            db.setAutoCommit(true);
        }
    }
    
    //User R(CUD?)
    
    public ArrayList<User> getUsers() throws SQLException
    {
        ResultSet results = query("SELECT * FROM user");
        ArrayList<User> output = new ArrayList<>();
        while(results.next())
        {
            User u = new User();
            u.userId = results.getInt("userId");
            u.userName = results.getString("userName");
            output.add(u);
        }
        return output;
    }
    
    //Reports
    
    public ArrayList<AppMonthType> getAppointmentsByMonthType() throws SQLException
    {
        String sql = "SELECT type,MONTH(start) AS 'month',MONTHNAME(start) AS 'monthName',COUNT(appointmentId) AS 'num'\n" +
            "FROM U04SVR.appointment GROUP BY type,MONTH(start);";
        ResultSet results = query(sql);
        
        ArrayList<AppMonthType> output = new ArrayList<>();
        while(results.next())
        {
            AppMonthType amt = new AppMonthType();
            amt.month  = results.getInt("month");
            amt.monthName = results.getString("monthName");
            amt.count = results.getInt("num");
            amt.type = results.getString("type");
        }
        return output;
    }
    
    class AppMonthType
    {
        public String type;
        public int month;
        public int count;
        public String monthName;
    }
    
    public ArrayList<Appointment> getAppointmentsForCustomer(int customerId) throws SQLException
    {
        String sql = "SELECT\n" +
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
        "INNER JOIN country ON city.countryId = country.countryId\n" +
        "WHERE A.customerId = ?";
        ResultSet results = parameterizedQuery(sql, customerId);
        ArrayList<Appointment> output = new ArrayList<>();
        while(results.next())
        {
            output.add(extractAppoint(results));
        }
        return output;
    }
    
    public ArrayList<Appointment> getAppointmentsForUser(int userId) throws SQLException
    {
        String sql = "SELECT\n" +
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
        "INNER JOIN country ON city.countryId = country.countryId\n" +
        "WHERE A.userId = ?";
        ResultSet results = parameterizedQuery(sql, userId);
        ArrayList<Appointment> output = new ArrayList<>();
        while(results.next())
        {
            output.add(extractAppoint(results));
        }
        return output;
    }
    
    
}

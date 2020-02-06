/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Capsi
 */
public class Scheduler extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        
        String password = Files.readAllLines(Paths.get("../password.txt")).get(0);
        
        String dbAddress = "jdbc:mysql://3.227.166.251";
        String dbName = "U04SVR";
        String user = "U04SVR";
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        
        Connection conn = (Connection) DriverManager.getConnection
            (dbAddress + "/" + dbName, user, password);
        
        String sql = "select * from country";
        Statement statement = (Statement) conn.createStatement();
        ResultSet result = statement.executeQuery(sql);
        while(result.next())
        {
            System.out.println(result.getString("country"));
        }
        conn.close();
        /*
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        */
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import scheduler.Controllers.LoginController;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;



public class Scheduler extends Application {

    private SchedulerDAL db;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        db = new SchedulerDAL();
        
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loginLoader.load();
        LoginController loginController = loginLoader.getController();
        loginController.construct(db, stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void stop() throws SQLException
    {
        db.close();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import scheduler.Controllers.SchedulerDatabaseController;

/**
 *
 * @author William Lockwood
 */
public class LoginController implements Initializable {
    private ResourceBundle foundRb;
    
    
    @FXML
    private TextField usernameTextbox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label unLabel;
    @FXML
    private Label pwLabel;
    @FXML
    private Label regionDetected;
    @FXML
    private Text regionDetectedLabel;
    @FXML
    private Button loginButton;
        
    SchedulerDatabaseController sdb;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Locale here = Locale.getDefault();
        foundRb = ResourceBundle.getBundle("scheduler/login", here);
        regionDetected.setText(foundRb.getString("regionHere"));
        unLabel.setText(foundRb.getString("username"));
        pwLabel.setText(foundRb.getString("password"));
        loginButton.setText(foundRb.getString("login"));
    }    

    @FXML 
    private void loginClick(ActionEvent event) throws SQLException {
        String user = usernameTextbox.getText();
        String pass = passwordField.getText();
        System.out.println("Trying to log in as '" + user + "'...");
        boolean successfulLogin = sdb.checkCredentials(user, pass);
        if(successfulLogin)
        {
            System.out.println("Logged in successfully.");
        }
        else
        {
            Alert badLogin = new Alert(Alert.AlertType.ERROR);
            badLogin.setContentText(foundRb.getString("incorrectUsernameOrPassword"));
            badLogin.show();
        }
        
    }
    
    public void construct(SchedulerDatabaseController _sdb)
    {
        sdb = _sdb;
    }
}

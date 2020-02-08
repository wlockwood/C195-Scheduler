package scheduler.Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;

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
        
    SchedulerDAL sdal;
    Stage stage;
    
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
    private void loginClick(ActionEvent event) throws SQLException, IOException {
        
        
        String user = usernameTextbox.getText();
        String pass = passwordField.getText();
        System.out.println("Trying to log in as '" + user + "'...");
        boolean successfulLogin = sdal.checkCredentials(user, pass);
        
        if(successfulLogin)
        {
            showMainMenu();
            System.out.println("Logged in successfully.");
        }
        else
        {
            Alert badLogin = new Alert(Alert.AlertType.ERROR);
            badLogin.setContentText(foundRb.getString("incorrectUsernameOrPassword"));
            badLogin.show();
        }
        
    }
    
    public void showMainMenu() throws IOException
    {
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/scheduler/Menu.fxml"));
        Parent root = menuLoader.load();
        MenuController menuController = menuLoader.getController();
        menuController.construct(sdal, stage);

        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
    
    public void construct(SchedulerDAL _sdal, Stage _stage) throws IOException
    {
        sdal = _sdal;
        stage = _stage;
    }
}

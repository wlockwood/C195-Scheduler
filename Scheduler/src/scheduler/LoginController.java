/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

/**
 *
 * @author Capsi
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField usernameTextbox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button button;
    @FXML
    private Label label;
    @FXML
    private Label unLabel;
    @FXML
    private Label pwLabel;
    @FXML
    private Label regionDetected;
    
    private Locale here;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        
        
        
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        here = Locale.getDefault();
        regionDetected.setText(here.getDisplayLanguage());// TODO
    }    
    
}

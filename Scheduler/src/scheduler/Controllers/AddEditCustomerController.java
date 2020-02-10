/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Controllers.ViewerController.TypeMode;
import scheduler.Model.Address;
import scheduler.Model.Customer;

/**
 * FXML Controller class
 *
 * @author Capsi
 */
public class AddEditCustomerController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    Customer editingCust;
    @FXML private TextField nameField;
    @FXML private ComboBox activeCombo;
    @FXML private TextField addressField;
    @FXML private TextField address2Field;
    @FXML private TextField cityField;
    @FXML private TextField countryField;
    @FXML private TextField phoneField;
    @FXML private Button addEditButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        activeCombo.setItems(FXCollections.observableArrayList("Active", "Inactive"));
    }    
    
    public void construct(SchedulerDAL _sdal, Stage _stage, Customer toEdit) 
            throws IOException, SQLException
    {
        sdal = _sdal;
        stage = _stage;
        editingCust = toEdit;
        
        if(editingCust == null)
        {
            addEditButton.setText("Add customer");
        }
        else 
        {
            addEditButton.setText("Update customer data");
            nameField.setText(editingCust.getName());
            
            Address custAddr = editingCust.getAddress();
            addressField.setText(custAddr.address);
            address2Field.setText(custAddr.address2);
            
        }
        

    }
    
}

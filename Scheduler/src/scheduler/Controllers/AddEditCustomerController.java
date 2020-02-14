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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import scheduler.DataAccess.SchedulerDAL;
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
    @FXML private TextField postalField;
    @FXML private TextField countryField;
    @FXML private TextField phoneField;
    @FXML private Button addEditButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    public void construct(SchedulerDAL _sdal, Stage _stage, Customer toEdit) 
            throws IOException, SQLException
    {
        sdal = _sdal;
        stage = _stage;
        editingCust = toEdit;
        
        activeCombo.setItems(FXCollections.observableArrayList(Customer.ActiveState.values()));
        
        if(editingCust == null)
        {
            addEditButton.setText("Add customer");
            activeCombo.getSelectionModel().select(Customer.ActiveState.Active);
        }
        else 
        {
            addEditButton.setText("Update customer data");
            nameField.setText(editingCust.getName());

            activeCombo.getSelectionModel().select(editingCust.getVerboseActive());
            
            Address custAddr = editingCust.getAddress();
            addressField.setText(custAddr.address);
            address2Field.setText(custAddr.address2);
            cityField.setText(custAddr.city);
            postalField.setText(custAddr.postalCode);
            countryField.setText(custAddr.country);
            phoneField.setText(custAddr.phone);
        }
    }

    @FXML
    private void addEditClick(ActionEvent event) throws SQLException {
        
        //Either way, all fields except address 2 are required
        if(!isFormValid())
        {
            new Alert(Alert.AlertType.ERROR,"All fields except address 2 are required. Phone must be formatted like a phone number.").show();
            return;
        }
        
        //Create model objects
            Address ad = new Address(
                0,
                addressField.getText(),
                address2Field.getText(),
                cityField.getText(),
                countryField.getText(),
                postalField.getText(),
                phoneField.getText()
            );
        
            boolean active = activeCombo.getSelectionModel().getSelectedItem() == Customer.ActiveState.Active;
            Customer formCust = new Customer(
                   0,
                   nameField.getText(),
                   ad,
                   active
            );  
            
        if(editingCust == null)
        {
            //Add customer
            sdal.addCustomer(formCust);
        }
        else 
        {
            //Save customer updates
            formCust.getAddress().addressId = editingCust.getAddress().addressId;
            formCust.setCustomerId(editingCust.getCustomerId());
            sdal.updateCustomer(formCust);
        }
    }
    
    private boolean isFormValid()
    {
        if("".equals(nameField.getText()) || nameField.getText() == null) { return false; }
        if("".equals(addressField.getText()) || addressField.getText() == null) { return false; }
        //Ignore address2
        if("".equals(cityField.getText()) || cityField.getText() == null) { return false; }
        if("".equals(postalField.getText()) || postalField.getText() == null) { return false; }
        if("".equals(countryField.getText()) || countryField.getText() == null) { return false; }
        if("".equals(phoneField.getText()) || phoneField.getText() == null) { return false; }
        
        
        //Check that phone number looks like a phone number
        //Regex from http://regexlib.com/Search.aspx?k=phone, flow from https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
        Pattern pat = Pattern.compile("^(\\(?\\+?[0-9]*\\)?)?[0-9_\\- \\(\\)]*$");
        Matcher m = pat.matcher(phoneField.getText());
        
        boolean matches = m.matches();
        if(!matches) 
        {
            return false;
        }
            
        
        return true;
    }
}

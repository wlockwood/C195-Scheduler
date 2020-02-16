/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;

/**
 * FXML Controller class
 *
 * @author Capsi
 */
public class AddEditAppointController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    Appointment editingAppoint;
    Appointment returnedAppoint;
    ObservableList<Customer> customers;
    
    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private TextField urlField;
    @FXML private TextField typeField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField contactField;
    @FXML private ComboBox<Customer> customerCombo;
    @FXML private Button addEditButton;
    @FXML private TextField startField;
    @FXML private TextField endField;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void construct(SchedulerDAL _sdal, Stage _stage, Appointment toEdit) 
            throws IOException, SQLException
    {
        sdal = _sdal;
        stage = _stage;
        editingAppoint = toEdit;
        customers = FXCollections.observableArrayList(sdal.getCustomers());
        
        customerCombo.setItems(customers);
        
        //Modified from https://stackoverflow.com/questions/41634789/javafx-combobox-display-text-but-return-id-on-selection
        customerCombo.setConverter(new StringConverter<Customer>() {

            @Override
            public String toString(Customer object) {
                return object.getName();
            }

            @Override
            public Customer fromString(String string) {
                return customerCombo.getItems().stream().filter(ap -> 
                    ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
        
        if(editingAppoint == null)
        {
            addEditButton.setText("Add appointment");
            startField.setText(instantToLocalString(Instant.now()));
            endField.setText(instantToLocalString(Instant.now().plus(30, ChronoUnit.MINUTES)));
        }
        else 
        {
            addEditButton.setText("Update appointment");
            
            titleField.setText(editingAppoint.getTitle());
            descriptionArea.setText(editingAppoint.getDescription());
            urlField.setText(editingAppoint.getUrl());
            typeField.setText(editingAppoint.getType());
            
            
            //Datetimes are stored in UTC and must be converted for display
            startField.setText(instantToLocalString(editingAppoint.getStart()));
            endField.setText(instantToLocalString(editingAppoint.getStop()));
            
            //This lambda replaces a for loop to accomplish the same thing.
            //It's ideally not necessary to be this big, but if two copies of 
            //  the same customer end up in memory for some reason, I don't want it to break things.
            Customer selectedCustomer = customers.stream()
                    .filter(c -> c.getCustomerId() == editingAppoint.getCustomer().getCustomerId())
                    .findFirst().get();
            customerCombo.getSelectionModel().select(selectedCustomer);
            contactField.setText(editingAppoint.getContact());
        }
    }
    
    private boolean isFormValid()
    {
        boolean requiredMissing = false;
        if("".equals(titleField.getText()) || titleField.getText() == null) { requiredMissing = true; }
        //if("".equals(locationField.getText()) || locationField.getText() == null) { requiredMissing = true; }
        //if("".equals(typeField.getText()) || typeField.getText() == null) { requiredMissing = true; }
        if("".equals(startField.getText()) || startField.getText() == null) { requiredMissing = true; }
        if("".equals(endField.getText()) || endField.getText() == null) { requiredMissing = true; }
        
        if(requiredMissing) {
            new Alert(Alert.AlertType.ERROR, "Title Start, and End are all required fields.").show();
            return false;
        }
        String dateErrorText = "formatting invalid. Should look like '" + instantToLocalString(Instant.now()) + "'.";
        try{
            localStringToInstant(startField.getText());    
        }
        catch (DateTimeParseException de)
        {
            new Alert(Alert.AlertType.ERROR, "Start " + dateErrorText).show();
        }
        
        try{
            localStringToInstant(endField.getText());    
        }
        catch (DateTimeParseException de)
        {
            new Alert(Alert.AlertType.ERROR, "End " + dateErrorText).show();
        }
        
        
        return true;
    }

    private Instant localStringToInstant(String toParse)
    {
        LocalDateTime local = LocalDateTime.parse(toParse);
        Instant output = local.toInstant(ZoneOffset.UTC);
        return output;
    }
    
    private String instantToLocalString(Instant toPrint)
    {
        TimeZone here = TimeZone.getDefault();
        return toPrint.atZone(here.toZoneId())
                    .toLocalDateTime()
                    .toString();
    }
    
    @FXML private void addEditClick(ActionEvent event) throws SQLException, Exception {
        
        if(!isFormValid())
        {
            //isFormValid() handles alerting
            return;
        }
        int appointId = 0;
        if(editingAppoint != null) 
        { 
            appointId = editingAppoint.getAppointmentId(); 
        }
        Appointment formAppoint = new Appointment(
               appointId,
               customerCombo.getValue(),
               titleField.getText(),
               descriptionArea.getText(),
               
               locationField.getText(),
               contactField.getText(),
               typeField.getText(),
               urlField.getText(),
               localStringToInstant(startField.getText()),
               localStringToInstant(endField.getText())
        );  
            
        if(editingAppoint == null)
        {
            //Add customer
            int newAppointId = sdal.addAppointment(formAppoint);
            formAppoint.setAppointmentId(newAppointId);
        }
        else 
        {
            //Save customer updates
            sdal.updateAppointment(formAppoint);
        }
        
        returnedAppoint = formAppoint;
        
        
        Stage s = (Stage) addEditButton.getScene().getWindow();
        s.close();
    }
    
}

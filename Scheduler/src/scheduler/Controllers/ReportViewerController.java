/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;


public class ReportViewerController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    TypeMode mode;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void construct(SchedulerDAL _sdal, Stage _stage, TypeMode _mode) throws IOException, SQLException {
        sdal = _sdal;
        stage = _stage;
        mode = _mode;

        ArrayList<Customer> customers = sdal.getCustomers();    //Used by both modes
        
        

    }
    
    public enum TypeMode
    {
        TypeMonthGroup,
        SingleUser,
        SinglelCustomer
    }
    
}

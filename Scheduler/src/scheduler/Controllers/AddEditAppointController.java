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
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Model.Appointment;

/**
 * FXML Controller class
 *
 * @author Capsi
 */
public class AddEditAppointController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    Appointment editingAppoint;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void construct(SchedulerDAL _sdal, Stage _stage, Appointment toEdit) 
            throws IOException, SQLException
    {
        sdal = _sdal;
        stage = _stage;
        editingAppoint = toEdit;
    }
    
    
}

package scheduler.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;



public class ViewerController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    ViewerMode mode;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    public void construct(SchedulerDAL _sdal, Stage _stage, ViewerMode _mode) throws IOException
    {
        sdal = _sdal;
        stage = _stage;
        mode = _mode;
    }

    @FXML
    private void handleAddClick(ActionEvent event) {
        
    }

    @FXML
    private void handleEditClick(ActionEvent event) {
        
    }

    @FXML
    private void handleDeleteClick(ActionEvent event) {
    }
    
    enum ViewerMode
    {
        Appointment,
        Customer
    }
}

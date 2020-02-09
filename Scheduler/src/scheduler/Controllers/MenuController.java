package scheduler.Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;


public class MenuController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    public void setDal(SchedulerDAL dal)
    {
        
    }
    
    @FXML
    private void handleCustomersClick(ActionEvent event) throws SQLException {
        System.out.println("Opening Customers window...");
        showViewer(ViewerController.ViewerMode.Customer);
    }

    @FXML
    private void handleAppointClick(ActionEvent event) throws SQLException {
        System.out.println("Opening Appointments window...");
        showViewer(ViewerController.ViewerMode.Appointment);
    }
    
    @FXML
    private void handleCalendarClick(ActionEvent event) {
        System.out.println("Opening Calendar window...");
        new Alert(Alert.AlertType.INFORMATION, "Not actually implemented yet.")
                .show();
    }

    void construct(SchedulerDAL _sdal, Stage _stage) {
        sdal = _sdal;
        stage = _stage;
    }
    
    private void showViewer(ViewerController.ViewerMode mode) throws SQLException
    {
        FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("/scheduler/Viewer.fxml"));
        try
        {
            Parent root = editorLoader.load();
            ViewerController vc = editorLoader.getController();
            vc.construct(sdal, stage, mode);

            //Open in new window
            Scene scene = new Scene(root);
            Stage substage = new Stage();
            substage.setScene(scene);
            substage.initOwner(stage);
            
            substage.show();
        }
        catch (IOException ioe)
        {
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setContentText("Unable to load Editor interface.");
            al.show();
        }
        
    }
    

    
    
}

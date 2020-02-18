package scheduler.Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Model.Appointment;


public class MenuController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    @FXML private Button viewCalendarButton;
    @FXML private Button appointmentsByUserButton;
    @FXML private Button appointmentsByCustomerButton;
    @FXML private Button appointmentMonthTypeButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML private void handleCustomersClick(ActionEvent event) throws SQLException {
        System.out.println("Opening Customers window...");
        showViewer(ViewerController.TypeMode.Customer);
    }

    @FXML private void handleAppointClick(ActionEvent event) throws SQLException {
        System.out.println("Opening Appointments window...");
        showViewer(ViewerController.TypeMode.Appointment);
    }
    
    @FXML private void handleCalendarClick(ActionEvent event) {
        System.out.println("Opening Calendar window...");
        new Alert(Alert.AlertType.INFORMATION, "Not actually implemented yet.")
                .show();
    }

    void construct(SchedulerDAL _sdal, Stage _stage) throws SQLException {
        sdal = _sdal;
        stage = _stage;
        
        ArrayList<Appointment> appointments = sdal.getAppointments();
        long upcomingLimit = Instant.now().plus(15, ChronoUnit.MINUTES).getEpochSecond();
        for(Appointment appt : appointments)
        {
            if(appt.getStart().getEpochSecond() < upcomingLimit 
                && appt.getStart().getEpochSecond() > Instant.now().getEpochSecond())
            {
                System.out.println(appt.toMultilineString());
                new Alert(AlertType.INFORMATION,"You have an upcoming appointment:\n" + appt.toMultilineString()).show();
            }
        }
    }
    
    private void showViewer(ViewerController.TypeMode mode) throws SQLException
    {
        FXMLLoader viewerLoader = new FXMLLoader(getClass().getResource("/scheduler/Viewer.fxml"));
        try
        {
            Parent root = viewerLoader.load();
            ViewerController vc = viewerLoader.getController();
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

    @FXML private void handleReportUserAppointments(ActionEvent event) {
    }

    @FXML private void handleReportCustomerAppointments(ActionEvent event) {
    }

    @FXML private void handleReportAppointmentTypesByMonth(ActionEvent event) {
    }
    

    
    
}

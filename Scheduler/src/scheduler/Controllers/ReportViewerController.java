/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Controllers;

import com.sun.javaws.exceptions.InvalidArgumentException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;


public class ReportViewerController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    ReportType mode;
    Customer selectedCustomer;
    int userId = -1;
    
    @FXML private Label reportTypeLabel;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private TextArea reportArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    public void construct(SchedulerDAL _sdal, Stage _stage, ReportType _mode) throws IOException, SQLException, Exception {
        sdal = _sdal;
        stage = _stage;
        mode = _mode;

        ArrayList<Customer> customers = sdal.getCustomers();    //Used by both modes
        
        if(mode == ReportType.SingleUser)
        {
            if(userId < 0)
            {
                throw new Exception("No user specified for user-specific report.");
            }
            ArrayList<Appointment> appoints = sdal.getAppointmentsForUser(userId);
        }
        else if (mode == ReportType.SingleCustomer)
        {
            
        }
        else if (mode == ReportType.TypeMonthGroup)
        {

        }
        else
        {
            throw new Exception("Invalid ReportViewer TypeMode specified.");
        }
    }
    
    private String enstringAppointmentsByMonth(ArrayList<Appointment> appoints)
    {
        //Sort appointments by start time
        appoints.sort(new Comparator<Appointment>() {
            @Override
            public int compare(Appointment a1, Appointment a2)
            {
                return (int) (a2.getStart().getEpochSecond() - a1.getStart().getEpochSecond());
            }
        });
        
        //List appointments
        StringBuilder sb = new StringBuilder(appoints.size() * 50);
        for(int month = 1; month <= 12; month++)
        {
            sb.append(" - " + java.time.Month.of(month).name() + " - \n");
            for(Appointment appoint : appoints)
            {
                if(appoint.getLocalizedStart().getMonthValue() == month)
                {
                    sb.append(appoint.toString() + "\n");
                }
            }
            
        }
        return sb.toString();
    }
    
    public void setCustomer(Customer _cust)
    {
        selectedCustomer = _cust;
    }
    
    public enum ReportType
    {
        TypeMonthGroup,
        SingleUser,
        SingleCustomer
    }
    
}

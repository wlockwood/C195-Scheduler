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
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;
import scheduler.Model.User;


public class ReportViewerController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    ReportType mode;
    Customer selectedCustomer;
    private User selectedUser;
    
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
            if(selectedUser == null)
            {
                throw new Exception("No user specified for user-specific report.");
            }
            ArrayList<Appointment> appointments = sdal.getAppointmentsForUser(selectedUser.userId);
            sortAppointments(appointments);
            StringBuilder sb = new StringBuilder();
            for(Appointment a : appointments)
            {
                sb.append(a.toString() + "\n");
            }
            reportArea.setText(sb.toString());
            
        }
        else if (mode == ReportType.SingleCustomer)
        {
            if(selectedCustomer == null)
            {
                throw new Exception("No customer specified for customer-specific report.");
            }
            ArrayList<Appointment> appointments = sdal.getAppointmentsForCustomer(selectedCustomer.getCustomerId());
            sortAppointments(appointments);
            StringBuilder sb = new StringBuilder();
            for(Appointment a : appointments)
            {
                sb.append(a.toString() + "\n");
            }
            reportArea.setText(sb.toString());
            
        }
        else if (mode == ReportType.TypeMonthGroup)
        {
            ArrayList<Appointment> appointments = sdal.getAppointments();
            reportArea.setText(enstringAppointmentsByMonth(appointments));
        }
        else
        {
            throw new Exception("Invalid ReportViewer TypeMode specified.");
        }
    }
    
    private void sortAppointments(ArrayList<Appointment> appoints)
    {
        //Sort appointments by start time
        appoints.sort(new Comparator<Appointment>() {
            @Override
            public int compare(Appointment a1, Appointment a2)
            {
                return (int) (a2.getStart().getEpochSecond() - a1.getStart().getEpochSecond());
            }
        });
    }
    
    private String enstringAppointmentsByMonth(ArrayList<Appointment> appoints)
    {
        sortAppointments(appoints);
        
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

    void setUser(User _user) {
        selectedUser = _user;
    }
    
    public enum ReportType
    {
        TypeMonthGroup,
        SingleUser,
        SingleCustomer
    }
    
}

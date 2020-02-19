package scheduler.Controllers;

import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Model.Appointment;

public class CalendarController implements Initializable {
    @FXML private TableView<Appointment> calendarTableView;
    @FXML private RadioButton weeklyRadio;
    @FXML private ToggleGroup calendarViewMode;
    @FXML private RadioButton monthlyRadio;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private TableColumn<Appointment, ZonedDateTime> startCol;
    @FXML private TableColumn<Appointment, ZonedDateTime> endCol;
    @FXML private TableColumn<Appointment, String> customerCol;
    @FXML private TableColumn<Appointment, String> titleCol;

    private SchedulerDAL sdal;
    private ZonedDateTime currentStart;
    private ZonedDateTime currentEnd;
    private ArrayList<Appointment> appointments;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    public void construct(SchedulerDAL _sdal) throws SQLException
    {
        sdal = _sdal;
        appointments = sdal.getAppointments();
        //Load customers into tableview
        SimpleListProperty<Appointment> apptProperty = new SimpleListProperty<>();
        apptProperty.set(FXCollections.observableList(appointments));
        calendarTableView.itemsProperty().bindBidirectional(apptProperty);
            
        //Set up TableView
        startCol.setCellValueFactory(new PropertyValueFactory<>("prettyLocalStart"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("prettyLocalEnd"));
        //This lambda allows us to resolve the customer name instead of displaying the customer's toString
        customerCol.setCellValueFactory(tc -> new SimpleStringProperty(tc.getValue().getCustomer().getName()));  //SSP suggestion from https://stackoverflow.com/questions/35534723/convert-a-string-to-an-observablevaluestring?rq=1
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        //Find current week
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime firstOfWeek = now.with(ChronoField.DAY_OF_WEEK,7);    //First day of the week
        currentStart = firstOfWeek.toLocalDate().atStartOfDay(ZoneId.systemDefault());
        setCalendarView();
    }

    @FXML private void previousClick(ActionEvent event) {
        if(weeklyRadio.selectedProperty().getValue())
        {
            currentStart = currentStart.minusWeeks(1);
        }
        else
        {
            currentStart = currentStart.minusMonths(1);
        }
        setCalendarView();
    }

    @FXML private void nextClick(ActionEvent event) {
        
        if(weeklyRadio.selectedProperty().getValue())
        {
            currentStart = currentStart.plusWeeks(1);
        }
        else
        {
            currentStart = currentStart.plusMonths(1);
        }
        setCalendarView();
    }

    @FXML private void weeklySelected(ActionEvent event) {
    
        ZonedDateTime weekStart = currentStart.with(ChronoField.DAY_OF_WEEK,7);
        currentStart = weekStart;
        setCalendarView();
    }

    @FXML private void monthlySelected(ActionEvent event) {
        ZonedDateTime monthStart = currentStart.with(ChronoField.DAY_OF_MONTH,1);
        currentStart = monthStart;
        setCalendarView();
    }
    
    private void setCalendarView()
    {
        if(weeklyRadio.selectedProperty().getValue())
        {
            currentEnd = currentStart.plusWeeks(1).minusNanos(1);
        }
        else
        {
            currentEnd = currentStart.plusMonths(1).minusNanos(1);
        }
        
        
        ObservableList<Appointment> newAppoints = FXCollections.observableArrayList();
        for(Appointment appt : appointments)
        {
            if((appt.getLocalizedStart().isAfter(currentStart) || appt.getLocalizedStart().isEqual(currentStart))
               && appt.getLocalizedStart().isBefore(currentEnd))
            {
                newAppoints.add(appt);
            }
        }
        
        calendarTableView.setItems(newAppoints);
        calendarTableView.getSortOrder().add(startCol);
        calendarTableView.sort();
        
        calendarTableView.itemsProperty().unbind();
        
        calendarTableView.setItems(newAppoints);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        startDateLabel.setText(currentStart.format(formatter));
        endDateLabel.setText(currentEnd.format(formatter));
    }
    
}

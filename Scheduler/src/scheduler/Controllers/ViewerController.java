package scheduler.Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;



public class ViewerController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    TypeMode mode;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> custIdCol;
    @FXML private TableColumn<Customer, String> custNameCol;
    @FXML private TableColumn<Customer, String> custAddrCol;
    @FXML private TableColumn<Customer, String> custPhoneCol;
    @FXML private TableColumn<Customer, Boolean> custActCol;
    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, Integer> apptIdCol;
    @FXML private TableColumn<Appointment, Timestamp> apptStartCol;
    @FXML private TableColumn<Appointment, Timestamp> apptEndCol;
    @FXML private TableColumn<Appointment, String> apptCustCol;
    @FXML private TableColumn<Appointment, String> apptTitleCol;
    @FXML private TableColumn<Appointment, String> apptLocationCol;
    @FXML private TableColumn<Appointment, String> apptTypeCol;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    public void construct(SchedulerDAL _sdal, Stage _stage, TypeMode _mode) throws IOException, SQLException
    {
        sdal = _sdal;
        stage = _stage;
        mode = _mode;
        
        customersTable.setVisible(false);
        appointmentsTable.setVisible(false);
        
        if(mode == TypeMode.Customer)
        {
            //Get customers from DB
            ArrayList<Customer> customers = sdal.getCustomers();


            //Load customers into tableview
            SimpleListProperty<Customer> custProperty = new SimpleListProperty<>();
            custProperty.set(FXCollections.observableList(customers));
            customersTable.itemsProperty().bind(custProperty);

            custIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            custAddrCol.setCellValueFactory(new PropertyValueFactory<>("addressSquash"));
            custPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            custActCol.setCellValueFactory(new PropertyValueFactory<>("active"));
            
            
            customersTable.setVisible(true);
        }
        else if (mode == TypeMode.Appointment)
        {
            //Get appointments from DB
            ArrayList<Appointment> appointments = sdal.getAppointments();


            //Load customers into tableview
            SimpleListProperty<Appointment> apptProperty = new SimpleListProperty<>();
            apptProperty.set(FXCollections.observableList(appointments));
            appointmentsTable.itemsProperty().bind(apptProperty);

            apptIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            apptStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
            apptEndCol.setCellValueFactory(new PropertyValueFactory<>("stop"));
            apptCustCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            apptTitleCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            apptLocationCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
            apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            
            
            appointmentsTable.setVisible(true);
        }
        else 
        {
            new Alert(AlertType.ERROR, "Invalid TypeMode detected in on Viewer load.")
                    .show();
        }
        

    }

    @FXML
    private void handleAddClick(ActionEvent event) {
        showCustomerAddEditWindow(false);
    }

    @FXML
    private void handleEditClick(ActionEvent event) {
        showCustomerAddEditWindow(true);
    }

    @FXML
    private void handleDeleteClick(ActionEvent event) {
        if(mode == TypeMode.Customer)
        {
            Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
            sdal.deleteCustomer(selectedCustomer.getCustomerId());
            customersTable.itemsProperty().get().remove(selectedCustomer);
        }
        else if(mode == TypeMode.Appointment)
        {
            Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
            sdal.deleteAppointment(selectedAppointment.getAppointmentId());
            customersTable.itemsProperty().get().remove(selectedAppointment);
        }
        else 
        {
            new Alert(AlertType.ERROR, "Invalid TypeMode detected in delete handler.")
                    .show();
        }
    }
    
    private void showCustomerAddEditWindow(boolean editing)
    {
        try
        {
            FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("/scheduler/AddEditCustomer.fxml"));
            Parent root = editorLoader.load();
            AddEditCustomerController vc = editorLoader.getController();
            if(editing)
            {
                Customer selected = customersTable.getSelectionModel().getSelectedItem();
                if(selected == null) { return; }
                vc.construct(sdal, stage, selected);
            }
            else
            {
                vc.construct(sdal, stage, null);
            }

            //Open in new window
            Scene scene = new Scene(root);
            Stage substage = new Stage();
            substage.setScene(scene);
            substage.initOwner(stage);

            substage.show();
        }
        catch (Exception e)
        {
            Alert al = new Alert(Alert.AlertType.ERROR);
            al.setContentText("Unable to load Editor interface, details below.\n\n"
             + e.getLocalizedMessage());
            al.show();
        }
    }
        
    private void showAppointmentAddEditWindow(boolean editing) throws SQLException
    {
        
        try
        {
            FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("/scheduler/AddEditAppoint.fxml"));
            Parent root = editorLoader.load();
            AddEditAppointController vc = editorLoader.getController();
            if(editing)
            {
                Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();
                vc.construct(sdal, stage, selected);
            }
            else
            {
                vc.construct(sdal, stage, null);
            }
            

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


            
    enum TypeMode
    {
        Appointment,
        Customer
    }
}

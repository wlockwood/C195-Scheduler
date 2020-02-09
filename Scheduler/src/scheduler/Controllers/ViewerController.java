package scheduler.Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduler.DataAccess.SchedulerDAL;
import scheduler.Model.Customer;



public class ViewerController implements Initializable {

    SchedulerDAL sdal;
    Stage stage;
    ViewerMode mode;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> custIdCol;
    @FXML private TableColumn<Customer, String> custNameCol;
    @FXML private TableColumn<Customer, String> custAddrCol;
    @FXML private TableColumn<Customer, String> custPhoneCol;
    @FXML private TableColumn<Customer, Boolean> custActCol;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    public void construct(SchedulerDAL _sdal, Stage _stage, ViewerMode _mode) throws IOException, SQLException
    {
        sdal = _sdal;
        stage = _stage;
        mode = _mode;
        
        //Get customers from DB
        ArrayList<Customer> customers = sdal.getCustomers();
        
        
        //Load customers into tableview
        SimpleListProperty<Customer> custProperty = new SimpleListProperty<>();
        custProperty.set(FXCollections.observableList(customers));
        customersTable.itemsProperty().bind(custProperty);
        
        custIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        custAddrCol.setCellValueFactory(new PropertyValueFactory<>("addressSquash"));
        
        
        
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

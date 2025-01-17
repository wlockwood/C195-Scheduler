package scheduler.Model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class Appointment {
    private int appointmentId;
    private Customer customer;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String url;
    private Instant start;
    private Instant end;

    public Appointment(int _apptId, Customer _cust, String _title, String _desc,
            String _loc, String _contact, String _type, String _url, 
            Instant _start, Instant _stop)
    {
        appointmentId = _apptId;
        customer = _cust;
        title = _title;
        description = _desc;
        location = _loc;
        contact = _contact;
        type = _type;
        url = _url;
        start = _start;
        end = _stop;
    }
    
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant end) {
        this.end = end;
    }
    
    public ZonedDateTime getLocalizedStart()
    {
        return LocalizeTime(start);
    }
    
    public ZonedDateTime getLocalizedEnd()
    {
        return LocalizeTime(end);
    }
    
    public String getPrettyLocalStart()
    {
        ZonedDateTime start = getLocalizedStart();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        String output = start.format(formatter);
        return output;
    }
    
    public String getPrettyLocalEnd()
    {
        ZonedDateTime end = getLocalizedEnd();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        String output = end.format(formatter);
        return output;
    }
    
    public String toMultilineString()
    {
         return "\nTitle: " + title
            + "\nCustomer: " + customer.getName()
            + "\nStart: " + getLocalizedStart().toString()
            + "\nEnd: " + getLocalizedEnd().toString();   
    }
    
    @Override
    public String toString()
    {
        return "Title: " + title + " | Customer: " + customer.getName() + " | Start: " + getPrettyLocalStart();
    }
  
    public static ZonedDateTime LocalizeTime(Instant utcTime)
    {
        return utcTime.atZone(ZoneId.systemDefault());
    }
}

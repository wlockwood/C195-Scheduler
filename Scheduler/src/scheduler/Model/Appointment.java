package scheduler.Model;

import java.time.Instant;

public class Appointment {
    int appointmentId;
    Customer customer;
    String title;
    String description;
    String location;
    String contact;
    String type;
    String url;
    Instant start;
    Instant stop;
}

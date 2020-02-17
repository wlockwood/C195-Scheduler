/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.TimeZone;
import java.time.ZonedDateTime;
import scheduler.DataAccess.SchedulerDAL;

/**
 *
 * @author Capsi
 */
public class TimeZoneTesting {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        TimeZone here = TimeZone.getDefault();
        Instant utcTime = Instant.now();
        
        
        SchedulerDAL db = new SchedulerDAL();
        
        
        ResultSet results = db.query("SELECT * FROM appointment");
        results.next();
        java.sql.Timestamp start = results.getTimestamp("start");
        System.out.println("Time from DB: " + start.toString());
        System.out.println("DB Time as Instant: " + start.toInstant().toString());
        long offsetMillis = here.getOffset(Instant.now().toEpochMilli());
        System.out.println("Offset millis: " + offsetMillis);
        Instant correctedInst = start.toInstant().plusMillis(offsetMillis);
        System.out.println("Corrected Inst: " + correctedInst);
        
        
        System.out.println();
        
        System.out.println("Now as Instant/UTC:" + utcTime.toString());
        System.out.println("Localizing time from UTC to " + here.getDisplayName() + "...");
        System.out.println("Local: " + utcTime.atZone(ZoneId.systemDefault()).toString());
        
        
      
    }
}

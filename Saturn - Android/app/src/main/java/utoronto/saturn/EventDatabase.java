package utoronto.saturn;

import android.annotation.SuppressLint;
import android.provider.ContactsContract;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventDatabase extends Database {

    private static final String table = "events";

    EventDatabase() {
        super();
        log.setLevel(Level.FINE);
    }

    boolean addEvent(String creator, String name, String description, String date, String type, String url, boolean isglobal) throws SQLException, ParseException{
        return DatabaseUtilities.addRowEvent(creator, name, description, date, type, url, isglobal);
    }

    boolean deleteEvent(int id) {
        return DatabaseUtilities.deleteRow(table, "id", Integer.toString(id));
    }

    public List<Event> getPopular() throws SQLException, MalformedURLException, ParseException {
        Statement st = super.connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT eventID, COUNT(*) AS count FROM users GROUP BY eventId ORDER BY count(*) DESC");
        ArrayList<Event> eventLst = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            eventLst.add(createEvent(rs.getInt(1)));
            if (eventLst.size() > 5)
                break;
        }
        rs.close();
        st.close();

        return eventLst;

    }
    public List<Event> getTrending() throws SQLException, MalformedURLException, ParseException{
        Statement st = super.connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT id FROM events ORDER BY date ORDER BY count(*) DESC");
        ArrayList<Event> eventLst = new ArrayList<>();
        while (rs.next()) {
            eventLst.add(createEvent(rs.getInt(1)));
            if(eventLst.size() > 5)
                break;
        }
        rs.close();
        st.close();
        return eventLst;
    }
    public List<Event> getSuggested() throws SQLException, MalformedURLException, ParseException{
        Statement st = super.connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT type, COUNT(*) FROM events GROUP BY type ORDER BY count(*) DESC");
        String type = "";
        while (rs.next()) {
            type = rs.getString(1);
        }
        rs = DatabaseUtilities.selectRow(table, "id", "type", type);
        if(rs == null) {
            return null;
        }
        ArrayList<Event> eventLst = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        while (rs.next()) {
            eventLst.add(createEvent(rs.getInt(1)));
            if(eventLst.size() > 5)
                break;
        }
        rs.close();
        st.close();

        return eventLst;

    }

    Event createEvent(int id) throws SQLException, ParseException, MalformedURLException {
        ResultSet rs = DatabaseUtilities.selectRows(table, Arrays.asList("name", "url", "date"), "id", Integer.toString(id));
        if(rs == null) {
            return null;
        }
        ResultSetMetaData rsmd = rs.getMetaData();

        String name = "";
        String url = "";
        String date = "";

        while (rs.next()) {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                if (i == 1)
                    name = rs.getString(i);
                if (i == 2)
                    url = rs.getString(i);
                if (i == 3)
                    date = rs.getString(i);
            }
        }

        //https://stackoverflow.com/questions/12473550/how-to-convert-a-string-date-to-long-millseconds
        @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Date parseDate = f.parse(date);
        long milliseconds = parseDate.getTime();

        URL u = new URL(url);

        Event newEvent = new Event(Integer.toString(id), name, u, milliseconds);
        rs.close();
        return newEvent;
    }
}

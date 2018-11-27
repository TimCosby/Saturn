package utoronto.saturn.app;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import utoronto.saturn.Event;
import utoronto.saturn.EventDatabase;
import utoronto.saturn.SQLBackgroundQuery;
import utoronto.saturn.User;
import utoronto.saturn.UserDatabase;

public class GuiManager {
    // This class is the connection between the front end and the back end
    // A singleton class accessed by the front end to query for info, etc.

    private static GuiManager instance = new GuiManager();
    private User currentUser;
    private static final String[] categories = new String[]{"Anime", "Concerts", "Movies", "Games"};
    private static String[] types;
    private static Map<String, List<Event>> allEvents;
    private static final Logger log = Logger.getLogger(GuiManager.class.getName());

    private GuiManager() {
        allEvents = new HashMap<>();
        types = new String[]{"anime", "concert", "movie", "game"};
        for (String type : types) {
            BackgroundQuery query = new BackgroundQuery();
            query.execute(type);
        }
    }

    // Use this to get the current instance of this class
    public static GuiManager getInstance() {
        return instance;
    }

    /*
        Checks to see if the given email, is already taken
        Returns true if the sign up is successful and false otherwise
     */
    public boolean signUp(String username, String email, String password) {
        // Create the new user account and add it to the users database
        User user = new User(username, email, password);
        boolean res = UserDatabase.openAccount(user);
        if (res) {
            setCurrentUser(user);
            return true;
        }
        return false;
    }

    /*
        Checks to see if the given email, and password are valid
        Returns true if the login is successful and false otherwise
     */
    public boolean logIn(String email, String password) {
        // Get the resulting relations after selecting email
        String username;
        if (UserDatabase.checkUserCredentials(email, password) && (username = UserDatabase.getUsername(email)) != null) {
            setCurrentUser(new User(username, email, password));
            return true;
        }
        return false;
    }

    // get event functions
    public List<Event> getEventsByCategory(String category) {
        return allEvents.get(category);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void setCurrentUser(User user) {
        currentUser = user;
    }

    // get suggestions based on the current user
    public List<Event> getSuggestions() {
        return null;
    }

    public static String[] getCategories() {
        return categories;
    }

    public List<Event> getUserFollowedEvents() {
        try {
            return EventDatabase.getUserFollowedEvents(currentUser.getEmail());
        } catch (ParseException | SQLException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static class BackgroundQuery extends AsyncTask<String, Void, ResultSet> {
        Connection conn;
        PreparedStatement st;
        String type;

        @Override
        protected ResultSet doInBackground(String... strings) {
            try {
                Class.forName("org.postgresql.Driver");
                //STEP 3: Open a connection
                Log.d("myTag", "Connecting to database...");
                conn = DriverManager.getConnection("jdbc:postgresql://tantor.db.elephantsql.com:5432/tjlevpcn"
                        , "tjlevpcn", "SlQEEkbB5hwPHBQxbyrEziDv7w5ozmUu");
                type = strings[0];
                st = conn.prepareStatement(String.format("SELECT DISTINCT id FROM events WHERE type = '%s'", type));
                ResultSet s = st.executeQuery();
                conn.close();
                return s;
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                throw new IllegalStateException("Invalid Query!");
            }
        }
            @Override
        protected void onPostExecute(ResultSet result) {
            log.info("onPostExecute");
            List<Event> events = allEvents.get(type);
            if (events == null) {
                allEvents.put(type, new ArrayList<>());
                events = allEvents.get(type);
            }
            try {
                while (result != null && result.next()) {
                    assert events != null;
                    events.add(EventDatabase.createEvent(result.getInt("id")));
                }
            } catch (SQLException | ParseException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}

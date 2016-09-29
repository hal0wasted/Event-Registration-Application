package ca.mcgill.ecse321.eventregistration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import ca.mcgill.ecse321.eventregistration.controller.EventRegistrationController;
import ca.mcgill.ecse321.eventregistration.controller.InvalidInputException;
import ca.mcgill.ecse321.eventregistration.model.Event;
import ca.mcgill.ecse321.eventregistration.model.Participant;
import ca.mcgill.ecse321.eventregistration.model.RegistrationManager;
import ca.mcgill.ecse321.eventregistration.persistence.PersistenceEventRegistration;

public class MainActivity extends AppCompatActivity {

    private String errorParticipant = null;
    private String errorEvent = null;
    private String errorRegister = null;
    private HashMap<Integer, Participant> participants;
    private HashMap<Integer, Event> events;
    private static boolean isFirstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //runs only on first instance of onCreate(). Does not re-run when activity is killed and revived.
        if (isFirstLaunch){
            //Set up persistence layer to current directory and get data.
            PersistenceEventRegistration.setFilename(getFilesDir().getAbsolutePath()+File.separator+"eventregistration.xml");
            PersistenceEventRegistration.loadEventRegistrationModel();
            isFirstLaunch = false;
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshData();
    }

    private void refreshData(){

        RegistrationManager rm = RegistrationManager.getInstance();

        //Reset text fields

        TextView participantNameView = (TextView) findViewById(R.id.newparticipant_name);
        TextView eventNameView = (TextView) findViewById(R.id.newevent_name);
        TextView registrationErrorView = (TextView) findViewById(R.id.registrationError);

        participantNameView.setError(errorParticipant);
        eventNameView.setError(errorEvent);
        registrationErrorView.setError(errorRegister);

        if (errorEvent == null && errorParticipant == null && errorRegister == null){
            participantNameView.setText("");
            eventNameView.setText("");

            // Initialize the data in the participant and spinner
            Spinner participantSpinner = (Spinner) findViewById(R.id.participantspinner);
            ArrayAdapter<CharSequence> participantAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
            participantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.participants = new HashMap<Integer, Participant>();
            int i = 0;
            for (Iterator<Participant> participants = rm.getParticipants().iterator(); participants.hasNext(); i++) {
                Participant p = participants.next();
                participantAdapter.add(p.getName());
                this.participants.put(i, p);
            }
            participantSpinner.setAdapter(participantAdapter);

            Spinner eventSpinner = (Spinner) findViewById(R.id.eventspinner);
            ArrayAdapter<CharSequence> eventAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
            eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.events = new HashMap<Integer, Event>();
            int j = 0;
            for (Iterator<Event> events = rm.getEvents().iterator(); events.hasNext(); j++) {
                Event e = events.next();
                eventAdapter.add(e.getName());
                this.events.put(j, e);
            }
            eventSpinner.setAdapter(eventAdapter);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addParticipant(View v){
        TextView tv = (TextView) findViewById(R.id.newparticipant_name);
        EventRegistrationController pc = new EventRegistrationController();

        errorEvent = null;
        errorParticipant = null;

        try{
            pc.createParticipant(tv.getText().toString());
        } catch(InvalidInputException e){
            errorParticipant = e.getMessage();
        }

        refreshData();
    }

    public void addEvent(View v){
        TextView eventNameView = (TextView) findViewById(R.id.newevent_name);
        TextView eventStartView = (TextView) findViewById(R.id.newevent_start_time);
        TextView eventEndView = (TextView) findViewById(R.id.newevent_end_time);
        TextView eventDateView = (TextView) findViewById(R.id.newevent_date);

        String eventName = eventNameView.getText().toString();
        java.sql.Date eventDate = getSqlDateFromLabel(eventDateView.getText());
        java.sql.Time eventStartTime = getSqlTimeFromLabel(eventStartView.getText());
        java.sql.Time eventEndTime = getSqlTimeFromLabel(eventEndView.getText());

        EventRegistrationController ec = new EventRegistrationController();

        errorEvent = null;
        errorParticipant = null;

        try{
            ec.createEvent(eventName,eventDate,eventStartTime,eventEndTime);
        }catch (InvalidInputException e){
            errorEvent = e.getMessage();
        }

        refreshData();
    }

    public void registerParticipant(View v){

        EventRegistrationController rc = new EventRegistrationController();

        Spinner participantSpinner = (Spinner) findViewById(R.id.participantspinner);
        Spinner eventSpinner = (Spinner) findViewById(R.id.eventspinner);

        Participant selectedParticipant = participants.get(participantSpinner.getSelectedItemPosition());
        Event selectedEvent = events.get(eventSpinner.getSelectedItemPosition());

        try {
            rc.register(selectedParticipant, selectedEvent);
        } catch (InvalidInputException e){
            //no error is currently possible given the UI

            errorRegister = e.getMessage();
        }

    }

    public void showDatePickerDialog(View v) {
        TextView tf = (TextView) v;
        Bundle args = getDateFromLabel(tf.getText());
        args.putInt("id", v.getId());
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        TextView tf = (TextView) v;
        Bundle args = getTimeFromLabel(tf.getText());
        args.putInt("id", v.getId());
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private Bundle getTimeFromLabel(CharSequence text) {
        Bundle rtn = new Bundle();
        String comps[] = text.toString().split(":");
        int hour = 12;
        int minute = 0;
        if (comps.length == 2) {
            hour = Integer.parseInt(comps[0]); minute = Integer.parseInt(comps[1]);
        }
        rtn.putInt("hour", hour); rtn.putInt("minute", minute);
        return rtn;
    }
    private Bundle getDateFromLabel(CharSequence text) {
        Bundle rtn = new Bundle();
        String comps[] = text.toString().split("-");
        int day = 1;
        int month = 1;
        int year = 1;
        if (comps.length == 3) {
            day = Integer.parseInt(comps[0]); month = Integer.parseInt(comps[1]); year = Integer.parseInt(comps[2]);
        }
        rtn.putInt("day", day); rtn.putInt("month", month-1); rtn.putInt("year", year);
        return rtn;
    }

    // Helper methods adapted from code included in PDF
    // Appropriated example code to be able to read the sql.date and sql.time formats from the label to add as inputs for controller

    private java.sql.Date getSqlDateFromLabel(CharSequence text) {
        String comps[] = text.toString().split("-");
        int day = 1;
        int month = 1;
        int year = 1;
        if (comps.length == 3) {
            day = Integer.parseInt(comps[0]); month = Integer.parseInt(comps[1]); year = Integer.parseInt(comps[2]);
        }

        String dateString = year+"-"+month+"-"+day;

        java.sql.Date returnDate = java.sql.Date.valueOf(dateString);

        return returnDate;
    }

    private java.sql.Time getSqlTimeFromLabel(CharSequence text) {
        String timeString = text.toString()+":00";

        return java.sql.Time.valueOf(timeString);


    }

    public void setTime(int id, int h, int m) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d:%02d", h, m));
    }

    public void setDate(int id, int d, int m, int y) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(String.format("%02d-%02d-%04d", d, m + 1, y));
    }

}

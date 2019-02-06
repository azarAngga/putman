package approval.com.approval_sdi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import reportku.com.id.R;

public class report_progress extends AppCompatActivity {
    private boolean undo = false;
    private CaldroidFragment caldroidFragment;
    ProgressDialog pg;
    private CaldroidFragment dialogCaldroidFragment;
    JSONArray jsArray;
    String message = null;
    ArrayList<HashMap<String,String>> ahas2 = new ArrayList<HashMap<String,String>>();

    public static final 	String KEY_NAMA 	= "nama";
    public static final 	String KEY_USERNAME = "username";
    public static final 	String KEY_PASSWORD = "password";
    public static final 	String KEY_ROLE = "role";
    private static final 	String PREFER_NAME 	= "AndroidExamplePref";

    int kendala = 0;
    SharedPreferences sharedPreferences;
    UserSessionManager session;

    private void setCustomResourceForDates() {
        String date = null;
        String type = null;

        // Min date is last 7 days
        /*cal.add(Calendar.DATE, -5);
        Date blueDate = cal.getTime();

        // Max date is next 7 days
        cal = Calendar.getInstance();
        Log.v("calendar",String.valueOf(Calendar.getInstance()));
        cal.add(Calendar.DATE, 0);
        Date greenDate = cal.getTime();*/

        for(HashMap<String,String> map :ahas2){
            for(Map.Entry<String,String> entryMap : map.entrySet()){
                String key = entryMap.getKey();
                String value = entryMap.getValue();

                if(key.equals("date")){
                    date = value;
                }

                if(key.equals("type")){
                    type  = value;
                }
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, Integer.parseInt(date));
            Date blueDate = cal.getTime();
            //preventive
            if(type.equals("1")){
                if (caldroidFragment != null) {
                    ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.blue));
                    ColorDrawable green = new ColorDrawable(Color.GREEN);
                    caldroidFragment.setBackgroundDrawableForDate(blue, blueDate);
                    //caldroidFragment.setBackgroundDrawableForDate(green, greenDate);
                    caldroidFragment.setTextColorForDate(R.color.white, blueDate);
                    //caldroidFragment.setTextColorForDate(R.color.white, greenDate);
                }
            //corective
            }else if(type.equals("2")){
                if (caldroidFragment != null) {
                    //ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.blue));
                    ColorDrawable green = new ColorDrawable(Color.GREEN);
                    caldroidFragment.setBackgroundDrawableForDate(green, blueDate);
                    //caldroidFragment.setBackgroundDrawableForDate(green, greenDate);
                    caldroidFragment.setTextColorForDate(R.color.white, blueDate);
                    //caldroidFragment.setTextColorForDate(R.color.white, greenDate);
                }
            }else{
                if (caldroidFragment != null) {
                    //ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.blue));
                    ColorDrawable orange = new ColorDrawable(getResources().getColor(R.color.orange));
                    caldroidFragment.setBackgroundDrawableForDate(orange, blueDate);
                    //caldroidFragment.setBackgroundDrawableForDate(green, greenDate);
                    caldroidFragment.setTextColorForDate(R.color.white, blueDate);
                    //caldroidFragment.setTextColorForDate(R.color.white, greenDate);
                }

            }
        }

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_droid);
        session 	= new UserSessionManager(getApplicationContext());
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Button logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(report_progress.this,login.class);
                startActivity(in);
                session.logoutUser();
            }
        });
        // Setup caldroid fragment
        // **** If you want normal CaldroidFragment, use below line ****
        caldroidFragment = new CaldroidFragment();

        // //////////////////////////////////////////////////////////////////////
        // **** This is to show customized fragment. If you want customized
        // version, uncomment below line ****
//		 caldroidFragment = new CaldroidSampleCustomFragment();

        // Setup arguments

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            // Uncomment this to customize startDayOfWeek
            // args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
            // CaldroidFragment.TUESDAY); // Tuesday

            // Uncomment this line to use Caldroid in compact mode
            // args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

            // Uncomment this line to use dark theme
//            args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);

            caldroidFragment.setArguments(args);
        }

        new get_data().execute();

        // Attach to the activity


        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {

                Intent in  = new Intent(getApplicationContext(),view_progress.class);
                in.putExtra("date",date.toString());

                startActivity(in);
                //Toast.makeText(getApplicationContext(), date.toString(),
                       // Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
                //Toast.makeText(getApplicationContext(), text,
                 //       Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                //Toast.makeText(getApplicationContext(),
                //        "Long click " + formatter.format(date),
                //        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    //Toast.makeText(getApplicationContext(),
                    //        "Caldroid view is created", Toast.LENGTH_SHORT)
                    //        .show();
                }
            }

        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

        final TextView textView = (TextView) findViewById(R.id.textview);

        final Button customizeButton = (Button) findViewById(R.id.customize_button);

        // Customize the calendar
        customizeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (undo) {
                    customizeButton.setText("custome");
                    textView.setText("");

                    // Reset calendar
                    caldroidFragment.clearDisableDates();
                    caldroidFragment.clearSelectedDates();
                    caldroidFragment.setMinDate(null);
                    caldroidFragment.setMaxDate(null);
                    caldroidFragment.setShowNavigationArrows(true);
                    caldroidFragment.setEnableSwipe(true);
                    caldroidFragment.refreshView();
                    undo = false;
                    return;
                }

                // Else
                undo = true;
                customizeButton.setText("undo");
                Calendar cal = Calendar.getInstance();

                // Min date is last 7 days
                cal.add(Calendar.DATE, -7);
                Date minDate = cal.getTime();

                // Max date is next 7 days
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 14);
                Date maxDate = cal.getTime();

                // Set selected dates
                // From Date
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 2);
                Date fromDate = cal.getTime();

                // To Date
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 3);
                Date toDate = cal.getTime();

                // Set disabled dates
                ArrayList<Date> disabledDates = new ArrayList<Date>();
                for (int i = 5; i < 8; i++) {
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, i);
                    disabledDates.add(cal.getTime());
                }

                // Customize
                caldroidFragment.setMinDate(minDate);
                caldroidFragment.setMaxDate(maxDate);
                caldroidFragment.setDisableDates(disabledDates);
                caldroidFragment.setSelectedDates(fromDate, toDate);
                caldroidFragment.setShowNavigationArrows(false);
                caldroidFragment.setEnableSwipe(false);

                caldroidFragment.refreshView();

                // Move to date
                // cal = Calendar.getInstance();
                // cal.add(Calendar.MONTH, 12);
                // caldroidFragment.moveToDate(cal.getTime());

                String text = "Today: " + formatter.format(new Date()) + "\n";
                text += "Min Date: " + formatter.format(minDate) + "\n";
                text += "Max Date: " + formatter.format(maxDate) + "\n";
                text += "Select From Date: " + formatter.format(fromDate)
                        + "\n";
                text += "Select To Date: " + formatter.format(toDate) + "\n";
                for (Date date : disabledDates) {
                    text += "Disabled Date: " + formatter.format(date) + "\n";
                }

                textView.setText(text);
            }
        });

        Button showDialogButton = (Button) findViewById(R.id.show_dialog_button);

        final Bundle state = savedInstanceState;
        showDialogButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Setup caldroid to use as dialog
                dialogCaldroidFragment = new CaldroidFragment();
                dialogCaldroidFragment.setCaldroidListener(listener);

                // If activity is recovered from rotation
                final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
                if (state != null) {
                    dialogCaldroidFragment.restoreDialogStatesFromKey(
                            getSupportFragmentManager(), state,
                            "DIALOG_CALDROID_SAVED_STATE", dialogTag);
                    Bundle args = dialogCaldroidFragment.getArguments();
                    if (args == null) {
                        args = new Bundle();
                        dialogCaldroidFragment.setArguments(args);
                    }
                } else {
                    // Setup arguments
                    Bundle bundle = new Bundle();
                    // Setup dialogTitle
                    dialogCaldroidFragment.setArguments(bundle);
                }

                dialogCaldroidFragment.show(getSupportFragmentManager(),
                        dialogTag);
            }
        });
    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
    }

    api api = new api();
    private class get_data extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            try{
                JSONParser jsParser  = new JSONParser();
                JSONObject object = jsParser.AmbilJson(api.url+"get_report.php");
                Log.v("url",api.url+"get_report.php");
                jsArray = object.getJSONArray("data");
                for(int in = 0;in<jsArray.length();in++){
                    JSONObject ob = jsArray.getJSONObject(in);
                    HashMap<String,String> hmString = new HashMap<String,String>();
                    String date = ob.getString("date");
                    String type = ob.getString("type");

                    hmString.put("date",date);
                    hmString.put("type",type);

                    ahas2.add(hmString);
                    Log.v("masukkk","masukk");
                }
            }catch (Exception e){
                Log.v("tidak","tidak");
                message = String.valueOf(e);
            }
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(report_progress.this);
            pg.setMessage("Please Wait...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            pg.dismiss();
            setCustomResourceForDates();
        }
    }

}
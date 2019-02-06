package approval.com.approval_sdi;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import reportku.com.id.R;

/**
 * Created by ruphia on 1/4/2018.
 */

public class form_input extends AppCompatActivity {

    Button input;
    EditText nama,alamat,no_telp,latitude,longitude;
    GPSTracker gps;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_input);
        nama    = (EditText)findViewById(R.id.nama);
        alamat  = (EditText)findViewById(R.id.alamat);
        no_telp = (EditText)findViewById(R.id.no_telp);
        latitude = (EditText)findViewById(R.id.latitude);
        longitude = (EditText)findViewById(R.id.longitude);

        gps = new GPSTracker(this);
        getgps();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        input = (Button) findViewById(R.id.input);

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new submit().execute();
            }
        });
    }

    GPSTracker2 gps2;
    api api = new api();
    public void getgps(){
        gps2 = new GPSTracker2(form_input.this);

        // check if GPS enabled
        if(gps2.canGetLocation()){

            double latitude = gps2.getLatitude();
            double longitude = gps2.getLongitude();

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps2.showSettingsAlert();
        }
    }

    ProgressDialog dialog1;
    JSONArray array;
    class submit extends AsyncTask<String, String,String > {
        @Override
        protected void onPreExecute(){
            // TODO Auto-generated method stub
            super.onPreExecute();
            try{
                dialog1 = new ProgressDialog(form_input.this);
                dialog1.setMessage("Please wait...");
                dialog1.setIndeterminate(false);
                dialog1.setCancelable(false);
                dialog1.show();
            }catch (Exception e){
            }
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            //super.onPreExecute();
            JSONParser parser = new JSONParser();
            JSONObject object = null;
            String nama_="";
            String alamat_="";
            String no_telp_="";
            String latitude_="";
            String longitude_="";

            try{
                nama_ = URLEncoder.encode(nama.getText().toString().trim(),"utf-8");
                alamat_ = URLEncoder.encode(alamat.getText().toString().trim(),"utf-8");
                no_telp_ = URLEncoder.encode(no_telp.getText().toString().trim(),"utf-8");
                latitude_ = URLEncoder.encode(latitude.getText().toString().trim(),"utf-8");
                longitude_ = URLEncoder.encode(longitude.getText().toString().trim(),"utf-8");
            }catch (UnsupportedEncodingException e){

            }
            object = parser.AmbilJson(api.url+"insert.php?nama="+nama_+"&alamat="+alamat_+"&no_telp="+no_telp_+"&latitude="+latitude_+"&longitude="+longitude_);
            try {
                array = object.getJSONArray("data");
                for (int i = 0; i < array.length(); i++){
                    JSONObject ar    = array.getJSONObject(i);
                }
            }catch (JSONException e){
                // TODO Auto-generated catch block
            }catch (Exception e){
                Log.v("tuluse","tuluse");
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result2){
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(form_input.this,"Data Berhasil Terinput",Toast.LENGTH_LONG).show();
                    clear();
                    dialog1.dismiss();
                }
            });
        }
    }

    public void clear(){
        nama.setText("");
        alamat.setText("");
        no_telp.setText("");
        latitude.setText("");
        longitude.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish(); return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

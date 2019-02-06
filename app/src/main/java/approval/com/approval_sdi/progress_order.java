package approval.com.approval_sdi;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import reportku.com.id.R;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class progress_order extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback, View.OnClickListener {

    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static int MY_PERMISSIONS_REQUEST_STORAGE = 2;
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;

    static int w = 1000;
    static int h = 1000;
    private String imageFilePath = "";
    public static final 	String KEY_NAMA 	= "nama";
    public static final 	String KEY_USERNAME = "username";
    public static final 	String KEY_PASSWORD = "password";
    public static final 	String KEY_ROLE = "role";
    private static final 	String PREFER_NAME 	= "AndroidExamplePref";
    boolean is_capture = false;

    int kendala = 0;
    SharedPreferences sharedPreferences;
    UserSessionManager session;
    String s_id_userk = null;
    private static final Location TODO = null;

    private static final String IMAGE_DIRECTORY_NAME = "tagging";
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_2 = 101;
    private static final int RESULT_LOAD_IMG = 103;
    private static final int RESULT_LOAD_IMG2 = 104;
    static File file_foto;
    String nama_foto = "-";
    static String currentDateandTime;
    ImageView img;
    TextView address;
    String Path;
    GPSTracker gps;
    LocationManager lm;
    LocationListener ll;
    TextView t_latitude,t_longitude;

    private Uri fileUri= null;
    LinearLayout listlinear;
    String order = null;
    String in = null;
    String activity = null;
    String nik_ = null;
    RadioGroup rg_ring;

    Spinner task;
    Context ctx;

    EditText nama_task,deskripsion_task;
    Button simpan,submit,prompt_task;
    String s_task_id = "1";

    String id_creator ="1";
    modelData model;
    String s_type = null;
    String s_address = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);



      //  getActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.blue)));

        gps = new GPSTracker(this);
        ctx = this;
        model = new modelData();

        submit      = (Button)findViewById(R.id.submit);
        prompt_task = (Button)findViewById(R.id.prompt_task);

        submit.setOnClickListener(this);
        prompt_task.setOnClickListener(this);

        Intent intent = getIntent();
        s_type = intent.getStringExtra("program");
        Log.v("status",s_type);
        loadMap();
        getgps();
        loadElement();

        currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        sharedPreferences = getSharedPreferences(PREFER_NAME,Context.MODE_PRIVATE);
        session 	= new UserSessionManager(getApplicationContext());
        try{
            order = getIntent().getStringExtra("id_status");
            activity = order;
            Log.v("order aa",order);
        }catch(Exception e){}

        in = sharedPreferences.getString(KEY_ROLE, "");

        //set model
        model.setRole(in);
        model.setIdUser(sharedPreferences.getString(KEY_USERNAME, ""));
        id_creator = sharedPreferences.getString(KEY_USERNAME, "");
        Log.v("role",in);
        if(order == null){
            if(in.trim().equals("1")){
                order = "1";
                activity = order;
            }else{
                order = "4";
                activity = order;
            }
        }

        Log.v("order","dd"+order);
        if(order.trim().equals("1")){
            setTitle("List Order");
        }else if(order.trim().equals("2")){
            setTitle("Order Teknisi");
        }else if(order.trim().equals("3")){
            setTitle("Order Teknisi");
        }else if(order.trim().equals("4")){
            setTitle("Inbox Approval");
        }else if(order.trim().equals("5")){
            setTitle("Progress Order");
        }

        Log.v("order","order"+order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(s_type.equals("1")){
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
            toolbar.setBackgroundColor(Color.parseColor("#159afc"));
            toolbar.setTitle("Preventive");
        }else{
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
            toolbar.setBackgroundColor(Color.parseColor("#159afc"));
            toolbar.setTitle("Corrective");
        }

        setSupportActionBar(toolbar);

        //listlinear = (LinearLayout)findViewById(R.id.list_order) ;
        //gps = new GPSTracker(this);
        showCurrentLocation();
        //new get_list_lokasi().execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        View header = navigationView.getHeaderView(0);

        TextView nama   = (TextView)header.findViewById(R.id.nama);
        TextView nik       = (TextView)header.findViewById(R.id.nik);
        nama.setText(sharedPreferences.getString(KEY_NAMA, ""));
        nik.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        s_id_userk = sharedPreferences.getString(KEY_USERNAME, "");

        new getTask().execute();

        Log.v("role",in);
        if(in.trim().equals("1")){
            navigationView.inflateMenu(R.menu.activity_main_drawer_2);
        }else{
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadElement() {
        task =  (Spinner)findViewById(R.id.task);
        img =  (ImageView)findViewById(R.id.img);
        address  = (TextView)findViewById(R.id.address);

        //load_map =  (ImageView)findViewById(R.id.load_map);
        BitmapFactory.Options options = new BitmapFactory.Options();

        // downsizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 2;

        //final Bitmap bitmap = BitmapFactory.decodeFile("content://media/external/images/media/46013.jpg", options);
        //final Bitmap bitmap = BitmapFactory.decodeFile("file:///storage/emulated/0/Pictures/tagging/2018-09-24%2022%3A49%3A11.jpg", options);
        //img.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent in = null;
        if (id == R.id.nav_list_order) {
             in.putExtra("id_status","1");
            finish();
        } else if (id == R.id.nav_order_petugas) {
            in.putExtra("id_status","2");
            finish();
        }else if(id == R.id.nav_order_petugas){
            in.putExtra("id_status","3");
            finish();
        }else if(id == R.id.home){
            in  = new Intent(progress_order.this,menu_role.class);
            startActivity(in);
        }else if(id == R.id.task){
            in  = new Intent(progress_order.this,fragmen_history.class);
            in.putExtra("type","1"); // task
            startActivity(in);
        }else if(id == R.id.task_done){
            in  = new Intent(progress_order.this,fragmen_history.class);
            in.putExtra("type","2"); // done
            startActivity(in);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    ProgressDialog pg;
    String url_list ="get_task.php";
    JSONArray jsArray;
    api api = new api();
    ArrayList<HashMap<String,String>> ahas = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> ahas2 = new ArrayList<HashMap<String,String>>();

    ArrayList<String> id_task = new ArrayList<>();
    ArrayList<String> task_name = new ArrayList<>();
    String message = null;


    // action click
    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.submit:
                submit();
                break;
            case R.id.prompt_task:
                prompt_task();
                break;
        }

    }

    private class getTask extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            ahas.clear();
            id_task.clear();
            task_name.clear();
            try{
                JSONParser jsParser  = new JSONParser();
                Log.v("urlx",api.url+url_list+"?id="+s_id_userk+"&type="+s_type);
                JSONObject object = jsParser.AmbilJson(api.url+url_list+"?id="+s_id_userk+"&type="+s_type);
                String status = "";
                status = object.getString("status");
                Log.v("isi",String.valueOf(object));

                id_task.add("0");
                task_name.add("--Task--");

                if(status.equals("T")){
                    jsArray = object.getJSONArray("data");
                    for(int in = 0;in<jsArray.length();in++){
                        JSONObject ob = jsArray.getJSONObject(in);
                        Log.v("isi",ob.getString("id"));
                        String id = ob.getString("id");
                        String nama = ob.getString("nama_task");

                        id_task.add(id);
                        task_name.add(nama);
                    }

                }
            }catch (Exception e){
                Log.v("tidak","tidak");
                Log.v("error_text",String.valueOf(e));
                Log.v("urlx_erro",api.url+url_list+"?id="+s_id_userk);
                message = String.valueOf(e);
            }
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(progress_order.this);
            pg.setMessage("Please Wait...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            setAdapterSpinner();
            pg.dismiss();
        }

    }

    private void setAdapterSpinner(){
        ArrayAdapter<String> adapter_task = new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,task_name);
        task.setAdapter(adapter_task);
        task.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                s_task_id = id_task.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    String id_;
    JSONArray array;
    class submit extends AsyncTask<String, String,String > {
        @Override
        protected void onPreExecute(){
            // TODO Auto-generated method stub
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            //super.onPreExecute();
            JSONParser parser = new JSONParser();
            JSONObject object = null;
            String ket_="";

            try{
            ket_ = URLEncoder.encode(userInput.getText().toString().trim(),"utf-8");
            }catch (UnsupportedEncodingException e){

            }catch(Exception e){
                ket_ = "";
            }
            if(order.equals("1")){
                object = parser.AmbilJson(api.url+"input.php?id="+id_+"&id_user="+nik_+"&id_status="+order+"&latitude="+s_latitude+"&longitude="+s_longitude);
                Log.i("url",api.url+"input.php?id="+id_+"&id_user="+nik_);
                Log.v("url",api.url+"input.php?id="+id_+"&id_user="+nik_);
            }else if(order.equals("2")){
                object = parser.AmbilJson(api.url+"input.php?id="+id_+"&id_user="+nik_+"&id_status="+order+"&keterangan="+ket_+"&nama_file="+nama_foto+"&latitude="+s_latitude+"&longitude="+s_longitude+"&kendala="+String.valueOf(kendala));
                Log.v("url",api.url+"input.php?id="+id_+"&id_user="+nik_+"&id_status="+order+"&keterangan="+ket_+"&nama_file="+nama_foto+"&latitude="+s_latitude+"&longitude="+s_longitude);
            }else if(order.equals("3")){
                object = parser.AmbilJson(api.url+"input.php?id="+id_+"&id_user="+nik_+"&id_status="+order+"&keterangan="+ket_+"&nama_file="+nama_foto+"&latitude="+s_latitude+"&longitude="+s_longitude+"&kendala="+String.valueOf(kendala));
                Log.v("url",api.url+"input.php?id="+id_+"&id_user="+nik_+"&id_status="+order+"&keterangan="+ket_+"&nama_file="+nama_foto+"&latitude="+s_latitude+"&longitude="+s_longitude);
            }else if(order.equals("4")){
                object = parser.AmbilJson(api.url+"input.php?id="+id_+"&id_user="+nik_+"&id_status="+order+"&keterangan="+ket_+"&nama_file="+nama_foto+"&latitude="+s_latitude+"&longitude="+s_longitude);
                Log.v("url",api.url+"input.php?id="+id_+"&id_user="+nik_+"&id_status="+order+"&keterangan="+ket_+"&nama_file="+nama_foto+"&latitude="+s_latitude+"&longitude="+s_longitude);
            }else{
                object = parser.AmbilJson(api.url+"input.php?id="+id_+"&id_user="+nik_+"&id_status="+order+"&latitude="+s_latitude+"&longitude="+s_longitude);
            }

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
                    kendala = 0;
                    nama_foto = "";
                    Log.v("dddd","tulus");
//                    dialog1.dismiss();
                    //new get_list_lokasi().execute();
                }
            });
        }
    }

    EditText userInput = null;

    public void prompt(){
        LayoutInflater li = LayoutInflater.from(progress_order.this);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(progress_order.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        userInput = (EditText) promptsView
                .findViewById(R.id.prompt);

       img = (ImageView) promptsView
                .findViewById(R.id.img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //captureImage();
                //camera2();
                //Toast.makeText(progress_order.this,"tttt",Toast.LENGTH_LONG).show();
              /*                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
*/
            }
        });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //if(order.trim().equals("2")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            kendala = 0;
                                            //new up().execute();
                                        }
                                    });

                                    //new submit().execute();
                                //}else{
                                 //   new submit().execute();
                                    //new up().execute();
                                //}
                            }
                        })
                .setNegativeButton("Kendala",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //dialog.cancel();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        kendala = 1;
                                        //new up().execute();
                                    }
                                });
                                //finish();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    RelativeLayout ln;
    public void promptImage(){
        //new putProgress().execute();
        LayoutInflater li = LayoutInflater.from(progress_order.this);
        View promptsView = li.inflate(R.layout.prompt_upload, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(progress_order.this);
        ImageView foto = (ImageView)promptsView.findViewById(R.id.img);
        ImageView map  = (ImageView)promptsView.findViewById(R.id.map);
        TextView address = (TextView)promptsView.findViewById(R.id.address);
        TextView latitude = (TextView)promptsView.findViewById(R.id.d_latitude);
        TextView longitude = (TextView)promptsView.findViewById(R.id.d_longitude);
        ln = (RelativeLayout)promptsView.findViewById(R.id.ln_prompt);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        foto.setImageURI(Uri.parse(imageFilePath));

        Bitmap myBitmap = BitmapFactory.decodeFile(imageFilePath);
        foto.setImageBitmap(myBitmap);
        foto.setRotation(90);

        foto.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 3000, 2000, false));

        map.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/vendor/capture_map.jpg"));
        address.setText(s_address);
        map.setAlpha(150);
        latitude.setText(s_latitude);
        longitude.setText(s_longitude);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("simpan",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run(){
                                        Log.v("save","ok");
                                        new up().execute();
                                    }
                                });
                            }
                        })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //dialog.cancel();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + currentDateandTime + ".jpg");
        } else {
            return null;
        }
        file_foto = mediaFile;
        return mediaFile;
    }

    public void prompt2(){
        LayoutInflater li = LayoutInflater.from(progress_order.this);
        View promptsView = li.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(progress_order.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        userInput = (EditText) promptsView
                .findViewById(R.id.prompt);

        userInput.setVisibility(View.GONE);
        img = (ImageView) promptsView
                .findViewById(R.id.img);
        img.setVisibility(View.GONE);

        TextView t = (TextView)promptsView.findViewById(R.id.keterangan);
        if(order.trim().equals("4")){
            t.setText("Apakah anda akan approve project ini?");
        }else if(order.trim().equals("1")){
            t.setText("Apakah Material tersedia?");
        }else{
            t.setText("Apakah Sudah Selesai Instalasi atau ada Kendala?");
        }

        // set dialog message
        String x = null;
        String z = null;
        if(order.trim().equals("4")){
             x = "OK";
             z = "Cancel";
        }else if(order.trim().equals("1")){
             x = "Ya";
            z = "Tidak";
        }else{
             x = "Selesai";
            z = "Cancel";
        }

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(x,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //executeMultipartPost();
                                if(order.trim().equals("4")){
                                   // new up().execute();
                                   // new submit().execute();
                                }else{
                                   // new submit().execute();
                                }

                            }
                        })
                .setNegativeButton(z,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //dialog.cancel();
                                if(order.trim().equals("1")){
                                    order = "7";
                                    //new submit().execute();
                                }else{
                                    dialog.cancel();
                                }
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    LinearLayout log;
    TextView nama_log;
    TextView nama_pekerjaan_log;
    TextView keterangan_kerja;
    ImageView image_laporan;

    AlertDialog alertDialog_task;
    String s_id_ring = "1";
    RadioButton other;
    RadioButton ring_1,ring_2;
    EditText e_other;
    public void prompt_task(){
        LayoutInflater li = LayoutInflater.from(progress_order.this);
        final View promptsView = li.inflate(R.layout.task, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(progress_order.this);
        nama_task           = (EditText)promptsView.findViewById(R.id.nama_task);
        e_other           = (EditText)promptsView.findViewById(R.id.other);
        deskripsion_task    = (EditText)promptsView.findViewById(R.id.deskripsi_task);
        simpan              = (Button) promptsView.findViewById(R.id.simpan);
        other              = (RadioButton) promptsView.findViewById(R.id.ring_other);

        ring_1 = (RadioButton) promptsView.findViewById(R.id.ring_1);
        ring_2 = (RadioButton) promptsView.findViewById(R.id.ring_2);

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(progress_order.this,"ring 3",Toast.LENGTH_LONG).show();
                ring_1.setChecked(false);
                ring_2.setChecked(false);
                e_other.setVisibility(View.VISIBLE);
                s_id_ring = "3";
            }
        });

        ring_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(progress_order.this,"ring 3",Toast.LENGTH_LONG).show();
                other.setChecked(false);
                ring_2.setChecked(false);
                ring_1.setChecked(true);
                e_other.setVisibility(View.GONE);
                s_id_ring = "1";
            }
        });

        ring_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(progress_order.this,"ring 2",Toast.LENGTH_LONG).show();
                other.setChecked(false);
                ring_1.setChecked(false);
                ring_2.setChecked(true);
                e_other.setVisibility(View.GONE);
                s_id_ring = "2";
            }
        });

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // create alert dialog
        alertDialog_task = alertDialogBuilder.create();

        // show it
        alertDialog_task.show();

    }

    public void submit(){
        if(s_task_id.equals("0")){
            Toast.makeText(progress_order.this,"Pilih Task terlebih dahulu, jika task belum ada klik tombol 'Create New Task'",Toast.LENGTH_LONG).show();
        }else if(is_capture){
            promptImage();
        }else{
            Toast.makeText(progress_order.this,"Ambil Foto Terlebih Dahulu",Toast.LENGTH_LONG).show();
        }
    }


    public void prompt3(){
        LayoutInflater li = LayoutInflater.from(progress_order.this);
        View promptsView = li.inflate(R.layout.prompt_view, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(progress_order.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        nama_log = (TextView)promptsView.findViewById(R.id.nama);
        nama_pekerjaan_log = (TextView)promptsView.findViewById(R.id.nama_pekerjaan);
        keterangan_kerja = (TextView)promptsView.findViewById(R.id.keterangan);
        image_laporan = (ImageView)promptsView.findViewById(R.id.image_laporan);
        log = (LinearLayout)promptsView.findViewById(R.id.log);
        
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    Uri targetUri = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                //img.setImageURI(Uri.parse(imageFilePath));

                Bitmap myBitmap = BitmapFactory.decodeFile(imageFilePath);
                img.setImageBitmap(myBitmap);
                img.setRotation(90);
                img.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 1600, 1200, false));
                is_capture = true;
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
                is_capture = false;
            }
        }else{
            is_capture = false;
        }

       /* Bitmap bm;

        try {
            bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            img.setImageBitmap(bm);
            showExif(Uri.parse(imageFilePath));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

       /* if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.v("uri",String.valueOf(fileUri));
                previewCapturedImage();

            }
        }*/
                /*
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                //======================================================
                final BitmapFactory.Options options = new BitmapFactory.Options();
                // Calculate inSampleSize
                options.inSampleSize = 2;

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                Bitmap newBitmap = Bitmap.createScaledBitmap(photo, 800, 800,
                        true);
                File file = new File(this.getFilesDir(), "Image"
                        + new Random().nextInt() + ".jpeg");
                try{
                    FileOutputStream out = this.openFileOutput(file.getName(),
                            Context.MODE_WORLD_READABLE);
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                }catch (Exception e){

                }


                img.setImageBitmap(newBitmap);
            }
        }*/


        /*if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //try{
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    //Uri uri = bitmapToUriConverter(photo) ;6
                //Bitmap newBitmap = Bitmap.createScaledBitmap(photo, 800, 800,true);
                //Uri tempUri = getImageUri(getApplicationContext(), newBitmap);
                    //realPath = getRealPathFromURI(tempUri);
                    //File myFile = new File(uri.getPath()); //bikin rusak
                    //realPath = myFile.getAbsolutePath();
                    //realPath = uri.getPath();
                    //realPath = getRealPathFromURI(uri.get);
                    //Log.v("dataaaa","zzz"+fileUri.getPath());
                    img.setImageBitmap(photo);
                    new up().execute();

                    *//*if (resultCode == Activity.RESULT_OK) {

                        Uri dataUri = data.getData();

                        targetUri = dataUri;

                    }*//*
                *//*}catch (Exception e){

                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    img.setImageBitmap(imageBitmap);

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    *//**//*Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);
                    fileUri = tempUri;

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    Log.v("error_foto",getRealPathFromURI(tempUri));
                    realPath = getRealPathFromURI(tempUri);*//**//*
                    //Toast.makeText(CameraAppActivity.this,"Here "+ getRealPathFromURI(tempUri),
                }*//*


               // new up().execute();


               *//* Bitmap bm;
                try {
                    bm = BitmapFactory.decodeStream(
                            getContentResolver()
                                    .openInputStream(targetUri));
                    img.setImageBitmap(bm);
                    showExif(targetUri);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*//*
            }
        }*/
    };

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    String realPath = null;
    private void previewCapturedImage() {
        try {
            // hide video preview
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 2;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            img.setImageBitmap(bitmap);
            Path = String.valueOf(fileUri.getPath());
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.v("erorpreview","_test"+String.valueOf(e));
        }
    }

    String s_latitude;
    String s_longitude;
    public void showCurrentLocation(){
        //if (gps != null) {
            s_latitude  = String.valueOf(gps.getLatitude());
            s_longitude = String.valueOf(gps.getLongitude());
            Log.v("latitde1",s_latitude+"xxx"+String.valueOf(gps.getLatitude()));
            //Toast.makeText(getApplicationContext(),s_latitude,Toast.LENGTH_LONG).show();
        //}else{
            ll = new MyLocation();
            lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            try{
                Log.v("oke","s");
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, ll);
            }catch (SecurityException e){
                showCurrentLocation2();
                Toast.makeText(progress_order.this,"searching GPS please wait",Toast.LENGTH_LONG).show();
            }
       // }
    }

    public void showCurrentLocation2(){
        //if (gps != null) {
        s_latitude  = String.valueOf(gps.getLatitude());
        s_longitude = String.valueOf(gps.getLongitude());
        Log.v("latitde1",s_latitude+"xxx"+String.valueOf(gps.getLatitude()));
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        //Toast.makeText(getApplicationContext(),s_latitude,Toast.LENGTH_LONG).show();
        //}else{
        /*if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ll = new MyLocation2();
                lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                try{
                    Log.v("oke","s");
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, ll);
                }catch (SecurityException e){
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //showCurrentLocation();
                    }
                    //Toast.makeText(progress_order.this,"searching GPS please wait",Toast.LENGTH_LONG).show();
                }

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }*/

        /*if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_STORAGE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }*/
    }

    public class MyLocation implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if(location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                s_latitude 	= String.valueOf(latitude);
                s_longitude = String.valueOf(longitude);
                Log.v("latitde2",s_latitude);
            }else{
                s_latitude 	= String.valueOf("-6.209274");
                s_longitude = String.valueOf("106.738272");
                Log.v("latitdepalsu",s_latitude);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }

    public class MyLocation2 implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if(location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                s_latitude 	= String.valueOf(latitude);
                s_longitude = String.valueOf(longitude);
                Log.v("latitde2",s_latitude);
            }else{
                s_latitude 	= String.valueOf("-6.209274");
                s_longitude = String.valueOf("106.738272");
                Log.v("latitdepalsu",s_latitude);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }

//    private String getRealPathFromURI(Uri contentURI) {
//        String filePath;
//        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
//        if (cursor == null) {
//            filePath = contentURI.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            filePath = cursor.getString(idx);
//            cursor.close();
//        }
//        return filePath;
//    }

    String urlServer = api.url+"image_upload.php";


    private void executeMultipartPost(){
        // TODO Auto-generated method stub
        Log.v("masuk","tes");
        //Log.v("url","urlFoto"+fileUri.getPath());
        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bm;
            //bm = BitmapFactory.decodeFile(path1.getText().toString(), options);

            int heightRatio = (int) Math
                    .ceil(options.outHeight / (float) h);
            int widthRatio = (int) Math.ceil(options.outWidth / (float) w);

            if (heightRatio > 1 || widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    options.inSampleSize = heightRatio;
                } else {
                    options.inSampleSize = widthRatio;
                }
            }

            options.inJustDecodeBounds = false;

            String nama_file_ = null;
            String dir = null;

                Log.v("namapaht2","path: "+realPath.split("/")[realPath.split("/").length-1]);
                nama_file_ = realPath.split("/")[realPath.split("/").length-1];
                nama_foto = realPath.split("/")[realPath.split("/").length-1];
                dir = realPath;

            bm = BitmapFactory.decodeFile(dir, options);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(urlServer);
            ByteArrayBody bab = new ByteArrayBody(data,  nama_file_);
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("uploaded", bab);
            reqEntity.addPart("photoCaption", new StringBody("sfsdfsdf"));
            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out
                    .println("responseD:覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧・> "
                            + s);
            //new input_insert().execute();


        }catch (NetworkOnMainThreadException e){
            Log.v("response2","Response2 :"+e);
        }catch(Exception e){
            Log.v("keluar","tes"+String.valueOf(e));
            Log.v("responseD","Response :"+e);
        }
    }
    Bitmap bm_create;

    private void executeMultipartPostBitmap(){
        // TODO Auto-generated method stub
        try {
            String nama_file_ = null;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            nama_file_ = timeStamp+".jpg";
            nama_foto = nama_file_;
            Log.v("nama_bit",timeStamp);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bm_create.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data = bos.toByteArray();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(urlServer);
            ByteArrayBody bab = new ByteArrayBody(data,  nama_file_);
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("uploaded", bab);
            reqEntity.addPart("photoCaption", new StringBody("sfsdfsdf"));
            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out
                    .println("responseD:覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧覧・> "
                            + s);
            //new input_insert().execute();

            uploaded = true;
        }catch (NetworkOnMainThreadException e){
            Log.v("response2","Response2 :"+e);
            uploaded = false;
        }catch(Exception e){
            uploaded = false;
            Log.v("keluar","tes"+String.valueOf(e));
            Log.v("responseD","Response :"+e);
        }
    }

    public Uri bitmapToUriConverter(Bitmap mBitmap){
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = 10;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 800, 800,
                    true);
            File file = new File(this.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = this.openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    boolean uploaded = false;
    class up extends AsyncTask<String, String,String > {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            try{
                 bm_create = loadBitmapFromView(ln);
                pg = new ProgressDialog(progress_order.this);
                pg.setMessage("Please wait...");
                pg.setIndeterminate(false);
                pg.setCancelable(false);
                pg.show();
            }catch(Exception e){}
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            //super.onPreExecute();
            try {
                executeMultipartPostBitmap();
            }catch (Exception e){
                Log.v("ikierrror","err"+e);
                // TODO Auto-generated catch block
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result2) {
            // TODO Auto-generated method stub

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                   // new submit().execute();
                    //Toast.makeText(progress_order.this,"Image Uploaded",Toast.LENGTH_LONG).show();
                    pg.dismiss();
                    if(uploaded){
                        new putProgress().execute();
                        img.setImageDrawable(ctx.getDrawable(R.drawable.camera));
                        img.setRotation(0);

                    }else{
                        Toast.makeText(progress_order.this,"Upload gagal check koneksi anda terlebih dahulu",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }


    GPSTracker2 gps2;
    public void getgps(){
        gps2 = new GPSTracker2(progress_order.this);

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

    String status_put_task = null;
    String message_put_task = null;
    private class putTask extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            ahas.clear();
            try{
                JSONParser jsParser  = new JSONParser();
                String s_nama = URLEncoder.encode(nama_task.getText().toString(),"utf-8");
                String s_deskripsi = URLEncoder.encode(deskripsion_task.getText().toString(),"utf-8");
                String s_other = URLEncoder.encode(e_other.getText().toString(),"utf-8");
                Log.v("url",api.url+"put_task.php?task="+s_nama+"&deskripsi="+s_deskripsi+"&id="+id_creator+"&type="+s_type+"&other="+s_other+"&id_ring="+s_id_ring);
                JSONObject object = jsParser.AmbilJson(api.url+"put_task.php?task="+s_nama+"&deskripsi="+s_deskripsi+"&id="+id_creator+"&type="+s_type+"&other="+s_other+"&id_ring="+s_id_ring);
                String status = "";
                status = object.getString("status");
                status_put_task = status;
                message_put_task = object.getString("message");
            }catch (Exception e){
                Log.v("tidak","tidak");
                Log.v("urlx",api.url+url_list+"?id_status="+order);
                message = String.valueOf(e);
            }
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(progress_order.this);
            pg.setMessage("Please Wait...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            pg.dismiss();
            try{
                if(status_put_task.equals("T")){
                    Toast.makeText(ctx,message_put_task,Toast.LENGTH_LONG).show();
                    alertDialog_task.dismiss();
                    new getTask().execute();
                }else{
                    Toast.makeText(ctx,message_put_task,Toast.LENGTH_LONG).show();

                }
            }catch (Exception e){

            }
        }
    }

    private class putProgress extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            ahas.clear();
            String S_latitude = null;
            String S_longitude = null;
            String S_nama_foto = null;
            String S_task_id = null;
            String S_address = null;
            Log.v("put_progress","test");
            try {
                 S_latitude = URLEncoder.encode(s_latitude,"utf-8");
                 S_longitude = URLEncoder.encode(s_longitude,"utf-8");
                 S_nama_foto = URLEncoder.encode(nama_foto,"utf-8");
                 S_task_id = URLEncoder.encode(s_task_id,"utf-8");
                 S_address = URLEncoder.encode(s_address,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try{
                JSONParser jsParser  = new JSONParser();

                Log.v("test",api.url+"put_progress.php?task_id="+S_task_id+
                        "&url_name="+S_nama_foto+
                        "&latitude="+S_latitude+
                        "&longitude="+S_longitude+
                        "&address="+S_address+
                        "&type="+s_type+
                        "&creator="+id_creator);
                JSONObject object = jsParser.AmbilJson(api.url+"put_progress.php?task_id="+S_task_id+
                        "&url_name="+S_nama_foto+
                        "&latitude="+S_latitude+
                        "&longitude="+S_longitude+
                        "&S_address="+S_address+
                        "&type="+s_type+
                        "&creator="+id_creator
                );
                String status = "";
                status = object.getString("status");
                status_put_task = status;
                message_put_task = object.getString("message");
            }catch (Exception e){
                Log.v("tidak","tidak");
                Log.v("urlx",api.url+"put_progress.php?task_id="+S_task_id+
                        "&url_name="+S_nama_foto+
                        "&latitude="+S_latitude+
                        "&longitude="+S_longitude+
                        "&creator="+id_creator);
                message = String.valueOf(e);
            }
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(progress_order.this);
            pg.setMessage("Please Wait...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            pg.dismiss();
            try{
                alertDialog_task.dismiss();
                if(status_put_task.equals("T")){
                    Toast.makeText(ctx,message_put_task,Toast.LENGTH_LONG).show();

                    new getTask().execute();
                }else{
                    Toast.makeText(ctx,message_put_task,Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){

            }

        }

    }

    public void simpanTask(View v){
        if(nama_task.getText().toString().equals("")){
            Toast.makeText(ctx, R.string.alert_nama_kosong, Toast.LENGTH_SHORT).show();
        }else if(deskripsion_task.getText().toString().equals("")){
            Toast.makeText(ctx, R.string.alert_deskripsi_kosong, Toast.LENGTH_SHORT).show();
        }else if(s_id_ring.equals("3") && e_other.getText().toString().length() < 1){
            Toast.makeText(ctx, R.string.alert_ring_kosong, Toast.LENGTH_SHORT).show();
        }else{
            new putTask().execute();
        }
    }

    public void takeFoto(View v){
        openCameraIntent();
        /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);*/

        /*Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);*/
    }

    void showExif(Uri photoUri){

        if(photoUri != null){

            String photoPath = getRealPathFromURI(photoUri);

            try {
                /*
                ExifInterface (String filename) added in API level 5
                 */
                ExifInterface exifInterface = new ExifInterface(photoPath);

                String exif="Exif: ";
                exif += "\nIMAGE_LENGTH: " +
                        exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                exif += "\nIMAGE_WIDTH: " +
                        exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                exif += "\n DATETIME: " +
                        exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                exif += "\n TAG_MAKE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_MAKE);
                exif += "\n TAG_MODEL: " +
                        exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                exif += "\n TAG_ORIENTATION: " +
                        exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                exif += "\n TAG_WHITE_BALANCE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
                exif += "\n TAG_FOCAL_LENGTH: " +
                        exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                exif += "\n TAG_FLASH: " +
                        exifInterface.getAttribute(ExifInterface.TAG_FLASH);
                exif += "\nGPS related:";
                exif += "\n TAG_GPS_DATESTAMP: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
                exif += "\n TAG_GPS_TIMESTAMP: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
                exif += "\n TAG_GPS_LATITUDE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                exif += "\n TAG_GPS_LATITUDE_REF: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                exif += "\n TAG_GPS_LONGITUDE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                exif += "\n TAG_GPS_LONGITUDE_REF: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                exif += "\n TAG_GPS_PROCESSING_METHOD: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);

                Toast.makeText(getApplicationContext(),
                        exif,
                        Toast.LENGTH_LONG).show();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),
                    "photoUri == null",
                    Toast.LENGTH_LONG).show();
        }
    };

    public Bitmap loadBitmapFromView(View v) {
         Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    static int TAKE_PIC =1;
    Uri outPutfileUri;
    /*public void camera2(){

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        		File file = new File(Environment.getExternalStorageDirectory(),
        				"MyPhoto.jpg");
        				outPutfileUri = Uri.fromFile(file);
        				intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
        				startActivityForResult(intent, TAKE_PIC);
    }*/

    /*private void captureImageNew() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            Toast.makeText(this, "error good", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }
    }*/

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", photoFile);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE);
        }
    }

    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        Log.v("foto",imageFilePath);
        return image;
    }

    public void saveBtm(){
        /*String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/vendor");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        Log.i("", "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public void loadMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(s_latitude), Double.parseDouble(s_longitude));
        mMap.addMarker(new MarkerOptions().position(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        googleMap.getUiSettings().setScrollGesturesEnabled(false);

        String koordinat = String.valueOf(s_latitude+","+s_longitude);
        valueAddress valAddress = new valueAddress();
        // Start downloading the geocoding places
        valAddress.execute(koordinat);

        mapCapt();
    }

    public void mapCapt(){
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            public void onMapLoaded() {
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    public void onSnapshotReady(Bitmap bitmap) {
                        // Write image to disk
                        Log.v("insert_",Environment.getExternalStorageDirectory().getAbsolutePath()+"vendor/capture_map.jpg");
                        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"vendor");
                        if (!dir.exists())
                            dir.mkdirs();
                        File file = new File(dir, "capture_map.jpg");

                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.v("errorMap",String.valueOf(e));
                        }
                        try{
                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        }catch (Exception e){
                            Toast.makeText(progress_order.this,"Error 303",Toast.LENGTH_LONG).show();
                        }

                        //img.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/vendor/capture_map.jpg"));
                        //load_map.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/vendor/capture_map.jpg"));

                    }
                });
            }
        });
    }

    List<Address> addresses = null;
    String s_kecamatan;
    private class valueAddress extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            View v = null;
            /*g_address.setText("Loading...");
            g_address2.setText("Loading...");*/

        }

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... p) {

            String l = p[0];

            String[] latlong = l.split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);



            Geocoder geocoder;
                geocoder = new Geocoder(progress_order.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        s_address = addresses.get(0).getAddressLine(0);
                        s_kecamatan = addresses.get(0).getLocality();
                        Log.v("address",s_address);
                        Log.v("address","kondisi");
                        Log.v("address",String.valueOf(addresses));

                        s_latitude = String.valueOf(Double.parseDouble(latlong[0]));
                        s_longitude = String.valueOf(Double.parseDouble(latlong[1]));
                    } else {
                        Log.v("address","tidak ada");
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    //SS_address = "1";
                    Log.v("address","exception: "+String.valueOf(e));
                }

            // Here 1 represent max location result to returned, by documents
            // it recommended 1 to 5
            return null;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            if(s_latitude.equals("0.0")){
                doneTask();
            }else{
                address.setText(s_address);

                t_latitude = (TextView)findViewById(R.id.d_latitude);
                t_longitude = (TextView)findViewById(R.id.d_longitude);

                t_latitude.setText(" "+s_latitude);
                t_longitude.setText(" "+s_longitude);
            }

        }
    }

    public void doneTask(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent in = new Intent(progress_order.this,progress_order.class);
                in.putExtra("program",s_type);
                startActivity(in);

                Toast.makeText(ctx,"yes",Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setMessage("Koordinat tidak di temukan klik ya untuk mendapatkan lokasi GPS");
        builder.setTitle("Peringatan!");
        builder.show();

    }
}

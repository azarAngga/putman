package approval.com.approval_sdi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import reportku.com.id.R;


public class view_progress extends AppCompatActivity {

    TextView nama_log,nama_pekerjaan_log,keterangan_kerja;
    ImageView image_laporan;
    LinearLayout log;
    static File file_foto;
    String message = null;
    ProgressDialog pg;
    api api = new api();
    JSONArray jsArray;
    String s_date_parse;
    String s_role = "2";
    //modelData model;

    public static final 	String KEY_NAMA 	= "nama";
    public static final 	String KEY_USERNAME = "username";
    public static final 	String KEY_PASSWORD = "password";
    public static final 	String KEY_ROLE = "role";
    private static final 	String PREFER_NAME 	= "AndroidExamplePref";

    SharedPreferences sharedPreferences;
    UserSessionManager session;

    ArrayList<HashMap<String,String>> ahas2 = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_view);

        Intent in = getIntent();
        //model = new modelData();

        sharedPreferences = getSharedPreferences(PREFER_NAME,Context.MODE_PRIVATE);
        session 	= new UserSessionManager(getApplicationContext());

        s_date_parse    = in.getStringExtra("date");
        id_user         =  sharedPreferences.getString(KEY_USERNAME, "");
        s_role         =  sharedPreferences.getString(KEY_ROLE, "");
        Log.v("id_user",id_user+"d");
        Log.v("role",s_role+"d");

        nama_log            = (TextView)findViewById(R.id.nama);
        nama_pekerjaan_log  = (TextView)findViewById(R.id.nama_pekerjaan);
        keterangan_kerja    = (TextView)findViewById(R.id.keterangan);
        image_laporan       =  (ImageView)findViewById(R.id.image_laporan);
        log = (LinearLayout)findViewById(R.id.log);
        new get_log().execute();
    }

    String id_user;
    private class get_log extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            String date_parse = null;
            message = null;
            try {
                date_parse = URLEncoder.encode(s_date_parse,"utf-8");
            } catch (UnsupportedEncodingException e) {

            }catch (Exception e){}

            try{
                ahas2.clear();

                JSONParser jsParser  = new JSONParser();
                JSONObject object = null;
                object = jsParser.AmbilJson(api.url+"get_progress.php?id="+id_user+"&date="+date_parse);
                Log.v("xxx",api.url+"get_progress.php?id="+id_user+"&date="+date_parse);
                jsArray = object.getJSONArray("data");
                for(int in = 0;in<jsArray.length();in++){
                    JSONObject ob = jsArray.getJSONObject(in);
                    HashMap<String,String> hmString = new HashMap<String,String>();
                    String id = ob.getString("id");
                    String task = ob.getString("task");
                    String  deskripsi  = ob.getString("deskripsi");
                    Log.v("deskrispi",deskripsi);
                    String url_name = ob.getString("url_name");
                    String created_dtm = ob.getString("created_dtm");
                    String latitude = ob.getString("latitude");
                    String longitude = ob.getString("longitude");
                    String type = ob.getString("type");

                    hmString.put("id",id);
                    hmString.put("task",task);
                    hmString.put("deskripsi",deskripsi);
                    hmString.put("url_name",url_name);
                    hmString.put("created_dtm",created_dtm);
                    hmString.put("latitude",latitude);
                    hmString.put("longitude",longitude);
                    hmString.put("type",type);
                    ahas2.add(hmString);
                }
            }catch (Exception e){
                Log.v("tidak","tidak");
                message = String.valueOf(e);
            }
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(view_progress.this);
            pg.setMessage("Please Wait...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if(message != null ){
                pg.dismiss();
                Log.v("err",message);
            }else{

                try {
                    parsingDataLog();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void parsingDataLog() throws URISyntaxException {

        String id  = null;
        String task             = null;
        String deskripsi           = null;
        String url_name          = null;
        String created_dtm          = null;
        String latitude          = null;
        String longitude          = null;
        String type          = null;

        Log.v("ggggg","gggggggggg");
        for(HashMap<String,String> map :ahas2){
            for(Map.Entry<String,String> entryMap : map.entrySet()){
                String key = entryMap.getKey();
                String value = entryMap.getValue();

                if(key.equals("id")){
                    id = value;
                }

                if(key.equals("task")){
                    task  = value;
                }

                if(key.equals("deskripsi")){
                    deskripsi  = value;
                }

                if(key.equals("url_name")){
                    url_name  = value;
                }

                if(key.equals("created_dtm")){
                    created_dtm  = value;
                }

                if(key.equals("latitude")){
                    latitude  = value;
                }

                if(key.equals("longitude")){
                    longitude  = value;
                }

                if(key.equals("type")){
                    type  = value;
                }
            }

            LayoutInflater ln = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = ln.inflate(R.layout.row_log,null);
            final TextView txt_task = (TextView)v.findViewById(R.id.txt_task);
            TextView txt_koordinat = (TextView)v.findViewById(R.id.txt_koordinat);
            TextView txt_date = (TextView)v.findViewById(R.id.txt_tanggal);
            final TextView txt_deskripsi = (TextView)v.findViewById(R.id.txt_deskripsi);
            Button btn_update = (Button)v.findViewById(R.id.button_update);
            Button btn_view_detil = (Button)v.findViewById(R.id.button_view_detil);
            final TextView txt_id = (TextView)v.findViewById(R.id.txt_id);
            final TextView header = (TextView)v.findViewById(R.id.header);

            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prompt_task(txt_task.getText().toString(),txt_id.getText().toString(),txt_deskripsi.getText().toString());
                }
            });

            btn_view_detil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(view_progress.this,detil_image_laporan.class);
                    in.putExtra("task_id",txt_id.getText().toString());
                    in.putExtra("creator_id",id_user);
                    in.putExtra("type",header.getText().toString());
                    startActivity(in);
                }
            });

            header.setText(type);
            if(s_role.equals("1")){
                btn_update.setVisibility(View.GONE);
            }

            if(type.equals("PREVENTIVE")){
                header.setBackgroundColor(getResources().getColor(R.color.blue));
            }else{
                header.setBackgroundColor(getResources().getColor(R.color.green));
            }


            ImageView image_laporan = (ImageView)v.findViewById(R.id.image_laporan);
            Picasso.with(view_progress.this).load(url_name).into(image_laporan);

            //txt_type.setText(task);
            txt_koordinat.setText(latitude+","+longitude);
            txt_date.setText(created_dtm);
            nama_log.setText(task);
            txt_task.setText(task);
            txt_id.setText(id);
            nama_pekerjaan_log.setText(deskripsi);
            txt_deskripsi.setText(deskripsi);
            log.addView(v);
            pg.dismiss();
        }
    }

    AlertDialog alertDialog_task;
    EditText  deskripsion_task;
    String s_id_update = null;
    public void prompt_task(String task,String id_task,String deskripsi){
        LayoutInflater li = LayoutInflater.from(view_progress.this);
        View promptsView = li.inflate(R.layout.task, null);
//
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view_progress.this);
        EditText nama_task           = (EditText)promptsView.findViewById(R.id.nama_task);
        deskripsion_task    = (EditText)promptsView.findViewById(R.id.deskripsi_task);
        EditText txt_id_task          = (EditText)promptsView.findViewById(R.id.id_task);
        Button simpan               = (Button)promptsView.findViewById(R.id.simpan);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new update_task().execute();
            }
        });

        txt_id_task.setText(id_task);
        nama_task.setText(task);
        s_id_update = id_task;
        deskripsion_task.setText(deskripsi);

//        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
//
//        // create alert dialog
        alertDialog_task = alertDialogBuilder.create();
//
//        // show it
        alertDialog_task.show();

    }

    String s_status;
    private class update_task extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            String deskripsi_parse = null;
            try {
                deskripsi_parse = URLEncoder.encode(deskripsion_task.getText().toString(),"utf-8");
            } catch (UnsupportedEncodingException e) {

            }catch (Exception e){

            }

            try{
                JSONParser jsParser  = new JSONParser();
                JSONObject object = null;
                object = jsParser.AmbilJson(api.url+"update_task.php?deskripsi="+deskripsi_parse+"&id="+s_id_update);
                Log.v("xxx",api.url+"update_task.php?deskripsi="+deskripsi_parse+"&id="+s_id_update);
                message = object.getString("message");
                s_status = object.getString("status");
            }catch (Exception e){
                Log.v("tidak","tidak");
                message = String.valueOf(e);
            }
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(view_progress.this);
            pg.setMessage("Please Wait...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            Toast.makeText(view_progress.this,message,Toast.LENGTH_LONG).show();
            pg.dismiss();

            if(s_status.equals("T")){
                alertDialog_task.dismiss();

                log.removeAllViews();
                new get_log().execute();
            }


        }
    }

}

package approval.com.approval_sdi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.IdRes;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reportku.com.id.R;

public class detil_view_laporan extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
//public class slide  {

    private ViewPager viewPager;
    private RadioGroup group;
    ArrayList<HashMap<String,String>> ahas = new ArrayList<HashMap<String,String>>();
    api api = new api();
    ProgressDialog pg;
    String message = null;
    Context ctx;
    Button submit,excel;
    String s_id ;
    String s_submit ;
    String s_is_done;
    String s_type;
    String s_date_parse;
    ArrayList<String> ar = new ArrayList<>();
    ImageSliderAdapter adapter;

    TextView t_task_name,t_category,t_location,t_date,t_deskripsi;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detil_view_laporan);

        ctx  = this;
        context = this;

        viewPager   = (ViewPager)findViewById(R.id.image_slider);
        group       = (RadioGroup)findViewById(R.id.slider_indicator_group);

        Intent in = getIntent();

        s_id        = in.getStringExtra("id");
        s_submit    = in.getStringExtra("submit"); // untuk user
        s_is_done   = in.getStringExtra("is_done");
        s_type      = in.getStringExtra("type"); // untuk user
        s_date      = in.getStringExtra("date");


        try{
            if(s_date.equals("")){
                s_date_parse  = "";
            }else{
                s_date_parse  = s_date;
            }
        }catch (Exception e){
            s_date_parse  = "";
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        t_task_name      = (TextView) findViewById(R.id.task);
        t_category       = (TextView) findViewById(R.id.category);
        t_location       = (TextView) findViewById(R.id.location);
        t_deskripsi      = (TextView) findViewById(R.id.deskripsi);
        t_date           = (TextView) findViewById(R.id.tanggal);
        t_category       = (TextView) findViewById(R.id.category);
        submit           = (Button) findViewById(R.id.submit);
        excel            = (Button) findViewById(R.id.excel);
        img_category     = (ImageView)findViewById(R.id.img_category);

        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new generateExcel().execute();
            }
        });


        if(s_is_done.equals("1")){
                submit.setVisibility(View.GONE);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneTask();
            }
        });

        group.setOnCheckedChangeListener(this);
        viewPager.addOnPageChangeListener(this);

        adapter = new ImageSliderAdapter(getSupportFragmentManager());
        new getData().execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if(s_date_parse.equals("")){
//            Intent in = new Intent(detil_view_laporan.this,fragmen_history.class);
//            in.putExtra("submit",s_submit);
//            in.putExtra("is_done",s_is_done);
//            in.putExtra("type",s_type);
//            startActivity(in);
//
//        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // when current page change -> update radio button state
        int radioButtonId = group.getChildAt(position).getId();
        group.check(radioButtonId);
        t_date.setText(ar.get(position));
        Log.v("urutan",String.valueOf(position));
    }


    String status_put_task = null;
    String s_message = null;
    JSONArray jsArray;
    JSONArray ar_date;
    ImageView img_category;
    String s_status = "";
    String s_task_name,s_date,s_deskripsi,s_location,s_category;
    private class getData extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            ahas.clear();
            String S_id       = null;
            String S_user_id  = null;
            String S_date  = null;
            JSONParser jsParser  = new JSONParser();
            try{

                 S_id       = URLEncoder.encode(s_id,"utf-8");
                 S_date       = URLEncoder.encode(s_date,"utf-8");

            }catch (Exception e){
                Log.v("tidak","tidak");
                Log.v("urlx",api.url+"get_detil_report.php?id="+S_id+"&S_user_id="+S_user_id);
                message = String.valueOf(e);
                Log.v("urlx",String.valueOf(message));
                S_date = "";

            }


            JSONObject object = jsParser.AmbilJson(api.url+"get_detil_report.php?id="+S_id+"&user_id="+S_user_id+"&date="+S_date);
            Log.v("url",api.url+"get_detil_report.php?id="+S_id+"&user_id="+S_user_id+"&date="+S_date);


            try {
                s_status    = object.getString("status");
                jsArray     = object.getJSONArray("url");
                s_task_name = object.getString("task_name");
                s_deskripsi = object.getString("deskripsi");
                s_location  = object.getString("location");
                s_category  = object.getString("category");
                ar_date     = object.getJSONArray("date");
            } catch (JSONException e) {
                Log.v("tdk","blm ada kegiatan");
                s_message = "belum ada kegiatan";
            }


            //Log.v("array2",String.valueOf(object));
            Log.v("array",String.valueOf(ar_date));
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(ctx);
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

                if(s_status.equals("T")){
                    if(s_category.equals("preventive")){
                        img_category.setImageDrawable(ctx.getDrawable(R.drawable.ic_mantenance_new));
                    }else{
                        img_category.setImageDrawable(ctx.getDrawable(R.drawable.ic_mantenance_2new));
                    }
                    JSONArray for_img = jsArray
                            ,for_radio = jsArray;

                    final RadioButton[] rb = new RadioButton[for_radio.length()];
                    for(int i=0; i<(for_img.length()); i++){

                        rb[i]  = new RadioButton(ctx);
                        if(i == 0){
                            rb[i].setChecked(true);
                        }
                        rb[i].setText(" ");
                        rb[i].setId(i + 100);
                        group.addView(rb[i]);
                    }


                    Log.v("url length",String.valueOf(for_img.length()));
                    for(int in = 0;in<for_img.length();in++){
                        JSONObject ob = for_radio.getJSONObject(in);
                        ar.add(ar_date.getString(in));
                        Log.v("url img",String.valueOf(ob.getString("url")));
                        Log.v("nilai in",String.valueOf(in));
                        adapter.addFragment(ImageSliderFragment.newInstance(ob.getString("url")));

                        //adapter.addFragment(ImageSliderFragment.newInstance(ob.getString("url")));
                    }

                    t_date.setText(ar.get(0));
                    viewPager.setAdapter(adapter);
                    t_task_name.setText(s_task_name);
                    t_deskripsi.setText(s_deskripsi);
                    t_location.setText(s_location);
                    t_category.setText(s_category);


                }else{
                    Toast.makeText(ctx,s_message,Toast.LENGTH_LONG).show();
                    finish();
                }
            }catch (Exception e){

            }
        }
    }

    String s_url;
    private class generateExcel extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            ahas.clear();
            String S_id    = null;
            String S_date  = null;

            try{

                S_id       = URLEncoder.encode(s_id,"utf-8");
                S_date       = URLEncoder.encode(s_date,"utf-8");

            }catch (Exception e){

                S_date = "";

            }

            try{
                JSONParser jsParser  = new JSONParser();

                JSONObject object = jsParser.AmbilJson(api.url+"excel/Examples/simple.php?id="+S_id+"&date="+S_date);
                Log.v("url",api.url+"excel/Examples/simple.php?id="+S_id+"&date="+S_date);

                s_url    = object.getString("url");
                Log.v("urlz",s_url);

            }catch (Exception e){

            }
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(ctx);
            pg.setMessage("Please Wait...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            pg.dismiss();

                final DownloadTask downloadTask = new DownloadTask(detil_view_laporan.this);
                downloadTask.execute(s_url);
                String[] g = s_url.split("/");
                Log.v("slash",String.valueOf(g.length));
                Log.v("slash",String.valueOf(g[8]));
        }
    }

    public void doneTask(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setMessage("Apakah anda yakin menyelesaikan task ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               new put_update().execute();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setTitle("Peringatan!");
        builder.show();

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        // when checked radio button -> update current page
        viewPager.setCurrentItem(group.indexOfChild(group.findViewById(checkedId)), true);
    }

    String message_put_task;
    private class put_update extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {

            try{
                JSONParser jsParser  = new JSONParser();
                JSONObject object = jsParser.AmbilJson(api.url+"update_done_task.php?id="+s_id);
                String status = "";
                status = object.getString("status");
                status_put_task = status;
                message_put_task = object.getString("message");
            }catch (Exception e){
                Log.v("tidak","tidak");
                message = String.valueOf(e);
            }
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(detil_view_laporan.this);
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
                    startActivity(new Intent(detil_view_laporan.this,fragmen_history.class));
                   finish();
                }else{
                    Toast.makeText(ctx,message_put_task,Toast.LENGTH_LONG).show();

                }
            }catch (Exception e){

            }
        }
    }


    ProgressDialog mProgressDialog;
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String[] g = s_url.split("/");
            Log.v("slash",String.valueOf(g.length));
            Log.v("slash",String.valueOf(g[8]));

            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();

                output = new FileOutputStream("/sdcard/reportku/"+g[8]);
                //output = new FileOutputStream("/sdcard/reportku/hana.apk");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                Log.v("error","error2");
                return e.toString();

            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    Log.v("error","error");
                }

                if (connection != null)
                    connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,getClass().getName());
            mWakeLock.acquire();

            mProgressDialog = new ProgressDialog(detil_view_laporan.this);
            mProgressDialog.setMessage("Mohon Tunggu");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }



        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download Error" + result, Toast.LENGTH_LONG).show();
            else{
                String[] g = s_url.split("/");
                Log.v("slash",String.valueOf(g.length));
                Log.v("slash",String.valueOf(g[8]));

                Toast.makeText(context, "Berhasil Terdownload", Toast.LENGTH_SHORT).show();

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    File f = new File("/mnt/sdcard/"+g[8]);

                    Uri uri = null;


                    // So you have to use Provider
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(detil_view_laporan.this,getApplicationContext().getPackageName() + ".provider", f);

                        // Add in case of if We get Uri from fileProvider.
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }else{
                        uri = Uri.fromFile(f);
                    }

                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                    startActivity(intent);
                } catch(Exception e) {
                }


            }

        }
    }

}

package approval.com.approval_sdi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

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
    Button submit;
    String s_id ;
    ImageSliderAdapter adapter;

    TextView t_task_name,t_category,t_location,t_date,t_deskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detil_view_laporan);

        viewPager   = (ViewPager)findViewById(R.id.image_slider);
        group       = (RadioGroup)findViewById(R.id.slider_indicator_group);

        Intent in = getIntent();

        s_id        = in.getStringExtra("id");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        t_task_name      = (TextView) findViewById(R.id.task);
        t_category       = (TextView) findViewById(R.id.category);
        t_location       = (TextView) findViewById(R.id.location);
        t_deskripsi      = (TextView) findViewById(R.id.deskripsi);
        t_date           = (TextView) findViewById(R.id.tanggal);
        t_category       = (TextView) findViewById(R.id.category);
        submit           = (Button) findViewById(R.id.submit);
        img_category     = (ImageView)findViewById(R.id.img_category);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneTask();
            }
        });

        ctx  = this;

        group.setOnCheckedChangeListener(this);
        viewPager.addOnPageChangeListener(this);

        adapter = new ImageSliderAdapter(getSupportFragmentManager());

        new getData().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(detil_view_laporan.this,fragmen_history.class));
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
        Log.v("urutan",String.valueOf(position));
    }


    String status_put_task = null;
    String s_message = null;
    JSONArray jsArray;
    ImageView img_category;
    String s_status = "";
    String s_task_name,s_date,s_deskripsi,s_location,s_category;
    private class getData extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            ahas.clear();
            String S_id       = null;
            String S_user_id  = null;
            try{
                JSONParser jsParser  = new JSONParser();
                 S_id       = URLEncoder.encode(s_id,"utf-8");

                JSONObject object = jsParser.AmbilJson(api.url+"get_detil_report.php?id="+S_id+"&user_id="+S_user_id);
                Log.v("url",api.url+"get_detil_report.php?id="+S_id+"&user_id="+S_user_id);

                s_status    = object.getString("status");
                jsArray     = object.getJSONArray("url");
                s_task_name = object.getString("task_name");
                s_deskripsi = object.getString("deskripsi");
                s_location  = object.getString("location");
                s_date      = object.getString("date");
                s_category  = object.getString("category");
                jsArray     = object.getJSONArray("url");

                Log.v("test",s_task_name);
                Log.v("length",String.valueOf(jsArray.length()));

            }catch (Exception e){
                Log.v("tidak","tidak");
                Log.v("urlx",api.url+"get_detil_report.php?id="+S_id+"&S_user_id="+S_user_id);
                message = String.valueOf(e);
                Log.v("urlx",String.valueOf(message));
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

                        Log.v("url img",String.valueOf(ob.getString("url")));
                        Log.v("nilai in",String.valueOf(in));
                        adapter.addFragment(ImageSliderFragment.newInstance(ob.getString("url")));

                        //adapter.addFragment(ImageSliderFragment.newInstance(ob.getString("url")));
                    }

                    viewPager.setAdapter(adapter);
                    t_task_name.setText(s_task_name);
                    t_deskripsi.setText(s_deskripsi);
                    t_location.setText(s_location);
                    t_category.setText(s_category);
                    t_date.setText(s_date);

                }else{
                    Toast.makeText(ctx,s_message,Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){

            }
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


}

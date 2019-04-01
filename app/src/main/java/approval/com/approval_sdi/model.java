package approval.com.approval_sdi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import reportku.com.id.R;

public class model {

    api api = new api();
    Context ctx;
    JSONObject object;
    String url;
    ProgressDialog pg;
    String s_user;
    MyCallback listener;

    public static final 	String KEY_NAMA 	= "nama";
    public static final 	String KEY_USERNAME = "username";
    public static final 	String KEY_PASSWORD = "password";
    public static final 	String KEY_ROLE = "role";
    private static final 	String PREFER_NAME 	= "AndroidExamplePref";

    SharedPreferences sharedPreferences;

    public model(Context context){
        try{
            sharedPreferences = context.getSharedPreferences(PREFER_NAME,Context.MODE_PRIVATE);
            s_user = sharedPreferences.getString(KEY_USERNAME,"0");
        }catch (Exception e){

        }

        this.ctx = context;
    }

    public void toast(Context ctx,String s){
        Toast.makeText(ctx,s,Toast.LENGTH_LONG).show();
    }

    public void get(String url,MyCallback listener){
        this.listener = listener;
        this.url = url;
        new get().execute();
    }


    private class get extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {

            try{
                JSONParser jsParser  = new JSONParser();
                object = jsParser.AmbilJson(api.url+url);
                Log.v("urlnya",api.url+url);
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
            listener.callbackCall(object);
        }
    }


    //*
    // class create task
    // *


   LinearLayout ln_copy;
    public void loadElementCreateCopyTask(View v){
        ln_copy         = (LinearLayout)v.findViewById(R.id.row_ln);

    }

    //*
    // class create task
    // *


    String s_role_id;
    String s_date_po;
    String s_location = "1";
    String s_category = "1";
    EditText e_nama_rute;
    EditText e_nomor_po;
    EditText e_deskripsi;
    EditText e_ring_other;

    RadioButton rb_ring_1;
    RadioButton rb_ring_2;
    RadioButton rb_ring_other;

    RadioGroup rg_category;

    Button simpan;


    DatePicker date_po;
    Calendar calendar;

    LinearLayout ln_task;
    public void settingAwalCreateUser(){
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
    }

    public void ClearElementCreateTask(){
        e_nama_rute.setText("");
        e_nomor_po.setText("");
        e_deskripsi.setText("");
        e_ring_other.setText("");
    }

    public void loadElementCreateTask(View v){

        date_po         = (DatePicker)v.findViewById(R.id.date_po);

        e_nama_rute     = (EditText)v.findViewById(R.id.nama_task);
        e_nomor_po      = (EditText)v.findViewById(R.id.nomor_po);
        e_deskripsi     = (EditText)v.findViewById(R.id.deskripsi_task);
        e_ring_other    = (EditText)v.findViewById(R.id.other);

        rb_ring_1       = (RadioButton)v.findViewById(R.id.ring_1);
        rb_ring_2       = (RadioButton)v.findViewById(R.id.ring_2);
        rb_ring_other   = (RadioButton)v.findViewById(R.id.ring_other);

        rg_category     = (RadioGroup)v.findViewById(R.id.rg_category);
        simpan          = (Button)v.findViewById(R.id.simpan);
        ln_task         = (LinearLayout)v.findViewById(R.id.ln_task);
    }

    //*
    // class create user
    // *
    ArrayList<String> ar_id = new ArrayList<>();
    ArrayList<String> ar_nama = new ArrayList<>();

    Button submit;
    Spinner sp_role;
    EditText e_nama,e_username,e_password,e_re_password;
    LinearLayout ln_user;

    public void loadElementCreateUser(View v){
        sp_role  	    = (Spinner)v.findViewById(R.id.sp_role);
        submit 		    = (Button)v.findViewById(R.id.btn_submit);
        e_username 	    = (EditText)v.findViewById(R.id.e_username);
        e_password	    = (EditText)v.findViewById(R.id.e_password);
        e_nama  	    = (EditText)v.findViewById(R.id.e_nama);
        e_re_password  	= (EditText)v.findViewById(R.id.e_re_password);
        ln_user   	    = (LinearLayout)v.findViewById(R.id.ln_user);
    }

    public void ClearElementCreateUser(){
        e_username.setText("");
        e_password.setText("");
        e_username.setText("");
        e_re_password.setText("");
        e_nama.setText("");
    }

    //*
    // class create mapping
    // *

    ArrayList<String> ar_id_rute    = new ArrayList<>();
    ArrayList<String> ar_nama_rute  = new ArrayList<>();
    ArrayList<String> ar_id_user    = new ArrayList<>();
    ArrayList<String> ar_nama_user  = new ArrayList<>();

    Spinner sp_rute,sp_user;
    String s_id_rute,s_id_user;
    LinearLayout ln_mapping;


    public void loadElementCreateMapping(View v){
        sp_user 		    = (Spinner)v.findViewById(R.id.sp_user);
        sp_rute 		    = (Spinner)v.findViewById(R.id.sp_rute);
        submit  		    = (Button)v.findViewById(R.id.btn_submit);

        rg_category  		= (RadioGroup)v.findViewById(R.id.rg_category);

        ln_mapping          = (LinearLayout)v.findViewById(R.id.ln_mapping);

    }

    //*
    // class create report by date
    // *

    Button btn_generate;
    EditText e_date_awal,e_date_akhir,e_url;
    LinearLayout ln_url,row_ln;
    String s_url_excel;
    String s_nama_file;


    public void loadElementCreateReportByDate(View v){
        e_date_awal 		    = (EditText) v.findViewById(R.id.e_date_awal);
        e_date_akhir 		    = (EditText) v.findViewById(R.id.e_date_akhir);
        e_url        		    = (EditText) v.findViewById(R.id.e_url);
        btn_generate 		    = (Button) v.findViewById(R.id.btn_generate);

        ln_url                  = (LinearLayout)v.findViewById(R.id.ln_url);
        row_ln                  = (LinearLayout)v.findViewById(R.id.row_ln);
    }

    DatePicker dtp_call;
    String s_call;
    Button btn_pasangkan;
    
    public void showPromptDate(Context ctx, final EditText e){
        LayoutInflater li = LayoutInflater.from(ctx);
        View promptsView = li.inflate(R.layout.prompt_date, null);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        dtp_call        = (DatePicker)promptsView.findViewById(R.id.dtp_call);
        btn_pasangkan   = (Button)promptsView.findViewById(R.id.btn_pasangkan);
        s_call = String.valueOf(this.calendar.get(Calendar.DAY_OF_MONTH) +"/"+ (this.calendar.get(Calendar.MONTH)+1)+"/"+this.calendar.get(Calendar.YEAR));
        e.setText(s_call);
        btn_pasangkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        dtp_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.setText(s_call);
                alertDialog.dismiss();
            }
        });

        this.dtp_call.init(this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH), this.calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                s_call =  dayOfMonth+"/"+(month + 1)+"/"+year;
                e.setText(s_call);
            }
        });

        alertDialog.show();

    }


    //*
    // class frame management
    // *

    Button btn_create_user,btn_create_rute,copy_rute,btn_report;
    public void loadElementFrameManagement(View v){
        btn_create_user 		= (Button) v.findViewById(R.id.create_user);
        copy_rute 	            = (Button) v.findViewById(R.id.copy_rute);
        btn_create_rute 		= (Button) v.findViewById(R.id.create_rute);
        btn_report   		    = (Button) v.findViewById(R.id.report);

    }

    public void showPromptUpdate(final Context ctx, String id, String nama, final String type){
        LayoutInflater li = LayoutInflater.from(ctx);
        View promptsView = li.inflate(R.layout.prompt_update_nama, null);


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setView(promptsView);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText e_nama        = (EditText) promptsView.findViewById(R.id.nama);
        final EditText e_id        = (EditText) promptsView.findViewById(R.id.id);

        e_nama.setText(nama);
        e_id.setText(id);

        Button update   = (Button)promptsView.findViewById(R.id.update_nama);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_nama(e_nama.getText().toString(),e_id.getText().toString(),type,ctx);
            }
        });


        alertDialog.show();

    }


    public void update_nama(String nama, String id, final String type, final Context ctx){
        String S_nama =null;
        String S_id = null;
        String S_type = null;

        try{
            S_nama = URLEncoder.encode(nama,"UTF-8");
            S_id = URLEncoder.encode(id,"UTF-8");
            S_type = URLEncoder.encode(type,"UTF-8");

        }catch (Exception e){

        }

        String S_url        = "update_nama.php?type="+S_type+"&id="+S_id+"&nama="+S_nama;
        get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    if(obj.getString("status").equals("T")){
                            if(type.equals("user")){
                                ctx.startActivity(new Intent(ctx,create_user.class));
                            }else{
                                ctx.startActivity(new Intent(ctx,create_task.class));
                            }

                            toast(ctx,String.valueOf(obj.getString("message")));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}

interface MyCallback{
    void callbackCall(JSONObject obj);

}
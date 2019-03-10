package approval.com.approval_sdi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.util.Calendar;

import reportku.com.id.R;

public class create_task extends AppCompatActivity implements OnClickListener, DatePicker.OnDateChangedListener, RadioGroup.OnCheckedChangeListener {

    Context ctx = create_task.this;
    model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater li = LayoutInflater.from(ctx);
        View view = li.inflate(R.layout.create_task, null);
        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = new model(ctx);
        model.loadElementCreateTask(view);
        model.settingAwalCreateUser();

        model.simpan.setOnClickListener(this);
        model.rb_ring_1.setOnClickListener(this);
        model.rb_ring_2.setOnClickListener(this);
        model.rb_ring_other.setOnClickListener(this);

        //model.toast(ctx,model.s_user);
        model.rg_category.setOnCheckedChangeListener(this);
        model.s_date_po = String.valueOf(model.calendar.get(Calendar.YEAR)+"/"+(model.calendar.get(Calendar.MONTH)+1)+"/"+model.calendar.get(Calendar.DAY_OF_MONTH));
        Log.v("dates", model.s_date_po);
        model.date_po.init(model.calendar.get(Calendar.YEAR), model.calendar.get(Calendar.MONTH), model.calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                model.s_date_po = model.s_date_po = dayOfMonth+"/"+(month + 1)+"/"+year;
                Log.v("date",model.s_date_po);
            }
        });
        get_task();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.simpan){
            if(model.e_nama_rute.equals("")){
                model.toast(ctx,"Nama Rute Tidak Boleh Kosong");
            }else if(model.e_nomor_po.equals("")){
                model.toast(ctx,"Nomor PO Tidak Boleh Kosong");
            }else if(model.e_deskripsi.equals("")){
                model.toast(ctx,"Deskripsi Tidak Boleh Kosong");
            }else{put_user();}
        }else if(v.getId() == R.id.ring_1){
            model.rb_ring_2.setChecked(false);
            model.rb_ring_other.setChecked(false);
            model.s_location = "1";
            model.e_ring_other.setVisibility(View.GONE);

        }else if(v.getId() == R.id.ring_2){

            model.rb_ring_1.setChecked(false);
            model.rb_ring_other.setChecked(false);
            model.s_location = "2";
            model.e_ring_other.setVisibility(View.GONE);

        }else if(v.getId() == R.id.ring_other){

            model.rb_ring_1.setChecked(false);
            model.rb_ring_2.setChecked(false);
            model.s_location = "3";
            model.e_ring_other.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        model.s_date_po = dayOfMonth+"/"+(monthOfYear + 1)+"/"+year;

        Log.v("date2", model.s_date_po);
    }

    public void put_user(){
        String S_nama_rute      = null;
        String S_nomor_po       = null;
        String S_date_po        = null;
        String S_deskripsi      = null;
        String S_category       = null;
        String S_other_ring     = null;
        String S_creator        = null;
        String S_ring           = null;
        String S_url            = "put_task.php";

        try {
            S_nama_rute  = URLEncoder.encode(model.e_nama_rute.getText().toString(),"utf-8");
            S_nomor_po   = URLEncoder.encode(model.e_nomor_po.getText().toString(),"utf-8");
            S_date_po    = URLEncoder.encode(model.s_date_po,"utf-8");
            S_ring       = URLEncoder.encode(model.s_location,"utf-8");
            S_deskripsi  = URLEncoder.encode(model.e_deskripsi.getText().toString(),"utf-8");
            S_category   = URLEncoder.encode(model.s_category,"utf-8");
            S_creator    = URLEncoder.encode(model.s_user,"utf-8");
            S_other_ring = URLEncoder.encode(model.e_ring_other.getText().toString(),"utf-8");

            S_url += "?nama_rute="+S_nama_rute+
                    "&nomor_po="+S_nomor_po+
                    "&date="+S_date_po+
                    "&creator="+S_creator+
                    "&id_ring="+S_ring+
                    "&type="+S_category+
                    "&other="+S_other_ring+
                    "&deskripsi="+S_deskripsi;
            Log.v("url_put",S_url);
            model.get(S_url, new MyCallback() {
                @Override
                public void callbackCall(JSONObject obj) {
                    try {
                        Toast.makeText(ctx,obj.getString("message"),Toast.LENGTH_LONG).show();
                        if(obj.getString("status").equals("T")){
                            model.ClearElementCreateTask();
                            startActivity(new Intent(ctx,create_task.class));
                            finish();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                    }
                }
            });

        }catch (Exception e){
            Toast.makeText(ctx,"Error pada encript data:"+String.valueOf(e),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId){
            case R.id.rb_corrective:
                model.s_category = "2";
                break;
            case R.id.rb_preventive:
                model.s_category = "1";
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void  popupDelete(String nama, final String id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setMessage("apakah anda yakin akan Delete Rute "+nama+" ini?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                put_delete(id);
            }
        });
        alertDialogBuilder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialogBuilder.show();
    }

    public void put_delete(String id){
        String S_url        = "delete_task.php?id_task="+id;
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    if(obj.getString("status").equals("T")){
                        model.toast(ctx,obj.getString("message"));
                        startActivity(new Intent(ctx,create_task.class));
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void get_task(){
        String S_url        = "get_task2.php";
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    if(obj.getString("status").equals("T")){
                        for(int in = 0;in < obj.getJSONArray("data").length();in++){
                            JSONObject o = obj.getJSONArray("data").getJSONObject(in);
                            LayoutInflater ln = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v = ln.inflate(R.layout.row_create_task,null);
                            final TextView txt_nama = (TextView)v.findViewById(R.id.nama_task);
                            final TextView txt_id_task = (TextView)v.findViewById(R.id.id_task);
                            ImageView delete = (ImageView)v.findViewById(R.id.delete_usr);
                            ImageView update_nama = (ImageView)v.findViewById(R.id.update);

                            update_nama.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    model.showPromptUpdate(ctx,txt_id_task.getText().toString(),txt_nama.getText().toString(),"task");
                                }
                            });


                            delete.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupDelete(txt_nama.getText().toString(),txt_id_task.getText().toString());
                                }
                            });
                            txt_nama.setText(o.getString("task"));
                            txt_id_task.setText(o.getString("id"));
                            model.ln_task.addView(v);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        //model.toast(ctx,"stop");
        finish();
        super.onStop();
    }


}


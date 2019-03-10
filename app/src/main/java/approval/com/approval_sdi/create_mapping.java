package approval.com.approval_sdi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import reportku.com.id.R;

public class create_mapping extends AppCompatActivity implements OnClickListener, RadioGroup.OnCheckedChangeListener {
    Context ctx = create_mapping.this;
    model model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater li = LayoutInflater.from(ctx);
        View view = li.inflate(R.layout.create_mapping, null);
        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = new model(ctx);

        model.loadElementCreateMapping(view);
        model.submit.setOnClickListener(this);
        model.rg_category.setOnCheckedChangeListener(this);
        get_task();

    }

    public void getListRuteAndUser(String category){
        model.ar_id_rute.clear();
        model.ar_nama_rute.clear();
        model.ar_id_user.clear();
        model.ar_nama_user.clear();

        model.ar_nama_rute.add("-Pilih Rute-");
        model.ar_id_rute.add("0");
        model.ar_nama_user.add("-Pilih Rute-");
        model.ar_id_user.add("0");

        String S_url        = "get_rute_and_user.php?category="+category;
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    if(obj.getString("status").equals("T")){
                        for(int in = 0;in < obj.getJSONArray("rute").length();in++){
                            String S_rute_name  = obj.getJSONArray("rute").getJSONObject(in).getString("nama_rute");
                            String S_rute_id    = obj.getJSONArray("rute").getJSONObject(in).getString("id_rute");
                            model.ar_nama_rute.add(S_rute_name);
                            model.ar_id_rute.add(S_rute_id);
                        }

                        for(int in = 0;in < obj.getJSONArray("user").length();in++){
                            String S_nama_user  = obj.getJSONArray("user").getJSONObject(in).getString("nama_user");
                            String S_id_user    = obj.getJSONArray("user").getJSONObject(in).getString("id_user");
                            model.ar_nama_user.add(S_nama_user);
                            model.ar_id_user.add(S_id_user);
                        }

                        insert_in_spinner();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void insert_in_spinner(){
        ArrayAdapter<String> rute = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,model.ar_nama_rute);
        ArrayAdapter<String> user = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,model.ar_nama_user);

        model.sp_rute.setAdapter(rute);
        model.sp_user.setAdapter(user);
        model.sp_rute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model.s_id_rute = model.ar_id_rute.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        model.sp_user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model.s_id_user = model.ar_id_user.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void put_mapping(){
        String S_id_rute   = null;
        String S_id_user   = null;
        String S_url        = "put_mapping.php";

        try {
            S_id_rute  = URLEncoder.encode(model.s_id_rute,"utf-8");
            S_id_user  = URLEncoder.encode(model.s_id_user,"utf-8");

            S_url += "?id_user="+S_id_user+"&id_rute="+S_id_rute;
            model.get(S_url, new MyCallback() {
                @Override
                public void callbackCall(JSONObject obj) {
                    try {
                        model.toast(ctx,obj.getString("message"));
                        if(obj.getString("status").equals("T")){
                            //model.ClearElementCreateUser();
                            startActivity(new Intent(ctx,create_mapping.class));
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
    public void onClick(View v) {
        if(model.s_id_rute.equals("0") && model.s_id_user.equals(0)){
            model.toast(ctx,"Pilih rute atau user terlebih dahulu");
        }else{
            put_mapping();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId){
            case R.id.rb_preventive:
                getListRuteAndUser("1");
                break;

            case R.id.rb_corrective:
                getListRuteAndUser("2");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    public void get_task(){
        String S_url        = "get_mapping.php";
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj){
                try {
                    if(obj.getString("status").equals("T")){
                        for(int in = 0;in < obj.getJSONArray("data").length();in++){
                            JSONObject o = obj.getJSONArray("data").getJSONObject(in);
                            LayoutInflater ln = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v = ln.inflate(R.layout.row_create_mapping,null);

                            final TextView txt_nama_task    = (TextView)v.findViewById(R.id.nama_task);
                            final TextView txt_id           = (TextView)v.findViewById(R.id.id);
                            final TextView txt_nama_user    = (TextView)v.findViewById(R.id.nama_user);

                            ImageView delete = (ImageView)v.findViewById(R.id.delete_usr);
                            delete.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupDelete(txt_nama_task.getText().toString(),txt_nama_user.getText().toString(),txt_id.getText().toString());
                                }
                            });

                            txt_nama_task.setText(o.getString("task"));
                            txt_id.setText(o.getString("id"));
                            txt_nama_user.setText(o.getString("nama"));

                            model.ln_mapping.addView(v);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
                getListRuteAndUser("1");
            }
        });
    }

    public void  popupDelete(String task,String nama, final String id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setMessage("apakah anda yakin akan Delete nama "+nama+" denga rute "+task+" ini?");
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
        String S_url        = "delete_mapping.php?id="+id;
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    if(obj.getString("status").equals("T")){
                        model.toast(ctx,obj.getString("message"));
                        startActivity(new Intent(ctx,create_mapping.class));
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
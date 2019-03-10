package approval.com.approval_sdi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import reportku.com.id.R;

public class create_user extends AppCompatActivity implements OnClickListener {

    Context ctx = create_user.this;
    model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater li = LayoutInflater.from(ctx);
        View view = li.inflate(R.layout.create_user, null);
        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = new model(ctx);
        model.loadElementCreateUser(view);
        model.submit.setOnClickListener(this);
        get_role();

    }

    public void put_user(){
        String S_username   = null;
        String S_password   = null;
        String S_nama       = null;
        String S_role_id    = null;
        String S_url        = "put_user.php";

        try {
            S_username  = URLEncoder.encode(model.e_username.getText().toString(),"utf-8");
            S_password  = URLEncoder.encode(model.e_password.getText().toString(),"utf-8");
            S_nama      = URLEncoder.encode(model.e_nama.getText().toString(),"utf-8");
            S_role_id   = URLEncoder.encode(model.s_role_id,"utf-8");

            S_url += "?username="+S_username+"&password="+S_password+"&nama="+S_nama+"&role_id="+S_role_id;
            model.get(S_url, new MyCallback() {
                @Override
                public void callbackCall(JSONObject obj) {
                    try {
                        Toast.makeText(ctx,obj.getString("message"),Toast.LENGTH_LONG).show();
                        if(obj.getString("status").equals("T")){
                            startActivity(new Intent(ctx,create_user.class));
                            finish();
                            model.ClearElementCreateUser();
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(ctx,"Error pada encript data:"+String.valueOf(e),Toast.LENGTH_LONG).show();
        }
    }

    public void get_role(){
        String S_url        = "get_role.php";
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    if(obj.getString("status").equals("T")){
                        for(int in = 0;in < obj.getJSONArray("data").length();in++){
                            String S_nama_role = obj.getJSONArray("data").getJSONObject(in).getString("role_name");
                            String S_id_role = obj.getJSONArray("data").getJSONObject(in).getString("role_id");
                            model.ar_nama.add(S_nama_role);
                            model.ar_id.add(S_id_role);
                            Log.v("role_name",String.valueOf(obj.getJSONArray("data").length()));
                        }
                        insert_role_in_spinner();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }

                //done
                get_user();
            }
        });
    }

    public void get_user(){
        String S_url        = "get_user.php";
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    if(obj.getString("status").equals("T")){
                        for(int in = 0;in < obj.getJSONArray("data").length();in++){
                            JSONObject o = obj.getJSONArray("data").getJSONObject(in);
                            LayoutInflater ln = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v = ln.inflate(R.layout.row_create_user,null);
                            final TextView txt_nama = (TextView)v.findViewById(R.id.nama_user);
                            final TextView txt_id_user = (TextView)v.findViewById(R.id.id_user);
                            ImageView delete = (ImageView)v.findViewById(R.id.delete_usr);
                            ImageView update_nama = (ImageView)v.findViewById(R.id.update);

                            update_nama.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    model.showPromptUpdate(ctx,txt_id_user.getText().toString(),txt_nama.getText().toString(),"user");
                                }
                            });

                            delete.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupDelete(txt_nama.getText().toString(),txt_id_user.getText().toString());
                                }
                            });



                            txt_nama.setText(o.getString("nama"));
                            txt_id_user.setText(o.getString("id_user"));
                            model.ln_user.addView(v);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void put_delete(String id){
        String S_url        = "delete_user.php?id_user="+id;
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    if(obj.getString("status").equals("T")){
                        model.toast(ctx,obj.getString("message"));
                        startActivity(new Intent(ctx,create_user.class));
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {

        if(model.e_nama.getText().toString().trim().equals("")){
            Toast.makeText(create_user.this,"Nama Kosong" , Toast.LENGTH_LONG).show();
        }else if(model.e_username.getText().toString().trim().equals("")){
            Toast.makeText(create_user.this,"Username Kosong" , Toast.LENGTH_LONG).show();
        }else if(model.e_password.getText().toString().trim().equals("")){
            Toast.makeText(create_user.this,"Password Kosong" , Toast.LENGTH_LONG).show();
        }else if(!model.e_re_password.getText().toString().equals(model.e_password.getText().toString())){
            Toast.makeText(create_user.this,"Password dan Re-password tidak sama" , Toast.LENGTH_LONG).show();
        }else{
            put_user();
        }
    }

    public void insert_role_in_spinner(){
        ArrayAdapter<String> aa = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,model.ar_nama);
        Log.v("test",model.ar_id.get(1));

        model.sp_role.setAdapter(aa);
        model.sp_role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model.s_role_id = model.ar_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void  popupDelete(String nama, final String id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setMessage("apakah anda yakin akan Delete User "+nama+" ini?");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        //model.toast(ctx,"stop");
        finish();
        super.onStop();
    }

    @Override
    protected void onPause() {
        //model.toast(ctx,"pause");

        super.onPause();
    }

}


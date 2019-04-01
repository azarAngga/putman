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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import reportku.com.id.R;

public class copy_rute extends AppCompatActivity {

    Context ctx = copy_rute.this;
    model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater li = LayoutInflater.from(ctx);
        View view = li.inflate(R.layout.copy_rute, null);
        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = new model(ctx);
        model.loadElementCreateCopyTask(view);
        get_prev();

    }


    public void get_prev(){
        String S_url        = "get_prev.php";
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    if(obj.getString("status").equals("T")){
                        for(int in = 0;in < obj.getJSONArray("data").length();in++){
                            JSONObject o = obj.getJSONArray("data").getJSONObject(in);
                            LayoutInflater ln = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v = ln.inflate(R.layout.row_copy_rute,null);
                            final TextView txt_nama = (TextView)v.findViewById(R.id.nama_task);
                            final TextView txt_id = (TextView)v.findViewById(R.id.id_task);
                            Button copy = (Button)v.findViewById(R.id.copy);

                            copy.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupCopy(txt_nama.getText().toString(),txt_id.getText().toString());
                                }
                            });

                            Log.v("test",o.getString("nama"));


                            txt_nama.setText(o.getString("nama"));
                            txt_id.setText(o.getString("id"));
                            model.ln_copy.addView(v);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void put_copy(String id,String id_user){
        String S_url        = "put_copy_corective.php?id="+id+"&id_user="+id_user;
        model.get(S_url, new MyCallback() {
            @Override
            public void callbackCall(JSONObject obj) {
                try {
                    model.toast(ctx,obj.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void  popupCopy(String nama, final String id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setMessage("apakah anda yakin akan Copy Task "+nama+" ini?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                put_copy(id,model.s_user);
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


package approval.com.approval_sdi.fragment;

/**
 * Created by azar on 12/12/2015.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import approval.com.approval_sdi.JSONParser;
import approval.com.approval_sdi.api;
import approval.com.approval_sdi.detil_view_laporan;
import reportku.com.id.R;

/**
 * Created by azar on 9/12/2015.
 */

@SuppressLint("ValidFragment")
public class corrective extends android.support.v4.app.Fragment {
    JSONArray jsArray = null;
    ImageView img_empty;
    approval.com.approval_sdi.api api = new api();
    String s_id_user ;
    String s_is_done = "0" ;
    private String url_list = api.url+"get_progress_user.php";
    LinearLayout listlinear;

    public static final 	String KEY_USERNAME = "username";
    private static final 	String PREFER_NAME 	= "AndroidExamplePref";

    SharedPreferences sharedPreferences;
    String s_;

    ArrayList<HashMap<String,String>> ahas = new ArrayList<HashMap<String, String>>();
    @SuppressLint("ValidFragment")
    public corrective(String s){
        // Required empty public constructor
        s_ = s;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list,container,false);
        listlinear = (LinearLayout)v.findViewById(R.id.list);
        img_empty = (ImageView)v.findViewById(R.id.empty);
        //Toast.makeText(getActivity(),s_,Toast.LENGTH_LONG).show();
        sharedPreferences = getActivity().getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        s_id_user = sharedPreferences.getString(KEY_USERNAME, "");
        Log.v("logUrl",s_id_user);
        new get_list().execute();
        return v;
    }

    ProgressDialog pg;
    private class get_list extends AsyncTask<Void,Void,String[]>{

        @Override
        protected String[] doInBackground(Void... voids) {
            JSONParser parser = new JSONParser();
            JSONObject object = null;

            JSONParser jsParser  = new JSONParser();
            object = jsParser.AmbilJson(url_list+"?id_user="+s_id_user+"&type=2&is_done="+s_is_done);
            Log.v("logUrl",url_list+"?id_user="+s_id_user+"&type=2&is_done="+s_is_done);
            try{
                jsArray = object.getJSONArray("data");
                Log.v("seta",String.valueOf(jsArray.length()));
                for(int in = 0;in<jsArray.length();in++){
                    JSONObject ob = jsArray.getJSONObject(in);
                    HashMap<String,String> hmString = new HashMap<String,String>();
                    String id_pemesanan = ob.getString("id");
                    String task = ob.getString("task");
                    String deskripsi = ob.getString("deskripsi");

                    hmString.put("id",id_pemesanan);
                    hmString.put("task",task);
                    hmString.put("deskripsi",deskripsi);

                    ahas.add(hmString);
                }


            }catch (Exception e){
            }
            return new String[0];
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(getActivity());
            pg.setMessage("Please Wait...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

            String id           = null;
            String task         = null;
            String deskripsi   = null;

            if(ahas.size() > 0 ){
                img_empty.setVisibility(View.GONE);
            }

            for(HashMap<String,String> map :ahas){
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


                }

                LayoutInflater ln = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = ln.inflate(R.layout.row_corrective,null);
                TextView txt_task            = (TextView)v.findViewById(R.id.task);
                TextView txt_deskirpsi       = (TextView)v.findViewById(R.id.description);
                final TextView txt_id_task         = (TextView)v.findViewById(R.id.id_task);

                txt_task.setText(task);
                txt_deskirpsi.setText(deskripsi);
                txt_id_task.setText(id);


                listlinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(getActivity(),detil_view_laporan.class);
                        in.putExtra("id",txt_id_task.getText().toString());
                        startActivity(in);
                        getActivity().finish();

                    }
                });

                listlinear.addView(v);

            }

            pg.dismiss();

        }
    }

}

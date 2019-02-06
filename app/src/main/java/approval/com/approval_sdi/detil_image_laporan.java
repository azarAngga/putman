package approval.com.approval_sdi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import reportku.com.id.R;

public class detil_image_laporan extends AppCompatActivity {

    ProgressDialog pg;
    String message = null;
    TextView task_header;
    String s_task_header = null;
    LinearLayout log;
    String task_id = "1";
    String s_creator = "1";
    String s_type = "1";
    api api = new api();
    JSONArray jsArray;
    ArrayList<HashMap<String,String>> ahas2 = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detil_image_laporan);
        Intent in = getIntent();
        task_id = in.getStringExtra("task_id");
        s_creator = in.getStringExtra("creator_id");
        s_type = in.getStringExtra("type");

        log = (LinearLayout)findViewById(R.id.log);
        task_header =(TextView)findViewById(R.id.task);
        new get_log().execute();

    }

    private class get_log extends AsyncTask<Void,Void,String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            try{
                JSONParser jsParser  = new JSONParser();
                JSONObject object = jsParser.AmbilJson(api.url+"get_detil_foto.php?id="+s_creator+"&task_id="+task_id+"&type="+s_type);
                Log.v("xxx",api.url+"get_detil_foto.php?id="+s_creator+"&task_id="+task_id+"&type="+s_type);
                jsArray = object.getJSONArray("data");
                for(int in = 0;in<jsArray.length();in++){
                    JSONObject ob = jsArray.getJSONObject(in);
                    HashMap<String,String> hmString = new HashMap<String,String>();
                    String url = ob.getString("url");
                    s_task_header = ob.getString("task");
                    hmString.put("url",url);
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
            pg = new ProgressDialog(detil_image_laporan.this);
            pg.setMessage("Please Wait...");
            pg.setCancelable(false);
            pg.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            pg.dismiss();
            task_header.setText(s_task_header);
            if(message != null ){

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
        String url  = null;

        Log.v("ggggg","gggggggggg");
        for(HashMap<String,String> map :ahas2){
            for(Map.Entry<String,String> entryMap : map.entrySet()){
                String key = entryMap.getKey();
                String value = entryMap.getValue();

                if(key.equals("url")){
                    url = value;
                }
            }

            LayoutInflater ln = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = ln.inflate(R.layout.row_image,null);
            ImageView image_laporan = (ImageView)v.findViewById(R.id.image_laporan);
            Picasso.with(detil_image_laporan.this).load(url).into(image_laporan);

            log.addView(v);
            pg.dismiss();
        }
    }
}

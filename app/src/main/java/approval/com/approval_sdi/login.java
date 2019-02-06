package approval.com.approval_sdi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import reportku.com.id.R;


public class login extends Activity{
    public  static final api urls = new api();
    Button login;
    EditText username,password;
    String status;
    String s_nama;

    String role_id;
    String s_status;
    Boolean isInternetPresent = false;
    String respon;
    UserSessionManager session;
    ProgressDialog dialog;

    public static final 	String KEY_NAMA 	= "nama";
    public static final 	String KEY_USERNAME = "username";
    public static final 	String KEY_PASSWORD = "password";
    public static final 	String KEY_ROLE = "role";
    private static final 	String PREFER_NAME 	= "AndroidExamplePref";

    int kendala = 0;
    SharedPreferences sharedPreferences;

    class get_info extends AsyncTask<String, String,String >{
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(login.this);
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params){
            // TODO Auto-generated method stub
            //super.onPreExecute();
            //if (isInternetPresent) {
                String url = urls.url+"login.php";
                try{
                    String Str_username	= URLEncoder.encode(params[0],"utf-8");
                    String Str_password	= URLEncoder.encode(params[1],"utf-8");
                    url +="?username="+Str_username+"&password="+Str_password;
                    getRequest(username,url);
                    Log.v("url login",url);
                }catch(UnsupportedEncodingException e){

                }
            return null;
        }

        public void getRequest(EditText txtResult, String SUrl){
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(SUrl);
            try{
                HttpResponse response = client.execute(request);
                respon = request(response);

                if(respon.indexOf("/") < 0){
                    String[] a = respon.split(",");
                    Log.w("status",respon);

                    s_status = a[0];
                    if(s_status.trim().equals("gagal")){
                        status = "7";
                    }else if(s_status.trim().equals("sukses")){
                        s_nama = a[2];
                        role_id = a[3];
                        Log.v("role_id","role"+role_id);
                        status = "8";
                    }else if(s_status.trim().equals("update_nik")){
                        s_nama = a[1];
                        status = "9";
                    }

                    //status = "4";

                }else{
                    status = "4";
                }
            }catch(Exception ex){
                status = "3";
                Log.v("test","te"+String.valueOf(ex));
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            dialog.dismiss();
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    if(status.equals("1")){
                        Toast.makeText(login.this, "Hidupkan Koneksi Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                    }else if(status.equals("2")){
                        Toast.makeText(login.this, "Tidak Bisa Masuk", Toast.LENGTH_SHORT).show();
                    }else if(status.equals("3")){
                        Toast.makeText(login.this, "Failed Connect to server!", Toast.LENGTH_LONG).show();
                    }else if(status.equals("4")){
                        Toast.makeText(login.this, respon, Toast.LENGTH_LONG).show();
                    }else if(status.equals("5")){
                        Toast.makeText(login.this, "User Tidak Ada", Toast.LENGTH_LONG).show();
                    }else if(status.equals("7")){
                        String[] a = respon.split(",");
                        Toast.makeText(login.this, a[1], Toast.LENGTH_SHORT).show();
                    }else if(status.equals("8")){
                        String[] a = respon.split(",");
                        session.createUserLoginSession(a[1],s_nama,password.getText().toString(),a[3].trim());
                        Log.v("texx",role_id);
                        Intent i = null;
                        i = new Intent(login.this, menu_role.class);
                        startActivity(i);
                        finish();
                        /*if(role_id.trim().equals("3")){
                            Intent i = null;
                            i = new Intent(login.this, menu_utama.class);
                            startActivity(i);
                        }else{
                            Intent i = null;
                            i = new Intent(login.this, report_progress.class);
                            startActivity(i);
                        }*/

                        /*Intent i = null;


                        if(a[3].equals("1")){
                            i = new Intent(login.this, report_progress.class);
                            Log.v("ini","ggg"+a[1]+":sss"+a[2]+":Dfdfdf"+a[3]);

                        }if(a[3].equals("2")){
                            i = new Intent(login.this, report_progress.class);
                        }else{
                            i = new Intent(login.this, progress_order.class);
                        }
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();*/
                    }else if(status.equals("9")){
                        Intent i = new Intent(login.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    Log.v("statusangka",status);
                }

            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        session = new UserSessionManager(getApplicationContext());
        sharedPreferences = getSharedPreferences(PREFER_NAME,Context.MODE_PRIVATE);
        String islogin = sharedPreferences.getString(KEY_USERNAME, "");
        if(session.checkLogin(login.this)){
            finish();
        }

        login 		= (Button)findViewById(R.id.btn_login);
        username 	= (EditText)findViewById(R.id.ed_username);
        password	= (EditText)findViewById(R.id.ed_password);

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(username.getText().toString().trim().equals("")){
                    Toast.makeText(login.this,"Username Kosong" , Toast.LENGTH_LONG).show();
                }else if(password.getText().toString().trim().equals("")){
                    Toast.makeText(login.this,"Password Kosong" , Toast.LENGTH_LONG).show();
                }else{
                    String a = username.getText().toString().trim();
                    String b = password.getText().toString().trim();
                    new get_info().execute(a,b);
                }

            }

        });

    }

    private String request(HttpResponse response) {
        // TODO Auto-generated method stub
        String result = "";

        try {
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        } catch (Exception ex) {
            result = "Error";
        }
        return result;
    }
}

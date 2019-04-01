package approval.com.approval_sdi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import reportku.com.id.R;

public class create_report_by_date extends AppCompatActivity implements View.OnClickListener{

    Context ctx = create_report_by_date.this;
    model model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater li = LayoutInflater.from(ctx);
        View view = li.inflate(R.layout.create_report_by_date, null);
        setContentView(view);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        int PERMISSION_ALL = 1;

        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        model = new model(ctx);
        model.loadElementCreateReportByDate(view);
        model.btn_generate.setOnClickListener(this);

        model.e_date_awal.setOnClickListener(this);
        model.e_date_akhir.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.e_date_awal:
                model.showPromptDate(ctx,model.e_date_awal);
                break;

            case R.id.e_date_akhir:
                model.showPromptDate(ctx,model.e_date_akhir);
                break;
            case R.id.btn_generate:
                generate();
                break;
        }
    }

    public void generate(){
        createDir();

        String S_date_awal      = null;
        String S_date_akhir     = null;
        String S_id_user        = null;
        String S_url            = "get_url.php";

        try {
            S_date_awal     = URLEncoder.encode(model.e_date_awal.getText().toString(),"utf-8");
            S_date_akhir    = URLEncoder.encode(model.e_date_akhir.getText().toString(),"utf-8");
            S_id_user       = URLEncoder.encode("","utf-8");

            S_url += "?date_awal="+S_date_awal+
                    "&date_akhir="+S_date_akhir+
                    "&id_user="+S_id_user;
            Log.v("url_put",S_url);
            model.get(S_url, new MyCallback() {
                @Override
                public void callbackCall(final JSONObject obj){
                    try {
                        //Toast.makeText(ctx,obj.getString("message"),Toast.LENGTH_LONG).show();
                        if(obj.getString("status").equals("T")){
                            //model.toast(ctx,String.valueOf(obj.getJSONArray("data_file").length()));
                            for(int in = 0;in < obj.getJSONArray("data_file").length();in++){
                                LayoutInflater ln = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View v = ln.inflate(R.layout.row_excel,null);
                                final TextView nama_file = (TextView)v.findViewById(R.id.nama_file);
                                final LinearLayout row = (LinearLayout)v.findViewById(R.id.row);
                                row.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            model.s_url_excel   = obj.getString("url")+"/"+nama_file.getText().toString();
                                            model.s_nama_file   = nama_file.getText().toString();
                                            doneTask(model.s_nama_file);
                                            //model.toast(ctx,model.s_url_excel);
                                            //final DownloadTask downloadTask = new DownloadTask(ctx);
                                            //downloadTask.execute(model.s_url_excel);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                                nama_file.setText(obj.getJSONArray("data_file").getString(in));
                                model.row_ln.addView(v);
                            }
                            //model.e_url.setText(obj.getString("url"));
                            //model.ln_url.setVisibility(View.VISIBLE);
                            //Log.v("data",String.valueOf(obj.getString("data_file")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //Toast.makeText(ctx,String.valueOf(e),Toast.LENGTH_LONG).show();
                    }
                }
            });

        }catch (Exception e){
            Toast.makeText(ctx,"Error pada encript data:"+String.valueOf(e),Toast.LENGTH_LONG).show();
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

                output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/reportku/"+model.s_nama_file);
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
                Log.v("error",String.valueOf(e));
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

            mProgressDialog = new ProgressDialog(ctx);
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
            if(category_file == 1){
                if (result != null)
                    Toast.makeText(context, "Download Error" + result, Toast.LENGTH_LONG).show();
                else{
                    Toast.makeText(context, "Berhasil Terdownload", Toast.LENGTH_SHORT).show();

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //File f = new File("/mnt/sdcard/reportku/"+model.s_nama_file);
                        File f = new File(Environment.getExternalStorageDirectory()+"/reportku/"+model.s_nama_file);

                        Uri uri = null;


                        // So you have to use Provider
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(ctx,getApplicationContext().getPackageName() + ".provider", f);

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
            }else{
                try{
                    share(model.s_nama_file);
                }catch (Exception e){
                    model.toast(ctx,"error:"+String.valueOf(e));
                    Log.v("error",String.valueOf(e));
                }

            }
        }
    }

    int category_file = 0;
    public void doneTask(final String nama_file){
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);

        builder.setTitle("Action");
        builder.setMessage("Pilih Action Dibawah ini");
        builder.setPositiveButton("Buka File", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                category_file = 1;
                //model.toast(ctx,model.s_url_excel);
                final DownloadTask downloadTask = new DownloadTask(ctx);
                downloadTask.execute(model.s_url_excel);

            }
        });
        builder.setNegativeButton("Share File", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                category_file = 2;
                //model.toast(ctx,model.s_url_excel);
                final DownloadTask downloadTask = new DownloadTask(ctx);
                downloadTask.execute(model.s_url_excel);
            }
        });
        builder.show();

    }
    public static final int REQUEST_PERMISSION = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void createDir() {
        // create a File object for the parent directory
        String dir = Environment.getExternalStorageDirectory()+"/reportku/";
        //model.toast(ctx,dir);
        File wallpaperDirectory = new File(dir);
        // have the object build the directory structure, if needed.
        wallpaperDirectory.mkdirs();
    }


//    private void share2(String nama) {
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("application/x-excel");
//        String imagePath = Environment.getExternalStorageDirectory()
//                + "/reportku/"+nama_file;
//        intent.putExtra(Intent.EXTRA_STREAM, );
//        startActivity(Intent.createChooser(intent, "Send "));
//    }

    private void share(String nama_file) {
        Intent share = new Intent(Intent.ACTION_SEND);

        share.setType("application/x-excel");

        String imagePath = Environment.getExternalStorageDirectory()
                + "/reportku/"+nama_file;

        Log.v("list",imagePath);

        //File imageFileToShare = new File(imagePath);
        //Uri uri = Uri.fromFile(imageFileToShare);

        File f = new File(Environment.getExternalStorageDirectory()+"/reportku/"+nama_file);


        Uri imageUri = FileProvider.getUriForFile(
                ctx,
                "reportku.com.id", f);

        share.putExtra(Intent.EXTRA_STREAM, imageUri);

        startActivity(Intent.createChooser(share, "Share laporan Excel"));
    }

}

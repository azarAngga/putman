package approval.com.approval_sdi;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import reportku.com.id.R;

import static android.support.v4.content.FileProvider.getUriForFile;
import static java.security.AccessController.getContext;

public class menu_utama extends AppCompatActivity implements View.OnClickListener {
    ImageView preventive,corrective;
    Context ctx = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_utama);
        //getSupportActionBar().setIcon(R.drawable.calendar_next_arrow);
        ctx = this;

        preventive = (ImageView) findViewById(R.id.preventive);
        corrective = (ImageView)findViewById(R.id.ccorrective);

        preventive.setOnClickListener(this);
        corrective.setOnClickListener(this);

//        try {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //File f = new File("/mnt/sdcard/test.pdf");
//            File f = new File("/mnt/sdcard/34_2_task_testing_1.xls");
//
//            Uri uri = null;
//
//
//            // So you have to use Provider
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                uri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() + ".provider", f);
//
//                // Add in case of if We get Uri from fileProvider.
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            }else{
//                uri = Uri.fromFile(f);
//            }
//
//            intent.setDataAndType(uri, "application/vnd.ms-excel");
//            startActivity(intent);
//        } catch(Exception e) {
//            //e.printstacktrace();
//        }



        }



    @Override
    public void onClick(View v) {

        Intent in  = null;
        in = new Intent(ctx,progress_order.class);
        if(v.getId() == R.id.preventive){
            in.putExtra("program","1");// presentive
        }else{
            in.putExtra("program","2");// corective
        }
        startActivity(in);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(menu_utama.this,menu_role.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}

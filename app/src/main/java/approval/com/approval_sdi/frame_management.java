package approval.com.approval_sdi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;
import reportku.com.id.R;

public class frame_management extends AppCompatActivity implements View.OnClickListener{

    Context ctx = frame_management.this;
    model model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater li = LayoutInflater.from(ctx);
        View view = li.inflate(R.layout.frame_management, null);
        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = new model(ctx);
        model.loadElementFrameManagement(view);

        model.btn_create_rute.setOnClickListener(this);
        model.btn_create_user.setOnClickListener(this);
        model.btn_mapping_rute_user.setOnClickListener(this);
        model.btn_report.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent in  = null;
        switch (v.getId()){
            case R.id.create_user:
                in = new Intent(ctx,create_user.class);
                break;
            case R.id.create_rute:
                in = new Intent(ctx,create_task.class);
                break;
            case R.id.create_mapping:
                in = new Intent(ctx,create_mapping.class);
                break;
            case R.id.report:
                in = new Intent(ctx,create_report_by_date.class);
                break;
        }
        startActivity(in);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}

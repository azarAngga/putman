package approval.com.approval_sdi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import reportku.com.id.R;

public class menu_utama extends AppCompatActivity implements View.OnClickListener{
    ImageView preventive,corrective;
    Context ctx = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_utama);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ctx = this;

        preventive = (ImageView) findViewById(R.id.preventive);
        corrective = (ImageView)findViewById(R.id.ccorrective);

        preventive.setOnClickListener(this);
        corrective.setOnClickListener(this);
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

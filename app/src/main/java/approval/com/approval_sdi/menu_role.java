package approval.com.approval_sdi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import reportku.com.id.R;

public class menu_role extends AppCompatActivity {

    public static final 	String KEY_NAMA 	= "nama";
    public static final 	String KEY_USERNAME = "username";
    public static final 	String KEY_PASSWORD = "password";
    public static final 	String KEY_ROLE = "role";
    private static final 	String PREFER_NAME 	= "AndroidExamplePref";

    int kendala = 0;
    SharedPreferences sharedPreferences;
    UserSessionManager session;
    String role;
    Button new_task,view_task,logout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_role);
        session = new UserSessionManager(getApplicationContext());
        sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        role = sharedPreferences.getString(KEY_ROLE, "");

        ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
        ColorDrawable grey = new ColorDrawable(getResources().getColor(R.color.grey));

        new_task    = (Button)findViewById(R.id.new_task);
        view_task   = (Button)findViewById(R.id.view_task);
        logout      = (Button)findViewById(R.id.logout);

        if(role.trim().equals("3")){
            new_task.setBackground(blue);
            view_task.setBackground(grey);

            view_task.setEnabled(false);
            //Toast.makeText(getApplicationContext(),"login",Toast.LENGTH_LONG).show();
        }else{
            new_task.setBackground(grey);
            view_task.setBackground(blue);

            new_task.setEnabled(false);
            //Toast.makeText(getApplicationContext(),"logout",Toast.LENGTH_LONG).show();
        }


        new_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = null;
                in = new Intent(menu_role.this,menu_utama.class);
                startActivity(in);
                finish();
            }
        });

        view_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = null;
                in = new Intent(menu_role.this,report_progress.class);
                startActivity(in);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
            }
        });
    }
}

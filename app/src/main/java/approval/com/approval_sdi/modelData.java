package approval.com.approval_sdi;

import android.util.Log;

public class modelData {
    String role = null;
    String id_user = null;

    public void setRole(String role){
        this.role = role;
    }

    public String getRole(){
        return this.role;
    }

    public void setIdUser(String id_user){
        this.id_user = id_user;
        Log.v("ini_model","model"+id_user);
    }

    public String getIdUser(){
        return this.id_user;
    }
}

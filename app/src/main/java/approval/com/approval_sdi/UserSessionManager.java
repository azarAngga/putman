package approval.com.approval_sdi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.HashMap;

public class UserSessionManager {
	
	// Shared Preferences reference
	SharedPreferences pref;
	
	// Editor reference for Shared preferences
	Editor editor;
	
	// Context
	Context _context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	boolean is_login = false;
	
	// Sharedpref file name
	private static final String PREFER_NAME = "AndroidExamplePref";
	
	// All Shared Preferences Keys
	private static final String IS_USER_LOGIN = "IsUserLoggedIn";
	
	
	// Email address (make variable public to access from outside)
	public static final String KEY_USERNAME = "username";
	public static final String KEY_NAMA = "nama";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_ROLE = "role";

	// Constructor
	public UserSessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	//Create login session
	public void createUserLoginSession(String username, String nama, String password,String role){
		// Storing login value as TRUE
		editor.putBoolean(IS_USER_LOGIN, true);
        // Storing name in pref
		editor.putString(KEY_NAMA, nama);
		// Storing email in pref
		editor.putString(KEY_USERNAME, username);
		editor.putString(KEY_PASSWORD, password);
		editor.putString(KEY_ROLE, role);

		// commit changes
		editor.commit();
	}	
	
	/**
	 * Check login method will check user login status
	 * If false it will redirect user to login page
	 * Else do anything
	 * */
	public boolean checkLogin(Context ctx){
		// Check login status
		if(this.isUserLoggedIn()){
			
			// user is not logged in redirect him to Login Activity
			Intent i = null;
			i = new Intent(_context, menu_role.class);
			// Closing all the Activities from stack
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			_context.startActivity(i);
			
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		
		//Use hashmap to store user credentials
		HashMap<String, String> user = new HashMap<String, String>();
		
		// user email id
		user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
		user.put(KEY_NAMA, pref.getString(KEY_NAMA, null));
		user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
		user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));

		// return user
		return user;
	}
	
	/**
	 * Clear session details
	 * */
	public void logoutUser(){
		
		// Clearing all user data from Shared Preferences
		editor.clear();
        editor.putBoolean(IS_USER_LOGIN, false);
        //Log.v("logsession",pref.getString(KEY_USERNAME, null));
		editor.commit();
		
		// After logout redirect user to Login Activity
		Intent i = new Intent(_context, login.class);
		
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);

	}
	
	
	// Check for login
	public boolean isUserLoggedIn(){
		return pref.getBoolean(IS_USER_LOGIN, false);
	}
}

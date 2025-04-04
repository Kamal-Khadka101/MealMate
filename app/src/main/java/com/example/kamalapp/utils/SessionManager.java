package com.example.kamalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

/**
 * SessionManager class to handle user session management
 * Using SharedPreferences to store session data
 */
public class SessionManager {
    // LogCat tag
    private static final String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    // Shared preferences file name
    private static final String PREF_NAME = "MealMateLogin";

    // Shared preferences keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LAST_LOGIN = "lastLogin";
    private static final String KEY_SESSION_TOKEN = "sessionToken";
    
    // Session expiry time in milliseconds (default: 30 days)
    private static final long SESSION_EXPIRY_TIME = 30 * 24 * 60 * 60 * 1000;

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     *
     * @param email user email
     * @param token authentication token (optional, can be null)
     */
    public void createLoginSession(String email, String token) {
        // Store login values in shared preferences
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putLong(KEY_LAST_LOGIN, new Date().getTime());
        
        if (token != null) {
            editor.putString(KEY_SESSION_TOKEN, token);
        }
        
        // Commit changes
        editor.apply();
        
        Log.d(TAG, "User login session created for: " + email);
    }

    /**
     * Check if user is logged in and session is valid
     */
    public boolean isLoggedIn() {
        boolean isLoggedIn = pref.getBoolean(KEY_IS_LOGGED_IN, false);
        
        if (isLoggedIn) {
            // Check if session has expired
            long lastLoginTime = pref.getLong(KEY_LAST_LOGIN, 0);
            long currentTime = new Date().getTime();
            
            if (currentTime - lastLoginTime > SESSION_EXPIRY_TIME) {
                // Session expired, clear data
                Log.d(TAG, "Session expired. Logging out user.");
                logoutUser();
                return false;
            }
            
            // Update last login time to extend session
            editor.putLong(KEY_LAST_LOGIN, currentTime);
            editor.apply();
            
            return true;
        }
        
        return false;
    }

    /**
     * Get stored session data
     */
    public String getUserEmail() {
        return pref.getString(KEY_EMAIL, null);
    }
    
    public String getSessionToken() {
        return pref.getString(KEY_SESSION_TOKEN, null);
    }
    
    public long getLastLoginTime() {
        return pref.getLong(KEY_LAST_LOGIN, 0);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clear all data from shared preferences
        editor.clear();
        editor.apply();
        
        Log.d(TAG, "User logged out. Session cleared.");
    }
} 
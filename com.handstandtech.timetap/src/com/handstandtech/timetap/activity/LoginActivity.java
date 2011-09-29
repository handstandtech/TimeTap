package com.handstandtech.timetap.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.handstandtech.harvest.impl.WhoAmIResponse;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;
import com.handstandtech.timetap.task.AsyncTaskCallback;
import com.handstandtech.timetap.task.LoginTask;

/**
 * Login Screen
 * 
 * @author Sam Edwards
 */
public class LoginActivity extends TimeTapBaseActivity {

  /** Called when the activity is first created. */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.e(TAG, LoginActivity.class.getSimpleName() + " Created!");
    setContentView(R.layout.activity_login);
    // Initialize Button Listeners
    initializeLoginButton();
    initializeForgotPasswordLink();

    if (getTimeTap().isPreviouslyLoggedIn(LoginActivity.this)) {
      Intent showHomeIntent = new Intent(this, ClientListActivity.class);
      startActivity(showHomeIntent);
    }

    String username = getUsernameFromPrefs();
    String password = getPasswordFromPrefs();
    String subdomain = getSubdomainFromPrefs();

    ((EditText) findViewById(R.id.usernameText)).setText(username);
    ((EditText) findViewById(R.id.passwordText)).setText(password);
    ((EditText) findViewById(R.id.subdomainText)).setText(subdomain);
  }

  /**
   * Prevent User from going back to the Home Screen if they just logged out
   */
  @Override
  public void onBackPressed() {
    return;
  }

  private void initializeLoginButton() {
    // Capture our button from layout
    Button loginButton = (Button) findViewById(R.id.loginButton);
    // Register the onClick listener with the implementation above
    loginButton.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        // Toast.makeText(LoginActivity.this, "Login!?",
        // Toast.LENGTH_SHORT).show();

        final String subdomain = getSubdomain();
        final String username = getUsername();
        final String password = getPassword();

        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Verifying Credentials");
        dialog.setIndeterminate(true);
        dialog.show();
        LoginTask loginTask = new LoginTask(LoginActivity.this, subdomain, username, password,
            new AsyncTaskCallback<WhoAmIResponse>() {
              public void onTaskComplete(WhoAmIResponse result) {
                if (dialog.isShowing()) {
                  dialog.dismiss();
                }

                if (result != null) {
                  Log.i(Constants.TAG, "Email:" + result.getUser().getEmail());
                  Log.i(Constants.TAG, "Timezone:" + result.getUser().getTimezone());

                  LoginActivity.this.setUsernameInPrefs(username);
                  LoginActivity.this.setPasswordInPrefs(password);
                  LoginActivity.this.setSubdomainInPrefs(subdomain);

                  Intent showActivityIntent = new Intent(LoginActivity.this, ClientListActivity.class);
                  LoginActivity.this.startActivity(showActivityIntent);
                } else {
                  Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_LONG).show();
                }
              }
            });
        loginTask.execute(null);
      }

    });
  }

  private void initializeForgotPasswordLink() {
    // Capture our button from layout
    TextView loginButton = (TextView) findViewById(R.id.forgetPasswordLink);
    // Register the onClick listener with the implementation above
    loginButton.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        Toast.makeText(LoginActivity.this, "Forgot Password!?", Toast.LENGTH_SHORT).show();

      }
    });
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    Log.i(TAG, "onSaveInstanceState");
    super.onSaveInstanceState(savedInstanceState);
    // Save UI state changes to the savedInstanceState.
    // This bundle will be passed to onCreate if the process is
    // killed and restarted.
    savedInstanceState.putString(PROP_USERNAME, getUsername());
    savedInstanceState.putString(PROP_PASSWORD, getPassword());
    // etc.
  }

  private String getUsername() {
    EditText usernameText = (EditText) findViewById(R.id.usernameText);
    return usernameText.getText().toString();
  }

  private String getSubdomain() {
    EditText text = (EditText) findViewById(R.id.subdomainText);
    return text.getText().toString();
  }

  private String getPassword() {
    EditText usernameText = (EditText) findViewById(R.id.passwordText);
    return usernameText.getText().toString();
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    Log.i(TAG, "onRestoreInstanceState");
    super.onRestoreInstanceState(savedInstanceState);
    // Restore UI state from the savedInstanceState.
    // This bundle has also been passed to onCreate.

    setUsername(savedInstanceState.getString(PROP_USERNAME));
    setPassword(savedInstanceState.getString(PROP_PASSWORD));
  }

  private void setUsername(String string) {
    if (string != null) {
      EditText usernameText = (EditText) findViewById(R.id.usernameText);
      usernameText.setText(string);
    }
  }

  private void setPassword(String string) {
    if (string != null) {
      EditText passwordText = (EditText) findViewById(R.id.passwordText);
      passwordText.setText(string);
    }
  }

}
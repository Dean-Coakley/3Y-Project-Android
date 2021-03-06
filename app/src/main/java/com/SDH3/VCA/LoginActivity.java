package com.SDH3.VCA;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth userAuth;
    private EditText userNameInput;
    private EditText passwordInput;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        pd = new ProgressDialog(this);

        userNameInput = (EditText) findViewById(R.id.user_in);
        passwordInput = (EditText) findViewById(R.id.pw_in);
        Button login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = "";
                userName = userNameInput.getText().toString();
                String password = "";
                password = passwordInput.getText().toString();
                signIn(userName, password);
            }
        });


        userAuth = FirebaseAuth.getInstance();
        FirebaseUser user = userAuth.getCurrentUser();
        if (user != null){
            Log.i(getPackageName(), "User already logged in. Opening MainActivity.");
            openMainActivity(userAuth.getCurrentUser());
        }
    }


    private void signIn(String uName, String password) {

        if (!validateForm()) return;
        userAuth.signInWithEmailAndPassword(uName, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = userAuth.getCurrentUser();
                                    openMainActivity(user);
                                } else {
                                    resetForm(true);
                                    pd.hide();
                                }
                            }
                        }
                );
    }

    private boolean validateForm() {
        boolean BBB = true;
        if (userNameInput.getText().toString().equals("")|| passwordInput.getText().toString().equals("")) {
            BBB = false;
            Toast.makeText(LoginActivity.this, R.string.fields, Toast.LENGTH_LONG ).show();
        }
        else
        {
            pd.setMessage("Logging in..");
            pd.show();
        }
        return BBB;
    }

    private void resetForm(boolean showErrorMessage) {
        if (showErrorMessage) {
            TextView errorMessage = (TextView) findViewById(R.id.login_error_message);
            errorMessage.setVisibility(View.VISIBLE);
            //userNameInput.setText("");
            passwordInput.setText("");
        }
    }

    public void openMainActivity(FirebaseUser user){
        Intent startApp = new Intent(getApplicationContext(), MainActivity.class);
        startApp.putExtra("username", user.getDisplayName());
        startApp.putExtra("email", user.getEmail());
        startApp.putExtra("uID", user.getUid());
        startActivity(startApp);
    }
  
    @Override
    public void onPause() {
        super.onPause();
        pd.hide();
    }

}


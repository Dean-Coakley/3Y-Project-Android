package com.SDH3.VCA;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth userAuth;
    private EditText userNameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();

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
    }


    private void signIn(String uName, String password) {
        if (!validateForm()) return;
        userAuth.signInWithEmailAndPassword(uName, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = userAuth.getCurrentUser();
                                    Intent startApp = new Intent(getApplicationContext(), MainActivity.class);
                                    startApp.putExtra("username", user.getDisplayName());
                                    startApp.putExtra("email", user.getEmail());
                                    startApp.putExtra("uID", user.getUid());
                                    startActivity(startApp);
                                } else {
                                    resetForm(true);
                                }
                            }
                        }
                );
    }

    private boolean validateForm() {
        return true;
    }

    private void resetForm(boolean showErrorMessage) {
        if (showErrorMessage) {
            TextView errorMessage = (TextView) findViewById(R.id.login_error_message);
            errorMessage.setVisibility(View.VISIBLE);
            userNameInput.setText("");
            passwordInput.setText("");
        }
    }

}


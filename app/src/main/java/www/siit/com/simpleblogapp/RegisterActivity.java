package www.siit.com.simpleblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText nameEdit,emailEdit,passwordEdit;
    Button singInButton;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        mAuth=FirebaseAuth.getInstance();
        mDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress=new ProgressDialog(this);




        nameEdit=findViewById(R.id.nameFieldId);
        emailEdit=findViewById(R.id.emailFeildId);
        passwordEdit=findViewById(R.id.passwordId);
        singInButton=findViewById(R.id.LogsignInbtnId);

        singInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {

        final String userName=nameEdit.getText().toString().trim();
        String userEmail=emailEdit.getText().toString().trim();
        String userPassword=passwordEdit.getText().toString().trim();

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword))
        {

            mProgress.setMessage("Signing Up.......");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {

                        String userId=mAuth.getCurrentUser().getUid();

                        DatabaseReference currentUser=mDatabaseRef.child(userId);
                        currentUser.child("name").setValue(userName);
                        currentUser.child("image").setValue("default");
                        mProgress.dismiss();

                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }
            });
        }
    }


}

package www.siit.com.simpleblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    EditText SemailEdit,SpassEdit;
    Button signin,createAcc;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;
    private ProgressDialog mProgress;
    //private FirebaseAuth.AuthStateListener authStateListener;

    //-----------Goole Sign In Variable----------
    SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    private static String TAG="Login Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth=FirebaseAuth.getInstance();


        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUser.keepSynced(true);
        mProgress=new ProgressDialog(this);

        //
        mGoogleBtn=findViewById(R.id.SAgoogleSignBtnId);

        SemailEdit=findViewById(R.id.sEmailId);
        SpassEdit=findViewById(R.id.sPassId);

        signin=findViewById(R.id.signInBtnId);
        createAcc=findViewById(R.id.CreateAccId);

        //--------------Google Sign In Option--------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient=new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(SignInActivity.this, "You Got An Error...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();
            }
        });

        //--------------Google Sign In Option--------------


        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignInActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //-------------sing In---------
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinProcess();
            }
        });

    }

    //--------------Google Sign In-------------

    private void singIn(){

        Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

        //--------
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);



            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                mProgress.setMessage("Staring SignIn..... ");
                mProgress.show();

                if (task.isSuccessful()) {
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        //Log.w(TAG, "Google sign in failed", e);
                        // ...

                    }
                }else {
                    mProgress.dismiss();
                }
            }
        }

    //AuthCredential authCredential=GoogleAuthProvider.getCredential()

        private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
            //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                        //Toast.makeText(LogInMainActivity.this, "Authentication Successfully....", Toast.LENGTH_SHORT).show();

                        if (!task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = firebaseAuth.getCurrentUser();

                            Toast.makeText(SignInActivity.this, "Authentication Failed....", Toast.LENGTH_SHORT).show();
                            //updateUI(user);
                        }else {

                            mProgress.dismiss();
                            checkUserExits();
                        }
                        // ...
                    }
                });
        }





    //---------------------Method of Sign In-----------------
    private void signinProcess() {

        String userSEmail=SemailEdit.getText().toString().trim();
        String userSPass=SpassEdit.getText().toString().trim();

        if (!TextUtils.isEmpty(userSEmail) && !TextUtils.isEmpty(userSPass))
        {

            mProgress.setMessage("Checking Login ..... ");
            mProgress.show();
            mAuth.signInWithEmailAndPassword(userSEmail,userSPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        mProgress.dismiss();
                        checkUserExits();
                    }
                    else {
                        mProgress.dismiss();
                        Toast.makeText(SignInActivity.this, "Error Login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //---------------------CheckUserExits Method-----------------------
    private void checkUserExits() {

        if (mAuth.getCurrentUser() != null) {

            final String userId = mAuth.getCurrentUser().getUid();
            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(userId)) {
                        Intent LoginIntent = new Intent(SignInActivity.this, MainActivity.class);
                        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(LoginIntent);
                    } else {
                        Intent setUpIntent = new Intent(SignInActivity.this, SetupActivity.class);
                        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setUpIntent);
                        //Toast.makeText(SignInActivity.this, "Error Login", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}

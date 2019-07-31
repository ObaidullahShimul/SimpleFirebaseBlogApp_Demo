package www.siit.com.simpleblogapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mblog_List;

    private List<Upload> uploadList;
    private MyAdapter myAdapter;
    DatabaseReference databaseReference;

    //-----user log in or not
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------user log in or Not-----
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser()==null)
                {
                    Intent LoginIntent=new Intent(MainActivity.this,SignInActivity.class);
                    LoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(LoginIntent);
                }

            }
        };

        //checkUserExits();

        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        //mDatabaseUsers.keepSynced(true);
        //-----off Line Capabilites----------------
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //databaseReference.keepSynced(true);
        /*FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference.keepSynced(true);

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso build=builder.build();
        build.setIndicatorsEnabled(false);
        build.setLoggingEnabled(true);
        Picasso.setSingletonInstance(build);*/

        mblog_List=findViewById(R.id.blog_List);
        mblog_List.setHasFixedSize(true);
        mblog_List.setLayoutManager(new LinearLayoutManager(this));





        uploadList=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference( "Up");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Upload upload=dataSnapshot1.getValue(Upload.class);
                    uploadList.add(upload);
                }

                myAdapter=new MyAdapter(MainActivity.this,uploadList);
                mblog_List.setAdapter(myAdapter);


                //progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, "Error"+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.INVISIBLE);
            }
        });






    }


    @Override
    protected void onStart() {
        checkUserExits();
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);



    }


    //---------------------Copy from Sign In Activity-----------------
    private void checkUserExits() {

        if (mAuth.getCurrentUser() != null) {

            final String userId = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(userId)) {
                        Intent setUpIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setUpIntent);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.postId){
            Intent intent=new Intent(MainActivity.this,PostActivity.class);
            startActivity(intent);
        }

        if (item.getItemId()==R.id.LogOutId){
            mAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}

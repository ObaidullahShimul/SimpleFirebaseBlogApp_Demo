package www.siit.com.simpleblogapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mSelectImage;
    private static final int GALLERY_REQUEST=1;

    private EditText mPostTitle;
    private EditText mPostDece;
    private Button submitBtn;
    private Uri mImageUri;

    Random random;

    StorageReference storageReference;
    //private ProgressDialog mProgress;
    DatabaseReference databaseReference;
    //Datab
    StorageTask uploadTask;

    //-----user name--------
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //mAuth=FirebaseAuth.getInstance();
        //currentUser=mAuth.getCurrentUser();

        //mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());

        //------2------
        //databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference("Up");
        storageReference=FirebaseStorage.getInstance().getReference("Up");


        mPostTitle=findViewById(R.id.titleField);
        mPostDece=findViewById(R.id.descField);
        submitBtn=findViewById(R.id.submitBtnID);

        mSelectImage=findViewById(R.id.selectImgBtn);


        mSelectImage.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        //mSelectImage.setOnClickListener(this);
        //storageReference=FirebaseStorage.getInstance().getReference();



        //mProgress=new ProgressDialog(this);


        /*mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });*/

        /*submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uploadTask!=null && uploadTask.isInProgress())
                {
                    Toast.makeText(PostActivity.this, "Uploading in Progress", Toast.LENGTH_SHORT).show();
                }else {
                    startPosting();
                }


            }
        });*/

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selectImgBtn:

                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
                break;

            case R.id.submitBtnID:

                if (uploadTask!=null && uploadTask.isInProgress())
                {
                    Toast.makeText(PostActivity.this, "Uploading in Progress", Toast.LENGTH_SHORT).show();
                }else {
                    startPosting();
                }

                break;

        }
    }

    //-----------for Image Extension---------
    public String getFileExtension(Uri imageUri){

        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

    }

    private void startPosting() {
        //mProgress.setMessage("Posting Blog...... ");
        //mProgress.show();

        final String title_value=mPostTitle.getText().toString().trim();
        final String description=mPostDece.getText().toString().trim();

        //final String userName=mAuth.getCurrentUser().getUid();

        if (title_value.isEmpty())
        {
            mPostTitle.setError("Enter Image Name");
            mPostTitle.requestFocus();
            return;
        }

        else if (description.isEmpty())
        {
            mPostDece.setError("Enter Description");
            mPostDece.requestFocus();
            return;
        }


        //StorageReference filePath=storageReference.child("Blog_Images").child(mImageUri.getLastPathSegment());
        StorageReference filePath=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
        //StorageReference filePath2=storageReference.child(Random.class.toString());

        filePath.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(PostActivity.this, "Image Store Successfully", Toast.LENGTH_SHORT).show();

                //-------3--------------
                //Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                //while (!urlTask.isSuccessful());
                //Uri downloadUrl=urlTask.getResult();

                //Upload upload=new Upload(title_value,downloadUrl.toString());

                //-------3--------------
                Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl=urlTask.getResult();

                //Upload upload=new Upload(title_value,description,downloadUrl.toString());
                Upload upload1=new Upload(title_value,description,downloadUrl.toString());

                //Upload upload1=new Upload(title_value,description,taskSnapshot.getStorage().getDownloadUrl().toString());

                String uploadId=databaseReference.push().getKey();
                databaseReference.child(uploadId).setValue(upload1);

                startActivity(new Intent(PostActivity.this,MainActivity.class));


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(PostActivity.this, "Image Store Not Successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }




        //-----------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            mImageUri=data.getData();
            //Picasso.with(this).load(mImageUri).into(mSelectImage);
            Picasso.with(this).load(mImageUri).networkPolicy(NetworkPolicy.OFFLINE).into(mSelectImage);
        }
    }



}

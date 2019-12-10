package org.techtown.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;


public class ProfileFragment extends Fragment
{
    ImageView ivUser;
    Bitmap bitmap;
    private StorageReference mStorageRef;
    String userEmail;
    String userUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("email",Context.MODE_PRIVATE);
        userUid = sharedPreferences.getString("uid","");
        userEmail = sharedPreferences.getString("email","");

        Toast.makeText(getActivity(), userEmail, Toast.LENGTH_SHORT).show();



        ivUser = (ImageView) v.findViewById(R.id.ivUser);
        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 1);
            }
        });

        return v;
    }

    public void uploadImage()
    {
        StorageReference mountainsRef = mStorageRef.child("users").child(userUid+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                String photoUri = String.valueOf(downloadUrl);
                Log.d("url",photoUri);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");

                Hashtable<String, String> profile = new Hashtable<String, String>();
                profile.put("email", userEmail);
                profile.put("photo", photoUri);

                myRef.child(userUid).setValue(profile);
                myRef.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        String s = dataSnapshot.getValue().toString();
                        Log.d("Profile",s);
                        if(dataSnapshot != null)
                            Toast.makeText(getActivity(),"사진 업로드가 잘 됬습니다.",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        Uri image = data.getData();
        try
        {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),image);
            ivUser.setImageBitmap(bitmap);
            uploadImage();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



}

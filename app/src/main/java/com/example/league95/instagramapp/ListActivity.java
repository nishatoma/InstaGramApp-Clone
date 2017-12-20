package com.example.league95.instagramapp;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    public void setIntent() {
        //This will ask the user to pick photos either from gallery!
        //By using intents we can do awesome things like this :))
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Do something with the image
        startActivityForResult(intent, 1);
    }

    ImageView imageView;

    //We need to do something IF the user has given us permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setIntent();
            }
        }
    }

    //Code for menu as usual
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void logOut()
    {
        ParseUser.logOut();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share) {
            //We also need to ask for user permission here!
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                setIntent();
            }
        } else if (item.getItemId() == R.id.logOut)
        {
            logOut();
            if (ParseUser.getCurrentUser() != null)
            {
                Log.i("Log", "sTill in");
            } else
            {
                Log.i("Log", "out");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We use Intent data to get our resulting image or data.

        if ((requestCode == 1 || requestCode == RESULT_OK) && data != null) {
            //Link to our image
            Uri selectedImage = data.getData();
            try {
                //Generated image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("Photo", "Received");

                //Okay this is where we write code to send/receive photos to/from Parse Server!
                //This like allows us to convert our image to a Parse file.
                //Which we can then upload as a part object
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //Compress the image and send it as an output to the stream
                //Converts our image to a PNG format
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //Then convert that image to a Byte format.
                byte[] byteArray = stream.toByteArray();
                //To get to a parse file, you need a byte array.
                //And then we can finally convert that to a parse file
                ParseFile file = new ParseFile("image.png", byteArray);

                ParseObject parseObject = new ParseObject("Image");
                parseObject.put("image", file);
                //This username corresponds to the current user logged in.
                //Obviously we want the image uploaded to be attached to that user.
                parseObject.put("username", ParseUser.getCurrentUser().getUsername());

                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                        {
                            Toast.makeText(ListActivity.this, "Image shared", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            Toast.makeText(ListActivity.this, "Image could not be shared, try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final ArrayList<String> users = new ArrayList<>();
        final ListView listView = findViewById(R.id.listView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);

        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseUser user : objects) {
                        users.add(user.getString("username"));
                    }
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ListActivity.this, UserInfo.class);
                String userName = adapterView.getItemAtPosition(i).toString();
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });


    }
}

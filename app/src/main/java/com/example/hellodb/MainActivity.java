package com.example.hellodb;

import static android.content.ContentValues.TAG;

import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private LinearLayout mLinearLayout;
    ArrayList<String[]> mobileArrayList = new ArrayList<>();
    List<String> stringList = new ArrayList<>();
    private DatabaseReference mDbRef;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        WallpaperColors wallpaperColors = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM);
        int primaryColor = wallpaperColors.getPrimaryColor().toArgb();
        int secondaryColor = Color.WHITE;
        int tertiaryColor = wallpaperColors.getTertiaryColor().toArgb();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        getWindow().setStatusBarColor(primaryColor);

        Button ins = findViewById(R.id.insert);
        ins.setBackgroundColor(primaryColor);
        ins.setTextColor(secondaryColor);
        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseDatabase.getInstance();
                mDbRef = mDatabase.getReference("students");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
                String formattedDateTime = now.format(ISO_FORMATTER);
                String userId = mDbRef.push().getKey();
                EditText name = findViewById(R.id.stuname);
                EditText no = findViewById(R.id.stuno);
                Users user = new Users(name.getText().toString(),no.getText().toString(),formattedDateTime);
                mDbRef.child(userId).setValue(user);
            }
        });
        Button del = findViewById(R.id.delete);
        del.setBackgroundColor(primaryColor);
        del.setTextColor(secondaryColor);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseDatabase.getInstance();
                mDbRef = mDatabase.getReference("students");
                EditText name = findViewById(R.id.stuname);
                EditText no = findViewById(R.id.stuno);
                String studentName = name.getText().toString().trim();
                String studentNo = no.getText().toString().trim();

                Query query = mDbRef.orderByChild("name").equalTo(studentName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String key = ds.getKey();
                            String noValue = ds.child("no").getValue(String.class);
                            if (noValue.equals(studentNo)) {
                                mDbRef.child(key).setValue(null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "loadPost:onCancelled", error.toException());
                    }
                });

            }
        });

        Button upd = findViewById(R.id.update);
        upd.setBackgroundColor(primaryColor);
        upd.setTextColor(secondaryColor);
        upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseDatabase.getInstance();
                mDbRef = mDatabase.getReference("students");

// get the name and new no value from the EditText fields
                EditText nameField = findViewById(R.id.stuname);
                EditText noField = findViewById(R.id.stuno);
                String name = nameField.getText().toString().trim();
                String newNo = noField.getText().toString().trim();

// query the database for the node with the specified name
                Query query = mDbRef.orderByChild("name").equalTo(name);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            // get the key of the node to update
                            String key = ds.getKey();
                            // update the value of the "no" field with the new value
                            LocalDateTime now = LocalDateTime.now();
                            final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
                            String formattedDateTime = now.format(ISO_FORMATTER);
                            mDbRef.child(key).child("no").setValue(newNo);
                            mDbRef.child(key).child("date").setValue(formattedDateTime);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "loadPost:onCancelled", error.toException());
                    }
                });

            }
        });
        mDatabase = FirebaseDatabase.getInstance();

        mDbRef = mDatabase.getReference("students");
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mobileArrayList.clear();
                stringList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    LocalDateTime now = LocalDateTime.now();
                    Log.d(TAG, "snapshot value: " + snapshot.getValue());
                    String name = ds.child("name").getValue(String.class);
                    String no = ds.child("no").getValue(String.class);
                    String date = ds.child("date").getValue(String.class);
                    String[] mobileArray;
                    LocalDateTime storedDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
                    Duration duration = Duration.between(storedDateTime, now);
                    long minutes = duration.toMinutes();
                    long days = (minutes/1440);
                    long hrs = (minutes/60);
                    if(days <= 0 && hrs<=1){
                        mobileArray = new String[]{name + "," + no + " ( added " + minutes + "  minutes ago)"};
                    }
                    else if(hrs>=1 && days<=0){
                        mobileArray = new String[]{name + "," + no + " ( added " + hrs + "  hours ago)"};
                    }
                    else{
                        mobileArray = new String[]{name+","+no+" ( added "+(int)days+"  days ago)"};
                    }

                    mobileArrayList.add(mobileArray);
                }

                // Flatten the list of arrays into a single list of strings

                for (String[] mobileArray : mobileArrayList) {
                    stringList.addAll(Arrays.asList(mobileArray));
                }

                // Create the adapter with the list of strings
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.activity_listview, stringList.toArray(new String[0]));
                ListView listView = (ListView) findViewById(R.id.stulist);
                listView.setAdapter(adapter);
                if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                    // Dark theme
                    listView.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    // Light theme
                    listView.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());

            }
        });


    }
}
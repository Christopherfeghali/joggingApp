package com.example.runningapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.runningapp.MyContentProvider.CONTENT_URL;

//MainActivity class is responsible for displaying the previous running sessions and offering the user  a choice of starting a new
// session by turning the switch for tracking on as well as the option of looking at their jogging progresses on a live map.



public class MainActivity extends AppCompatActivity {


    CursorAdapter cursoradapter;
    ContentResolver resolver;
    private BroadcastReceiver broadcastReceiver;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) { // on creation initialize the buttons
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapButton();
        getList();
        refreshButton();
        checkIf();
    }


    public void refreshButton() // retrieves the new updated list after a sessions has been added
    {
        Button refresh = (Button)findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getList();
            }
        });
    }

    public void mapButton() // opens up the map activity where we can see our current location on the map
    {
        Button mapbutton = (Button)findViewById(R.id.mapButton);
        mapbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
    }


    public void getList() // Retrieves the inserted data from using the content resolver and is read by a cursor which is adapted to be able to be inserted into a listview
    {
        Uri uri = CONTENT_URL;

        String[] projection = {MyContentProvider.id, MyContentProvider.name, MyContentProvider.distance,};
        String[] From = {MyContentProvider.id, MyContentProvider.name, MyContentProvider.distance,};
        int[] to = {R.id.listid, R.id.listName, R.id.listDistance,};

        Cursor customCursor = getContentResolver().query(uri,projection, null, null, null);
        cursoradapter = new SimpleCursorAdapter(this, R.layout.title_list, customCursor, From, to, 0);
        ListView listView = (ListView) findViewById( R.id.listView );
        listView.setAdapter(cursoradapter);
    }

    public void checkIf() // Checks if the switch button is on or off
    {
        final Switch switchstate = (Switch) findViewById(R.id.switch1);
        switchstate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) // if the switch is on start the service responsible for tracking our location
                {
                    Intent intent = new Intent(getApplicationContext(),Service.class);
                    startService(intent);
                    Toast.makeText(getApplicationContext(),"Tracking started",Toast.LENGTH_SHORT).show();
                }
                else // if the switch is off stop the service responsible for tracking our location
                {
                    Intent intent = new Intent(getApplicationContext(),Service.class);
                    stopService(intent);
                    Toast.makeText(getApplicationContext(),"Tracking stopped",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

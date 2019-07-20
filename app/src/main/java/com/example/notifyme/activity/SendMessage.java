package com.example.notifyme.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.notifyme.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SendMessage extends AppCompatActivity implements View.OnClickListener {

    EditText MessageText = null;
    EditText Number = null;
    ListView listView = null;
    ArrayAdapter<String> arrayAdapter = null;
    ArrayList<String> Numbers = null;
    ImageView googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        MessageText = findViewById(R.id.msgText);
        Number = findViewById(R.id.newNumber);
        listView = findViewById(R.id.numbers);
        googleMap = findViewById(R.id.googlemap);
        googleMap.setOnClickListener(this);
        registerForContextMenu(listView);
        Numbers = new ArrayList<>();
    }

    public void sendMessage(View view) {
        String Message = MessageText.getText().toString();
        for (String s : Numbers)
        {
            sendSmsMsgFnc(s,Message);
        }
    }

    public void addNumber(View view) {
        if(Number.getText().length() != 0) {
            if(Number.getText().length() == 11) {
                String num = Number.getText().toString();
                Number.setText("");
                Numbers.add(num);
                listView.setAdapter(null);
                arrayAdapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        Numbers);
                listView.setAdapter(arrayAdapter);
            }
            else
            {
                Toast.makeText(this, "Invalid number entered must be 11 characters", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Enter Number first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void sendSmsMsgFnc(String mblNumVar, String smsMsgVar)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
        {
            try
            {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(mblNumVar, null, smsMsgVar, null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception ErrVar)
            {
                Toast.makeText(getApplicationContext(),ErrVar.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
            }
        }

    }
    private String convertToString(ArrayList<String> list) {

        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (String s : list)
        {
            sb.append(delim);
            sb.append(s);;
            delim = ",";
        }
        return sb.toString();
    }

    private ArrayList<String> convertToArray(String string) {

        ArrayList<String> list = new ArrayList<String>(Arrays.asList(string.split(",")));
        return list;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==listView.getId()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.numberlistmenu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long id = ((AdapterView.AdapterContextMenuInfo)info).id;
        int position  = ((AdapterView.AdapterContextMenuInfo)info).position;
        if (item.getItemId() == R.id.delete) {
            Numbers.remove(position);
            arrayAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == googleMap.getId())
        {
            Intent intent = new Intent(this,MapsActivity.class);
            startActivity(intent);
            SharedPreferences sharedPreferences = getSharedPreferences("SelectedAddress", MODE_PRIVATE);
            sharedPreferences.edit().clear().commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("SelectedAddress", MODE_PRIVATE);
        if (!sharedPreferences.getString("Address", "").equals("")) {
            MessageText.setText(MessageText.getText().toString() + sharedPreferences.getString("Address", ""));
        }
    }
}

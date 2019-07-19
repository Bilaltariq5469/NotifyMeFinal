package com.example.notifyme.activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.notifyme.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SendMessage extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;
    EditText MessageText = null;
    EditText Number = null;
    ListView listView = null;
    ArrayAdapter<String> arrayAdapter = null;
    ArrayList<String> Numbers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        editor = getSharedPreferences("myprefs", MODE_PRIVATE).edit();
        sharedPreferences = getSharedPreferences("myprefs", MODE_PRIVATE);



        MessageText = findViewById(R.id.msgText);
        Number = findViewById(R.id.newNumber);
        listView = findViewById(R.id.numbers);

        String nums = sharedPreferences.getString("numbers",null);
        if(nums!=null)
        {
            Numbers = convertToArray(nums);
            arrayAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    Numbers );
            listView.setAdapter(arrayAdapter);
        }
else {
            Numbers=new ArrayList<>();
        }

    }

    public void sendMessage(View view) {
        String Message = MessageText.getText().toString();
        for (String s : Numbers)
        {
            sendSmsMsgFnc(s,Message);
        }
    }

    public void addNumber(View view) {

        String num = Number.getText().toString();
        Numbers.add(num);
        listView.setAdapter(null);
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                Numbers );
        listView.setAdapter(arrayAdapter);
        editor.putString("numbers",convertToString(Numbers));
    }

    @Override
    protected void onStop() {
        super.onStop();
        editor.commit();
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
}

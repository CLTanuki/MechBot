package ru.labadabadoo.mechbot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class BotControll extends Activity {

    Spinner bot_spin;
    public ArrayAdapter<String> bot_spinnerAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_controll);

        final BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBtAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Важное сообщение!")
                    .setMessage("Покормите кота!")
                    .setCancelable(false)
                    .setNegativeButton("ОК, иду на кухню",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        final Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        final List<String> pairedDevicesList = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            pairedDevicesList.add(bt.getName());

        bot_spin = (Spinner) findViewById(R.id.bot_spinner);

        bot_spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pairedDevicesList);
        bot_spinnerAdapter.add("MechBot");
        bot_spin.setAdapter(bot_spinnerAdapter);
        bot_spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bot_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinned_bot = bot_spin.getSelectedItem().toString();
                for (BluetoothDevice i : pairedDevices)
                    if (i.getName().equals(spinned_bot))
                    {
                        try {
                            BluetoothSocket socket = i.createRfcommSocketToServiceRecord(UUID.fromString("00001101-" +
                                    "0000-1000-8000-00805F9B34FB"));
                            tmpIn = socket.getInputStream();
                            tmpOut = socket.getOutputStream();
                            Toast.makeText(getApplicationContext(),"Connected" ,
                                    Toast.LENGTH_LONG).show();
                        }
                        catch (IOException e) {
                            Toast.makeText(getApplicationContext(),"Failed" ,
                                    Toast.LENGTH_LONG).show();
                        }

                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.pick_color)
//                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // The 'which' argument contains the index position
//                        // of the selected item
//                    }
//                });
//        return builder.create();
//    }

//    public void btOn(){
//        Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        bot_spinnerAdapter.notifyDataSetChanged();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bot_controll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

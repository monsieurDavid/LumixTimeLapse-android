package com.example.lumixtimelapse;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity
{
	int REQUEST_ENABLE_BT = 1234;
	BluetoothAdapter mBluetoothAdapter = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void doIt(View view)
    {
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    	if (mBluetoothAdapter == null)
    	{
    	    // Device does not support Bluetooth
    		return;
    	}
    	
    	if (!mBluetoothAdapter.isEnabled())
    	{
        	System.out.println("Bluetooth is not enabled");
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        	System.out.println("Made the call to enable BT");
    	}
    	else
    	{
    		System.out.println("Bluetooth is enabled, looking for shield");
    		keepDoingIt();
    	}
    	System.out.println("Done in doIt()");
    }
    
    private void keepDoingIt()
    {
    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    	BluetoothDevice fireFly = null;
    	// If there are paired devices
    	if (pairedDevices.size() > 0)
    	{
    	    // Loop through paired devices
    	    for (BluetoothDevice device : pairedDevices)
    	    {
    	        // Add the name and address to an array adapter to show in a ListView
    	        System.out.println(device.getName() + "\n" + device.getAddress());
    	        if(device.getName().equals("FireFly-AF85")) { fireFly = device; }
    	    }
    	}
    	
    	if(fireFly != null)
    	{
    		UUID uuid = null;
    		ParcelUuid[] ids = fireFly.getUuids();
    		for(ParcelUuid id : ids)
    		{
    			System.out.println("ID = "+id.toString());
    			uuid = id.getUuid();
    		}
    		
    		BluetoothSocket socket = null;
    		OutputStream btOutStream = null;
    		
    		try
    		{
    			System.out.println("Attempting to get Bluetooth Socket");
        		socket = fireFly.createRfcommSocketToServiceRecord(uuid);
    			socket.connect();
    			System.out.println("Attempting to create OutputStream");
        		btOutStream = socket.getOutputStream();
        		byte[] word = { '5' };
        		btOutStream.write(word);
    		}
    		catch(Exception e)
    		{
    			System.out.println(e.toString());
    		}
    		finally
    		{
    			try
    			{
    				btOutStream.close();
        			socket.close();
    			}
    			catch(Exception ignore) {}
    		}
    	}
    	else { System.out.println("Did not find FireFly"); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT)
        {
        	System.out.println("Got the result for trying to enable BT");
            // Make sure the request was successful
            if (resultCode == RESULT_OK)
            {
            	System.out.println("Yea ... BT enabled");
            }
            else
            {
            	System.out.println("Sorry .... no BT for you!");
            }
        }
    }
}

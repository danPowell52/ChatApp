package com.example.daniel.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Daniel on 23/02/2017.
 */
public class MyWiFiActivity extends AppCompatActivity {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    TextView textView;

    Boolean WifiP2pEnabled;
    ViewGroup layout;

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        WifiP2pEnabled = isWifiP2pEnabled;
    }

    public void setPeerStatus(List<WifiP2pDevice> peers){

        StringBuilder peerList = new StringBuilder();
        peerList.append("hello");
        TextView peerStatus = new TextView(this);
        peerStatus.setTextSize(40);
        Log.d("Payara", "searching " + peers.size());
        for (WifiP2pDevice ddevice : peers){
            Log.d("Payara", ddevice.deviceName);
            Log.d("Payara", ddevice.toString());
        }
        if (peers.size() < 1){
            textView.setText("no devices found");
        } else {
            for(WifiP2pDevice device: peers){
                peerList.append(device);
            }
            textView.setText(peerList);
            connect(peers.get(0));
        }
        layout.addView(peerStatus);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState){
        //UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText("wfif page");
        layout =(ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);


        //functionality
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        search();
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }



    public void search(){

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                textView.setText("Searching");
                //Searching is an asynchronous search and will not return in time to set status
                //setPeerStatus(((WiFiDirectBroadcastReceiver)mReceiver).getPeers());
            }

            @Override
            public void onFailure(int reasonCode) {
                textView.setText("fail because ... " + reasonCode);
            }
        });

    }
    public void connect(WifiP2pDevice device) {
        //obtain a peer from the WifiP2pDeviceList

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic, this one is immediate and synchronous
                textView.setText("WE HAVE LIFTOFF");
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
            }
        });
    }


}


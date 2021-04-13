package dev.changetech.uptoovpn;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dev.changetech.uptoovpn.adapter.TweakAdapter;
import dev.changetech.uptoovpn.model.Data;
import dev.changetech.uptoovpn.model.Tweak;
import dev.changetech.uptoovpn.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;

public class UptooVpn extends AppCompatActivity {
    TextView downSpeedTv,upSpeedTv;
    RecyclerView recyclerViewUae,recyclerViewKsa;
    private ArrayList<Tweak> uaeArrayList;
    private ArrayList<Tweak> ksaArrayList;
    TweakAdapter adapter;
    Data data;
    Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uptoo_vpn);
        initComponent();
        loading = new Loading(this, false);
        recyclerViewUae.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewKsa.setLayoutManager(new LinearLayoutManager(this));

        uaeArrayList = new ArrayList();
        ksaArrayList = new ArrayList();

        ConnectivityManager cm = (ConnectivityManager) UptooVpn.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();


        new Timer().schedule(new TimerTask(){
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        NetworkCapabilities nc;
                      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            nc = cm.getNetworkCapabilities(cm.getActiveNetwork());

                            int downSpeed = nc.getLinkDownstreamBandwidthKbps();
                            int upSpeed = nc.getLinkUpstreamBandwidthKbps();

                            downSpeedTv.setText(String.valueOf(downSpeed));
                            upSpeedTv.setText(String.valueOf(upSpeed));
                        }
                    };
                });
            }
        }, 0, 1000);


        getUaeData();
        getKsaData();

    }

    private void initComponent() {
        recyclerViewUae = findViewById(R.id.recyclerViewUae);
        recyclerViewKsa = findViewById(R.id.recyclerViewKsa);
        downSpeedTv=findViewById(R.id.downSpeedTv);
        upSpeedTv=findViewById(R.id.upSpeedTv);
    }

    public void getUaeData() {
        loading.start();
        ApiClient.getInstance().getApi().getServerData().enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
//                    Log.e("response",new Gson().toJson(response.body()));
                loading.end();
                if (response.code() == 200) {
                    data = response.body();
                    uaeArrayList.clear();
                    uaeArrayList.add(data.getTweaks().get(0));
                    uaeArrayList.add(data.getTweaks().get(1));
                    uaeArrayList.add(data.getTweaks().get(2));
                    uaeArrayList.add(data.getTweaks().get(3));
                    adapter = new TweakAdapter(UptooVpn.this, uaeArrayList);
                    recyclerViewUae.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(UptooVpn.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
//                    Log.e("error",new Gson().toJson(t.getLocalizedMessage()));
            }
        });

    }

    public void getKsaData() {
        loading.start();
        ApiClient.getInstance().getApi().getServerData().enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
//                    Log.e("response",new Gson().toJson(response.body()));
                loading.end();
                if (response.code() == 200) {
                    data = response.body();
                    ksaArrayList.clear();
                    ksaArrayList.addAll(data.getTweaks());
                    ksaArrayList.remove(data.getTweaks().get(0));
                    ksaArrayList.remove(data.getTweaks().get(1));
                    ksaArrayList.remove(data.getTweaks().get(2));
                    ksaArrayList.remove(data.getTweaks().get(3));
                    adapter = new TweakAdapter(UptooVpn.this, ksaArrayList);
                    recyclerViewKsa.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(UptooVpn.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
//                    Log.e("error",new Gson().toJson(t.getLocalizedMessage()));
            }
        });

    }
}
package com.edu.bodybuddyandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.edu.bodybuddyandroid.R;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private List<String > listProviders;
    private final String TAG=this.getClass().getName();
    private LocationManager locationManager;
    TextView t_lati;
    TextView t_longi;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t_lati=(TextView)findViewById(R.id.t_lati);
        t_longi=(TextView)findViewById(R.id.t_longi);

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)){
                //권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

        //1. 위치관리자 객체 생성
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //여기를 NETWORK_PROVIDER로 잡는게 더 정확할 듯 싶다.
        Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastKnownLocation !=null){
            double lat = lastKnownLocation.getLatitude();
            double lng = lastKnownLocation.getLongitude();
            Log.d(TAG, "받아온 경도 값은 : "+lng+",,,받아온 위도값은 : "+lat);
            t_lati.setText("경도값은:"+Double.toString(lat));
            t_longi.setText("위도값은:"+Double.toString(lng));


        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = 0.0;
        double longitude = 0.0;

        //실내에 있을 때는 잡히지 않는 그냥 거의 안잡히는 GPS_PROVIDER(GPS를 이용 위치로 제공)
        if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            t_lati.setText(": " + Double.toString(latitude ));
            t_longi.setText((": " + Double.toString(longitude)));
            Log.d(TAG + " GPS 위도는 :", Double.toString(latitude)+"경도는 :"+ Double.toString(longitude));
        }
        //가장 많이 잡히는 NETWORK_PROVIDER(기지국 와이파이를 이용위치로 제공)
        if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            t_lati.setText(": " + Double.toString(latitude ));
            t_longi.setText((": " + Double.toString(longitude)));
            Log.d(TAG + " NETWORK : ", Double.toString(latitude )+ '/' + Double.toString(longitude));
        }
        //얘는 좌표값을 구하는 것이 아닌 다른 어플리케이션이나 서비스가 좌표값을 구하면 단순히 그 값을 받아 오기만 하는
        //전달자 역할
        if(location.getProvider().equals(LocationManager.PASSIVE_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            t_lati.setText(": " + Double.toString(latitude ));
            t_longi.setText((": " + Double.toString(longitude)));
            Log.d(TAG + " PASSIVE : ", Double.toString(latitude )+ '/' + Double.toString(longitude));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
        Log.d(TAG+"현재 GPS_Provider의 상태는", Integer.toString(status) );
        Log.d(TAG+"현재 GPS_Provider의 provider는", provider );
        Log.d(TAG+"현재 GPS_Provider의 extra는", String.valueOf(extras));
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates( this::onLocationChanged);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //위치정보를 업데이트 하는 최소시간을 800ms(=0.8초) 로 잡음
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 800, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 800, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 800, 0, this);
    }
}
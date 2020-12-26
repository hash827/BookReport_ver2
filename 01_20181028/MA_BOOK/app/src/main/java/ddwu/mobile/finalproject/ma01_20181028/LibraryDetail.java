package ddwu.mobile.finalproject.ma01_20181028;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

public class LibraryDetail extends AppCompatActivity {

    TextView tvLibName;
    TextView tvTel;
    TextView tvAddress;
    TextView tvHomepage;
    TextView tvOperating;
    TextView tvClosed;
    String name;
    String tel;
    String address;
    String closed;
    String homepage;
    String operating;
    String xcnts;
    String ydnts;
    TextView tvDistance;
    private GoogleMap mgoogleMap;
    private LocationManager locationManager;
    final static int MY_PERMISSIONS_REQ_LOC=1000;
    private String bestProvider;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_detail);

        Intent intent = getIntent();
        name = intent.getStringExtra("libName");
        address = intent.getStringExtra("address");
        tel = intent.getStringExtra("tel");
        homepage = intent.getStringExtra("homepage");
        operating = intent.getStringExtra("operating");
        closed = intent.getStringExtra("closed");
        xcnts = intent.getStringExtra("xcnts");
        ydnts = intent.getStringExtra("ydnts");

        tvAddress = findViewById(R.id.tv_libAddress);
        tvLibName = findViewById(R.id.tv_libName);
        tvTel = findViewById(R.id.tv_libTel);
        tvHomepage = findViewById(R.id.tv_libHomepage);
        tvOperating = findViewById(R.id.tv_libOperating);
        tvClosed = findViewById(R.id.tv_libClosed);
        tvDistance = findViewById(R.id.tv_distance);

        tvClosed.setText(closed);
        tvOperating.setText(operating);
        tvHomepage.setText(homepage);
        tvTel.setText(tel);
        tvAddress.setText(address);
        tvLibName.setText(name);
        tvClosed.setText(closed);

        tvHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage));
                startActivity(intent);
            }
        });
        tvTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent();
               intent.setAction(Intent.ACTION_DIAL);
               intent.setData(Uri.parse("tel:"+tel));
               startActivity(intent);
            }
        });

        bestProvider = LocationManager.GPS_PROVIDER;

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        getCurrentLocation();
    }
    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mgoogleMap = googleMap;

            LatLng currentLoc = new LatLng(Double.parseDouble(xcnts),Double.parseDouble(ydnts) );
            mgoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,16));
            MarkerOptions options = new MarkerOptions();
            options.position(currentLoc);
            options.title(name);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mgoogleMap.addMarker(options).showInfoWindow();


        }
    };
    private void getCurrentLocation() {
        if (checkPermission()) {
            locationManager.requestLocationUpdates(bestProvider, 5000, 10, locListener);
        }
    }
    LocationListener locListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng libraryPosition = new LatLng(Double.parseDouble(xcnts),Double.parseDouble(ydnts));
            double distance = SphericalUtil.computeDistanceBetween(currentPosition, libraryPosition);
            tvDistance.setText("현재 위치와의 거리 : " +String.valueOf((int)distance) +"m");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {		}

        @Override
        public void onProviderEnabled(String provider) {		}

        @Override
        public void onProviderDisabled(String provider) {		}
    };

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                }, MY_PERMISSIONS_REQ_LOC);
                return false;
            } else
                return true;
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQ_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    getCurrentLocation();
                    /*권한을 승인받았을 때 수행하여야 하는 동작 지정*/

                } else {
                    /*사용자에게 권한 제약에 따른 안내*/
                    Toast.makeText(this, "Permissions are not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}

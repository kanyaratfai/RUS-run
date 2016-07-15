package rus.kanyaratroungphueng.rusrun;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latRusADouble = 13.837263;
    private double lngARusDouble = 100.471980;
    private LocationManager locationManager;
    private Criteria criteria;
    private double latUserADouble, lngUserADouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Set Location Service
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//สิ่งที่เห็นอาจอยู่ใน 300 เมตร
        criteria.setAltitudeRequired(false); //ระยะห่าง ที่มีความสูงจากท้องทะเล
        criteria.setBearingRequired(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }   // Main Method

    @Override
    protected void onResume() {
        super.onResume();

        locationManager.removeUpdates(locationListener);

        latUserADouble = latRusADouble;
        lngUserADouble = lngARusDouble;

        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (networkLocation != null) { //คือทำการหาพิกัดผ่านเน็ต จะถามตำแหน่งเน็ตโทรสับ ไม่เท่ากับความว่างเปล่า
            latUserADouble = networkLocation.getLatitude();
            lngARusDouble = networkLocation.getAccuracy();
        }
        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);//ไม่มีเน็ตค้นหาได้
        if (gpsLocation != null) {
            latUserADouble = gpsLocation.getLatitude();
            lngUserADouble = gpsLocation.getLatitude();
        }




    }   //onResume

    @Override
    protected void onStop() {
        super.onStop();

        locationManager.removeUpdates(locationListener);

    }

    public Location myFindLocation(String strProvider) {
        Location location = null;
        if (locationManager.isProviderEnabled(strProvider)) {//ทำการเช็คว่าสัญญา เน็ตอยู่ในตำแหน่งไหน
            locationManager.requestLocationUpdates(strProvider,100,10,locationListener);
            location = locationManager.getLastKnownLocation(strProvider);
        } else {
            Log.d("RusV2","Cannot Find Location");
        }
        return null;
    }
    //Create class ถ้าโทรสับเคลื่อนที่พิกัดก็จะทำงานเคลื่อนที่ตามโทรสับอัตโนมัติ
    // *ถ้าไม่สามารถเชื่อมต่อเน็ตได้ ให้ใช้สัญญาของโทรสับแทน
    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latUserADouble = location.getLatitude();
            lngUserADouble = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Set Center Map
        LatLng latLng = new LatLng(latRusADouble,lngARusDouble);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));//ยิ่งตั้งค่าตัวเลขมากยิ่งใกล้มาก(latLng,...)
        //Loop
        myLoop();
        
    }   //onMapReady

    private void myLoop() {
        //To Do เพิ่มค่าตำแหน่ง
        Log.d("RusV3", "latUser ==>" + latUserADouble);
        Log.d("RusV3", "lnlUser ==>" + lngARusDouble);


        //Delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myLoop();
            }
        },3000);//การหน่วงเวลา3วินาที
    }
}   //Main Class

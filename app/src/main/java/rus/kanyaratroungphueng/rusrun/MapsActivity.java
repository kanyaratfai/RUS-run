package rus.kanyaratroungphueng.rusrun;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

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

    private class CreateMarker extends AsyncTask<Void, Void, String> {
        //Explicit
        private Context context;
        private GoogleMap googleMap;
        private String urlJSON = "http://swiftcodingthai.com/rus/get_user_master.php";

        private CreateMarker(Context context, GoogleMap googleMap) {
            this.context = context;
            this.googleMap = googleMap;
        } //Constructor

        @Override
        protected String doInBackground(Void... voids) {

            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                return null;
            }

        }//doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("RusV4", "JSON==>" + s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    double douLat = Double.parseDouble(jsonObject.getString("Lat"));
                    double douLng = Double.parseDouble(jsonObject.getString("Lng"));
                    String strName = jsonObject.getString("NAME");

                    LatLng latLng = new LatLng(douLat, douLng);
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(strName));

                }   //for
            } catch (Exception e) {
                e.printStackTrace();
            }
        }   // onPost
    }   //CreateMarker Class

    private void myLoop() {
        //To Do เพิ่มค่าตำแหน่ง
        Log.d("RusV3", "latUser ==>" + latUserADouble);
        Log.d("RusV3", "lnlUser ==>" + lngARusDouble);
        //Edit Lat,Lng on Server
        editLatLngOnServer();

        //Create Marker

        mMap.clear();
        CreateMarker createMarker = new CreateMarker(this, mMap);
        createMarker.execute();

        //Delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myLoop();
            }
        },3000);//การหน่วงเวลา3วินาที
    }

    private void editLatLngOnServer() {
        String urlPHP = "http://swiftcodingthai.com/rus/edit_location_master.php";
        String strID = getIntent().getStringExtra("LoginID");
        Log.d("RusV3", "idUser==>" + strID);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormEncodingBuilder()
                .add("isAdd", "true")
                .add("id", getIntent().getStringExtra("loginID"))
                .add("Lat", Double.toString(latUserADouble))
                .add("Lng", Double.toString(lngARusDouble))
                .build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(urlPHP).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }
}   //Main Class

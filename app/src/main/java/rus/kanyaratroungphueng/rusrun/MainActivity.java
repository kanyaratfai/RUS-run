package rus.kanyaratroungphueng.rusrun;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Explicit
    private EditText useEditText, passwordEditText;
    private ImageView imageView;
    private static final String urlLogo = "http://swiftcodingthai.com/rus/image/logo_rus.png";
    private String userString, passwordString;
    private static final String urlJSON = "http://swiftcodingthai.com/rus/get_user_fai.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind Widget
        useEditText = (EditText) findViewById(R.id.editText4);
        passwordEditText = (EditText) findViewById(R.id.editText5);
        imageView = (ImageView) findViewById(R.id.imageView6);

        //Load Image from Server โหลดรูปภาพจาก Server
        Picasso.with(this).load(urlLogo).into(imageView);

    } //Main Method

    //Create Inner Class การสร้าง Class ภายใน
    private class SynUser extends AsyncTask<Void, Void, String> {

        // Explicit ประกาศตัวแปร ต้องการเชื่อมต่ออีกคลาสหนึ่ง
        private String myJSONString,myUserString, passwordString;
        private Context context; //การกำหนดค่าให้กับตัวแปร
        private boolean statusABoolean = true;
        private String truePassword;

        public SynUser(String myJSONString,
                    String myUserString,
                       String myPasswordString,
                    Context context) {
                this.myJSONString = myJSONString;
                this.myUserString = myUserString;
                this.passwordString = myPasswordString;
                this.context = context;
        }

        @Override   // คือการสืบทอด
        protected String doInBackground(Void... params) {

            try {   //การหยุดการ error

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(myJSONString).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) { //ฟ้องการ error
                Log.d("RusV1", "e doIn ==>" + e.toString());
                return null;
            }


        } // doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("RusV1", "JSON==>" + s);
            try {   //เสี่ยงต่อการ error

                JSONArray jsonArray = new JSONArray(s);

                for (int i=0;i<jsonArray.length();i+=1) {   //จะเริ่มต้นจาก 0 แล้วจะทำการวน

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (myJSONString.equals(jsonObject.getString("User"))) {
                        statusABoolean = false;
                        truePassword = jsonObject.getString("Password");

                    }

                }//for
                if (statusABoolean) { //ทำการแสดงกล่องข้อความถ้าไม่มีชื่อในฐานข้อมูล

                MyAlert myAlert = new MyAlert();
                myAlert.myDialog(context,"ไม่มี User นี้",
                        "ไม่มี"+myUserString+"ในฐานข้อมูลเรา");
                } else if (passwordString.equals(truePassword)) {
                    //Password True
                    Toast.makeText(context,"Welcome",Toast.LENGTH_SHORT).show();

                } else {
                    //Password False
                    MyAlert myAlert = new MyAlert();
                    myAlert.myDialog(context,"Password False",
                            "Please Try Agin Password False");
                }

        } catch (Exception e) {
                Log.d("RusV1", "e onPost ==>" + e.toString());
            }
        } //onPost

    }   //SynUser Class



    public void clickSignIn(View view) {
        userString = useEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();
        //Check Space
        if (userString.equals("") || passwordString.equals("")) {
            //Have Space
            MyAlert myAlert = new MyAlert();
            myAlert.myDialog(this, "Have Space",
                    "Please Fill All Every Blank");
        } else {
            //No Space
            SynUser synUser = new SynUser(urlJSON, userString, passwordString, this);
            synUser.execute();

        } //if
    } //clickSign

    public void clickSignUpMain(View view){
        startActivity(new Intent(MainActivity.this,SignUpActivity.class));
    }
}   //Main Class นี่ คือ คลาสหลัก

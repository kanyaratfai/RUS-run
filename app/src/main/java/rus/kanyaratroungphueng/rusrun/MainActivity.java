package rus.kanyaratroungphueng.rusrun;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

        @Override
        protected String doInBackground(Void... params) {
            return null;
        } // doInBack
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
        } //if
    } //clickSign

    public void clickSignUpMain(View view){
        startActivity(new Intent(MainActivity.this,SignUpActivity.class));
    }
}   //Main Class นี่ คือ คลาสหลัก

package com.fiftheleven.natt.nattrestaurant;

import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    //Explicit
    private UserTABLE objUserTABLE;
    private FoodTABLE objFoodTABLE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Crete & connected Database
        createAndConnectedDatabase();

        //Synchronize JSON to SQLite
        synJSONtoSQLite();

        //Delete Data
        deleteAllData();

        //Tester Add Value
        //testerAddValue();


    }   //onCreate

    private void synJSONtoSQLite() {

        //Change Policy
        StrictMode.ThreadPolicy objThreadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(objThreadPolicy);

        int intTimes = 0;
        while (intTimes <= 1) {

            InputStream objInputStream = null;
            String strJSON = null;
            String strUserURL = "http://swiftcodingthai.com/3sep/php_get_data_natt.php";
            String strFoodURL = "http://swiftcodingthai.com/8aug/php_get_data_food.php";
            HttpPost objHttpPost = null;

            //1. Create InputStream
            try {
                HttpClient objHttpClient = new DefaultHttpClient();

                if (intTimes != 1) {

                    objHttpPost = new HttpPost(strUserURL);

                } else {

                    objHttpPost = new HttpPost(strFoodURL);
                }

                HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
                HttpEntity objHttpEntity = objHttpResponse.getEntity();
                objInputStream = objHttpEntity.getContent();
            } catch (Exception e) {
                Log.d("Rest", "Input ==> " + e.toString());
            }

            //2. Create JSON
            try {
                BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream, "UTF-8"));
                StringBuilder objStringBuilder = new StringBuilder();
                String strLine = null;
                while ((strLine = objBufferedReader.readLine()) != null) {
                    objStringBuilder.append(strLine);
                }
                objInputStream.close();
                strJSON = objStringBuilder.toString();

            } catch (Exception e) {
                Log.d("Rest", "strJSON ==> " + e.toString());
            }


            //3. Update to SQLite
            try {
                final JSONArray objJsonArray = new JSONArray((strJSON));
                for (int i = 0; i < objJsonArray.length(); i++) {
                    JSONObject jsonObject = objJsonArray.getJSONObject(i);

                    if (intTimes != 1) {
                        String strUser = jsonObject.getString("User");
                        String strPassword = jsonObject.getString("Password");
                        String strName = jsonObject.getString("Name");
                        objUserTABLE.addNewUser(strUser, strPassword, strName);

                    } else {
                        String strFood = jsonObject.getString("Food");
                        String strSource = jsonObject.getString("Source");
                        String strPrice = jsonObject.getString("Price");
                        objFoodTABLE.addNewFood(strFood, strSource, strPrice);

                    }
                }


            } catch (Exception e) {
                Log.d("Rest", "Update SQLite ==> " + e.toString());
            }


            intTimes += 1;

        }   //while

    }   //synJSONtoSQLite

    private void deleteAllData() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase("Restaurant.db", MODE_PRIVATE, null);
        objSqLiteDatabase.delete("userTABLE", null, null);
        objSqLiteDatabase.delete("foodTABLE", null, null);

    }

    private void testerAddValue() {
        objUserTABLE.addNewUser("testUser", "12345", "natt");
        objFoodTABLE.addNewFood("ผัดกะเพรา", "testsource", "200");
    }


    private void createAndConnectedDatabase() {
        objUserTABLE = new UserTABLE(this);
        objFoodTABLE = new FoodTABLE(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}   //Main Class


package com.example.pc3.sendnotification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pc3.sendnotification.model.MyList;
import com.example.pc3.sendnotification.model.RequestNotificaton;
import com.example.pc3.sendnotification.model.SendNotificationModel;
import com.example.pc3.sendnotification.retrofit.ApiClient;
import com.example.pc3.sendnotification.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private ArrayList<MyList> tokenList = new ArrayList<>();
    private ArrayList<SendNotificationModel> DataList = new ArrayList<>();
    private int position;
    private SendNotificationModel proData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btn_notify = (Button) findViewById(R.id.btn_notify);
        spinner = (Spinner) findViewById(R.id.country_Name);
        getPromotionalData();
        btn_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationToPatner();
                Toast.makeText(MainActivity.this, "Send Notification", Toast.LENGTH_SHORT).show();
            }
        });
        getTokenFromJson();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                proData = DataList.get(pos);
                btn_notify.setVisibility(View.VISIBLE);
                position = pos;
                //selectedAppPos =0;
                // MyList list = (MyList) adapterView.getSelectedItem();
                // Toast.makeText(MainActivity.this, "Key: " + list.getKey() + ",  APP Name : " + list.getName(), Toast.LENGTH_SHORT).show();
                //String token=   spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();

                //  Toast.makeText(getApplicationContext(), list.getKey(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("data/notification.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String loadJSON1FromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("data/tokens.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void getPromotionalData() {
        String promotional_image;
        String largeIconURI;
        String message;
        String package_name;
        String title;
        try {
            JSONObject reader = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = reader.getJSONArray("data");
            Log.d("m_jArry", "" + m_jArry);
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Log.d("jo_inside", "" + jo_inside);
                promotional_image = jo_inside.getString("promotional_image");
                largeIconURI = jo_inside.getString("largeIconURI");
                message = jo_inside.getString("message");
                package_name = jo_inside.getString("package_name");
                title = jo_inside.getString("title");

                DataList.add(new SendNotificationModel(promotional_image, largeIconURI, message, package_name, title));

            }
            //  spinner.setAdapter(new ArrayAdapter<SendNotificationModel>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, DataList));
            // spinner.setSelection(adapter.getPosition(myItem));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void getTokenFromJson() {
//        JSONObject reader = null;
        try {
            JSONObject reader = new JSONObject(loadJSON1FromAsset());
            JSONArray m_jArry = reader.getJSONArray("list");
            Log.d("m_jArry", "" + m_jArry);
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Log.d("jo_inside", "" + jo_inside);
                String name = jo_inside.getString("name");
                String key = jo_inside.getString("key");
                tokenList.add(new MyList(name, key));
//                m_li = new HashMap<String, String>();
//                m_li.put("name", name);
//                m_li.put("key", key);
//
//                formList.add(m_li);
//HashMap<String, String>

                //  //promotional_image = jo_inside.getString("promotional_image");
//            JSONArray jsonarray = new JSONArray("");
//            for (int i = 0; i < jsonarray.length(); i++) {
//                JSONObject jsonobject = jsonarray.getJSONObject(i);
//                String name = jsonobject.getString("name");
//                String url = jsonobject.getString("key");
            }
            spinner.setAdapter(new ArrayAdapter<MyList>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, tokenList));
            // spinner.setSelection(adapter.getPosition(myItem));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void sendNotificationToPatner() {
        RequestNotificaton requestNotificaton = new RequestNotificaton();
        requestNotificaton.setSendNotificationModel(proData);
        //token is id , whom you want to send notification ,
        requestNotificaton.setToken("/topics/all");
        tokenList.remove(position);
        if (!tokenList.isEmpty()) {
            for (int i = 0; i < tokenList.size(); i++) {
                ApiInterface apiService = ApiClient.getClient(tokenList.get(i).getKey()).create(ApiInterface.class);
                retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(requestNotificaton);

                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        Log.d("ok", "done");
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        Log.d("fail", "not Done");

                    }
                });
            }
//                responseBodyCall2.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        Log.d("token","done");
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        Log.d("token","failed");
//
//                    }
//                });
//          //  }


//                String  promotional_image = "https://mobologics.files.wordpress.com/2018/09/feature-call-recoder.png";
//                String largeIconURI = "https://lh3.googleusercontent.com/GIysZQR53Aaq8rPwjPNutaTNmITyLSooi2qdxj2LfB3kDi2bHvtRe6mCyqR_HHTIKgo=s180-rw";
//                String message = "Record your All Calls Now";
//                String package_name = "https://play.google.com/store/apps/details?id=com.mobologics.callrecorderautomatic";
//                String title = "Automatic Call Recorder";

        }
    }
}


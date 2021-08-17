package com.codewithshreya.flobizassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    List<Recycler_List> models;
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));// *that how i made background transparent so that **Only** rounded border can be seen*
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        models = new ArrayList<>();

        String url="https://restcountries.eu/rest/v2/all";
        Log.e("URL",url);

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Retrieving Data From API.. Please Wait !!");

        pd.show();

        StringRequest stringRequest= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0;i<55;i++){
                        JSONObject obj = jsonArray.getJSONObject(i);

                        if(!obj.getString("name").startsWith("B")){
                            String name = obj.getString("name");
                            String url = obj.getString("flag");

                            models.add(new Recycler_List(url,name));
                            Log.d("rdd", name+"     "+ url);
                        }



                    }

                    adapter = new RecyclerAdapter(models);
                    rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    rv.setAdapter(adapter);

                    //Log.d("rrs",response);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("rrs",e.getMessage());
                    Toast.makeText(MainActivity.this,"e"+e.toString(),Toast.LENGTH_LONG).show();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("rrs", error.toString());
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject data = new JSONObject(responseBody);
                            JSONObject errors = data.getJSONObject("error");
                            Toast.makeText(getApplicationContext(), String.valueOf(errors), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                        } catch (UnsupportedEncodingException errorr) {
                        }
                    }
                }
        );

        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        int a = sh.getInt("boole", 1);

        if (a==1){
            findViewById(R.id.rl).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.rl).setVisibility(View.GONE);
        }

        pd.dismiss();

        ImageView imgview = findViewById(R.id.cancel_btn);

        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.rl).setVisibility(View.GONE);

                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);

                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putInt("boole", 0);
                myEdit.commit();
            }
        });

    }
}
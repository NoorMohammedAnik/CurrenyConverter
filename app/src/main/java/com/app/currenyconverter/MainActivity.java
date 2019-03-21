package com.app.currenyconverter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    //objects declaration
    MaterialSpinner msCurrencyFrom, msCurrencyTo;
    Button btnConvert;
    EditText etxtAmount;
    TextView txtResult;

    String currencyFrom, currencyTo, standard_rate;
    List<String> country_list = new ArrayList<>();

    String api = "https://jsonvat.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Linked to java object to ui
        msCurrencyFrom = findViewById(R.id.txt_from);
        msCurrencyTo = findViewById(R.id.txt_to);
        btnConvert = findViewById(R.id.btn_convert);
        etxtAmount = findViewById(R.id.etxt_amount);
        txtResult = findViewById(R.id.txt_result);


        //checking is network is available or not
        if (isNetworkConnected()) {
            getCountry();
        } else {

            Toast.makeText(this, "No Internet Connection! Please Connect to Internet", Toast.LENGTH_LONG).show();
        }


        //Select country to getRateCurrencyFrom from
        msCurrencyFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                msCurrencyFrom.setItems(country_list);
                msCurrencyFrom.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {


                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        Toast.makeText(MainActivity.this, "Clicked " + item, Toast.LENGTH_SHORT).show();

                        getRateCurrencyFrom(item);

                    }
                });

            }
        });


        msCurrencyTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                msCurrencyTo.setItems(country_list);
                msCurrencyTo.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {


                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        Toast.makeText(MainActivity.this, "Clicked " + item, Toast.LENGTH_SHORT).show();


                        getCurrencyRateTo(item);

                    }
                });


            }
        });


        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = etxtAmount.getText().toString();


                if (msCurrencyFrom.getText().toString().isEmpty()) {
                    msCurrencyFrom.setError("Select");
                } else if (msCurrencyTo.getText().toString().isEmpty()) {
                    msCurrencyTo.setError("Select");
                } else if (amount.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                } else {
                    double value = Double.parseDouble(amount);
                    double f = Double.parseDouble(currencyFrom);
                    double t = Double.parseDouble(currencyTo);

                    double total = (f / t) * value;

                    txtResult.setText("" + total);

                }
            }
        });


    }


    //Method for get all country name from api
    private void getCountry() {


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                api, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    JSONArray array = response.getJSONArray("rates");

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jsonobject = array.getJSONObject(i);
                        String name = jsonobject.getString("name");
                        country_list.add(name);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        // Adding request to request queue
        queue.add(jsonObjReq);

    }



    //Method for get standard rate for selected country
    private void getRateCurrencyFrom(final String currency) {

        final String getCurrency = currency;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                api, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Tag", response.toString());

                try {

                    JSONArray array = response.getJSONArray("rates");
                    Log.d("array", array.toString());

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jsonobject = array.getJSONObject(i);
                        String name = jsonobject.getString("name");

                        if (getCurrency.equals(name)) {
                            JSONArray array2 = jsonobject.getJSONArray("periods");

                            for (int j = 0; j < 1; j++) {
                                JSONObject jsonobject2 = array2.getJSONObject(j);

                                JSONObject jsonobject3 = jsonobject2.getJSONObject("rates");

                                standard_rate = jsonobject3.getString("standard");

                                Log.d("standard", standard_rate);

                                currencyFrom = standard_rate;


                            }


                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());


            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        // Adding request to request queue
        queue.add(jsonObjReq);


    }



    //Method for get standard rate for selected country
    private void getCurrencyRateTo(final String currency) {

        final String getCurrency = currency;


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                api, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("Tag", response.toString());

                try {

                    JSONArray array = response.getJSONArray("rates");
                    Log.d("array", array.toString());

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jsonobject = array.getJSONObject(i);
                        String name = jsonobject.getString("name");

                        if (getCurrency.equals(name)) {
                            JSONArray array2 = jsonobject.getJSONArray("periods");

                            for (int j = 0; j < 1; j++) {
                                JSONObject jsonobject2 = array2.getJSONObject(j);
                                JSONObject jsonobject3 = jsonobject2.getJSONObject("rates");

                                standard_rate = jsonobject3.getString("standard");
                                Log.d("standard", standard_rate);
                                currencyTo = standard_rate;

                            }

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "No Internet",
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());


            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        // Adding request to request queue
        queue.add(jsonObjReq);


    }


    //for checking Internet connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}

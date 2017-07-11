package com.example.android.weather;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.android.weather.R.id.date;
import static com.example.android.weather.R.id.pressure;
import static com.example.android.weather.R.id.temp;
import static com.example.android.weather.R.id.weather;
import static java.lang.Long.getLong;

public class Main2Activity extends AppCompatActivity {

    String location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        location = intent.getStringExtra("Location");
        WeatherAsyncTask task = new WeatherAsyncTask();
        task.urlApi ="http://api.openweathermap.org/data/2.5/forecast?q="+location+"&appid=d08bef5ef5ca9b45f1bfd5a0553c5000";
        task.execute();
    }

    public void updateUI(ArrayList<Weather> weathers) {
        TextView textView = (TextView) findViewById(R.id.location);
        textView.setText(location.substring(0,1).toUpperCase()+location.substring(1));
        WeatherAdapter weatherAdapter = new WeatherAdapter(getApplicationContext(), weathers);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(weatherAdapter);
    }

    private class WeatherAsyncTask extends AsyncTask<URL, Void,ArrayList<Weather>> {

        public String urlApi;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView error = (TextView) findViewById(R.id.error2);
            error.setVisibility(View.GONE);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<Weather> doInBackground(URL... urls) {
            URL url = createUrl(urlApi);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.v("Main","IOException");
            }

            ArrayList<Weather> weather = extractFeatureFromJson(jsonResponse);

            return weather;
        }

        @Override
        protected void onPostExecute(ArrayList<Weather> weather) {
            super.onPostExecute(weather);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
            progressBar.setVisibility(View.GONE);
            if(weather!=null) {

                updateUI(weather);

            }else {
                TextView error = (TextView) findViewById(R.id.error2);
                error.setText("No Results Found!");
                error.setVisibility(View.VISIBLE);
            }

        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e("main", "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                if(url != null) {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                        inputStream = urlConnection.getInputStream();
                        jsonResponse = readFromStream(inputStream);
                    }
                    else {
                        Log.e("Main activity", "Status code:" + urlConnection.getResponseCode());
                    }
                }
            } catch (IOException e) {


                Log.e("Main Activity","IOException: ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {

                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private ArrayList<Weather> extractFeatureFromJson(String weatherJSON) {
            try {
                JSONObject baseJsonResponse = new JSONObject(weatherJSON);
                JSONArray list = baseJsonResponse.getJSONArray("list");
                ArrayList<Weather> weathers = new ArrayList<>();
                for(int i=0;i<list.length();i+=8) {
                    JSONObject object = list.getJSONObject(i);
                    Long date = object.getLong("dt");
                    JSONArray weatherArray = object.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    String desc = weather.getString("description");
                    String imgcode = weather.getString("icon");
                    String image = "http://openweathermap.org/img/w/" + imgcode + ".png";
                    JSONObject main = object.getJSONObject("main");
                    Double temp = main.getDouble("temp") - 273.00;
                    int pressure = main.getInt("pressure");
                    int humidity = main.getInt("humidity");
                    JSONObject wind = object.getJSONObject("wind");
                    Double speed = wind.getDouble("speed");
                    weathers.add(new Weather(date,"",desc,temp,humidity,speed,0,pressure,image));
                }
                return weathers;
            } catch (JSONException e) {
                Log.e("main", "Problem parsing the JSON results", e);
            }
            return null;
        }
    }
}


package com.example.android.weather;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.button;
import static android.R.attr.visibility;
import static android.transition.Fade.IN;
import static com.example.android.weather.R.id.date;
import static com.example.android.weather.R.id.desc;
import static com.example.android.weather.R.id.error;
import static com.example.android.weather.R.id.weather;

public class MainActivity extends AppCompatActivity {

    String Api ="http://api.openweathermap.org/data/2.5/weather?q=";
    int net =0;
    DataBaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBaseHandler(this);
        Button button = (Button) findViewById(R.id.search_btn);
        final TextView error = (TextView) findViewById(R.id.error);

        if(!isNetworkAvailable()){
            error.setText("Please Connect to the Internet for accurate data");
            error.setVisibility(View.VISIBLE);
            net = 0;

        }
        else {
            error.setVisibility(View.GONE);
            net=1;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        EditText editText =(EditText) findViewById(R.id.enter_city);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    search();
                    handled=true;
                }
                return handled;
            }
        });
    }

    private void search(){
        EditText editText = (EditText) findViewById(R.id.enter_city);
        TextView error = (TextView) findViewById(R.id.error);
        String name = editText.getText().toString().toLowerCase();
        if(net==1) {
            WeatherAsyncTask task = new WeatherAsyncTask();
            task.urlApi = Api + name + "&appid=d08bef5ef5ca9b45f1bfd5a0553c5000";
            task.execute();
        }else {
            Weather weather = db.select(name);
            if(weather!=null) {
                error.setVisibility(View.GONE);
                updateUI(weather);
                LinearLayout info = (LinearLayout) findViewById(R.id.weather);
                info.setVisibility(View.VISIBLE);

            }else {

                error.setText("Not in Database");
                error.setVisibility(View.VISIBLE);
                LinearLayout info = (LinearLayout) findViewById(R.id.weather);
                info.setVisibility(View.GONE);
            }
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void updateUI(final Weather weather){
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(weather.getMname().substring(0,1).toUpperCase()+weather.getMname().substring(1));
        if(net==1) {
            Long date = weather.getMtime() * 1000;
            Date dt = new java.util.Date(date);
            String formatted_date = new SimpleDateFormat("EEEE, h:mm a").format(dt);
            TextView time = (TextView) findViewById(R.id.date);
            time.setText(formatted_date);
        }else {
            TextView time = (TextView) findViewById(R.id.date);
            time.setText(weather.getMdate());
        }
        TextView desc = (TextView) findViewById(R.id.desc);
        desc.setText(weather.getMdesc());
        TextView temp = (TextView) findViewById(R.id.act_temp);
        String pattern = "###.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String formatted_temp = decimalFormat.format(weather.getMtemp());
        temp.setText(formatted_temp+" \u2103");
        TextView humidity = (TextView) findViewById(R.id.act_humidity);
        humidity.setText(weather.getMhumidity()+"%");
        TextView pressure = (TextView) findViewById(R.id.act_pressure);
        pressure.setText(weather.getMvisibility()+" hPa");
        TextView speed = (TextView) findViewById(R.id.act_windspeed);
        speed.setText(weather.getMwindSpeed()+" m/s");
        TextView visibilty = (TextView) findViewById(R.id.act_visibility);
        visibilty.setText(weather.getMvisibility()+ " m");
        ImageView image = (ImageView) findViewById(R.id.img);
        if(net==1) {
            Picasso.with(getApplicationContext()).load(weather.getMimage()).resize(60, 60).into(image);
        }else
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(weather.getMimageByte(), 0, weather.getMimageByte().length);
            image.setImageBitmap(bitmap);
        }
        if(net==1) {
            Button button = (Button) findViewById(R.id.forecast_btn);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    intent.putExtra("Location", weather.getMname().toLowerCase());
                    startActivity(intent);
                }

            });
        }else {
            Button button = (Button) findViewById(R.id.forecast_btn);
            button.setVisibility(View.GONE);
        }


    }

    private class WeatherAsyncTask extends AsyncTask<URL, Void,Weather > {

        public String urlApi;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView error = (TextView) findViewById(R.id.error);
            error.setVisibility(View.GONE);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            progressBar.setVisibility(View.VISIBLE);
            LinearLayout info = (LinearLayout) findViewById(weather);
            info.setVisibility(View.GONE);
        }

        @Override
        protected Weather doInBackground(URL... urls) {
            URL url = createUrl(urlApi);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.v("Main","IOException");
            }

            Weather weather = extractFeatureFromJson(jsonResponse);
            if(weather!=null){
                db.addWeather(weather);
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            progressBar.setVisibility(View.GONE);
            if(weather!=null) {

                updateUI(weather);
                LinearLayout info = (LinearLayout) findViewById(R.id.weather);
                info.setVisibility(View.VISIBLE);
            }else {
                TextView error = (TextView) findViewById(R.id.error);
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

        private Weather extractFeatureFromJson(String weatherJSON) {
            try {
                JSONObject baseJsonResponse = new JSONObject(weatherJSON);
                String name = baseJsonResponse.getString("name").toLowerCase();
                Long date = baseJsonResponse.getLong("dt");
                JSONArray weatherArray = baseJsonResponse.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                String desc = weather.getString("description");
                String imgcode = weather.getString("icon");
                String image ="http://openweathermap.org/img/w/"+imgcode+".png";
                int visibility =  baseJsonResponse.getInt("visibility");
                JSONObject main = baseJsonResponse.getJSONObject("main");
                Double temp = main.getDouble("temp")-273.00;
                int pressure = main.getInt("pressure");
                int humidity = main.getInt("humidity");
                JSONObject wind = baseJsonResponse.getJSONObject("wind");
                Double speed = wind.getDouble("speed");
                byte[] image_byte=null;
                try {
                    URL url = new URL(image);
                    Bitmap imageBit = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBit.compress(Bitmap.CompressFormat.PNG, 0, stream);
                    image_byte = stream.toByteArray();
                } catch(IOException e) {
                    System.out.println(e);
                }
                Weather weather1 = new Weather(date,name,desc,temp,humidity,speed,visibility,pressure,image);
                weather1.setImageByte(image_byte);
                return weather1;
            } catch (JSONException e) {
                Log.e("main", "Problem parsing the JSON results", e);
            }
            return null;
        }
    }
}

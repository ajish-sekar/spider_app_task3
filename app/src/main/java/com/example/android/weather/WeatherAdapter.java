package com.example.android.weather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.os.Build.VERSION_CODES.M;
import static com.example.android.weather.R.id.weather;

/**
 * Created by Ajish on 10-07-2017.
 */

public class WeatherAdapter extends ArrayAdapter<Weather> {
    public WeatherAdapter (Context context, ArrayList<Weather> weatherArrayList){
        super(context,0,weatherArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        final Weather currentweather = getItem(position);

        Long date = currentweather.getMtime()*1000;
        Date dt = new java.util.Date(date);
        String formatted_date = new SimpleDateFormat("EEEE, h:mm a").format(dt);
        TextView time = (TextView) listItemView.findViewById(R.id.date_forecast);
        time.setText(formatted_date);
        TextView desc = (TextView) listItemView.findViewById(R.id.desc_forecast);
        desc.setText(currentweather.getMdesc());
        TextView temp = (TextView) listItemView.findViewById(R.id.act_temp_forecast);
        String pattern = "###.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String formatted_temp = decimalFormat.format(currentweather.getMtemp());
        temp.setText(formatted_temp+" \u2103");
        TextView humidity = (TextView) listItemView.findViewById(R.id.act_humidity_forecast);
        humidity.setText(currentweather.getMhumidity()+"%");
        TextView speed = (TextView) listItemView.findViewById(R.id.act_windspeed_forecast);
        speed.setText(currentweather.getMwindSpeed()+" m/s");

        ImageView image = (ImageView) listItemView.findViewById(R.id.img_forecast);
        Picasso.with(getContext()).load(currentweather.getMimage()).resize(60,60).into(image);





        return listItemView;

    }
}

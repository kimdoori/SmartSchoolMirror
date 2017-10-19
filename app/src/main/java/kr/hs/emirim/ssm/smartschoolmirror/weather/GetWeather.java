package kr.hs.emirim.ssm.smartschoolmirror.weather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by doori on 2017-10-17.
 */
public class GetWeather {

    private static double temp;


    public static double getTemp() {
        return temp;
    }

    public static void getWeather(final View view, final TextView weather_temp_text){

        String lat= "37.466375";
        String lon = "126.932903";

        Retrofit client = new Retrofit.Builder().baseUrl("http://api.openweathermap.org").addConverterFactory(GsonConverterFactory.create()).build();

        ApiInterface service = client.create(ApiInterface.class);
        Call<MainRepo> call = service.repo("f1a57edbaf846b218ffaaaf2af4cb08f", Double.valueOf(lat), Double.valueOf(lon),"metric");
        call.enqueue(new Callback<MainRepo>() {
            @Override
            public void onResponse(Response<MainRepo> response) {
                if (response.isSuccess()) {
                    MainRepo repo = response.body();

                    weather_temp_text.setText(String.valueOf(repo.getMain().getTemp())+"°C");
                    temp=repo.getMain().getTemp();
                } else {

                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    public static void getWeatherIcon(final View view, final ImageView weather_image){

        String lat= "37.466375";
        String lon = "126.932903";

        Retrofit client_weather = new Retrofit.Builder().baseUrl("http://api.openweathermap.org").addConverterFactory(GsonConverterFactory.create()).build();

        ApiInterface service_weather = client_weather.create(ApiInterface.class);
        Call <WeatherRepo> call_e = service_weather.repo("f1a57edbaf846b218ffaaaf2af4cb08f", Double.valueOf(lat), Double.valueOf(lon));
        call_e.enqueue(new Callback<WeatherRepo>() {
            @Override
            public void onResponse(Response<WeatherRepo> response_weather) {


                if (response_weather.isSuccess()) {
                    WeatherRepo repo_weather = response_weather.body();
                    Log.e("날씨","성공");
                    String icon=repo_weather.getWeather().get(0).getIcon();
                    Log.e("아이콘",icon);
                    new LoadWeatherIcon( ).execute( weather_image, "http://openweathermap.org/img/w/"+icon+".png" );

                } else {

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("날씨","실패");

            }
        });
    }

    public static class LoadWeatherIcon extends AsyncTask< Object, Void, Bitmap> {

        ImageView ivPreview = null;

        @Override

        protected Bitmap doInBackground( Object... params ) {

            this.ivPreview = (ImageView) params[0];

            String url = (String) params[1];

            return loadBitmap( url );

        }

        @Override

        protected void onPostExecute( Bitmap result ) {

            super.onPostExecute( result );

            ivPreview.setImageBitmap( result );

        }

    }
    public static Bitmap loadBitmap(String url) {

        URL newurl = null;

        Bitmap bitmap = null;

        try {

            newurl = new URL( url );

            bitmap = BitmapFactory.decodeStream( newurl.openConnection( ).getInputStream( ) );

        } catch ( MalformedURLException e ) {

            e.printStackTrace( );

        } catch ( IOException e ) {

            e.printStackTrace( );

        }

        return bitmap;

    }

}
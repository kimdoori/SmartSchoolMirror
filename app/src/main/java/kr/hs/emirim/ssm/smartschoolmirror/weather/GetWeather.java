package kr.hs.emirim.ssm.smartschoolmirror.weather;

import android.view.View;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by doori on 2017-10-17.
 */

public class GetWeather {


    public static void getWeather(final View view, final TextView weather_temp_text){

        String lat= "37.466375";
        String lot = "126.932903";

        Retrofit client = new Retrofit.Builder().baseUrl("http://api.openweathermap.org").addConverterFactory(GsonConverterFactory.create()).build();

        ApiInterface service = client.create(ApiInterface.class);
        Call<Repo> call = service.repo("f1a57edbaf846b218ffaaaf2af4cb08f", Double.valueOf(lat), Double.valueOf(lot),"metric");
        call.enqueue(new Callback<Repo>() {
            @Override
            public void onResponse(Response<Repo> response) {
                if (response.isSuccess()) {
                    Repo repo = response.body();

                    weather_temp_text.setText(String.valueOf(repo.getMain().getTemp())+"°C");
                } else {

                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

}

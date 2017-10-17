package kr.hs.emirim.ssm.smartschoolmirror;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import kr.hs.emirim.ssm.smartschoolmirror.weather.ApiInterface;
import kr.hs.emirim.ssm.smartschoolmirror.weather.Repo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends Activity {

    private TimerTask time_update_second;
    private TimerTask weather_update_second;

    private TextView date_text;
    private TextView time_text;
    private TextView weather_temp_text;


    private final Handler timer_handler = new Handler();
    private final Handler weather_handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        date_text = (TextView) findViewById(R.id.time_text);
        time_text = (TextView) findViewById(R.id.date_text);
        weather_temp_text=(TextView) findViewById(R.id.weather_text);

        ButterKnife.bind(this);

        time_updater_Start();
        weather_updater_Start();
    }






    //시간업데이트
    public void time_updater_Start() {

        time_update_second = new TimerTask() {
            @Override
            public void run() {
                time_Update();
            }
        };
        Timer timer = new Timer();
        timer.schedule(time_update_second, 0, 1000);
    }

    protected void time_Update() {
        Runnable updater = new Runnable() {
            public void run() {
                String format_time = new String("HH : mm");
                String format_date = new String("MM월  dd일  E요일");

                SimpleDateFormat tf = new SimpleDateFormat(format_time, Locale.KOREA);
                date_text.setText(tf.format(new Date()));

                SimpleDateFormat df = new SimpleDateFormat(format_date, Locale.KOREA);
                time_text.setText(df.format(new Date()));
            }
        };
        timer_handler.post(updater);
    }


    //날씨 업데이트
    public void weather_updater_Start() {

        weather_update_second = new TimerTask() {
            @Override
            public void run() {
              weather_Update();

            }
        };
        Timer timer = new Timer();
        timer.schedule(weather_update_second, 0, 5000);
    }

    protected void weather_Update() {
        Runnable updater = new Runnable() {
            public void run() {
                getWeather(getCurrentFocus());
            }
        };
        weather_handler.post(updater);
    }

    public void getWeather(View view){

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





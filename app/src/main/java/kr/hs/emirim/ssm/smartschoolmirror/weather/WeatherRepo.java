package kr.hs.emirim.ssm.smartschoolmirror.weather;

import java.util.ArrayList;

/**
 * Created by doori on 2017-10-18.
 */

public class WeatherRepo {
    ArrayList<Weather> weather=new ArrayList<>();

    public ArrayList<Weather> getWeather() {
        return weather;
    }
}

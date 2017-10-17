package kr.hs.emirim.ssm.smartschoolmirror.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    //@GET("/data/2.5/weather?lat={lat}&lon={lon}&appid=684b98e21b4f35b7d52abe9ff6279349")
    //Call<Repo> repo(@Path("lat") String lat, @Path("lon") String lon);
    @GET("/data/2.5/weather")
    Call<MainRepo> repo(@Query("appid") String appid, @Query("lat") double lat, @Query("lon") double lon, @Query("units") String metric);

    @GET("/data/2.5/weather")
    Call<WeatherRepo> repo(@Query("appid") String appid, @Query("lat") double lat, @Query("lon") double lon);

}

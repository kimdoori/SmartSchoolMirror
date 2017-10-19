package kr.hs.emirim.ssm.smartschoolmirror;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import kr.hs.emirim.ssm.smartschoolmirror.date.GetDate;
import kr.hs.emirim.ssm.smartschoolmirror.model.StudentInfo;
import kr.hs.emirim.ssm.smartschoolmirror.schoolInfo.GetSchoolInfo;
import kr.hs.emirim.ssm.smartschoolmirror.schoolInfo.School;
import kr.hs.emirim.ssm.smartschoolmirror.schoolInfo.SchoolException;
import kr.hs.emirim.ssm.smartschoolmirror.weather.GetWeather;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends Activity implements TtsSpeaker.Listener, PocketSphinx.Listener{
    public final static String TAG = "스스미 - >>>> Main - >>>>>";

    private String textToSpeak;


    private TimerTask time_update_second;
    private TimerTask weather_update_second;

    private TextView date_text;
    private TextView time_text;
    private TextView weather_temp_text;
    private TextView school_food_text;
    private TextView school_schedule_text;
    private TextView school_food_title;
    private TextView school_schedule_title;
    private ImageView weather_image;

    private final Handler timer_handler = new Handler();
    private final Handler weather_handler = new Handler();

    //음성
    private enum State {
        INITALIZING,
        LISTENING_TO_KEYPHRASE,
        CONFIRMING_KEYPHRASE,
        LISTENING_TO_ACTION,
        CONFIRMING_ACTION,
        TIMEOUT
    }

    private TtsSpeaker tts;
    private PocketSphinx pocketsphinx;
    private State state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TtsSpeaker(this, this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("NotoSansCJKkr-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        date_text = (TextView) findViewById(R.id.time_text);
        time_text = (TextView) findViewById(R.id.date_text);
        weather_temp_text=(TextView) findViewById(R.id.weather_text);
        school_food_text=(TextView) findViewById(R.id.school_food_text);
        school_schedule_text=(TextView) findViewById(R.id.school_schedule_text);
        school_food_title=(TextView) findViewById(R.id.school_food_title);
        school_schedule_title=(TextView) findViewById(R.id.school_schedule_title);
        weather_image=(ImageView) findViewById(R.id.weather_image);


        ButterKnife.bind(this);

        time_updater_Start();
        weather_updater_Start();


        getSchoolInfo();

        getData();

    }


    //급식과 일정 업데이트
    public void getSchoolInfo(){
        School api_food = new School(School.Type.HIGH, School.Region.SEOUL, "B100000439");
        School api_schedule = new School(School.Type.HIGH, School.Region.SEOUL, "B100000639");

        try {
            api_food.getMonthlyMenu(school_food_title,school_food_text,GetDate.getYear(), GetDate.getMonth());
            api_schedule.getMonthlySchedule(school_schedule_title,school_schedule_text,GetDate.getYear(), GetDate.getMonth());

        } catch (SchoolException e) {
            Log.e("에러",e.toString());
        }
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
                GetDate.updateDate(date_text,time_text);
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
        timer.schedule(weather_update_second, 0, 1800000);//1800000 //30분마다 갱신
    }



    protected void weather_Update() {
        Runnable updater = new Runnable() {
            public void run() {
                GetWeather.getWeather(getCurrentFocus(),weather_temp_text);
                GetWeather.getWeatherIcon(getCurrentFocus(),weather_image);

            }
        };
        weather_handler.post(updater);
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void getData()
    {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference sInfoReference = mDatabase.getReference("SmartSchoolMirror/Call_Student/BasicInfo");

        ValueEventListener sInfoListener = new ValueEventListener()
        {
            // 파베 데이터가 바뀔 때 마다 실행 (즉, 웹사이트에서 바꿀 때마다~)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                StudentInfo sInfo = dataSnapshot.getValue(StudentInfo.class);

                textToSpeak =  sInfo.grade + "학년 " +  sInfo.ban + "반 " + sInfo.student_num + "번 " +
                        sInfo.student_name + "학생 지금 즉시 " + sInfo.location + "으로 내려오세요.";

                tts.say(textToSpeak);
                //Log 찍어보기
                Log.d(TAG, sInfo.grade + " / " + sInfo.ban + " / " + sInfo.student_num);
                Log.d(TAG, sInfo.student_name + " / " + sInfo.location);
            } // end of onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            } // end of onCancelled
        };

        sInfoReference.addValueEventListener(sInfoListener);
    } // end of getData


//지원


    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.onDestroy();
        pocketsphinx.onDestroy();
    }

    @Override
    public void onTtsInitialized() {
        // There's no runtime permissions on Android Things.
        // Otherwise, we would first have to ask for the Manifest.permission.RECORD_AUDIO
        pocketsphinx = new PocketSphinx(this, this);
    }

    @Override
    public void onTtsSpoken() {
        switch (state) {
            case INITALIZING:
            case CONFIRMING_ACTION:
            case TIMEOUT:
                state = State.LISTENING_TO_KEYPHRASE;
                pocketsphinx.startListeningToActivationPhrase();
                break;
            case CONFIRMING_KEYPHRASE:
                state = State.LISTENING_TO_ACTION;
                pocketsphinx.startListeningToAction();
                break;
        }
    }

    @Override
    public void onSpeechRecognizerReady() {
        state = State.INITALIZING;
        Log.e("ready", String.valueOf(state));
        tts.say("준비 완료");
    }

    @Override
    public void onActivationPhraseDetected(String KEYPHRASE) {
        Log.e("state", String.valueOf(state));

        if(KEYPHRASE.equals(PocketSphinx.ACTIVATION_KEYPHRASE)) {
            state = State.CONFIRMING_KEYPHRASE;
            Log.e("nowstate", String.valueOf(state));
            tts.say("말씀하세요.");
        }
        else if(state.equals(State.LISTENING_TO_ACTION)){
            state = State.CONFIRMING_ACTION;
            Log.e("nowstate",String.valueOf(state));

            String answer="";

            if (KEYPHRASE.equals(PocketSphinx.ACTIVATION_KEYPHRASE_time)) {
                answer = "지금은 " + GetDate.getHour()+"시"+GetDate.getMinute()+"분 입니다.";
            } else if (KEYPHRASE.equals(PocketSphinx.ACTIVATION_KEYPHRASE_date)) {
                answer = "오늘은 " + GetDate.getMonth()+"월"+GetDate.getDate()+"일 입니다.";
            } else if (KEYPHRASE.equals(PocketSphinx.ACTIVATION_KEYPHRASE_weather)) {
                answer =  "지금은 " + GetWeather.getTemp()+" 도 입니다";
            }else if (KEYPHRASE.equals(PocketSphinx.ACTIVATION_KEYPHRASE_food)) {
                answer = "급식 메뉴는 "+ GetSchoolInfo.getFood()+"입니다.";
            }else if (KEYPHRASE.equals(PocketSphinx.ACTIVATION_KEYPHRASE_schedule)) {
                answer = "오늘의 일정은 "+ GetSchoolInfo.getSchedule()+"입니다.";
            }else if (KEYPHRASE.equals(PocketSphinx.ACTIVATION_KEYPHRASE_mirror)) {
                answer = "거울";
            }
            else {
                answer = KEYPHRASE ;
            }
            Log.e("answer",answer);
            tts.say(answer);
        }else{
            tts.say("미러를 다시 불러주세요");
        }

        //DateFormat dateFormat = new SimpleDateFormat("HH시 mm분", Locale.KOREAN);

        //tts.say("지금은 " + dateFormat.format(new Date()));
    }

    @Override
    public void onTimeout() {
        state = State.TIMEOUT;
        tts.say("시간이 초과되었습니다.");
    }

}

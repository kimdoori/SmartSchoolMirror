package kr.hs.emirim.ssm.smartschoolmirror.schoolInfo;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import kr.hs.emirim.ssm.smartschoolmirror.date.GetDate;

/**
 * Created by doori on 2017-10-18.
 */

public class GetSchoolInfo extends AsyncTask<Void, Void, String> {


    private static String mfood;
    private static String mSchedule;

    public static String getFood() {
        return mfood;
    }

    public static String getSchedule() {
        return mSchedule;
    }



    TextView title_text;
    TextView info_text;
    String getKind;
    URL url;
    String readAfter;
    String readBefore;

    public GetSchoolInfo(TextView title_text,TextView info_text,String getKind, URL url, String readAfter, String readBefore) {
        this.title_text=title_text;
        this.info_text=info_text;
        this.getKind=getKind;
        this.url = url;
        this.readAfter = readAfter;
        this.readBefore = readBefore;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            StringBuffer buffer = new StringBuffer();
            String inputLine;

            boolean reading = false;

            while ((inputLine = reader.readLine()) != null) {
                if (reading) {
                    if (inputLine.contains(readBefore))
                        break;
                    buffer.append(inputLine);
                } else {
                    if (inputLine.contains(readAfter))
                        reading = true;
                }
            }
            reader.close();
            String content = buffer.toString();

            try {
                if(getKind.equals("getMonthlySchedule")) {
                    List<SchoolSchedule> schedule = SchoolScheduleParser.parse(content);
                    updateText(true,String.valueOf(schedule.get(GetDate.getDate()-1)));
                    mSchedule=String.valueOf(schedule.get(GetDate.getDate()-1));

                }
                else {
                    List<SchoolMenu> menu = SchoolMenuParser.parse(content);
                    if(GetDate.getHour()<8){
                        updateText(false,"아침");
                        updateText(true,menu.get(GetDate.getDate()-1).breakfast);


                    }else if(GetDate.getHour()<13){
                        updateText(false,"점심");
                        updateText(true,menu.get(GetDate.getDate()-1).lunch);

                    }else{
                        updateText(false,"저녁");
                        updateText(true,menu.get(GetDate.getDate()-1).dinner);

                    }




                }

            } catch (SchoolException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            Log.e("교육청 서버에 접속하지 못하였습니다.","");
        }

        return null;
    }

    Handler handler = new Handler();

    public void updateText(final boolean update_content , final String result){
        new Thread(new Runnable() {
            @Override public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(update_content){
                            info_text.setText(toKorean(result));
                            mfood=result;
                        }
                        else if(!getKind.equals("getMonthlySchedule")){
                            title_text.setText("오늘의 "+result);
                        }

                    }
                });
            }
        }).start();
    }

    public static String toKorean(String str){
        str=str.replaceAll(System.getProperty("line.separator"),"ㅡ");
        str=str.replaceAll("[^\\uAC00-\\uD7AF\\u1100-\\u11FF\\u3130-\\u318F]","");
        return  str.replaceAll("ㅡ","\n");
    }
}


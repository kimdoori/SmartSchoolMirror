package kr.hs.emirim.ssm.smartschoolmirror.schoolInfo;

public class SchoolMenu {
    /**
     * 조식
     */
    public String breakfast;

    /**
     * 중식
     */
    public String lunch;

    /**
     * 석식
     */
    public String dinner;

    public SchoolMenu() {
        breakfast = lunch = dinner = "급식이 없습니다";
    }

    @Override
    public String toString() {
        return "[아침]\n" + breakfast + "\n" + "[점심]\n" + lunch + "\n" + "[저녁]\n" + dinner;
    }
}
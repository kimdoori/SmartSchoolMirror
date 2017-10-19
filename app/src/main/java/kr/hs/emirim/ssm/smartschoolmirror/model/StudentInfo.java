package kr.hs.emirim.ssm.smartschoolmirror.model;

public class StudentInfo
{
    //멤버 변수 선언
    public String grade;
    public String ban;
    public String location;
    public String student_name;
    public String student_num;

    //기본 생성자
    public StudentInfo(){ }

    //parameter 여러개인 생성자
    public StudentInfo( String ban, String grade, String location, String student_name, String student_num)
    {
        this.grade = grade;
        this.ban = ban;
        this.location = location;
        this.student_name = student_name;
        this.student_num = student_num;
    } // end of constructor

    // 출력 용(Test)
    public String toString()
    {
        return grade + " / " + ban + " / " + location + " / " + student_name + " / " + student_num + " / " ;
    }

} // end of StudentInfo
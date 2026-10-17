
public class Student {
    private String name;
    private String studentId;
    private double allowance;

    public Student(String name, String studentId, double allowance) {
        this.name = name;
        this.studentId = studentId;
        this.allowance = allowance;
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }

    public double getAllowance() {
        return allowance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setAllowance(double allowance) {
        this.allowance = allowance;
    }
}

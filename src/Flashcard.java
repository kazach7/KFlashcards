import java.io.Serializable;

class Flashcard implements Serializable {
    private String question;
    private String answer;
    private int grade;

    Flashcard (String question, String answer, int grade){
        super();
        this.question = question;
        this.answer = answer;
        this.grade = grade;
    }
    Flashcard (String question, String answer){
        this(question, answer, 1);
    }
    String getQuestion() {
        return question;
    }

    void setQuestion(String question) {
        this.question = question;
    }

    String getAnswer () { return answer; }

    void setAnswer(String answer) {
        this.answer = answer;
    }

    int getGrade(){
        return grade;

    }
    void incrementGrade() {
        ++grade;
    }
    void setGrade(int value) {
        grade = value;
    }
}

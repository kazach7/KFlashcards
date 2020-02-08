class Flashcard {
    private String question;
    private String answer;
    private int grade;

    Flashcard (String question, String answer){
        this.question = question;
        this.answer = answer;
        this.grade = 1;
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

    void incGrade() throws IllegalStateException{
        if (grade == 6){
            throw new IllegalStateException("Can't increment the maximum grade!");
        }
        else {
            ++grade;
        }
    }
    void setGrade(int value) throws IllegalArgumentException {
        if (value < 1 || value > 6){
            throw new IllegalArgumentException("Grade must an integer between 1 and 6");
        }
        else{
            grade = value;
        }
    }

    int getGrade(){
        return grade;

    }
    /*boolean compareAnswers (String userAnswer){
        return this.answer.equals(userAnswer);
    }
    /*public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }*/
}

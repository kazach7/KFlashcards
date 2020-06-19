import java.util.*;
import java.util.stream.Collectors;

class Model {
    static final short MAX_GRADE = 6;

    private View view;
    private DatabaseManager databaseManager;
    private ArrayList<Lesson> lessons;

    private ArrayList<Flashcard> currentLessonFlashcards; // Flashcards from the currently learned lesson.
    int currentFlashcardIndex; // The flashcard currently being displayed in the learning frame.

    Model(View view, DatabaseManager databaseManager) {
        this.view = view;
        this.databaseManager = databaseManager;
        this.lessons = new ArrayList<>();
        this.currentFlashcardIndex = 0;
    }

    void loadResourcesFromFiles() throws Exception{
        lessons = databaseManager.getAllLessons();
    }

    ArrayList<String> getLessonNamesList(){
        return lessons.stream()
                .map(Lesson::getName)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    ArrayList<String> getFlashcardQuestionsList (int lessonIndex) {
        assert (lessonIndex < lessons.size());
        return lessons.get(lessonIndex).getFlashcardsArray()
                .stream()
                .map(Flashcard::getQuestion)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    boolean addLesson (String lessonName) {
        if (lessonName.length() == 0) {
            view.displayMessage("The lesson name can't be empty!");
            return false;
        }
        else if (lessonName.contains("\\") || lessonName.contains("/") || lessonName.contains("\"")){
            view.displayMessage("The lesson name can't contain characters: \", \\, /");
            return false;
        }
        if (lessons.stream().anyMatch(l -> (l.getName().equals(lessonName)))){
            view.displayMessage("Lesson names must be unique!");
            return false;
        }

        Lesson lesson = new Lesson(lessonName);
        try {
            databaseManager.addLesson(lesson);
        } catch (Exception e){
            view.displayMessage("An unexpected error occurred while adding the lesson.\n" +
                    "Please try again.");
            return false;
        }
        lessons.add(lesson);

        return true;
    }
    boolean removeLesson(int lessonIndex){
        assert (lessonIndex < lessons.size());
        try {
            databaseManager.removeLesson(lessons.get(lessonIndex).getName());
        } catch (Exception e){
            view.displayMessage("An unexpected error occurred while removing the lesson.\n" +
                    "Please try again.");
            return false;
        }
        lessons.remove(lessonIndex);

        return true;
    }

    boolean saveFlashcard (int flashcardIndex, int lessonIndex, String question, String answer) {
        assert (lessonIndex < lessons.size());
        Lesson lesson = lessons.get(lessonIndex);
        assert (flashcardIndex <= lesson.getSize());

        if (question.length() == 0) {
            view.displayMessage("The front can't be empty!");
            return false;
        }
        else if (question.contains("\"") || question.contains("\\") || question.contains("/") ||
                answer.contains("\"") || answer.contains("\\") || answer.contains("/")) {
            view.displayMessage("Flashcards can't contain characters: \", \\, /");
            return false;
        }
        for (int i = 0; i < lesson.getSize(); ++i){
            if (i == flashcardIndex){
                continue;
            }
            if (lesson.getFlashcardsArray().get(i).getQuestion().equals(question)){
                view.displayMessage("Flashcards' fronts must be unique!");
                return false;
            }
        }

        if (flashcardIndex < lesson.getSize()){
            Flashcard flashcard = new Flashcard(question, answer, 1);
            try {
                databaseManager.editFlashcard(lesson.getName(), lesson.getFlashcardsArray().get(flashcardIndex).getQuestion(),
                        flashcard);
            } catch (Exception e){
                view.displayMessage("An unexpected error occurred while editing the flashcard.\n" +
                        "Please try again.");
                return false;
            }
            lesson.getFlashcardsArray().set(flashcardIndex, flashcard);
        }
        else {
            Flashcard flashcard = new Flashcard(question, answer, 1);
            try {
                databaseManager.addFlashcard(lesson.getName(), flashcard);
            } catch (Exception e){
                view.displayMessage("An unexpected error occurred while adding the flashcard.\n" +
                        "Please try again.");
                return false;
            }
            lesson.getFlashcardsArray().add(flashcard);
        }

        return true;
    }
    boolean removeFlashcard (int flashcardIndex, int lessonIndex) {
        assert (lessonIndex < lessons.size());
        Lesson lesson = lessons.get(lessonIndex);
        assert (flashcardIndex < lesson.getSize());
        try {
            databaseManager.removeFlashcard(lesson.getName(), lesson.getFlashcardsArray().get(flashcardIndex).getQuestion());
        } catch (Exception e){
            view.displayMessage("An unexpected error occurred while removing the flashcard.\n" +
                    "Please try again.");
            return false;
        }
        lesson.getFlashcardsArray().remove(flashcardIndex);

        return true;
    }
    String getFlashcardQuestion (int flashcardIndex, int lessonIndex) {
        assert (lessonIndex < lessons.size());
        Lesson lesson = lessons.get(lessonIndex);
        assert (flashcardIndex < lesson.getSize());
        return lesson.getFlashcardsArray().get(flashcardIndex).getQuestion();
    }
    String getFlashcardAnswer (int flashcardIndex, int lessonIndex) {
        assert (lessonIndex < lessons.size());
        Lesson lesson = lessons.get(lessonIndex);
        assert (flashcardIndex < lesson.getSize());
        return lesson.getFlashcardsArray().get(flashcardIndex).getAnswer();
    }

    void setCurrentLesson(int lessonIndex) {
        currentLessonFlashcards = lessons.get(lessonIndex).getFlashcardsArray();
    }
    int getCurrentLessonSize() throws NullPointerException{
        assert (currentLessonFlashcards != null);
        return currentLessonFlashcards.size();
    }
    boolean setRandomOrder(){
        assert (currentLessonFlashcards != null);
        Collections.shuffle(currentLessonFlashcards);
        return this.setCurrentFlashcardToFirstNotLearned(0);

    }
    boolean setGradeOrder(){
        assert (currentLessonFlashcards != null);
        currentLessonFlashcards.sort(Comparator.comparingInt(Flashcard::getGrade));
        return this.setCurrentFlashcardToFirstNotLearned(0);
    }

    private boolean setCurrentFlashcardToFirstNotLearned(int startingIndex){
        int size = currentLessonFlashcards.size();
        currentFlashcardIndex = startingIndex < size ? startingIndex : 0;
        int i = 0;
        while (currentLessonFlashcards.get(currentFlashcardIndex).getGrade() == Model.MAX_GRADE){
            if (++currentFlashcardIndex == size){
                currentFlashcardIndex = 0;
            }
            if (++i == size){
                return false;
            }
        }
        return true;
    }

    boolean setCurrentFlashcardToNextOne() {
        assert (currentLessonFlashcards != null);
        return this.setCurrentFlashcardToFirstNotLearned(currentFlashcardIndex+1);
    }

    String getCurrentFlashcardQuestion() {
        assert (currentLessonFlashcards != null);
        return currentLessonFlashcards.get(currentFlashcardIndex).getQuestion();
    }
    String getCurrentFlashcardAnswer() {
        assert (currentLessonFlashcards != null);
        return currentLessonFlashcards.get(currentFlashcardIndex).getAnswer();

    }
    int getCurrentFlashcardGrade() {
        assert (currentLessonFlashcards != null);
        return currentLessonFlashcards.get(currentFlashcardIndex).getGrade();
    }
    void incrementCurrentFlashcardGrade(){
        assert (currentLessonFlashcards != null);
        currentLessonFlashcards.get(currentFlashcardIndex).incrementGrade();
    }
    void resetCurrentFlashcardGrade(){
        assert (currentLessonFlashcards != null);
        currentLessonFlashcards.get(currentFlashcardIndex).setGrade(1);
    }
    void resetAllGradesInCurrentLesson() {
        assert (currentLessonFlashcards != null);
        currentLessonFlashcards.forEach(f -> f.setGrade(1));
    }
    long getLearnedFlashcardsInCurrentLessonCount() {
        assert (currentLessonFlashcards != null);
        return currentLessonFlashcards.stream()
                .filter(f -> (f.getGrade() == MAX_GRADE))
                .count();
    }


    // TODO not any lesson, but currently learned lesson?
    //  In that case pass currentLessonFlashcards to DatabaseManager,
    //  but we still need the lesson index (passing it to the method like now would be ugly).
    void updateAllGradesInLessonInDatabase(int lessonIndex) {
        Lesson lesson = lessons.get(lessonIndex);
        try {
            databaseManager.updateAllGradesInLesson(lesson.getName(), lesson.getFlashcardsArray());
        } catch (Exception e){
            view.displayMessage("An unexpected error occurred while saving the learning results.\n" +
                    "The grades changes will be lost after the application is restarted. We are sorry for the inconvenience.");
        }
    }
}

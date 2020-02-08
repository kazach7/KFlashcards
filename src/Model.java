import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class Model {
    private FilesManager filesManager;

    private ArrayList<Lesson> lessons;
    private ArrayList<Flashcard> flashcards;    // Flashcards of currently learned lesson.
    private int currentFlashcardIndex;          // Currently displayed flashcard in the learning.
    private int maxGrade;

    Model() {
        lessons = null;
        flashcards = null;
        filesManager = new FilesManager();
        maxGrade = 6; // Default value, the controller will update it anyway.
    }

    ArrayList<Lesson> getLessons(){
        return lessons;
    }


    // TODO czy to dobre rozwiazanie?
    void setMaxGrade(int maxGrade) {
        this.maxGrade = maxGrade;
    }

    void loadResourcesFromFiles() throws FileNotFoundException{
        lessons = new ArrayList<>();
        ArrayList<String> lessonsList = filesManager.getLessonsList();

        if (lessonsList != null) {
            for (String lessonName : lessonsList) {
                try {
                    lessons.add(filesManager.loadLessonFromFile(lessonName));
                } catch (FileNotFoundException e) {
                    throw new FileNotFoundException(e.getMessage());
                }
            }
        }
    }

    void setCurrentLesson(int lessonIndex) throws IndexOutOfBoundsException{
        flashcards = lessons.get(lessonIndex).getFlashcardsArray();
    }

    boolean checkLessonEmptiness(){
        return (flashcards == null || flashcards.size() == 0);
    }

    void randomOrder(){
        try{
            Collections.shuffle(flashcards);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        // Next displayed flashcard will have the 0 index.
        currentFlashcardIndex = -1;
    }

    void gradeOrder(){
        flashcards.sort(new Comparator<Flashcard>() {
            @Override
            public int compare(Flashcard o1, Flashcard o2) {
                return Integer.compare(o1.getGrade(), o2.getGrade());
            }
        });

        // Next displayed flashcard will have the 0 index.
        currentFlashcardIndex = -1;
    }

    void resetAllGrades() throws NullPointerException{
        for (int i = 0; i < flashcards.size(); ++i){
            flashcards.get(i).setGrade(1);
        }
    }

    void correctAnswer(){
        flashcards.get(currentFlashcardIndex).incGrade();
    }
    void incorrectAnswer(){
        flashcards.get(currentFlashcardIndex).setGrade(1);
    }

    Flashcard getCurrentFlashcard() throws NullPointerException {
        if (flashcards == null){
            return null;
        }
        return flashcards.get(currentFlashcardIndex);
    }
    Flashcard getFlashcardToDisplay() throws NullPointerException{
        Flashcard flashcard = null;

        // Skip learned flashcards.
        for (int i = 0; i<flashcards.size(); ++i){
            currentFlashcardIndex = (currentFlashcardIndex + 1)% flashcards.size();
            if ((flashcard = flashcards.get(currentFlashcardIndex)).getGrade() != maxGrade){
                return flashcard;
            }
        }

        return null;
    }

    int getLearnedFlashcardsQuantity() throws NullPointerException{
        int quantity = 0;

        for (int i = 0; i < flashcards.size(); ++i){
            if (flashcards.get(i).getGrade() == maxGrade){
                ++quantity;
            }
        }
        return quantity;
    }

    int getCurrentLessonSize() throws NullPointerException{
        return flashcards.size();
    }

    void updateGradesInFile(int lessonIndex){
        try {
            filesManager.updateGrades(flashcards, lessons.get(lessonIndex).getName());
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
    void addLesson (String lessonName) throws FileAlreadyExistsException{
        if (lessons != null)
        {
            // Check name uniqueness.
            for (Lesson lesson : lessons) {
                if (lesson.getName().equals(lessonName)) {
                    throw new FileAlreadyExistsException("Lesson name already used! Name: " + lessonName);
                }
            }
        }
        else{
            lessons = new ArrayList<Lesson>();
        }

        lessons.add(new Lesson(lessonName, filesManager));

        filesManager.addLesson(lessonName);
    }

    void removeLesson(int lessonIndex){
        try{
            lessons.remove(lessonIndex);
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        filesManager.removeLesson(lessonIndex);
    }

    ArrayList<String> getFlashcardsQuestions(int lessonIndex){
        Lesson lesson = null;
        try {
            lesson = lessons.get(lessonIndex);
        }
        catch (NullPointerException e) {
            throw new NullPointerException("The lesson does not exist!");
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

        ArrayList<Flashcard> flashcardsArray = lesson.getFlashcardsArray();

        if (flashcardsArray == null) {
            return null;
        }

        ArrayList<String> flashcardsQuestions = new ArrayList<>();

        for (Flashcard flashcard : flashcardsArray) {
                flashcardsQuestions.add(flashcard.getQuestion());
        }

        return flashcardsQuestions;
    }

    Flashcard getFlashcard (int flashcardIndex, int lessonIndex) throws IndexOutOfBoundsException{
        Flashcard flashcard = null;
        flashcard = lessons.get(lessonIndex).getFlashcard(flashcardIndex);
        return flashcard;
    }

    void saveFlashcard (int flashcardIndex, int lessonIndex, Flashcard flashcard) throws FileAlreadyExistsException{
        try {
            Lesson lesson = lessons.get(lessonIndex);
            if (lesson.getFlashcardsArray() == null || lesson.getFlashcardsArray().size() == flashcardIndex){
                // A new flashcard.
                lesson.addFlashcard(flashcard);
                filesManager.addFlashcard(flashcard, lesson.getName());
            }
            else {
                lesson.editFlashcard(flashcardIndex, flashcard);
                filesManager.editFlashcard(flashcard, flashcardIndex, lessons.get(lessonIndex).getName());
            }
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    void removeFlashcard (int flashcardIndex, int lessonIndex) throws IndexOutOfBoundsException{
        lessons.get(lessonIndex).removeFlashcard(flashcardIndex);
    }

}

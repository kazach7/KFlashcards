import java.util.*;
import java.util.stream.Collectors;

class Model {
    static final short MAX_GRADE = 6;

    private View view;
    //private FileManager fileManager;
    private DatabaseManager databaseManager;
    private ArrayList<Lesson> lessons;

    private ArrayList<Flashcard> currentLessonFlashcards; // Flashcards from the currently learned lesson.
    //int currentLessonIndex;     // The lesson currently being learned.
    int currentFlashcardIndex; // The flashcard currently being displayed in the learning frame.

    Model(View view, DatabaseManager databaseManager) {
        this.view = view;
        //this.fileManager = fileManager;
        this.databaseManager = databaseManager;
        this.lessons = new ArrayList<>();
        //this.currentLessonIndex = -1;
        this.currentFlashcardIndex = 0;
    }

    void loadResourcesFromFiles() throws Exception{
        lessons = databaseManager.getAllLessons();
        /*
        try {
            lessons = fileManager.deserializeLessonsList();
        } catch (Exception e){
            view.displayMessage("An unexpected error occurred while loading resources.\n" +
                    "Please restart the application or continue with empty lessons list.\n" +
                    "We are sorry for the inconvenience.");
            lessons = new ArrayList<>();
        }
       /* ArrayList<String> lessonsList = fileManager.loadLessonsListFromFile();
        if (lessonsList == null){
            throw new Exception();
        }
        for (String lessonName : lessonsList) {
            ArrayList<String> lessonAsString = fileManager.loadLessonFromFile(lessonName);
            if (lessonAsString == null){
                if (view.displayOptionDialog("Could not load lesson '" + lessonName +
                    "'.\nRemove the lesson?", "Lesson not found", new String[]{"Yes", "No"}, 1)
                    == 0) {
                        fileManager.removeLessonNameFromLessonsFile(lessonName);
                }
                continue;
            }
            if (lessonAsString.size() % 3 != 0){
                if (view.displayOptionDialog("The data of the lesson '" + lessonName + "' has been corrupted.\n" +
                    "Loading it may lead to missing files and/or erroneous flashcards. Load anyway?", "Lesson corrupted",
                    new String[]{"Yes", "No"}, 1)
                    == 1) {
                        if (view.displayOptionDialog("Remove the lesson?", "Question", new String[]{"Yes", "No"},1)
                            == 0) {
                            fileManager.removeLessonFile(lessonName);
                        }
                        continue;
                }
                do lessonAsString.remove(lessonAsString.size()-1);
                while (lessonAsString.size() % 3 != 0);
            }
            ArrayList<Flashcard> flashcards = new ArrayList<>();
            for (int i = 0; i < lessonAsString.size(); i += 3) {
                flashcards.add(new Flashcard(lessonAsString.get(i), lessonAsString.get(i+1),
                        Integer.parseInt(lessonAsString.get(i+2))));
            }
            lessons.add(new Lesson(lessonName, flashcards));
        }*/
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
        /*Lesson lesson = null;
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

        return flashcardsQuestions;*/
    }

    boolean addLesson (String lessonName/*, ArrayList<String> flashcardsAsString*/) {
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
        /*if (flashcardsAsString != null){
            ArrayList<Flashcard> flashcards;
            for (int i = 0; i < flashcardsAsString.size(); i += 3){
                flashcards.add(new Flashcard(flashcardsAsString.get(i), flashcardsAsString.get(i+1),
                        Integer.parseInt(flashcardsAsString.get(i+2))));
            }
        }*/

        Lesson lesson = new Lesson(lessonName);
        try {
            databaseManager.addLesson(lesson);
        } catch (Exception e){
            view.displayMessage("An unexpected error occurred while adding the lesson.\n" +
                    "Please try again.");
            return false;
        }
        lessons.add(lesson);
        /*try {
            fileManager.serializeLessonsList(lessons);
        } catch (Exception e){
            lessons.remove(lessons.size()-1);
            view.displayMessage("An unexpected error occurred while saving the lesson." +
                    "Please try again. We are sorry for the inconvenience.");
        }
        /*Lesson lesson = new Lesson(lessonName);
        try {
            fileManager.serializeLesson(lesson);
            fileManager.appendLessonNameToLessonsFile(lessonName);
        } catch (Exception e) {
            view.displayMessage("An unexpected error occurred while adding the lesson.\nPlease try again.");
            return false;
        }
        lessons.add(lesson);*/

        /*try {
            fileManager.createLessonFile(lessonName);
        } catch (Exception e){
            try {
                fileManager.removeLessonNameFromLessonsFile(lessonName);
            } catch (Exception ex){
                view.displayMessage("A fatal error occurred while adding the lesson. Please restart the application.\n" +
                        "The application will be closed now. We are sorry for the inconvenience.");
                System.exit(1);
            }
            view.displayMessage("An unexpected error occurred while adding the lesson.\nPlease try again.");
            return false;
        }*/
        //lessons.add(new Lesson(lessonName));
        return true;
    }
    boolean removeLesson(int lessonIndex){
        /*try{
            lessons.remove(lessonIndex);
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }*/
        assert (lessonIndex < lessons.size());
        try {
            databaseManager.removeLesson(lessons.get(lessonIndex).getName());
        } catch (Exception e){
            view.displayMessage("An unexpected error occurred while removing the lesson.\n" +
                    "Please try again.");
            return false;
        }
        lessons.remove(lessonIndex);
        /*try {
            fileManager.serializeLessonsList(lessons);
        } catch (Exception e){

        }
        /*String lessonName = lessons.get(lessonIndex).getName();
        try{
            fileManager.removeLessonFile(lessonName);
        } catch (Exception e){
            view.displayMessage("An unexpected error occurred while removing the lesson.\nPlease try again.");
            return false;
        }

        lessons.remove(lessonIndex);
        fileManager.serializeLessonsList(lessons);
        /*try {
            fileManager.removeLessonNameFromLessonsFile(lessonName);
        } catch (Exception e) {
            view.displayMessage("The lesson has been removed, but an unexpected error occurred while removing if from the list.\n" +
                    "The application will handle it after it has been restarted. You may see an information that a lesson could not be found.");
        }*/
        return true;
    }
    void saveLessonStateInFile(){
        //fileManager.serializeLessonsList(lessons);
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
            //fileManager.editFlashcard(question, answer, 1, flashcardIndex, lesson.getName());

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
            //fileManager.addFlashcard(question, answer, 1, lesson.getName());
        }

        return true;
        /*try {
            Lesson lesson = lessons.get(lessonIndex);
            if (lesson.getFlashcardsArray() == null || lesson.getFlashcardsArray().size() == flashcardIndex){
                // A new flashcard.
                lesson.addFlashcard(flashcard);
                fileManager.addFlashcard(flashcard, lesson.getName());
            }
            else {
                lesson.editFlashcard(flashcardIndex, flashcard);
                fileManager.editFlashcard(flashcard, flashcardIndex, lessons.get(lessonIndex).getName());
            }
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }*/
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
        //fileManager.removeFlashcard(flashcardIndex, lesson.getName());
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

        /*
        // Skip already learned flashcards.
        for (int i = 0; i < size; ++i) {
            if (++currentFlashcardIndex == size){
                currentFlashcardIndex = 0;
            }
            if (currentLessonFlashcards.get(currentFlashcardIndex).getGrade() != Model.MAX_GRADE){
                return true;
            }
        }
        return false;*/
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
        /*for (int i = 0; i < currentLessonFlashcards.size(); ++i){
            currentLessonFlashcards.get(i).setGrade(1);
        }*/
    }
    long getLearnedFlashcardsInCurrentLessonCount() {
        assert (currentLessonFlashcards != null);
        return currentLessonFlashcards.stream()
                .filter(f -> (f.getGrade() == MAX_GRADE))
                .count();
    }

    /*void updateAllGradesInLessonInFile(int lessonIndex){
        assert (lessonIndex < lessons.size());
        Lesson lesson = lessons.get(lessonIndex);
        ArrayList<String> grades = lesson.getFlashcardsArray().stream()
                .map(f -> String.valueOf(f.getGrade()))
                .collect(Collectors.toCollection(ArrayList::new));
        try {
            fileManager.updateAllGrades(grades, lesson.getName());
        }
        catch (Exception e){
            view.displayMessage("An unexpected error occurred and the learning results could not be saved.\n" +
                    "They will be lost after the application has been restarted. We are sorry for the inconvenience.");
        }
    }*/

    // TODO Nie dowolnej lekcji, tylko aktualnie uczonej? Wtedy dac currentLessonFlashcards do DatabaseManagera.
    //  Tylko wtedy trzeba znac lessonIndex, przekazywanie go do funcji jest troche brzydkie - teoretycznie
    //  mozna podac inny indeks niz aktualnie uczonej lekcji.
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

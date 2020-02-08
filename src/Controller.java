import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

class Controller {
    private View view;
    private Model model;

    final int maxGrade;

    Controller(View view, Model model) {
        this.view = view;
        this.model = model;

        maxGrade = 6;

        model.setMaxGrade(maxGrade);
        try {
            model.loadResourcesFromFiles();
        }
        catch(FileNotFoundException e){
            view.displayMessage("Could not load lesson: " + e.getMessage() + "!");
        }

        ArrayList<Lesson> lessons = model.getLessons();
        ArrayList<String> lessonNames = new ArrayList<>();
        for (Lesson lesson : lessons){
            lessonNames.add(lesson.getName());
        }
        view.initializeLessonsList(lessonNames);
    }

    void editLesson(int lessonIndex) {
        ArrayList<String> flashcardsQuestionsList = model.getFlashcardsQuestions(lessonIndex);
        view.openEditLessonFrame(flashcardsQuestionsList);
    }

    void removeLesson(int lessonIndex) {
        model.removeLesson(lessonIndex);
        view.removeLesson(lessonIndex);
    }

    void learnLesson(int lessonIndex) {
        try {
            model.setCurrentLesson(lessonIndex);
        } catch (IndexOutOfBoundsException e) {
            view.displayMessage("The lesson does not exist!");
            // TODO remove the lesson from the list in GUI?
            return;
        }

        if (model.checkLessonEmptiness()) {
            view.displayMessage("No cards in the lesson! Select 'Edit lesson' to add some.");
            return;
        }

        model.randomOrder();    // The order is random by default.

        view.openLearnLessonFrame(model.getFlashcardToDisplay(), model.getLearnedFlashcardsQuantity(), model.getCurrentLessonSize());
    }

    void finishLearning (int lessonIndex){
        model.updateGradesInFile(lessonIndex);
        view.closeLearnLessonFrame();
    }
    void addLesson(String lessonName) {
        if (lessonName.length() == 0) {
            view.displayMessage("Lesson name can't be empty!");
            return;
        }
        else if (lessonName.contains("\\") || lessonName.contains("/") || lessonName.contains("\"")){
            view.displayMessage("Lesson name can't contain characters: \", \\, /");
            return;
        }
        try {
            model.addLesson(lessonName);
        } catch (FileAlreadyExistsException e) {
            view.displayMessage("Lesson names must be unique!");
            return;
        }
        view.addLesson(lessonName);
    }

    void finishEditingLesson() {
        view.closeEditLessonFrame();
    }

    void addFlashcard() {
        view.openEditFlashcardFrame(null, null);
    }

    void editFlashcard(int flashcardIndex, int lessonIndex) {
        Flashcard flashcard;
        try {
            flashcard = model.getFlashcard(flashcardIndex, lessonIndex);
        } catch (IndexOutOfBoundsException e) {
            view.displayMessage("The flashcard does not exist!");
            // TODO moze usunac nieistniejaca fiszke z listy? ale ogarnac czy lessonIndex nie mogl wywolac wyjatku
            return;
        }
        view.openEditFlashcardFrame(flashcard.getQuestion(), flashcard.getAnswer());
    }

    void removeFlashcard(int flashcardIndex, int lessonIndex) {
        try {
            model.removeFlashcard(flashcardIndex, lessonIndex);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            view.displayMessage("The flashcard did not exist!");
        }
        view.removeFlashcard();
    }

    void saveFlashcard(int flashcardIndex, int lessonIndex, String question, String answer) {
        if (question.length() == 0) {
            view.displayMessage("The front can't be empty!");
            return;
        }
        else if (question.contains("\"") || question.contains("\\") || question.contains("/") ||
                  answer.contains("\"") || answer.contains("\\") || answer.contains("/")) {
            view.displayMessage("Flashcards can't contain characters: \", \\, /");
            return;
        }
        try {
            model.saveFlashcard(flashcardIndex, lessonIndex, new Flashcard(question, answer));
        } catch (FileAlreadyExistsException e) {
            view.displayMessage("Flashcard fronts must be unique!");
            return;
        }
        view.saveFlashcard(flashcardIndex); // Pass the index to update the flashcards list in the GUI.
    }

    void cancelEditingFlashcard() {
        view.closeEditFlashcardFrame();
    }

    void setRandomOrder() {
        try {
            model.randomOrder();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        view.nextFlashcard(model.getFlashcardToDisplay());
    }

    void setGradeOrder() {
        try {
            model.gradeOrder();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        view.nextFlashcard(model.getFlashcardToDisplay());
    }

    void resetAllGrades() {
        try {
            model.resetAllGrades();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        view.resetAllGrades();
    }

    void checkAnswer(String answer) {
        String correctAnswer = model.getCurrentFlashcard().getAnswer();
        if (correctAnswer.equals(answer)) {
            view.correctAnswer();
            model.correctAnswer();
        } else {
            view.incorrectAnswer(correctAnswer);
            model.incorrectAnswer();
        }
    }

    void askNextQuestion(boolean wasCurrentQuestionAnswered) {
        if (!wasCurrentQuestionAnswered) { // Skipping a question has the same consequences as answering it wrong.
            model.incorrectAnswer();
        }
        view.nextFlashcard(model.getFlashcardToDisplay());
    }
}
   /*
    void createMainFrameButtonListeners() {
        ActionListener addLessonButtonListener = e -> addLesson();
        ActionListener editLessonButtonListener = e -> editLesson();
        ActionListener removeLessonButtonListener = e -> removeLesson();
        ActionListener learnLessonButtonListener = e -> learnLesson();

        view.addMainFrameButtonListeners(addLessonButtonListener, editLessonButtonListener, removeLessonButtonListener,
                learnLessonButtonListener);
    }

    void createAddLessonFrameButtonListeners() {
        ActionListener saveLessonButtonListener = e -> saveLesson();
        ActionListener cancelAddingLessonButtonListener = e -> cancelAddingLesson();

        view.addAddLessonFrameButtonListeners(saveLessonButtonListener, cancelAddingLessonButtonListener);
    }

    void createEditLessonFrameButtonListeners() {
        ActionListener addFlashcardButtonListener = e -> addFlashcard();
        ActionListener editFlashcardButtonListener = e -> editFlashcard();
        ActionListener removeFlashcardButtonListener = e -> removeFlashcard();

        view.addEditLessonFrameButtonListeners(addFlashcardButtonListener, editFlashcardButtonListener, removeFlashcardButtonListener);
    }

    void createEditFlashcardFrameButtonListeners() {
        ActionListener saveFlashcardButtonListener = e -> saveEditedFlashcard();
        ActionListener cancelEditingFlashcardButtonListener = e -> cancelEditingFlashcard();

        view.addEditFlashcardFrameButtonListeners(saveFlashcardButtonListener, cancelEditingFlashcardButtonListener);
    }

    void createLearningFrameButtonListeners() {
        ActionListener randomOrderButtonListener = e -> setRandomOrder();
        ActionListener gradeOrderButtonListener = e -> setGradeOrder();
        ActionListener resetButtonListener = e -> resetAllGrades();
        ActionListener checkButtonListener = e -> checkAnswer();
        ActionListener nextButtonListener = e -> askNextQuestion();

        view.addLearningFrameButtonListeners(randomOrderButtonListener, gradeOrderButtonListener, resetButtonListener,
                checkButtonListener, nextButtonListener);
    }*/

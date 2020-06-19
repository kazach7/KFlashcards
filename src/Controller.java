import java.util.Optional;

class Controller {
    private View view;
    private Model model;

    Controller(View view, Model model) {
        this.view = view;
        this.model = model;

        try {
            model.loadResourcesFromFiles();
        }
        catch (Exception e){
            view.displayMessage("An unexpected error occurred while loading resources. Please restart the application.\n" +
                    "The application will be closed now automatically. We are sorry for the inconvenience.");
            System.exit(1);
        }

        view.initializeLessonsList(model.getLessonNamesList());
        view.closeLoadingResourcesFrame();
        /*ArrayList<String> lessonsList = fileManager.loadLessonsListFromFile();

        boolean[] rememberDecision = {false, false};
        int[] decision = {1,1};
        for (String lessonName : lessonsList) {
            ArrayList<String> lessonAsString;
            try {
                lessonAsString = fileManager.loadLessonFromFile(lessonName);
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException(e.getMessage());
            }

            if (lessonAsString.size() % 3 != 0){
                    if (!rememberDecision[0]) {
                        int dec = view.displayOptionDialog("Lesson '" + lessonName + "' data has been corrupted. " +
                                        "Loading it may lead to missing files and/or erroneous flashcards. Load anyway?",
                                "Question", new String[]{"Yes", "No", "Yes, remember decision", "No, remember decision"}, 1);
                        if (dec > 1) {
                            rememberDecision[0] = true;
                            decision[0] = dec - 2;
                        }
                    }
                    if (decision[0] == 1) {
                        if (!rememberDecision[1]) {
                            int dec = view.displayOptionDialog("Remove the lesson?", "Question",
                                    new String[]{"Yes", "No", "Yes, remember decision", "No, remember decision"}, 1);
                            if (dec > 1) {
                                rememberDecision[1] = true;
                                decision[1] = dec - 2;
                            }
                        }
                        if (decision[1] == 0) {
                            // remove lesson.
                        }
                        continue;
                    }
                    // User wants to load it - remove the incomplete ending;
                   do lessonAsString.remove(lessonAsString.size()-1);
                   while (lessonAsString.size() % 3 != 0);
            }

                // Load the lesson into the model.
                model.addLesson(lessonName, lessonAsString);
            }

        }*/
    }

    /* Responses to actions related to the main frame and the editing frames. */
    void addLesson(String lessonName) {
        boolean success = model.addLesson(lessonName);
        if (success){
            view.addNewLessonToTheList(lessonName);
        }
    }
    void removeLesson(int lessonIndex) {
        boolean success = model.removeLesson(lessonIndex);
        if (success){
            view.removeLessonFromTheList(lessonIndex);
        }
    }
    void startEditingLesson(int lessonIndex) {
        view.openEditLessonFrame(model.getFlashcardQuestionsList(lessonIndex));
    }
    void finishEditingLesson() {
        //model.saveLessonStateInFile();
        view.closeEditLessonFrame();
    }
    void startLearningLesson(int lessonIndex) {
        model.setCurrentLesson(lessonIndex);
        int lessonSize = model.getCurrentLessonSize();
        if (lessonSize == 0) {
            view.displayMessage("No cards in the lesson! Select 'Edit lesson' to add some.");
        }
        else {
            model.setRandomOrder();
            if (model.getLearnedFlashcardsInCurrentLessonCount() == lessonSize) {
                // Whole lesson learned. Don't display any flashcard.
                view.openLearnLessonFrame(Optional.empty(), Optional.empty(), lessonSize, lessonSize);
            } else {
                view.openLearnLessonFrame(Optional.of(model.getCurrentFlashcardQuestion()), Optional.of(model.getCurrentFlashcardGrade()),
                        model.getLearnedFlashcardsInCurrentLessonCount(), lessonSize);
            }
        }
    }
    void finishLearningLesson (int lessonIndex){
        model.updateAllGradesInLessonInDatabase(lessonIndex);
        //model.saveLessonStateInFile();
        view.closeLearnLessonFrame();
    }

    void startAddingFlashcard() {
        view.openEditFlashcardFrame(Optional.empty(), Optional.empty());
    }
    void startEditingFlashcard(int flashcardIndex, int lessonIndex) {
        view.openEditFlashcardFrame(Optional.of(model.getFlashcardQuestion(flashcardIndex, lessonIndex)),
                Optional.of(model.getFlashcardAnswer(flashcardIndex, lessonIndex)));
    }
    void saveAddedOrEditedFlashcard(int flashcardIndex, int lessonIndex, String question, String answer) {
        boolean success = model.saveFlashcard(flashcardIndex, lessonIndex, question, answer);
        if (success){
            view.updateAddedOrEditedFlashcardOnTheList(flashcardIndex);
        }
    }
    void cancelEditingFlashcard() {
        view.closeEditFlashcardFrame();
    }
    void removeFlashcard(int flashcardIndex, int lessonIndex) {
        try {
            model.removeFlashcard(flashcardIndex, lessonIndex);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            view.displayMessage("The flashcard did not exist!");
        }
        view.removeFlashcardFromTheList();
    }

    /* Responses to actions related to the learning frame. */
    void setRandomOrder() {
        boolean isThereNotLearnedFlashcard = model.setRandomOrder();
        if (isThereNotLearnedFlashcard){
            view.updateCurrentlyLearnedFlashcard(model.getCurrentFlashcardQuestion(), model.getCurrentFlashcardGrade());
        } else {
            view.lessonLearned();
        }
    }
    void setGradeOrder() {
        boolean isThereNotLearnedFlashcard = model.setGradeOrder();
        if (isThereNotLearnedFlashcard){
            view.updateCurrentlyLearnedFlashcard(model.getCurrentFlashcardQuestion(), model.getCurrentFlashcardGrade());
        } else {
            view.lessonLearned();
        }
    }

    void resetAllGradesInCurrentLesson() {
        model.resetAllGradesInCurrentLesson();
        view.resetLearnedFlashcardsCounterToZero();
    }
    void checkAnswer(String answer) {
        String correctAnswer = model.getCurrentFlashcardAnswer();
        if (correctAnswer.equals(answer)) {
            view.correctAnswer();
            model.incrementCurrentFlashcardGrade();
        } else {
            view.incorrectAnswer(correctAnswer);
            model.resetCurrentFlashcardGrade();
        }
    }
    void askNextQuestion(boolean wasCurrentQuestionAnswered) {
        if (!wasCurrentQuestionAnswered) { // Skipping a question has the same consequences as answering it wrong.
            model.resetCurrentFlashcardGrade();
        }
        boolean isThereNextOne = model.setCurrentFlashcardToNextOne();
        if (isThereNextOne){
            view.updateCurrentlyLearnedFlashcard(model.getCurrentFlashcardQuestion(), model.getCurrentFlashcardGrade());
        } else {
            view.lessonLearned();
        }
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

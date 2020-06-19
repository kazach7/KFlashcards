import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Optional;

class View {
    private Controller controller;

    // Windows (frames).
    private MainFrame mainFrame;                   // Main frame of the app, containing a list of lessons.
    //private AddLessonFrame addLessonFrame;         // Providing name for a new lesson frame.
    private EditLessonFrame editLessonFrame;       // Lesson editing frame.
    private EditFlashcardFrame editFlashcardFrame; // Flashcard editing frame.
    private LearningFrame learningFrame;           // Learning a lesson frame.
    private LoadingFrame loadingFrame;
    private boolean displaySkipQuestionWarning;  // If true, the warning will show. It will only show once.


    private class MainFrame extends JFrame {
        private JList<String> lessonsList;                  // List of lessons.
        private DefaultListModel<String> lessonsListModel;
        private JScrollPane listScroll;

        private JButton addLessonButton;    // Add a new lesson.
        private JButton editLessonButton;   // Edit the chosen lesson.
        private JButton removeLessonButton; // Remove the chosen lesson.
        private JButton learnLessonButton;  // Start learning the chosen lesson.

        private JPanel buttonPanel;
        private JPanel listPanel;


        MainFrame() {
            addLessonButton = new JButton("Add new lesson");
            editLessonButton = new JButton("Edit lesson");
            removeLessonButton = new JButton("Remove lesson");
            learnLessonButton = new JButton("Learn lesson");

            // List of created lessons.
            lessonsListModel = new DefaultListModel<>();
            lessonsList = new JList<String>(lessonsListModel);
            listScroll = new JScrollPane();
            listScroll.setViewportView(lessonsList);
            listScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            listScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            listScroll.setPreferredSize(new Dimension(450, 250));
            lessonsList.setLayoutOrientation(JList.VERTICAL);
            lessonsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            buttonPanel = new JPanel();
            listPanel = new JPanel();

            buttonPanel.add(addLessonButton);
            buttonPanel.add(editLessonButton);
            buttonPanel.add(removeLessonButton);
            buttonPanel.add(learnLessonButton);
            buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            listPanel.add(listScroll);

            this.setLayout(new BorderLayout());
            this.add(buttonPanel, BorderLayout.NORTH);
            this.add(listPanel, BorderLayout.CENTER);

            displaySkipQuestionWarning = true;

            createButtonListener();

            if (lessonsListModel.getSize() > 0) {
                lessonsList.setSelectedIndex(lessonsListModel.getSize() - 1);
            } else {
                editLessonButton.setEnabled(false);
                removeLessonButton.setEnabled(false);
                learnLessonButton.setEnabled(false);
            }

            //setLayout(new BorderLayout());
            setTitle("KFlashcards");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(600, 350);
            setResizable(false);
            setLocationRelativeTo(null);    // Center the GUI on the screen.
            setVisible(true);
        }

        private void createButtonListener() {
            ActionListener buttonListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == addLessonButton) {
                        String lessonName;
                        // TODO moze mozna jakos nie zamykac dialogu w przypadku gdy wprowadzone zostana bledne dane?
                        // TODO tzn. pusta nazwa, powtarzajaca sie nazwa (edit: zakladka w firefoksie - obczaj)
                        if ((lessonName = JOptionPane.showInputDialog(mainFrame, "Enter the lesson name", "Add lesson", JOptionPane.PLAIN_MESSAGE))
                                != null){
                            controller.addLesson(lessonName);
                        }
                    } else if (e.getSource() == editLessonButton) {
                        controller.startEditingLesson(mainFrame.lessonsList.getSelectedIndex());
                    } else if (e.getSource() == removeLessonButton) {
                        if(JOptionPane.showConfirmDialog(mainFrame, "Are you sure?", "Remove lesson", JOptionPane.YES_NO_OPTION)
                                == JOptionPane.YES_OPTION){
                            controller.removeLesson(mainFrame.lessonsList.getSelectedIndex());
                        }
                    } else if (e.getSource() == learnLessonButton) {
                        controller.startLearningLesson(mainFrame.lessonsList.getSelectedIndex());
                    }
                }
            };

            addLessonButton.addActionListener(buttonListener);
            editLessonButton.addActionListener(buttonListener);
            removeLessonButton.addActionListener(buttonListener);
            learnLessonButton.addActionListener(buttonListener);
        }

        void initializeLessonsList(final ArrayList<String> lessonsNames){
            for (String lessonName : lessonsNames){
                lessonsListModel.addElement(lessonName);
            }

            if (lessonsListModel.getSize() > 0) {
                lessonsList.setSelectedIndex(lessonsListModel.getSize() - 1);

                editLessonButton.setEnabled(true);
                removeLessonButton.setEnabled(true);
                learnLessonButton.setEnabled(true);
            }
        }
    }

    private class EditLessonFrame extends JFrame {
        private JButton addFlashcardButton;
        private JButton editFlashcardButton;
        private JButton removeFlashcardButton;
        private JButton doneButton;
        //private JButton flipButton;

        private JList<String> flashcardsList;                   // List of flashcards in the lesson.
        private DefaultListModel<String> flashcardsListModel;
        private JScrollPane listScroll;

        private JPanel buttonPanel;
        private JPanel listPanel;
        //private JPanel donePanel;

        EditLessonFrame(ArrayList<String> flashcardsQuestionsList) {
            addFlashcardButton = new JButton("Add new flashcard");
            editFlashcardButton = new JButton("Edit flashcard");
            removeFlashcardButton = new JButton("Remove flashcard");
            //flipButton = new JButton("Flip cards sides");
            doneButton = new JButton("Done");

            flashcardsListModel = new DefaultListModel<>();
            flashcardsList = new JList<>(flashcardsListModel);
            listScroll = new JScrollPane(flashcardsList);
            listScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            listScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            listScroll.setViewportView(flashcardsList);
            listScroll.setPreferredSize(new Dimension(350, 250));

            flashcardsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            flashcardsList.setLayoutOrientation(JList.VERTICAL);

            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
            buttonPanel.add(addFlashcardButton);
            buttonPanel.add(editFlashcardButton);
            buttonPanel.add(removeFlashcardButton);

            buttonPanel.add(Box.createRigidArea(new Dimension(0, 75)));
            //doneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(doneButton);

            //buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            //buttonPanel.add(flipButton);

            listPanel = new JPanel();
            listPanel.add(listScroll);
            //listPanel.setLayout(new GridLayout());

            /*donePanel = new JPanel();
            donePanel.add(doneButton);
            donePanel.add(doneButton);*/

            this.setLayout(new FlowLayout());
            this.add(listPanel);
            this.add(buttonPanel);
            //this.add(donePanel);

            createButtonListener();

            // When the X button is clicked.
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    controller.finishEditingLesson();
                }
            });

            if (flashcardsQuestionsList != null && flashcardsQuestionsList.size() > 0) {
                for (String question : flashcardsQuestionsList) {
                    this.flashcardsListModel.addElement(question);
                }
                flashcardsList.setSelectedIndex(flashcardsListModel.getSize()-1);
            }
            else{
                editFlashcardButton.setEnabled(false);
                removeFlashcardButton.setEnabled(false);
            }

            setTitle("Edit lesson");
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            setSize(550, 300);
            setResizable(false);
            setLocationRelativeTo(mainFrame);
            setVisible(true);
        }

        private void createButtonListener() {
            ActionListener buttonListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == addFlashcardButton) {
                        controller.startAddingFlashcard();
                    } else if (e.getSource() == editFlashcardButton) {
                        controller.startEditingFlashcard(flashcardsList.getSelectedIndex(), mainFrame.lessonsList.getSelectedIndex());
                    } else if (e.getSource() == removeFlashcardButton) {
                        controller.removeFlashcard(flashcardsList.getSelectedIndex(), mainFrame.lessonsList.getSelectedIndex());
                    } else if (e.getSource() == doneButton){
                        controller.finishEditingLesson();
                    }
                    /*else if (e.getSource() == flipButton){
                        controller.flipCardsSides();
                    }*/
                }
            };

            addFlashcardButton.addActionListener(buttonListener);
            editFlashcardButton.addActionListener(buttonListener);
            removeFlashcardButton.addActionListener(buttonListener);
            doneButton.addActionListener(buttonListener);
        }
    }

    private class EditFlashcardFrame extends JFrame {
        private JLabel questionLabel;
        private JLabel answerLabel;
        private JTextField questionTextField;
        private JTextField answerTextField;
        private JButton saveButton;
        private JButton cancelButton;
        private JCheckBox showAnswerCheckBox;

        private JPanel flashcardPanel;
        private JPanel buttonPanel;

        EditFlashcardFrame(Optional<String> question, Optional<String> answer) {
            questionLabel = new JLabel("Front:");
            answerLabel = new JLabel("Back:");
            questionTextField = new JTextField();
            answerTextField = new JTextField();
            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");
            showAnswerCheckBox = new JCheckBox("Show back");

            flashcardPanel = new JPanel();
            buttonPanel = new JPanel();

            questionTextField.setPreferredSize(new Dimension(300, 30));
            answerTextField.setPreferredSize(new Dimension(300, 30));

            flashcardPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            flashcardPanel.setPreferredSize(new Dimension(350, 75));
            flashcardPanel.add(questionLabel);
            //flashcardPanel.add(Box.createRigidArea(new Dimension(3, 0)));
            flashcardPanel.add(questionTextField);
            flashcardPanel.add(answerLabel);
           // flashcardPanel.add(Box.createRigidArea(new Dimension(3, 0)));
            flashcardPanel.add(answerTextField);

            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            this.setLayout(new FlowLayout());
            this.add(flashcardPanel);
            this.add(buttonPanel);

            if (question.isPresent()){
                // The frame was opened to edit an existing flashcard.
                setTitle("Edit flashcard");
                buttonPanel.add(showAnswerCheckBox);
                showAnswerCheckBox.setSelected(false);
                questionTextField.setText(question.get());
            }
            else{
                // The frame was opened to add a new flashcard.
                setTitle("Add flashcard");
                questionTextField.setText("");
            }
            answerTextField.setText("");

            createActionListener(answer);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    controller.cancelEditingFlashcard();
                }
            });

            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            this.setSize(400, 175);
            this.setResizable(false);
            this.setLocationRelativeTo(editLessonFrame);
            this.setVisible(true);
        }

        private void createActionListener(Optional<String> originalAnswer) {
            ActionListener actionListener = e -> {
                if (e.getSource() == saveButton ||
                        e.getSource() == questionTextField || e.getSource() == answerTextField) {
                    int flashcardIndex;
                    if (originalAnswer.isPresent()) { // We are saving an edited flashcard.
                        flashcardIndex = editLessonFrame.flashcardsList.getSelectedIndex();
                    }
                    else { // We are saving a new flashcard.
                        flashcardIndex = editLessonFrame.flashcardsListModel.getSize();
                    }
                    controller.saveAddedOrEditedFlashcard(flashcardIndex, mainFrame.lessonsList.getSelectedIndex(), questionTextField.getText(), answerTextField.getText());
                } else if (e.getSource() == cancelButton) {
                    controller.cancelEditingFlashcard();
                } else if (e.getSource() == showAnswerCheckBox){
                    if (showAnswerCheckBox.getModel().isSelected()){
                        answerTextField.setText(originalAnswer.get());
                    }
                    else{
                        if (answerTextField.getText().equals(originalAnswer.get())){
                            answerTextField.setText("");
                        }
                    }
                }
            };

            saveButton.addActionListener(actionListener);
            cancelButton.addActionListener(actionListener);
            questionTextField.addActionListener(actionListener);
            answerTextField.addActionListener(actionListener);
            if (originalAnswer.isPresent()){
                // Add the listener only if the frame was opened for editing an existing flashcard purpose.
                // Is this really necessary? The check box wouldn't be added to the panel otherwise.
                showAnswerCheckBox.addActionListener(actionListener);
            }
        }
    }

    private class LearningFrame extends JFrame {
        private JPanel buttonPanel;     // Panel with the main buttons.
        private JPanel flashcardPanel;  // Panel with the question and the answer.
        private JPanel gradePanel;      // Panel displaying grade associated with the flashcard.

        // Button panel.
        private JButton randomOrderButton;
        private JButton gradeOrderButton;
        private JButton resetButton;

        // Flashcard panel.
        private JLabel questionLabel;
        private JTextField answerTextField;
        private JLabel learnedLabel;            // How many flashcards from the lesson have been learned.
        private JLabel sizeLabel;               // How many flashcards there is in the lesson.
        private JTextField learnedTextField;
        private JButton checkButton;
        private JButton nextButton;

        // Grade panel.
        private JLabel gradeLabel;
        private JTextField gradeTextField;

        LearningFrame(Optional<String> initialQuestion, Optional<Integer> initialGrade, long learnedFlashcardsCount, int allFlashcardsCount) {
            buttonPanel = new JPanel();
            flashcardPanel = new JPanel();
            gradePanel = new JPanel();

            randomOrderButton = new JButton("Random order");
            gradeOrderButton = new JButton("Grade order");
            resetButton = new JButton("Reset all grades");

            questionLabel = new JLabel();
            answerTextField = new JTextField();
            answerTextField.setDisabledTextColor(Color.BLACK);
            learnedLabel = new JLabel("Learned flashcards:");
            learnedTextField = new JTextField(String.valueOf(learnedFlashcardsCount));
            learnedTextField.setOpaque(false);
            learnedTextField.setDisabledTextColor(Color.BLACK);
            learnedTextField.setEnabled(false);
            learnedTextField.setBorder(BorderFactory.createEmptyBorder());
            sizeLabel = new JLabel("/ " + allFlashcardsCount);
            checkButton = new JButton("Check");
            nextButton = new JButton("Next");

            gradeLabel = new JLabel("Grade:");
            gradeTextField = new JTextField();
            gradeTextField.setOpaque(false);
            gradeTextField.setDisabledTextColor(Color.BLACK);
            gradeTextField.setEnabled(false);
            gradeTextField.setBorder(BorderFactory.createEmptyBorder());

            buttonPanel.add(randomOrderButton);
            buttonPanel.add(gradeOrderButton);
            buttonPanel.add(resetButton);
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            flashcardPanel.add(questionLabel);
            flashcardPanel.add(Box.createRigidArea(new Dimension(350, 20)));
            answerTextField.setPreferredSize(new Dimension(300, 100));
            flashcardPanel.add(answerTextField);
            flashcardPanel.add(checkButton);
            flashcardPanel.add(nextButton);
            flashcardPanel.add(Box.createRigidArea(new Dimension( 100, 0)));
            flashcardPanel.add(gradeLabel);
            flashcardPanel.add(gradeTextField);
            flashcardPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            flashcardPanel.setBorder(BorderFactory.createEmptyBorder(20, 3, 0, 3));

            gradePanel.add(learnedLabel);
            gradePanel.add(learnedTextField);
            gradePanel.add(sizeLabel);
            /*gradePanel.add(Box.createRigidArea(new Dimension(50, 0)));
            gradePanel.add(gradeLabel);
            gradePanel.add(gradeTextField);*/
            gradePanel.setLayout(new FlowLayout());

            this.setLayout(new BorderLayout());
            this.add(buttonPanel, BorderLayout.NORTH);
            this.add(flashcardPanel, BorderLayout.CENTER);
            this.add(gradePanel, BorderLayout.SOUTH);

            if (initialQuestion.isPresent()){
                assert (initialGrade.isPresent());
                questionLabel.setText(initialQuestion.get());
                gradeTextField.setText(initialGrade.get().toString());
            }
            else {
                checkButton.setEnabled(false);
                nextButton.setEnabled(false);
                answerTextField.setEnabled(false);
                displayMessage("The lesson is learned! You can reset all grades by clicking the button.");
            }

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    controller.finishLearningLesson(mainFrame.lessonsList.getSelectedIndex());
                }
            });

            createButtonListener();

            this.setTitle("Lesson " + (mainFrame.lessonsList.getSelectedIndex() + 1));
            this.setSize(500, 300);
            this.setResizable(false);
            this.setLocationRelativeTo(mainFrame);
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            this.setVisible(true);
        }

        private void createButtonListener() {
            ActionListener buttonListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == randomOrderButton) {
                        controller.setRandomOrder();
                    } else if (e.getSource() == gradeOrderButton) {
                        controller.setGradeOrder();
                    } else if (e.getSource() == resetButton) {
                        controller.resetAllGradesInCurrentLesson();
                    } else if (e.getSource() == checkButton
                               || (e.getSource() == answerTextField && checkButton.isEnabled())) {
                        controller.checkAnswer(answerTextField.getText());
                        checkButton.setEnabled(false);
                        answerTextField.setEnabled(false);
                    } else if (e.getSource() == nextButton
                               || (e.getSource() == answerTextField && !checkButton.isEnabled())) {
                        boolean isCurrentQuestionAnswered = (answerTextField.getBackground() == Color.GREEN
                                                                || answerTextField.getBackground() == Color.RED);
                        if (!isCurrentQuestionAnswered && displaySkipQuestionWarning){
                            if (JOptionPane.showConfirmDialog(learningFrame, "Skipping a question will have the same consequences as answering it wrong. Skip?", "Skip question", JOptionPane.YES_NO_OPTION)
                                    != JOptionPane.YES_OPTION){
                                return;
                            }
                            displaySkipQuestionWarning = false;
                        }
                        controller.askNextQuestion(isCurrentQuestionAnswered);
                    }
                }
            };

            randomOrderButton.addActionListener(buttonListener);
            gradeOrderButton.addActionListener(buttonListener);
            resetButton.addActionListener(buttonListener);
            checkButton.addActionListener(buttonListener);
            nextButton.addActionListener(buttonListener);
            answerTextField.addActionListener(buttonListener);

            // TODO umozliwic pobranie nastepnego pytania enterem po sprawdzeniu odpowiedzi?
            // ale w sumie teraz mozna spacja. Przydaloby sie za to dac domyslny focus w okienku wpisywania

        }

        void correctAnswer(){
            answerTextField.setBackground(Color.GREEN);
            gradeTextField.setText(String.valueOf(Integer.parseInt(gradeTextField.getText())+1));
            if (gradeTextField.getText().equals(String.valueOf(Model.MAX_GRADE))){
                gradeTextField.setBackground(Color.GREEN);
                learnedTextField.setText(String.valueOf(Integer.parseInt(learnedTextField.getText())+1));
            }
        }
        void incorrectAnswer(final String correctAnswer){
            answerTextField.setBackground(Color.RED);
            answerTextField.setText(correctAnswer);
            gradeTextField.setText("1");
        }
        void updateFlashcard(String question, int grade){
            questionLabel.setText(question);
            answerTextField.setBackground(Color.WHITE);
            answerTextField.setText("");
            answerTextField.setEnabled(true);
            gradeTextField.setText(String.valueOf(grade));
            gradeTextField.setBackground(Color.WHITE);
            checkButton.setEnabled(true);
        }
        void lessonLearned(){
            nextButton.setEnabled(false);
            displayMessage("Congratulations! You have learned the whole lesson.");
        }
        void resetLearnedFlashcardsCounterToZero() {
            learningFrame.learnedTextField.setText("0");
            learningFrame.gradeTextField.setBackground(Color.WHITE);
            learningFrame.gradeTextField.setText("1");
            learningFrame.nextButton.setEnabled(true);
        }
    }

    private static class LoadingFrame extends JFrame {
        LoadingFrame() {
            JTextField textField = new JTextField("Loading resources...");
            textField.setDisabledTextColor(Color.BLACK);
            textField.setHorizontalAlignment(JTextField.CENTER);
            textField.setEnabled(false);
            this.add(textField);
            this.setSize(150, 70);
            this.setResizable(false);
            this.setLocationRelativeTo(null);
            this.setUndecorated(true);
            this.setVisible(true);
        }
    }

    View() {
        this.mainFrame = new MainFrame();
        this.loadingFrame = new LoadingFrame();
        this.mainFrame.setEnabled(false);
    }

    void setController(final Controller controller) {
        this.controller = controller;
    }

    void initializeLessonsList(final ArrayList<String> lessonsList){
        mainFrame.initializeLessonsList(lessonsList);
    }

    void closeLoadingResourcesFrame(){
        this.mainFrame.setEnabled(true);
        this.loadingFrame.setVisible(false);
        this.loadingFrame.dispose();
    }

    void openEditLessonFrame(final ArrayList<String> flashcardsQuestionsList) {
        editLessonFrame = new EditLessonFrame(flashcardsQuestionsList);
        mainFrame.setEnabled(false);
    }
    void closeEditLessonFrame(){
        mainFrame.setEnabled(true);
        editLessonFrame.setVisible(false);
        editLessonFrame.dispose();
    }

    void openEditFlashcardFrame(Optional<String> question, Optional<String> answer) {
        editFlashcardFrame = new EditFlashcardFrame(question, answer);
        editLessonFrame.setEnabled(false);
    }
    void closeEditFlashcardFrame() {
        editLessonFrame.setEnabled(true);
        editFlashcardFrame.setVisible(false);
        editFlashcardFrame.dispose();
    }

    void openLearnLessonFrame(Optional<String> initialQuestion, Optional<Integer> initialGrade,
                              long learnedFlashcardsCount, int allFlashcardsCount){
        learningFrame = new LearningFrame(initialQuestion, initialGrade, learnedFlashcardsCount, allFlashcardsCount);
        mainFrame.setEnabled(false);
    }
    void closeLearnLessonFrame(){
        mainFrame.setEnabled(true);
        learningFrame.setVisible(false);
        learningFrame.dispose();
    }

    void addNewLessonToTheList(String lessonName){
        mainFrame.lessonsListModel.addElement(lessonName);

        if (mainFrame.lessonsListModel.getSize() == 1) {
            mainFrame.editLessonButton.setEnabled(true);
            mainFrame.removeLessonButton.setEnabled(true);
            mainFrame.learnLessonButton.setEnabled(true);
        }
        mainFrame.lessonsList.setSelectedIndex(mainFrame.lessonsListModel.getSize()-1);
    }
    void removeLessonFromTheList(int lessonIndex){
        mainFrame.lessonsListModel.remove(lessonIndex);

        if (mainFrame.lessonsListModel.getSize() == 0) {
            mainFrame.editLessonButton.setEnabled(false);
            mainFrame.removeLessonButton.setEnabled(false);
            mainFrame.learnLessonButton.setEnabled(false);
        } else {
            if (lessonIndex == mainFrame.lessonsListModel.getSize()) {
                // Select the item above the removed one.
                mainFrame.lessonsList.setSelectedIndex(lessonIndex - 1);
            } else {
                // Select the item below the removed one.
                mainFrame.lessonsList.setSelectedIndex(lessonIndex);
            }
        }
    }

    void updateAddedOrEditedFlashcardOnTheList(int flashcardIndex){
        String question = editFlashcardFrame.questionTextField.getText();
        closeEditFlashcardFrame();

        if (flashcardIndex == editLessonFrame.flashcardsListModel.getSize()) {
            editLessonFrame.flashcardsListModel.addElement(question);
            editLessonFrame.flashcardsList.setSelectedIndex(editLessonFrame.flashcardsListModel.getSize()-1);
        }
        else {
            editLessonFrame.flashcardsListModel.set(flashcardIndex, question);
        }
        if (editLessonFrame.flashcardsListModel.getSize() == 1){
            editLessonFrame.editFlashcardButton.setEnabled(true);
            editLessonFrame.removeFlashcardButton.setEnabled(true);
        }
    }
    void removeFlashcardFromTheList(){
        int itemToRemoveIndex = editLessonFrame.flashcardsList.getSelectedIndex();
        editLessonFrame.flashcardsListModel.remove(itemToRemoveIndex);

        if (editLessonFrame.flashcardsListModel.getSize() == 0) {
            editLessonFrame.editFlashcardButton.setEnabled(false);
            editLessonFrame.removeFlashcardButton.setEnabled(false);
        } else {
            if (itemToRemoveIndex == editLessonFrame.flashcardsListModel.getSize()) {
                // Select the item above the removed one.
                editLessonFrame.flashcardsList.setSelectedIndex(itemToRemoveIndex - 1);
            } else {
                // Select the item below the removed one.
                editLessonFrame.flashcardsList.setSelectedIndex(itemToRemoveIndex);
            }
        }

    }

    void correctAnswer(){
        learningFrame.correctAnswer();
    }
    void incorrectAnswer(String correctAnswer){
        learningFrame.incorrectAnswer(correctAnswer);
    }
    void updateCurrentlyLearnedFlashcard(String question, int grade){
        learningFrame.updateFlashcard(question, grade);
    }
    void lessonLearned(){
        learningFrame.lessonLearned();
    }
    void resetLearnedFlashcardsCounterToZero() {
        learningFrame.resetLearnedFlashcardsCounterToZero();
    }

    void displayMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message);
    }
    int displayOptionDialog(String message, String title, String[] options, int defaultValue){
        return JOptionPane.showOptionDialog(mainFrame,  message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[defaultValue]);
    }
   /*void openAddLessonFrame() {
        addLessonFrame = new AddLessonFrame();
        mainFrame.setEnabled(false);
    }

    void closeAddLessonFrame() {
        mainFrame.setEnabled(true); // mainFrame must be enabled before addLessonFrame is disposed, otherwise it hides.
        addLessonFrame.setVisible(false);
        addLessonFrame.dispose();
    }*/
}
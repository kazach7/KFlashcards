import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 */
class FilesManager {
    private String lessonsListFile;
    private ArrayList<String> lessonFiles;

    FilesManager(){
        lessonsListFile = "res/lessonsList";
        loadLessonsListFromFile();
    }

    ArrayList<String> getLessonsList (){
        return lessonFiles;
    }
    private void loadLessonsListFromFile () {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(lessonsListFile))){
            lessonFiles = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lessonFiles.add(line);
            }

        } catch (FileNotFoundException e){
            // If the file does not exist, create it (and leave it empty).
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(lessonsListFile))){
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
            lessonFiles = null;
        }
        catch (IOException e){
            e.printStackTrace();
            lessonFiles = null;
        }
    }

    void addLesson (String lessonName){
        // Append the lesson name in the lessons list file.
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(lessonsListFile, true))){
            bufferedWriter.write(lessonName);
            bufferedWriter.write(System.getProperty("line.separator"));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        if (lessonFiles == null) {
            lessonFiles = new ArrayList<>();
        }
        lessonFiles.add(lessonName);

        // Create a file for the new lesson.
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("res/" + lessonName))){
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    void removeLesson (int lessonIndex) {
        String lessonName = null;
        try {
            lessonName = lessonFiles.get(lessonIndex);
            lessonFiles.remove(lessonIndex);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            try {
                Files.deleteIfExists(Paths.get(lessonName));

            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(lessonsListFile))) {
                StringBuffer buffer = new StringBuffer();
                String line;

                // Stop rewriting when you read the lesson name.
                while ((line = bufferedReader.readLine()) != null && !line.equals(lessonName)) {
                    buffer.append(line);
                    buffer.append(System.getProperty("line.separator"));
                }

                if (line == null) {
                    throw new FileNotFoundException("No such lesson name in lessonsFile: '" + lessonName + "'");
                }

                // Continue rewriting after the lesson name was read.
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append(System.getProperty("line.separator"));
                }

                // Replace the file content.
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(lessonsListFile))) {
                    bufferedWriter.write(buffer.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void addFlashcard (Flashcard flashcard, String lessonName){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("res/"+lessonName, true))){
            bufferedWriter.write(flashcard.getQuestion());
            bufferedWriter.newLine();
            bufferedWriter.write(flashcard.getAnswer());
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(flashcard.getGrade()));
            bufferedWriter.newLine();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    void editFlashcard (Flashcard flashcard, int flashcardIndex, String lessonName){
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader ("res/"+lessonName))){
            StringBuffer buffer = new StringBuffer();

            for (int i = 0; i < 3*flashcardIndex; ++i){
                buffer.append(bufferedReader.readLine());
                buffer.append(System.getProperty("line.separator"));
            }

            String toReplace;

            toReplace = flashcard.getQuestion();
            buffer.append(toReplace);                           // Append the new question to the buffer.
            buffer.append(System.getProperty("line.separator"));

            toReplace = flashcard.getAnswer();
            buffer.append(toReplace);
            buffer.append(System.getProperty("line.separator"));

            toReplace = String.valueOf(flashcard.getGrade());
            buffer.append(toReplace);
            buffer.append(System.getProperty("line.separator"));

            // Skip the out-of-date flashcard.
            for (int i = 0; i < 3; ++i){
                bufferedReader.readLine();
            }

            // Read the rest of the file.
            String line;
            while ((line = bufferedReader.readLine()) != null){
                buffer.append(line);
                buffer.append(System.getProperty("line.separator"));
            }

            // Replace the file content with the updated one.
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("res/"+lessonName))){
                bufferedWriter.write(buffer.toString());
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    void removeFlashcard(int flashcardIndex, String lessonName){
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("res/"+lessonName))){
            StringBuffer buffer = new StringBuffer();

            for (int i = 0; i < 3*flashcardIndex; ++i){
                buffer.append(bufferedReader.readLine());
                buffer.append(System.getProperty("line.separator"));
            }

            // Don't copy the flashcard which is to be removed.
            String toRemove = "";
            for (int i = 0; i < 3; ++i) {
                toRemove += bufferedReader.readLine();
            }

            String line;
            while ((line = bufferedReader.readLine()) != null){
                buffer.append(line);
                buffer.append(System.getProperty("line.separator"));
            }

            // Replace the file content.
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("res/"+lessonName))){
                bufferedWriter.write(buffer.toString());
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    void updateGrades(ArrayList<Flashcard> flashcards, String lessonName){
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("res/"+lessonName))){
            StringBuffer buffer = new StringBuffer();
            for (Flashcard flashcard : flashcards) {
                for (int j = 0; j < 2; ++j) {   // Rewrite question and the answer.
                    buffer.append(bufferedReader.readLine());
                    buffer.append(System.getProperty("line.separator"));
                }
                bufferedReader.readLine();  // Skip the out-of-date grade.
                buffer.append(String.valueOf(flashcard.getGrade())); // Append the new grade.
                buffer.append(System.getProperty("line.separator"));
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("res/"+lessonName))){
                bufferedWriter.write(buffer.toString());
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    /*
    String getSetsListFileName() {
        return setsListFileName;
    }

    void setSetsListFileName(String setsListFileName) {
        this.setsListFileName = setsListFileName;
    }*/
    Lesson loadLessonFromFile (String lessonName) throws FileNotFoundException{
        Lesson lesson = null;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("res/"+lessonName))) {
            String line;
            Flashcard flashcard;
            lesson = new Lesson(lessonName, this);

            while ((line = bufferedReader.readLine()) != null) {
                flashcard = new Flashcard(line, "n/a");
                if ((line = bufferedReader.readLine()) == null) {
                    break;
                }
                flashcard.setAnswer(line);
                if ((line = bufferedReader.readLine()) == null){
                    break;
                }
                flashcard.setGrade(Integer.parseInt(line));

                lesson.addFlashcard(flashcard); // Store the new flashcard in the lesson.
            }
        }
        catch (FileNotFoundException e){
            throw new FileNotFoundException(lessonName);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return lesson;
    }

    /*
    void updateSetFile (Lesson set){
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(set.getName()));

            Flashcard flashcard;
            /*for (int i = 0; i < set.getSize(); ++i){
                flashcard = set.getFlashcard(i);
                bufferedWriter.write(flashcard.getQuestion() + '\n');
                bufferedWriter.write(flashcard.getAnswer() + '\n');
            }*
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }*/
}

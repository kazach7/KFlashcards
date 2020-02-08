import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;

/**
 * Lesson is a single collection of flashcards, chosen by the user to learn.
 */
class Lesson {
    private String name;
    private ArrayList<Flashcard> flashcardsArray;
    private FilesManager filesManager;

    Lesson (String name, FilesManager filesManager){
        this.name = name;
        this.flashcardsArray = null;
        this.filesManager = filesManager;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    Flashcard getFlashcard (int index) {
        Flashcard flashcard = null;

        if (flashcardsArray != null) {
            try {
                flashcard = flashcardsArray.get(index);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        return flashcard;
    }

    ArrayList<Flashcard> getFlashcardsArray(){
        return flashcardsArray;
    }


    void addFlashcard (Flashcard flashcard) throws FileAlreadyExistsException{
        if (flashcardsArray == null){
            flashcardsArray = new ArrayList<>();
        }

        for (Flashcard existingFlashcard : flashcardsArray){
            if (existingFlashcard.getQuestion().equals(flashcard.getQuestion())){
                throw new FileAlreadyExistsException("Flashcard front not unique: " + flashcard.getQuestion());
            }
        }

        flashcardsArray.add(flashcard);
    }
    void editFlashcard (int flashcardIndex, Flashcard flashcard) throws IndexOutOfBoundsException, FileAlreadyExistsException{
        if (flashcardsArray == null) {
            throw new IndexOutOfBoundsException("The lesson is empty!");
        }

        int existingFlashcardIndex = 0;
        for (Flashcard existingFlashard : flashcardsArray){
            // Check questions uniqueness.
            if (existingFlashard.getQuestion().equals(flashcard.getQuestion())
                    && existingFlashcardIndex != flashcardIndex) // Exclude the flashcard that will be replaced.
            {
                throw new FileAlreadyExistsException("Flashcard front not unique: " + flashcard.getQuestion());
            }
            ++existingFlashcardIndex;
        }

        flashcardsArray.set(flashcardIndex, flashcard);
    }
    void removeFlashcard (int flashcardIndex) throws IndexOutOfBoundsException {
        flashcardsArray.remove(flashcardIndex);

        filesManager.removeFlashcard(flashcardIndex, this.name);
        /*try{
            if (setsFilesManager != null)
                setsFilesManager.updateSetFile(this);
            else
                throw new Exception("SetsFileManager not assigned in Set object!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}

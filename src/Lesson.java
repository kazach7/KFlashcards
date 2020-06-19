import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Lesson is a distinct collection of flashcards defined by the user.
 */
class Lesson implements Serializable {
    String name;
    ArrayList<Flashcard> flashcards;

    Lesson (String name){
        this(name, new ArrayList<>());
    }
    Lesson (String name, ArrayList<Flashcard> flashcards){
        this.name = name;
        this.flashcards = flashcards;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getSize(){
        return flashcards.size();
    }
    ArrayList<Flashcard> getFlashcardsArray(){
        return flashcards;
    }
/*
    Flashcard getFlashcard (int index) {
        assert (index < flashcards.size());
        return flashcards.get(index);
        /*
        Flashcard flashcard = null;
        if (flashcards != null) {
            try {
            flashcard = flashcards.get(index);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

        return flashcard;*
    }

    void addFlashcard (Flashcard flashcard) throws FileAlreadyExistsException{
        flashcards.
        for (Flashcard existingFlashcard : flashcards){
            if (existingFlashcard.getQuestion().equals(flashcard.getQuestion())){
                throw new FileAlreadyExistsException("Flashcard front not unique: " + flashcard.getQuestion());
            }
        }
        flashcards.add(flashcard);
    }
    void editFlashcard (int flashcardIndex, Flashcard flashcard) throws IndexOutOfBoundsException, FileAlreadyExistsException{
        int existingFlashcardIndex = 0;
        for (Flashcard existingFlashard : flashcards){
            // Check questions uniqueness.
            if (existingFlashard.getQuestion().equals(flashcard.getQuestion())
                    && existingFlashcardIndex != flashcardIndex) // Exclude the flashcard that will be replaced.
            {
                throw new FileAlreadyExistsException("Flashcard front not unique: " + flashcard.getQuestion());
            }
            ++existingFlashcardIndex;
        }

        flashcards.set(flashcardIndex, flashcard);
    }
    void removeFlashcard (int flashcardIndex) throws IndexOutOfBoundsException {
        flashcards.remove(flashcardIndex);
        /*try{
            if (setsFilesManager != null)
                setsFilesManager.updateSetFile(this);
            else
                throw new Exception("SetsFileManager not assigned in Set object!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }*
    }*/
}

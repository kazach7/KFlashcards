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
}

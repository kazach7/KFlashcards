# KFlashcards
A flashcards desktop app with custom learning modes and possibility to define multiple lessons.

The application was designed using the MVC pattern. The view was done in Swing, with listeners from AWT.

The application uses an embedded SQLite database. 
It creates the file for the database upon startup at the following location: ./db/db.db relative to the working directory.

### Running
To start the application, run the KFlashcards.jar file.

I recommend running on Windows for the best user experience. The application was also tested on Ubuntu and it works well, besides one frame looking strange. I'm planning on fixing it and making the app more suited for different plaforms.

### TODO
* Fix the 'Edit flashcard' frame, which doesn't look nice on Ubuntu.
* Refactor so that as little data as possible is kept in RAM.
* Consider adding a button to switch questions with answers in a whole lesson.
* Try to enable getting next question with ENTER after the answer has been checked (now it can be done with spacebar).
* Try to enable automatical focus on the textfield in the 'learning' frame.

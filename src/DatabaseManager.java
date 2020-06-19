import java.io.File;
import java.sql.*;
import java.util.ArrayList;

class DatabaseManager {
    String dbPath;

    DatabaseManager(String dbPath) throws Exception {
        this.dbPath = dbPath;

        boolean exists = true;
        try {
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                exists = false;
                dbFile.getParentFile().mkdirs();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            throw new Exception();
        }

        if (!exists) {
            this.initializeDatabase();
        }
    }

    ArrayList<Lesson> getAllLessons() throws Exception {
        ArrayList<Lesson> result = new ArrayList<>();

        Connection conn = null;
        Statement statement = null;
        String sql = "SELECT * FROM LESSON";
        try {
            conn = this.connect();
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                String lessonName = resultSet.getString("NAME");
                result.add(new Lesson(lessonName, this.getFlashcardsForLesson(lessonName)));
            }
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (statement != null){
                    statement.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        return result;
    }
    ArrayList<Flashcard> getFlashcardsForLesson(String lessonName) throws Exception{
        ArrayList<Flashcard> result = new ArrayList<>();

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "SELECT FRONT, BACK, GRADE FROM FLASHCARD WHERE (LESSON_NAME=?)";
        try {
            conn = this.connect();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, lessonName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result.add(new Flashcard(resultSet.getString("FRONT"),
                        resultSet.getString("BACK"),
                        resultSet.getInt("GRADE")));
            }
        } catch (SQLException e){
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (preparedStatement != null){
                    preparedStatement.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    void addLesson (Lesson lesson) throws Exception {
        String lessonSql =
                "INSERT INTO LESSON VALUES (?);";
        String flashcardSql = "INSERT INTO FLASHCARD VALUES (?,?,?,?);";

        Connection conn = null;
        PreparedStatement lessonStmt = null;
        PreparedStatement flashcardStmt= null;
        try {
            conn = this.connect();
            conn.setAutoCommit(false);

            lessonStmt = conn.prepareStatement(lessonSql);
            lessonStmt.setString(1, lesson.getName());
            lessonStmt.execute();
            for (Flashcard flashcard : lesson.getFlashcardsArray()) {
                flashcardStmt = conn.prepareStatement(flashcardSql);
                flashcardStmt.setString(1, flashcard.getQuestion());
                flashcardStmt.setString(2, flashcard.getAnswer());
                flashcardStmt.setInt(3, flashcard.getGrade());
                flashcardStmt.setString(4, lesson.getName());
                flashcardStmt.execute();
            }
            conn.commit();
        } catch (SQLException e){
            e.printStackTrace();
            if (conn != null){
                try {
                    conn.rollback();
                } catch (SQLException ex){
                    e.printStackTrace();
                }
            }
            throw new Exception();
        } finally {
            try {
                if (lessonStmt != null) {
                    lessonStmt.close();
                }
                if (flashcardStmt != null) {
                    flashcardStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    void removeLesson (String lessonName) throws Exception{
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "DELETE FROM LESSON WHERE (NAME = ?);";
        try {
            conn = this.connect();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, lessonName);
            preparedStatement.execute();
        } catch (SQLException e){
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (preparedStatement != null){
                    preparedStatement.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    void addFlashcard (String lessonName, Flashcard flashcard) throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "INSERT INTO FLASHCARD VALUES (?,?,?,?);";
        try {
            conn = this.connect();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, flashcard.getQuestion());
            preparedStatement.setString(2, flashcard.getAnswer());
            preparedStatement.setInt(3, flashcard.getGrade());
            preparedStatement.setString(4, lessonName);
            preparedStatement.execute();
        } catch (SQLException e){
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (preparedStatement != null){
                    preparedStatement.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    void editFlashcard (String lessonName, String oldQuestion, Flashcard flashcard) throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "UPDATE FLASHCARD SET FRONT=?, BACK=?, GRADE=? WHERE (FRONT=? AND LESSON_NAME=?)";
        try {
            conn = this.connect();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, flashcard.getQuestion());
            preparedStatement.setString(2, flashcard.getAnswer());
            preparedStatement.setInt(3, flashcard.getGrade());
            preparedStatement.setString(4, oldQuestion);
            preparedStatement.setString(5, lessonName);
            preparedStatement.execute();
        } catch (SQLException e){
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (preparedStatement != null){
                    preparedStatement.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    void removeFlashcard (String lessonName, String question) throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "DELETE FROM FLASHCARD WHERE (FRONT=? AND LESSON_NAME=?);";
        try {
            conn = this.connect();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, question);
            preparedStatement.setString(2, lessonName);
            preparedStatement.execute();
        } catch (SQLException e){
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (preparedStatement != null){
                    preparedStatement.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    void updateAllGradesInLesson (String lessonName, ArrayList<Flashcard> flashcards) throws Exception {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "UPDATE FLASHCARD SET GRADE=? WHERE (FRONT=? AND LESSON_NAME=?)";
        try {
            conn = this.connect();
            conn.setAutoCommit(false);
            for (Flashcard flashcard : flashcards){
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, flashcard.getGrade());
                preparedStatement.setString(2, flashcard.getQuestion());
                preparedStatement.setString(3, lessonName);
                preparedStatement.execute();
            }
            conn.commit();
        } catch (SQLException e){
            e.printStackTrace();
            if (conn != null){
                try {
                    conn.rollback();
                } catch (SQLException ex){
                    e.printStackTrace();
                }
            }
            throw new Exception();
        } finally {
            try {
                if (preparedStatement != null){
                    preparedStatement.close();
                }
                if (conn != null){
                    conn.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }
    private void initializeDatabase(){
        String lessonSql =
                "CREATE TABLE LESSON (" +
                    "NAME TEXT PRIMARY KEY" +
                ");";
        String flashcardSql =
                "CREATE TABLE FLASHCARD (" +
                    "FRONT TEXT NOT NULL, " +
                    "BACK TEXT NOT NULL, " +
                    "GRADE INTEGER NOT NULL, " +
                    "LESSON_NAME TEXT NOT NULL, " +
                    "PRIMARY KEY (FRONT, LESSON_NAME), " +
                    "FOREIGN KEY (LESSON_NAME) " +
                    "REFERENCES LESSON (NAME) " +
                        "ON UPDATE CASCADE " +
                        "ON DELETE CASCADE" +
                ");";
        Connection conn = null;
        PreparedStatement lessonStmt = null;
        PreparedStatement flashcardStmt = null;
        try {
            conn = this.connect();
            conn.setAutoCommit(false);

            lessonStmt = conn.prepareStatement(lessonSql);
            flashcardStmt = conn.prepareStatement(flashcardSql);
            lessonStmt.execute();
            flashcardStmt.execute();
            conn.commit();
        } catch (SQLException e){
            e.printStackTrace();
            if (conn != null){
                try {
                    conn.rollback();
                } catch (SQLException ex){
                    e.printStackTrace();
                }
            }
        } finally {
            try {
                if (lessonStmt != null) {
                    lessonStmt.close();
                }
                if (flashcardStmt != null) {
                    flashcardStmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }


}

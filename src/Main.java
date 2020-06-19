public class Main {
    public static void main (String[] args){
        DatabaseManager databaseManager = null;
        try {
            databaseManager = new DatabaseManager("db/db.db");
        } catch (Exception e){
            System.out.println("Fatal error. The database file, or any of the needed directories, could not be created.");
            System.exit(1);
        }
        View view = new View();
        Model model = new Model(view, databaseManager);
        Controller controller = new Controller(view, model);
        view.setController(controller);
    }
}

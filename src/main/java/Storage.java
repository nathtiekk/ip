import java.lang.reflect.Array;
import java.util.ArrayList;

class Storage {
    private static ArrayList<String> userTexts = new ArrayList<String>();

    public static void listUserTexts() {
        int counter = 1;
        System.out.println("____________________________________________________________");
        for(String userTexts: Storage.userTexts) {
            System.out.println(counter + ". " + userTexts);
            counter++;
        }
        System.out.println("____________________________________________________________");
    }

    public static void addUserTexts(String userTexts) {
        Storage.userTexts.add(userTexts);
        System.out.println("____________________________________________________________");
        System.out.println("added: " + userTexts);
        System.out.println("____________________________________________________________");
    }
}

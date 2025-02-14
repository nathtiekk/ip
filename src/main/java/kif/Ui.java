package kif;

public class Ui {

    public static void introduction() {
        String introduce =
                """
                ____________________________________________________________
                Hello! I'm Kif
                What can I do for you?
                ____________________________________________________________
                """;
        System.out.println(introduce);
    }

    public static void goodbye() {
        String goodbye =
                """
                ____________________________________________________________
                Kif: Bye. Hope to see you again soon!
                ____________________________________________________________""";
        System.out.println(goodbye);
    }

}

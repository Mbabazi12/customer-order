import java.util.Scanner;

public class MenuHandler {
    protected Scanner scanner = new Scanner(System.in);

    public int getChoice(String[] options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println(i + ". " + options[i]);
        }
        System.out.print("Choose: ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class WriteFile {
    public boolean write() {
        PrintStream file = null;
        try {
            file = new PrintStream("C:/Users/宋时平/Desktop/joke.txt");
        file.print(Main.jokesContent);
        file.flush();
        return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }finally {
            file.close();
        }
    }
}

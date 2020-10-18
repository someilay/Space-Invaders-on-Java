import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main {
    private static final int HEAD_WIDTH = 38;
    public  static final int WINDOW_WIDTH = 950;
    public  static final int WINDOW_HEIGHT = 480;

    public Main(){
        JFrame mainFrame = new JFrame();
        Game game = new Game();

        mainFrame.setBounds(20, 20, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainFrame.setTitle("Space Invaders");
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
        mainFrame.add(game);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            Image icon = ImageIO.read(new File("src/Image/Icon.png"));
            mainFrame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
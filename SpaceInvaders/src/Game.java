import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Time;

public class Game extends JPanel implements KeyListener, ActionListener {
    private Timer time;
    private GamePlay gameplay;
    private Image loseTitle;
    private Image victoryTitle;
    private boolean direction = true;
    private boolean fire = true;
    private boolean fire_pressed = false;
    private boolean pressed = false;
    private boolean first = true;
    private final int DELAY = 50;

    public Game(){
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        gameplay = new GamePlay();

        try {
            loseTitle = ImageIO.read(new File("src/Image/LoseTitle.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            victoryTitle = ImageIO.read(new File("src/Image/VictoryTitle.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        time = new Timer(DELAY, this);
        time.start();
    }

    public void paint(Graphics g){
        if (gameplay.GetAlive()){
            gameplay.Update();

            if (pressed)
                gameplay.UpdatePlayerPos(direction);

            if (fire_pressed && fire){
                gameplay.Fire();
                fire = false;
            }

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

            g.setColor(Color.GRAY);

            g.drawRect(5, 5, (GamePlay.CELL_SIZE_X + 1) * (GamePlay.FIELD_WIDTH), (GamePlay.CELL_SIZE_Y + 1) * (GamePlay.FIELD_HEIGHT));

            for (int i = 0; i < GamePlay.FIELD_WIDTH - 1; i++) {
                g.drawLine(5  + (i + 1) * (GamePlay.CELL_SIZE_X + 1), 5, 5  + (i + 1) * (GamePlay.CELL_SIZE_X + 1), 5 + GamePlay.FIELD_HEIGHT * (GamePlay.CELL_SIZE_Y + 1));
            }

            for (int i = 0; i < GamePlay.FIELD_HEIGHT- 1; i++) {
                g.drawLine(5, 5 + (i + 1) * (GamePlay.CELL_SIZE_Y + 1), 5  + GamePlay.FIELD_WIDTH * (GamePlay.CELL_SIZE_X + 1), 5 + (i + 1) * (GamePlay.CELL_SIZE_Y + 1));
            }

            Entity[][] f = gameplay.GetField();

            for (int y = 0; y < GamePlay.FIELD_HEIGHT; y++) {
                for (int x = 0; x < GamePlay.FIELD_WIDTH; x++) {
                    if (f[y][x] != null)
                        if (f[y][x].visible)
                            g.drawImage(f[y][x].GetImage(), 5 + 1 + (GamePlay.CELL_SIZE_X + 1) * x, 5 + 1 + (GamePlay.CELL_SIZE_Y + 1) * y, (GamePlay.CELL_SIZE_X - 1), (GamePlay.CELL_SIZE_Y - 1), null);
                }
            }

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Score: " + gameplay.GetScore(), 5, (GamePlay.FIELD_HEIGHT + 3) * GamePlay.CELL_SIZE_Y);
        }
        else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 18));

            if (gameplay.GetWin()){
                g.drawImage(victoryTitle, Main.WINDOW_WIDTH/2 - victoryTitle.getWidth(null)/2, Main.WINDOW_HEIGHT/3, null);
                g.drawString("Score: " + gameplay.GetScore(), Main.WINDOW_WIDTH/2 - victoryTitle.getWidth(null)/2, 2*Main.WINDOW_HEIGHT/3);
            }
            else{
                g.drawImage(loseTitle, Main.WINDOW_WIDTH/2 - loseTitle.getWidth(null)/2, Main.WINDOW_HEIGHT/3, null);
                g.drawString("Score: " + gameplay.GetScore(), Main.WINDOW_WIDTH/2 - loseTitle.getWidth(null)/2, 3*Main.WINDOW_HEIGHT/5);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        time.start();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            pressed = true;
            direction = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pressed = true;
            direction = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            fire_pressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            pressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            fire_pressed = false;
            fire = true;
        }
    }
}

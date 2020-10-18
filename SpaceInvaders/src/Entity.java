import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class Entity {
    boolean type = true; //true - enemy, false - player
    boolean is_bomb = false;// true - bomb, false - no bomb
    boolean is_laser = false;// true - laser, false - no laser
    boolean changed = false;
    boolean shield = false;
    boolean visible = true;
    private Image image;

    public Entity(){}

    public void SetImage(String path){
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Image GetImage(){
        return image;
    }
}

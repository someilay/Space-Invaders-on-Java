public class Enemy extends Entity{
    public Enemy(){
        super();
        type = true;
        is_bomb = false;
        is_laser = false;
        SetImage("src/Image/Invader.png");
    }
}

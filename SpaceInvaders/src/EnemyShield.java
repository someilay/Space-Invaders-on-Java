public class EnemyShield extends Entity {
    public EnemyShield(){
        super();
        type = true;
        is_bomb = false;
        is_laser = false;
        shield = true;
        SetImage("src/Image/InvaderShield.png");
    }
}

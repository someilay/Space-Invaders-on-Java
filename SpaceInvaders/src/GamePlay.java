import java.util.List;
import java.util.Random;

public class GamePlay {
    private Entity[][] field;
    private Random rnd = new Random();
    private int frame_counter = 0;
    private boolean direction = true; // true - right, false - left
    private final int ENEMY_START_POS_X = 7;
    private final int ENEMY_START_POS_Y = 0;
    private final int ENEMY_PER_LINE = 12;
    private final int ENEMY_PER_COLUMN = 5;
    public static final int FIELD_HEIGHT = 24;
    public static final int FIELD_WIDTH = 40;
    public static final int CELL_SIZE_Y = 16;
    public static final int CELL_SIZE_X = 22;
    private int player_pos = 0;
    private boolean isalive = true;
    private boolean fire_request = false;
    private boolean iswin = false;
    private int score = 0;
    private int additional_score = 0;

    public void Fire(){
        if (fire_request){
            field[FIELD_HEIGHT - 2][player_pos] = new PlayerLaser();
            fire_request = false;
        }
    }

    public int GetScore() { return score; }

    public boolean GetAlive(){
        return isalive;
    }

    public boolean GetWin() { return  iswin; }

    public GamePlay(){
        //Generate field array
        field = new Entity[FIELD_HEIGHT][FIELD_WIDTH];
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                field[y][x] = null;
            }
        }

        //Generate enemy
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (y >= ENEMY_START_POS_Y && y <= ENEMY_START_POS_Y + 2*(ENEMY_PER_COLUMN - 1) && (y - ENEMY_START_POS_Y) % 2 == 0){
                    if (x >= ENEMY_START_POS_X && x <= ENEMY_START_POS_X + 2*(ENEMY_PER_LINE - 1) && (x - ENEMY_START_POS_X) % 2 == 0){
                        if (y != ENEMY_START_POS_Y + 2*(ENEMY_PER_COLUMN - 1))
                            field[y][x] = new Enemy();
                        else
                            field[y][x] = new EnemyShield();
                    }
                }
            }
        }

        //Generate player
        field[FIELD_HEIGHT - 1][FIELD_WIDTH / 2] = new Player();
        player_pos = FIELD_WIDTH / 2;
    }

    public Entity[][] GetField(){
        return field;
    }

    public void UpdatePlayerPos(boolean direction){
        //True - right, False - left
        if (direction && player_pos < FIELD_WIDTH - 1){
            player_pos++;

            if (field[FIELD_HEIGHT - 1][player_pos] != null)
                if (field[FIELD_HEIGHT - 1][player_pos].is_bomb)
                    field[FIELD_HEIGHT - 1][player_pos - 1].visible = false;

            field[FIELD_HEIGHT - 1][player_pos] = field[FIELD_HEIGHT - 1][player_pos - 1];
            field[FIELD_HEIGHT - 1][player_pos - 1] = null;
        }
        else if (player_pos > 0){
            player_pos--;

            if (field[FIELD_HEIGHT - 1][player_pos] != null)
                if (field[FIELD_HEIGHT - 1][player_pos].is_bomb)
                    field[FIELD_HEIGHT - 1][player_pos + 1].visible = false;

            field[FIELD_HEIGHT - 1][player_pos] = field[FIELD_HEIGHT - 1][player_pos + 1];
            field[FIELD_HEIGHT - 1][player_pos + 1] = null;
        }

        isalive = field[FIELD_HEIGHT - 1][player_pos].visible;
    }

    public void Update(){
        int count = 0;

        if (frame_counter % 5 == 0)
            fire_request = true;

        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (field[y][x] != null)
                    if (field[y][x].type){
                        count++;
                    }
            }
        }

        int spead = 1 + (count * 10 / (ENEMY_PER_LINE * ENEMY_PER_COLUMN));

        if (frame_counter % spead == 0){
            EnemyMoves();
        }

        LasersMove();

        if (frame_counter % 3 == 0){
            EnemyLaserMoves();
        }

        if (count == 0){
            iswin = true;
            isalive = false;
            score += Math.max(0, (3000 - frame_counter));
        }
        else
            score = (ENEMY_PER_LINE * ENEMY_PER_COLUMN - count)*100 + additional_score;

        frame_counter++;
    }

    private void LasersMove(){
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (field[y][x] != null)
                    if (field[y][x].is_laser && !field[y][x].changed){
                        field[y][x].changed = true;
                        if (y > 0){
                            boolean enemy = false;

                            if (field[y - 1][x] != null)
                                if (field[y - 1][x].is_bomb | field[y - 1][x].type){
                                    enemy = true;

                                    if (field[y - 1][x].is_bomb)
                                        additional_score += 10;
                                }

                            if (!enemy)
                                field[y - 1][x] = field[y][x];
                            else if (field[y - 1][x].shield){
                                field[y - 1][x] = new Enemy();
                                additional_score += 50;
                            }
                            else
                                field[y - 1][x] = null;
                        }
                        field[y][x] = null;
                    }
            }
        }

        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (field[y][x] != null)
                    if (field[y][x].is_laser){
                        field[y][x].changed = false;
                    }
            }
        }
    }

    private void EnemyMoves(){
        boolean change = false;

        if (direction){
            for (int y = 0; y < FIELD_HEIGHT; y++) {
                if (field[y][FIELD_WIDTH - 1] != null)
                    if (field[y][FIELD_WIDTH - 1].type)
                        change = true;
            }
        }
        else{
            for (int y = 0; y < FIELD_HEIGHT; y++) {
                if (field[y][0] != null)
                    if (field[y][0].type)
                        change = true;
            }
        }

        if (!change){
            if (direction){
                for (int y = 0; y < FIELD_HEIGHT; y++) {
                    for (int x = 0; x < FIELD_WIDTH; x++) {
                        if (field[y][x] != null)
                            if (field[y][x].type && !field[y][x].changed){
                                field[y][x].changed = true;

                                boolean flag = false;
                                if (field[y][x + 1] != null)
                                    if (field[y][x + 1].is_laser)
                                        flag = true;

                                if (!flag)
                                    field[y][x + 1] = field[y][x];
                                else if (field[y][x].shield){
                                    field[y][x + 1] = new Enemy();
                                    field[y][x + 1].changed = true;
                                }
                                else
                                    field[y][x + 1] = null;

                                field[y][x] = null;
                            }
                    }
                }
            }
            else{
                for (int y = 0; y < FIELD_HEIGHT; y++) {
                    for (int x = 0; x < FIELD_WIDTH; x++) {
                        if (field[y][x] != null)
                            if (field[y][x].type && !field[y][x].changed){
                                field[y][x].changed = true;

                                boolean flag = false;
                                if (field[y][x - 1] != null)
                                    if (field[y][x - 1].is_laser)
                                        flag = true;

                                if (!flag)
                                    field[y][x - 1] = field[y][x];
                                else if (field[y][x].shield){
                                    field[y][x - 1] = new Enemy();
                                    field[y][x - 1].changed = true;
                                }
                                else
                                    field[y][x - 1] = null;

                                field[y][x] = null;
                            }
                    }
                }
            }
        }
        else{
            direction = !direction;

            for (int y = 0; y < FIELD_HEIGHT; y++) {
                for (int x = 0; x < FIELD_WIDTH; x++) {
                    if (field[y][x] != null)
                        if (field[y][x].type && !field[y][x].changed){
                            field[y][x].changed = true;

                            boolean flag = false;
                            if (field[y + 1][x] != null)
                                if (field[y + 1][x].is_laser)
                                    flag = true;

                            if (!flag)
                                field[y + 1][x] = field[y][x];
                            else if (field[y][x].shield){
                                field[y + 1][x] = new Enemy();
                                field[y + 1][x].changed = true;
                            }
                            else
                                field[y + 1][x] = null;

                            field[y][x] = null;
                        }
                }
            }
        }

        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (field[y][x] != null)
                    if (field[y][x].type){
                        field[y][x].changed = false;

                        boolean flag = true;

                        for (int i = y + 1; i < FIELD_HEIGHT; i++) {
                            if (field[i][x] != null)
                                if (field[i][x].type)
                                    flag = false;
                        }

                        if (rnd.nextInt() % 10 == 0 && flag){
                            if (field[y + 1][x] != null){
                                if (field[y + 1][x].is_laser){
                                    field[y + 1][x] = null;
                                }
                            }
                            else{
                                field[y + 1][x] = new EnemyLaser();
                            }
                        }

                        if (y == FIELD_HEIGHT - 2)
                            isalive = false;
                    }
            }
        }
    }

    private void EnemyLaserMoves(){
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (field[y][x] != null)
                    if (field[y][x].is_bomb && !field[y][x].changed){
                        field[y][x].changed = true;

                        if (y < FIELD_HEIGHT - 1){
                            if (field[y + 1][x] != null){
                                if (field[y + 1][x].is_laser){
                                    field[y + 1][x] = null;
                                    additional_score += 10;
                                }
                                else if (!field[y + 1][x].is_laser && !field[y + 1][x].type && !field[y + 1][x].is_bomb){
                                    field[y + 1][x].visible = false;
                                    isalive = false;
                                }
                            }
                            else
                                field[y + 1][x] = field[y][x];
                        }

                        field[y][x] = null;
                    }
            }
        }

        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_WIDTH; x++) {
                if (field[y][x] != null)
                    if (field[y][x].is_bomb){
                        field[y][x].changed = false;
                    }
            }
        }
    }
}

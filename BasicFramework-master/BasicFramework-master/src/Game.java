import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener {


    //window vars
    private final int MAX_FPS; //maximum refresh rate
    private final int WIDTH; //window width
    private final int HEIGHT; //window height
    public GAME_STATES GameState = GAME_STATES.MENU;
    Vector p, a, a2, p2, v, v2, v4, a4, p3, p4, p5, v5, a5, p6, v6, a6;
   // final float T = 10f;
    float friction, push;
    int sz, cooldown, sz2, sz3, sz4, sz5, sz6;
    int randomNum = 1, randomNum2 = 1, points = 0, randomNum3 = 1, randomNum4 = 1;
    private boolean accelerating = false;
    private boolean spacePressed;
    //double buffer strategy
    private BufferStrategy strategy;
    private ArrayList<Integer> keys = new ArrayList<>();
    //loop variables
    private boolean isRunning = true; //is the window running
    private long rest = 0; //how long to sleep the main thread
    //timing variables
    private float dt; //delta time
    private long lastFrame; //time since last frame
    private long startFrame; //time since start of frame
    private int fps; //current fps

    public Game(int width, int height, int fps) {
        super("My Game");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    //entry point for application
    public static void main(String[] args) {
        Game game = new Game(1080, 700, 60);
        game.run();
    }

    /*
     * init()
     * initializes all variables needed before the window opens and refreshes
     */
    void init() {
        //initializes window size
        setBounds(0, 0, WIDTH, HEIGHT);
        setResizable(false);

        //set jframe visible
        setVisible(true);

        //set default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create double buffer strategy
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        //set initial lastFrame var
        lastFrame = System.currentTimeMillis();
        addKeyListener(this);
        setFocusable(true);

        //set background window color
        setBackground(Color.DARK_GRAY);


        points = 0;
        p = new Vector(50, 50);
        p2 = new Vector(800, 600);
        p3 = new Vector(300, 300);
        p4 = new Vector(100, 600);
        p5 = new Vector(700, 30);
        p6 = new Vector(500, 100);


        v = new Vector(0, 0);
        v2 = new Vector(0, 0);
        v4 = new Vector(10, 10);
        v5 = new Vector(5, 5);
        v6 = new Vector(0, 0);


        friction = .99f;
        a = new Vector(0, 0);
        a2 = new Vector(1, 1);
        a4 = new Vector(10, 10);
        a5 = new Vector(3, 3);
        a6 = new Vector(1, 1);

        push = 100;
        cooldown = 1200;
        sz = 30;
        sz2 = 15;
        sz3 = 20;
        sz4 = 70;
        sz5 = 40;
        sz6 = 30;


    }

    private BufferedImage createTexture(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * update()
     * updates all relevant game variables before the frame draws
     */
    private void update() {
        //update current fps
        fps = (int) (1f / dt);
        randomNum = (int) (Math.random() - .5 + 10);
        randomNum2 = (int) (Math.random() * 360);
        randomNum3 = (int) (Math.random() * 900 + 30);
        randomNum4 = (int) (Math.random() * 600 + 30);

        handleKeys();

        switch ((GameState)) {
            case MENU:

            case SCORE:
                break;
            case PLAY:

                setCooldown();
                wallCollision();
                blockCollision();
                //makes acceleration stop if space isnt pressed
                if (!accelerating) {
                    a = new Vector(0, 0);
                }
                a2 = Vector.unit2D((float) Math.toRadians(randomNum * randomNum2));
                a2.mult(push * 5);

                a6 = Vector.unit2D((float) Math.toRadians(randomNum * randomNum2));
                a6.mult(push * 5);


                // v+= a *dt;
                // p += v* dt;
                v.add(Vector.mult(a, dt));
                v2.add(Vector.mult(a2, dt));

                p2.add(Vector.mult(v2, dt));

                v6.add(Vector.mult(a6, dt));

                p6.add(Vector.mult(v6, dt));

                v.mult(friction);
                v2.mult(friction);


                p.add(Vector.mult(v, dt));
                p2.add(Vector.mult(v2, dt));

                p6.add(Vector.mult(v6, dt));


                //following ai

                v4 = Vector.sub(p, p4);
                v4.setMag(100);
                v4.add(Vector.mult(a, dt));
                p4.add(Vector.mult(v4, dt));

                // predicting ai
             /*   v5 = Vector.sub(p,Vector.add(p5,Vector.mult(v5,T)));
                v5.setMag(100);
                v5.add(Vector.mult(a5, dt));
                p5.add(Vector.mult(v5, dt));
*/

                v5 = Vector.sub(p3,Vector.add(p5,Vector.mult(v,.25f)));
                v5.setMag(50);
                v5.add(Vector.mult(a5, dt));
                p5.add(Vector.mult(v5, dt));

                accelerating = false;
                spacePressed = false;

        }
    }


    private void setCooldown() {
        if (cooldown >= 12 && spacePressed) {
            cooldown -= 12;
        } else if (cooldown < 1200 && !spacePressed) {
            cooldown += 12;
        }
    }

    private void wallCollision() {
        // makes player bounce if they touch the wall
        if (p.x + sz > WIDTH - 14 || p.x < 17) {
            v.setX(v.x * -1);
            a.setX(a.x * -1);
            a = new Vector(0, 0);
        }

        if (p.y + sz > HEIGHT - 14 || p.y < 31) {
            v.setY(v.y * -1);
            a.setY(a.y * -1);
            a = new Vector(0, 0);
        }


        // makes block bounce off walls
        if (p2.x + sz2 > WIDTH - 14 || p2.x < 17) {
            v2.setX(v2.x * -1);
            a2.setX(a2.x * -1);
            a2 = new Vector(0, 0);
        }

        if (p2.y + sz2 > HEIGHT - 14 || p2.y < 31) {

            v2.setY(v2.y * -1);
            a2 = new Vector(0, 0);
        }

        if (p6.x + sz6 > WIDTH - 14 || p6.x < 17) {
            v6.setX(v6.x * -1);
            a6.setX(a6.x * -1);
            a6= new Vector(0, 0);
        }

        if (p6.y + sz6 > HEIGHT - 14 || p6.y < 31) {

            v6.setY(v6.y * -1);
            a6 = new Vector(0, 0);
        }
    }

    private void movePoints() {
        p3 = new Vector(randomNum3, randomNum4);
    }

    private void blockCollision() {

        //checks if player collides with yellow block
        if (checkCollision(p.x, p2.x, p.y, p2.y, sz, sz2)) {
            GameState = GAME_STATES.SCORE;
        }

        if (checkCollision(p.x, p4.x, p.y, p4.y, sz, sz4)) {
            GameState = GAME_STATES.SCORE;
        }
        //checks if fly collides with white block
        if (checkCollision(p2.x, p3.x, p2.y, p3.y, sz2, sz3)) {
            v2.setY(v2.y * -1);
            v2.setX(v2.x * -1);
            a2 = new Vector(0, 0);
        }
        // fly + red
        if (checkCollision(p4.x, p2.x, p4.y, p2.y, sz4, sz2)) {

            v2.setY(v2.y * -1);
            v2.setX(v2.x * -1);
            a2 = new Vector(0, 0);
            p2.add(Vector.mult(v2, dt * 3));
        }
      //fly + fly2
        if (checkCollision(p6.x, p2.x, p6.y, p2.y, sz6, sz2)) {

            v2.setY(v2.y * -1);
            v2.setX(v2.x * -1);
            a2 = new Vector(0, 0);
            p2.add(Vector.mult(v2, dt * 3));
        }


        // fly 2
        if (checkCollision(p6.x, p3.x, p6.y, p3.y, sz6, sz3)) {
            v6.setY(v6.y * -1);
            v6.setX(v6.x * -1);
            a6 = new Vector(0, 0);
        }
       //fly 2
        if (checkCollision(p4.x, p6.x, p4.y, p6.y, sz4, sz6)) {

            v6.setY(v2.y * -1);
            v6.setX(v2.x * -1);
            a6 = new Vector(0, 0);
            p6.add(Vector.mult(v2, dt * 15));
        }

        // checks if white and GReen blocks collide
        if (checkCollision(p.x, p3.x, p.y, p3.y, sz, sz3)) {
            points++;
            movePoints();
        }
        // White + red
        if (checkCollision(p4.x, p3.x, p4.y, p3.y, sz4, sz3)) {
            movePoints();
            points--;
            sz4 += 15;

            //white pink
            if (checkCollision(p5.x, p3.x, p5.y, p3.y, sz5, sz3)) {
                movePoints();
                points--;
                v5.mult(1.2f);
            }

            //red pink
            if (checkCollision(p5.x, p4.x, p5.y, p4.y, sz5, sz4)) {
                v5.setY(v5.y * -1);
                v5.setX(v5.x * -1);
                a5 = new Vector(0, 0);
                p5.add(Vector.mult(v5, dt * 3));
            }
            //pink green
            if (checkCollision(p.x, p5.x, p.y, p5.y, sz, sz5)) {
                GameState = GAME_STATES.SCORE;
            }

        }


    }

    private boolean checkCollision(float x, float x2, float y, float y2, int sz, int sz2) {

        return x < x2 + sz2 &&
                x + sz > x2 &&
                y < y2 + sz2 &&
                y + sz > y2;


    }


    /*
     * draw()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
    private void draw() {
        //get canvas
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

        //clear screen
        g.clearRect(0, 0, WIDTH, HEIGHT);
        switch (GameState) {
            case MENU:
                g.fillRect(0, 0, WIDTH, HEIGHT);


                g.drawImage(createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\Game Over.jpg"), 0, 0, WIDTH, HEIGHT, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 100));
                g.setColor(Color.WHITE);

                g.drawString("Press ENTER to play", 50, 550);

                break;
            case PLAY:
                g.clearRect(0, 0, WIDTH, HEIGHT);
                //player
                g.setColor(Color.GREEN);
                g.fillRect(p.ix, p.iy, sz, sz);

                //random
                g.setColor(Color.BLACK);
                g.fillRect(p2.ix, p2.iy, sz2, sz2);

                //points
                g.setColor(Color.WHITE);
                g.fillRect(p3.ix, p3.iy, sz3, sz3);

                //tracking
                g.setColor(Color.RED);
                g.fillRect(p4.ix, p4.iy, sz4, sz4);

                //predicting
                g.setColor(Color.PINK);
                g.fillRect(p5.ix, p5.iy, sz5, sz5);

                //
                g.setColor(Color.BLACK);
                g.fillRect(p6.ix, p6.iy, sz6, sz6);


                //Roof + Floor
                g.setColor(Color.YELLOW);
                g.fillRect(0, 0, WIDTH, 34);

                g.setColor(Color.YELLOW);
                g.fillRect(0, 686, WIDTH, 1080);
                // Walls
                g.setColor(Color.YELLOW);
                g.fillRect(0, 0, 16, HEIGHT);

                g.setColor(Color.YELLOW);
                g.fillRect(1066, 0, 1080, HEIGHT);


                //draw fps
                g.setColor(Color.GREEN);
                g.drawString(Long.toString(fps), 10, 40);
                g.drawString(Long.toString(cooldown), 540, 50);
                g.drawString(Long.toString(points), 900, 50);
                //release resources, show the buffer
                break;
            case SCORE:
                g.clearRect(0, 0, WIDTH, HEIGHT);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, WIDTH, HEIGHT);


                g.drawImage(createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\Game Over.jpg"), 0, 0, WIDTH, HEIGHT, null);

                g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
                g.setColor(Color.WHITE);
                g.drawString("Press R to restart", 200, 550);
                break;
        }


        g.dispose();
        strategy.show();
    }


    public void handleKeys() {
        for (int i = 0; i < keys.size(); i++) {

            switch (GameState) {

                case MENU:
                    switch (keys.get(i)) {
                        case KeyEvent.VK_ENTER:

                            init();
                            GameState = GAME_STATES.PLAY;


                            break;
                    }
                case PLAY:

                    switch (keys.get(i)) {

                        case KeyEvent.VK_UP:
                            a = Vector.unit2D((float) Math.toRadians(-90));
                            a.mult(push);
                            accelerating = true;
                            break;
                        case KeyEvent.VK_DOWN:
                            a = Vector.unit2D((float) Math.toRadians(90));
                            a.mult(push);

                            accelerating = true;
                            break;

                        case KeyEvent.VK_LEFT:
                            a = Vector.unit2D((float) Math.toRadians(180));
                            a.mult(push);
                            accelerating = true;
                            break;

                        case KeyEvent.VK_RIGHT:
                            a = Vector.unit2D((0));
                            a.mult(push);
                            accelerating = true;
                            break;

                        case KeyEvent.VK_SPACE:
                            spacePressed = true;
                            if (cooldown > 0) {
                                a.mult(5);

                            }


                            break;


                    }

                    break;
                case SCORE:
                    switch (keys.get(i)) {
                        case KeyEvent.VK_R:
                            init();
                            GameState = GAME_STATES.MENU;


                            break;
                    }
            }
        }

    }


    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (!keys.contains(keyEvent.getKeyCode())) {
            keys.add(keyEvent.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        for (int i = keys.size() - 1; i >= 0; i--) {
            if (keyEvent.getKeyCode() == keys.get(i))
                keys.remove(i);
        }
    }


    /*
     * run()
     * calls init() to initialize variables
     * loops using isRunning
        * updates all timing variables and then calls update() and draw()
        * dynamically sleeps the main thread to maintain a framerate close to target fps
     */
    public void run() {
        init();

        while (isRunning) {


            //new loop, clock the start
            startFrame = System.currentTimeMillis();

            //calculate delta time
            dt = (float) (startFrame - lastFrame) / 1000;

            //update lastFrame for next dt
            lastFrame = startFrame;

            //call update and draw methods
            update();
            draw();

            //dynamic thread sleep, only sleep the time we need to cap the framerate
            //rest = (max fps sleep time) - (time it took to execute this frame)
            rest = (1000 / MAX_FPS) - (System.currentTimeMillis() - startFrame);
            if (rest > 0) { //if we stayed within frame "budget", sleep away the rest of it
                try {
                    Thread.sleep(rest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    enum GAME_STATES {
        MENU,
        PLAY,
        SCORE;
    }

}

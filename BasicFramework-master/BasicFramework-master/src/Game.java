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

//todo  upgrade system(2 hours)
public class Game extends JFrame implements KeyListener {

    //window vars
    private final int MAX_FPS; //maximum refresh rate
    private final int WIDTH; //window width
    private final int HEIGHT; //window height
    public GAME_STATES GameState = GAME_STATES.MENU;
    Vector p, a, a2, p2, v, v2, v4, a4, p3, p4, p5, v5, a5, p6, v6, a6, p7,v7, a7;
   // final float T = 10f;
    float friction, push,time;
    int sz, cooldown, sz2, sz3, sz4, sz5, sz6,sz7,coolRate;
    int randomNum = 1, randomNum2 = 1, points = 0, randomNum3 = 1, randomNum4 = 1, c,coins,counter,counter2,cooldownMax,gCounter,iCounter;
    private boolean accelerating = false,right;
    private boolean spacePressed,invincible;
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




        points = 0;
        p = new Vector(50, 50);
        p2 = new Vector(800, 600);
        p3 = new Vector(300, 300);
        p4 = new Vector(100, 600);
        p5 = new Vector(700, 30);
        p6 = new Vector(500, 100);
        p7 = new Vector(400, 500);


        v = new Vector(0, 0);
        v2 = new Vector(0, 0);
        v4 = new Vector(10, 10);
        v5 = new Vector(5, 5);
        v6 = new Vector(0, 0);
        v7 = new Vector(10, 10);


        friction = .99f;
        a = new Vector(0, 0);
        a2 = new Vector(1, 1);
        a4 = new Vector(10, 10);
        a5 = new Vector(3, 3);
        a6 = new Vector(1, 1);
        a7 = new Vector(10, 10);

        push = 100;
        cooldown = 10;
        cooldownMax = 10;
        sz = 110;
        sz2 = 80;
        sz3 = 40;
        sz4 = 110;
        sz5 = 40;
        sz6 = 60;
        sz7 = 50;
        c= 0;
        coins=0;
        points=0;
        counter=0;
        counter2=0;
        coolRate=0;
        invincible=false;
        time=0;
        gCounter=0;

    }

    private BufferedImage createTexture(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Image loadTextureGif(String filepath){
        try{
            return new ImageIcon(new File(filepath).toURI().toURL()).getImage();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void movement(Vector v, Vector a,Vector p, float dt){
        v.add(Vector.mult(a, dt));
        v.mult(friction);
        p.add(Vector.mult(v, dt));

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
                counter++;
                break;
            case PLAY:
                gCounter++;
                setCooldown();
                wallCollision();
                blockCollision();
                //makes acceleration stop if space isnt pressed
                if (!accelerating) {
                    a = new Vector(0, 0);
                }
                a2 = Vector.unit2D((float) Math.toRadians(randomNum * randomNum2));
                a2.mult(push * 5);
                randomNum = (int) (Math.random() - .5 + 10);
                randomNum2 = (int) (Math.random() * 360);
                a6 = Vector.unit2D((float) Math.toRadians(randomNum * randomNum2));
                a6.mult(push * 5);

                if(invincible){
                    iCounter++;
                    if(iCounter>=180){
                        invincible=false;
                    }
                }


                movement(v,a,p,dt);

                movement(v2,a2,p2,dt);

                movement(v6,a6,p6,dt);

                //wander

                v7 = Vector.sub(p,Vector.add(p7,Vector.mult(v,50f)));
                v7.setMag(50);
                v7.add(Vector.mult(a7, dt));
                p7.add(Vector.mult(v7, dt));




                //following ai

                v4 = Vector.sub(p, p4);
                v4.setMag(75);
                v4.add(Vector.mult(a, dt));
                p4.add(Vector.mult(v4, dt));


                //[redicting ai
                v5 = Vector.sub(p3,Vector.add(p5,Vector.mult(v,.25f)));
                v5.setMag(25);
                v5.add(Vector.mult(a5, dt));
                p5.add(Vector.mult(v5, dt));


                accelerating = false;
                spacePressed = false;


        }
    }


    private void setCooldown() {
        if (cooldown >= 1 && spacePressed) {
            cooldown -= 1;
        } else if (cooldown < cooldownMax&& !spacePressed) {
           cooldown+=1;

        }
    }

    private void wallCollision() {
        // makes player bounce if they touch the wall
        if (p.x + sz > WIDTH  || p.x < 0) {
            v.setX(v.x * -1);
            a.setX(a.x * -1);
            a = new Vector(0, 0);
            p.add(Vector.mult(v,dt));
        }

        if (p.y + sz > HEIGHT  || p.y < 30) {
            v.setY(v.y * -1);
            a.setY(a.y * -1);
            a = new Vector(0, 0);
            p.add(Vector.mult(v,dt));
        }


        // makes block bounce off walls
        if (p2.x + sz2 > WIDTH  || p2.x < 0) {
            v2.setX(v2.x * -1);
            a2.setX(a2.x * -1);
            a2 = new Vector(0, 0);
        }

        if (p2.y + sz2 > HEIGHT || p2.y < 500) {

            v2.setY(v2.y * -1);
            a2 = new Vector(0, 0);
        }
        //wander
        if (p7.y + sz7 > HEIGHT || p7.y<25) {

            v7.setY(v2.y * -1);
            a7.setY(a7.y * -1);
            a7= new Vector(0, 0);
            p7.add(Vector.mult(v7,dt*3));
        }

        if (p7.x + sz7 > WIDTH || p7.x < 0) {
            v7.setX(v7.x * -1);
            a7.setX(a7.x * -1);
            a7 = new Vector(0, 0);
            p7.add(Vector.mult(v7,dt*3));
        }

        if (p6.x + sz6 > WIDTH  || p6.x <0) {
            v6.setX(v6.x * -1);
            a6.setX(a6.x * -1);
            a6= new Vector(0, 0);
        }

        if (p6.y + sz6 > HEIGHT  || p6.y < 14) {

            v6.setY(v6.y * -1);
            a6 = new Vector(0, 0);
        }

    }

    private void movePoints() {
        p3 = new Vector(randomNum3, randomNum4);
    }

    private void blockCollision()
    {
if(!invincible) {
    //fly
    if (checkCollision(p.x, p2.x, p.y, p2.y, sz, sz2)) {
        GameState = GAME_STATES.SCORE;
    }
    //red
    if (checkCollision(p.x, p4.x, p.y, p4.y, sz, sz4) && points >= 12) {
        GameState = GAME_STATES.SCORE;
    }
    //fly2
    if (checkCollision(p6.x, p.x, p6.y, p.y, sz6, sz) && points >= 22) {
        GameState = GAME_STATES.SCORE;
    }

    //pink green
    if (checkCollision(p.x, p5.x, p.y, p5.y, sz, sz5) && points >= 17) {
        GameState = GAME_STATES.SCORE;
    }

    if (checkCollision(p.x, p7.x, p.y, p7.y, sz, sz7) && points >= 7) {
        GameState = GAME_STATES.SCORE;
    }


    //checks if white and GReen blocks collide
    if (checkCollision(p.x, p3.x, p.y, p3.y, sz, sz3)) {
        points++;
        coins++;
        movePoints();

    }
}
        //checks if fly collides with white block
        if (checkCollision(p2.x, p3.x, p2.y, p3.y, sz2, sz3)) {
            movePoints();
            points--;
            sz2 += 30;
        }


        // White + red
        if (checkCollision(p4.x, p3.x, p4.y, p3.y, sz4, sz3)&& points>=11) {
            movePoints();
            points--;
            sz4 += 30;
        }

        // White + Pink
        if (checkCollision(p5.x, p3.x, p5.y, p3.y, sz5, sz3)&& points>=16) {
            movePoints();
            points--;
            sz5 += 30 ;
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




                Image backgroundF = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\background.png");
                g.drawImage(backgroundF ,0,0 ,WIDTH,HEIGHT,null);

                g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
                g.setColor(Color.RED);
                g.drawString("Press 1 to decrease size (5 coins)", 20, 80);

                g.setColor(Color.RED);
                g.drawString("Press 2 to increase speed boost length (1 coin)", 20, 160);

                g.setColor(Color.RED);
                g.drawString("Press 3 to become invincible for 3 seconds (5 coins)", 20, 240);

                g.setColor(Color.RED);
                g.drawString("Press 4 to fire a harpoon at the shark (5 coins)", 20, 320);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 100));
                g.setColor(Color.BLACK);
                g.drawString("Press ENTER to play", 50, 550);

                break;
            case PLAY:
                g.clearRect(0, 0, WIDTH, HEIGHT);

                Image background = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\background.png");
                g.drawImage(background ,0,0 ,WIDTH,HEIGHT,null);

                g.setColor(Color.GREEN);
               g.drawRect(350,30 ,cooldownMax ,35 );




                g.setColor(Color.GREEN);
               g.fillRect(350,30 , cooldown,35 );

                //player
                if(!right) {
                    Image heli = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\helicopter.png");
                    g.drawImage(heli, p.ix,p.iy,sz,sz,null);
                }else{
                    Image heli = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\helicopter2.png");
                    g.drawImage(heli, p.ix,p.iy,sz,sz,null);
                }



                //random
                Image crab = loadTextureGif("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\crab.gif");


                g.drawImage(crab, p2.ix,p2.iy,sz2,sz2 ,null);
                //points
                Image sprite = loadTextureGif("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\spincoin.gif");


                g.drawImage(sprite, p3.ix,p3.iy,sz3,sz3,null);




                if(points>=10) {
                    Image helicopter = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\helicopter3nd.png");
                    g.drawImage(helicopter, p4.ix, p4.iy, sz4, sz4, null);
                    if(points>11) {
                        //tracking
                        Image helicopterR = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\helicopter3.png");
                        g.drawImage(helicopterR, p4.ix, p4.iy, sz4, sz4, null);
                    }
                }

                if(points>=5) {
                    Image owlN = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\airballonng.png");
                    g.drawImage(owlN, p7.ix, p7.iy, sz7, sz7, null);
                   if(points>= 7) {
                       //tracking
                       Image owl = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\airballon.png");
                       g.drawImage(owl, p7.ix, p7.iy, sz7, sz7, null);
                   }
                }

              if(points>=15) {
                  Image dragonN = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\dragonnd.png");
                  g.drawImage(dragonN, p5.ix, p5.iy, sz5, sz5, null);
                    if(points>=17) {
                        //predicting
                        Image dragon = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\dragon.png");
                        g.drawImage(dragon, p5.ix, p5.iy, sz5, sz5, null);
                    }
              }

               if(points>=20) {
                   Image cth = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\cthulhund.png");
                   g.drawImage(cth, p6.ix, p6.iy, sz6, sz6, null);
                    if(points>=22) {
                        //fly2
                        Image cthnd = createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\cthulhu.png");
                        g.drawImage(cthnd, p6.ix, p6.iy, sz6, sz6, null);
                    }

               }




                //draw fps
                g.setColor(Color.GREEN);
                g.drawString(Long.toString(fps), 10, 40);
                g.drawString(Long.toString(coins), 950, 50);
                g.setColor(Color.BLACK);
                g.drawString(("Points:"), 20, 65);
                g.drawString(Long.toString(points), 60, 65);
                //release resources, show the buffer
                g.dispose();
                strategy.show();
                break;
            case SCORE:
                g.clearRect(0, 0, WIDTH, HEIGHT);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, WIDTH, HEIGHT);



                g.drawImage(createTexture("C:\\Users\\IGMAdmin\\Desktop\\NG\\BasicFramework-master\\BasicFramework-master\\Textures\\game over.png"), 0, 0, WIDTH, HEIGHT, null);
                g.setFont(new Font("TimesRoman", Font.PLAIN, 50));

               if (counter%40==0) {
                   counter2++;

               }
                if(counter2%2==0) {
                    g.setColor(Color.BLACK);
                    g.drawString("Press R to restart", 330, 550);
                }
                g.setColor(Color.GREEN);
                g.drawString("Score:", 330, 450);
                g.setColor(Color.GREEN);
                g.drawString(Long.toString(points), 490, 450);
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
                            right=true;
                            break;

                        case KeyEvent.VK_RIGHT:
                            a = Vector.unit2D((0));
                            a.mult(push);
                            accelerating = true;
                            right = false;
                            break;

                        case KeyEvent.VK_SPACE:
                            spacePressed = true;
                            if (cooldown > 0) {
                                a.mult(5);

                            }
                            break;
                        case KeyEvent.VK_1:

                            if (coins >= 5&& sz >25) {
                             sz-=20;
                            coins-=5;
                            }

                            break;

                        case KeyEvent.VK_2:

                            if (coins >= 1&&cooldownMax<=250) {
                             cooldownMax+=25;
                             coins-=1;
                            }

                            break;
                        case KeyEvent.VK_3:

                            if (coins >= 5) {
                                invincible=true;
                                coins-=5;
                            }

                            break;

                        case KeyEvent.VK_4:

                           //todo

                            break;
                        case KeyEvent.VK_P:
                            points++;
                            coins++;


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

        SCORE
    }

}

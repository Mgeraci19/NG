import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener {


    boolean gameOver = false, accelerating= false, spacePressed;
    //window vars
    private final int MAX_FPS; //maximum refresh rate
    private final int WIDTH; //window width
    private final int HEIGHT; //window height

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

    Vector p;
    Vector p2;
    Vector v;
    Vector a;

    float friction = .99f;
    float pushX;
    float pushY;
    int sz, cooldown=600, sz2;


    public Game(int width, int height, int fps){
        super("My Game");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    /*
     * init()
     * initializes all variables needed before the window opens and refreshes
     */
    void init(){
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



        p = new Vector(50, 50);
        p2= new Vector(300, 300);
        v = new Vector (0, 0);
        a = new Vector (0,0);
        pushX = 100;
        pushY= 100;

        sz=30;
        sz2=30;
    }

    /*
     * update()
     * updates all relevant game variables before the frame draws
     */
    private void update(){
        //update current fps
        fps = (int)(1f/dt);
        handleKeys();
        if(cooldown>0){
            cooldown--;
        }


        if(p.x + sz > WIDTH-14|| p.x<17){
          gameOver = true;
        }

        if(p.y+ sz > HEIGHT-14 || p.y<31){
          gameOver = true;
        }
        if (!accelerating){
            a = new Vector(0,0);
        }

        if(     p.x<p2.x+sz2 &&
               p.x+sz>p2.x &&
                p.y<p2.y+sz2&&
                p.y + sz >p2.y){
           gameOver = true;
       }

        // v+= a *dt;
        // p += v* dt;
        v.add(Vector.mult(a,dt));
        v.mult(friction);
        p.add(Vector.mult(v,dt));
        accelerating = false;

    }

    /*
     * draw()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
    private void draw(){
        //get canvas
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

        //clear screen
        g.clearRect(0,0,WIDTH, HEIGHT);


        if (gameOver == false) {
            g.setColor(Color.GREEN);
            g.fillRect(p.ix, p.iy, sz, sz);

            g.setColor(Color.YELLOW);
            g.fillRect(p2.ix, p2.iy, sz2, sz2);


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
            g.drawString(Long.toString(cooldown), 100, 40);
            //release resources, show the buffer

        } else{
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 100));
            g.setColor(Color.RED);

            g.drawString("Game Over", 200, 350);

            g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
            g.setColor(Color.WHITE);
            g.drawString("Press Space to restart", 200, 550);

        }
        g.dispose();
        strategy.show();

    }


    private void handleKeys(){
        for(int i =0; i <keys.size();i++){

                if (!gameOver) {

                    switch(keys.get(i)){
                    case KeyEvent.VK_UP:
                        a = Vector.unit2D((float) Math.toRadians(-90));
                        a.mult(pushY);
                        accelerating = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        a = Vector.unit2D((float) Math.toRadians(90));
                        a.mult(pushY);

                        accelerating = true;
                        break;

                    case KeyEvent.VK_LEFT:
                        a = Vector.unit2D((float) Math.toRadians(180));
                        a.mult(pushX);
                        accelerating = true;
                        break;

                    case KeyEvent.VK_RIGHT  :
                        a = Vector.unit2D((0));
                        a.mult(pushX);
                        accelerating = true;
                        break;

                        case KeyEvent.VK_SPACE:

                                if(cooldown>0) {
                                    a.mult(5);
                                    spacePressed = true;
                                }



                            break;

                }
                }else{
                        switch(keys.get(i)){
                            case KeyEvent.VK_SPACE:
                                p = new Vector(50, 50);
                              gameOver= false;
                                a = new Vector(0,0);
                                v = new Vector(0,0);
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
        if(!keys.contains(keyEvent.getKeyCode())){
            keys.add(keyEvent.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
for( int i = keys.size() -1; i>= 0; i--){
    if(keyEvent.getKeyCode()== keys.get(i))
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
    public void run(){
        init();

        while(isRunning){


            //new loop, clock the start
            startFrame = System.currentTimeMillis();

            //calculate delta time
            dt = (float)(startFrame - lastFrame)/1000;

            //update lastFrame for next dt
            lastFrame = startFrame;

            //call update and draw methods
            update();
            draw();

            //dynamic thread sleep, only sleep the time we need to cap the framerate
            //rest = (max fps sleep time) - (time it took to execute this frame)
            rest = (1000/MAX_FPS) - (System.currentTimeMillis() - startFrame);
            if(rest > 0){ //if we stayed within frame "budget", sleep away the rest of it
                try{ Thread.sleep(rest); }
                catch (InterruptedException e){ e.printStackTrace(); }
            }
        }

    }

    //entry point for application
    public static void main(String[] args){
        Game game = new Game(1080, 700, 60);
        game.run();
    }

}

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener {


    boolean gameOver = false, accelerating= false, spacePressed, wallHit= false;
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

    Vector p,a, a2, p2,v,v2, p3,p5;

    float friction , push;

    final float T =0.25f;

    int sz, cooldown, sz2,sz3, sz5;

    int randomNum=1, randomNum2 = 1,points=0, randomNum3= 1,randomNum4=1;


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
        p2= new Vector(800, 600);
        p3= new Vector(300, 300);
        p5 = new Vector (1800,0);
        v = new Vector (0, 0);
        v2 = new Vector(0, 0);



        friction = .99f;
        a = new Vector (0,0);
        a2 = new Vector (1,1);

        push= 100;
        cooldown = 1200;
        sz=30;
        sz2=50;
        sz3 = 20;
        sz5 = 400;


    }

    /*
     * update()
     * updates all relevant game variables before the frame draws
     */
    private void update(){
        //update current fps
        fps = (int)(1f/dt);
        randomNum = (int)(Math.random()-.5 +10);
        randomNum2 = (int)(Math.random() *360);
        randomNum3= (int)(Math.random()*900+ 30);
        randomNum4= (int)(Math.random()*600+30);
        p5.ix --;

        handleKeys();
        setCooldown();
        wallCollision();
        blockCollision();
        //makes acceleration stop if space isnt pressed
        if (!accelerating){
            a = new Vector(0,0);
        }
        a2 = Vector.unit2D((float) Math.toRadians(randomNum*randomNum2));
        a2.mult(push*15);
        // v+= a *dt;
        // p += v* dt;
        v.add(Vector.mult(a,dt));
        v2.add(Vector.mult(a2,dt));

        p2.add(Vector.mult(v2,dt));
        v.mult(friction);
        v2.mult(friction);
        p.add(Vector.mult(v,dt));
        p2.add(Vector.mult(v2,dt));


        //following ai
        /*v4 = Vector.sub(p3,Vector.add(p,Vector.mult(v,T)));

        v4.setMag(push);
        v4= Vector.sub(p3,p);
        v4.setMag(push);
*/

        accelerating = false;
        spacePressed= false;

    }

    private  void setCooldown(){
        if(cooldown>=12  && spacePressed==true){
            cooldown-=12;
        }else if(cooldown<1200 && spacePressed== false){
            cooldown+=12;
        }
    }

    private void wallCollision(){
        // makes player bounce if they touch the wall
        if(p.x + sz > WIDTH-14||  p.x<17){
            v.setX( v.x* -1);
            a.setX( a .x* -1);
            a= new Vector(0,0);
        }

        if(p.y+ sz > HEIGHT-14 || p.y<31){
            v.setY(v.y* -1);
            a.setY( a.y* -1);
            a= new Vector(0,0);
        }



        // makes block bounce off walls
        if(p2.x + sz2 > WIDTH-14|| p2.x<17){
            v2.setX( v2.x* -1);
            a2.setX( a2.x* -1);
            a2= new Vector(0,0);
            wallHit = true;
        }

        if(p2.y+ sz2 > HEIGHT-14 || p2.y<31) {

            v2.setY(v2.y* -1);
           a2= new Vector(0,0);
            wallHit = true;
        }
    }

    private void movePoints(){
        p3 = new Vector (randomNum3, randomNum4);
    }

    private void blockCollision(){
        if(     p.x<p2.x+sz2 &&
                p.x+sz>p2.x &&
                p.y<p2.y+sz2&&
                p.y + sz >p2.y)
            gameOver = true;


       if(     p2.x<p3.x+sz3 &&
    p2.x+sz>p3.x &&
    p2.y<p3.y+sz3&&
    p2.y + sz2 >p3.y) {
           v2.setY(v2.y * -1);
           a2 = new Vector(0, 0);
       }

        if(     p.x<p3.x+sz3 &&
                p.x+sz>p3.x &&
                p.y<p3.y+sz3&&
                p.y + sz >p3.y) {
           points++;
           movePoints();

        }

        if(     p.x<p5.x+10 &&
                p.x+sz>p5.x &&
                p.y<p5.y+sz5&&
                p.y + sz >p5.y) {
           System.out.println("W");

        }
}

private boolean checkCollision(int x , int x2, int y,int y2, int sz, int sz2){

    if(     x<x2+sz2 &&
            p.x+sz>p2.x &&
            p.y<p2.y+sz2&&
            p.y + sz >p2.y)


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


        if (!gameOver) {
            g.setColor(Color.GREEN);
            g.fillRect(p.ix, p.iy, sz, sz);


            g.setColor(Color.YELLOW);
            g.fillRect(p2.ix, p2.iy, sz2, sz2);

            g.setColor(Color.RED);
            g.fillRect(p5.ix, p5.iy, 10, sz5);

            g.setColor(Color.WHITE);
            g.fillRect(p3.ix, p3.iy, sz3, sz3);


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
            g.drawString(Long.toString(points), 900 , 50);
            //release resources, show the buffer

        } else{
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 100));
            g.setColor(Color.RED);

            g.drawString("Game Over", 200, 350);

            g.setFont(new Font("TimesRoman", Font.PLAIN, 50));
            g.setColor(Color.WHITE);
            g.drawString("Press R to restart", 200, 550);


        }
        g.dispose();
        strategy.show();
    }

    private void gameOver() {
        cooldown=1200;
        points=0;
        p3 = new Vector(300,300);
        p2= new Vector (600,600);
        p = new Vector(50, 50);

        a = new Vector(0,0);
        v = new Vector(0,0);
        gameOver= false;
    }


    private void handleKeys(){
        for(int i =0; i <keys.size();i++){

                if (!gameOver) {




                    switch(keys.get(i)){

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

                    case KeyEvent.VK_RIGHT  :
                        a = Vector.unit2D((0));
                        a.mult(push);
                        accelerating = true;
                        break;

                        case KeyEvent.VK_SPACE:
                            spacePressed= true;
                                if(cooldown>0) {
                                    a.mult(5);

                                }



                            break;

                }
                }else{
                        switch(keys.get(i)){
                            case KeyEvent.VK_R:


                                gameOver();

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

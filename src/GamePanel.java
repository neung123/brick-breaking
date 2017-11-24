import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements ActionListener, KeyListener{

    private boolean running;
    private BufferedImage image;
    private Graphics2D graphics;
    private ScoreBoard scoreBoard = new ScoreBoard();
    double x ,dx = 0;
    public int score = 0;
    Timer timer = new Timer(5 , this);
    Ball ball;
    Paddle paddle;
    World world;

    public enum STATE{
        STARTMENU, PLAY , GAMEOVER , NEWGAME
    };
    public static STATE state = STATE.STARTMENU;


    public GamePanel(){
        timer.start();
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        newGame();

        this.addMouseListener(new MouseInput());

        image = new BufferedImage(BrickBreakingMain.WIDTH,BrickBreakingMain.HEIGHT,BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

    }



    public void playGame() {

        graphics.setColor( new Color(0, 0, 0));
        graphics.fillRect(0,0,BrickBreakingMain.WIDTH,BrickBreakingMain.HEIGHT);

        while (state == STATE.STARTMENU){
            startMenu();
        }
        while (state == STATE.GAMEOVER){
            running = false;

            if(scoreBoard.checkIfHigher(score)){

            }
            drawRT();
        }

        while (state == STATE.NEWGAME) {
            newGame();
            state = STATE.STARTMENU;
        }

        drawstart();
        //game loop
        while (running){

            update();

            draw();

            repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkCollisions(){
        Rectangle ballRect = ball.getRect();
        Rectangle paddleRect = paddle.getRect();

        if(ballRect.intersects(paddleRect)){

            if(ball.getDY() > 0) {

                ball.setDY(-ball.getDY());

                    for (Brick b : world.bricks) {
                        b.drop();
                    }


                if(world.getMinimumHeight() > 0) {
                    addNewLine();
                }
            }

        }
        for(int i = 0; i < world.bricks.size(); i++) {
            if (ballRect.intersects(world.bricks.get(i).getRect())) {
                world.bricks.get(i).removeDef();


                if(world.bricks.get(i).getDef() == 0){
                    world.bricks.remove(world.bricks.get(i));
                    score ++;
                }
                else  ball.setDY(-ball.getDY());
            }
        }
    }

    public void addNewLine(){
        int h = world.getMinimumHeight() - Brick.height;
        for (int i = 0; i < world.column; i++) {
            world.addBrick(new Brick(i * Brick.width, h));
        }
    }

    public void update(){
        checkCollisions();
        ball.update();
    }

    public void draw(){

        //draw background
        graphics.setColor( new Color(0, 0, 0));
        graphics.fillRect(0,0,BrickBreakingMain.WIDTH,BrickBreakingMain.HEIGHT);


        ball.drawBall(graphics);
        paddle.drawPaddle(graphics,(int)x);
        world.drawWorld(graphics);

        drawScore();

        if(ball.gameOverBall()){
            state = STATE.GAMEOVER;
            drawGameOver();
            playGame();
        }
        if(world.checkGameOverBrick() == true){
            state = STATE.GAMEOVER;
            drawGameOver();
            playGame();
        }

    }

    public void drawScore(){
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Courier New", Font.BOLD,30));
        graphics.drawString("Score: ", 620, 550);
        graphics.drawString(Integer.toString(score), 750, 550);
        for(double k = 0; k < 3000 ; k++){
            repaint();
        }
    }

    public void drawstart(){

        graphics.setColor( new Color(0, 0, 0));
        graphics.fillRect(0,0,BrickBreakingMain.WIDTH,BrickBreakingMain.HEIGHT);


        for(double i = 0; i < 4000 ; i++){
            drawStartText();
            for(double k = 0; k < 4000 ; k++){
                repaint();
            }
        }
        graphics.setColor( new Color(0, 0, 0));
        graphics.fillRect(0,0,BrickBreakingMain.WIDTH,BrickBreakingMain.HEIGHT);
        for(double i = 0; i < 3000 ; i++){
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("Courier New", Font.BOLD,50));
            graphics.drawString("3", 370, 250);
            for(double k = 0; k < 3000 ; k++){
                repaint();
            }
        }
        graphics.setColor( new Color(0, 0, 0));
        graphics.fillRect(0,0,BrickBreakingMain.WIDTH,BrickBreakingMain.HEIGHT);
        for(double i = 0; i < 3000 ; i++){
            graphics.setColor(Color.YELLOW);
            graphics.setFont(new Font("Courier New", Font.BOLD,50));
            graphics.drawString("2", 370, 250);
            for(double k = 0; k < 3000 ; k++){
                repaint();
            }
        }
        graphics.setColor( new Color(0, 0, 0));
        graphics.fillRect(0,0,BrickBreakingMain.WIDTH,BrickBreakingMain.HEIGHT);
        for(double i = 0; i < 3000 ; i++){
            graphics.setColor(Color.GREEN);
            graphics.setFont(new Font("Courier New", Font.BOLD,50));
            graphics.drawString("1", 370, 250);
            for(double k = 0; k < 3000 ; k++){
                repaint();
            }
        }
    }

    public void drawRT(){
        Rectangle retryButton = new Rectangle(350,350,100,50);
        Rectangle quitButton = new Rectangle(350,250,100,50);

        Font font0 = new Font("arial", Font.BOLD, 50);
        graphics.setFont(font0);
        graphics.setColor(Color.WHITE);
        graphics.drawString("You cant give up now!!!",140,100);

        graphics.setColor(Color.WHITE);
        Font font1 = new Font("arial", Font.BOLD, 30);
        graphics.setFont(font1);
        graphics.drawString("Quit", quitButton.x + 12, quitButton.y + 30);
        graphics.draw(quitButton);
        graphics.drawString("Retry", retryButton.x + 12, retryButton.y + 30);
        graphics.draw(retryButton);
        repaint();
    }

    public void drawGameOver(){
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Courier New", Font.BOLD,50));
        graphics.drawString("Game Over", 265, 250);
    }

    public void drawStartText(){
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Courier New", Font.BOLD,50));
        graphics.drawString("Start Game", 250, 250);
    }

    public void startMenu(){

        Rectangle playButton = new Rectangle(350,150,100,50);
        Rectangle quitButton = new Rectangle(350,250,100,50);

        Font font0 = new Font("arial", Font.BOLD, 50);
        graphics.setFont(font0);
        graphics.setColor(Color.WHITE);
        graphics.drawString("BRICK BREAKER",200,100);

        graphics.setColor(Color.WHITE);
        Font font1 = new Font("arial", Font.BOLD, 30);
        graphics.setFont(font1);
        graphics.drawString("Play", playButton.x + 12, playButton.y + 30);
        graphics.draw(playButton);
        graphics.drawString("Quit", quitButton.x + 12, quitButton.y + 30);
        graphics.draw(quitButton);
        repaint();

    }

    public void newGame(){
        running = true;
        ball = new Ball();
        paddle = new Paddle();
        x = BrickBreakingMain.WIDTH / 2 - paddle.width / 2;
        world = new World();
        score = 0;
    }

    public void paintComponent(Graphics graphics){

        Graphics2D graphics2 = (Graphics2D) graphics; //update graphics to graphics2

        graphics2.drawImage(image,0,0,BrickBreakingMain.WIDTH,BrickBreakingMain.HEIGHT,null);
        graphics2.dispose();

    }

    public void actionPerformed(ActionEvent e) {
        try {
            x = Math.abs(Math.min(x + dx, BrickBreakingMain.WIDTH - paddle.width));
        } catch (Exception ex) {}
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_LEFT ){
            left();
        }
        if(key == KeyEvent.VK_RIGHT ){
            right();
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_LEFT ){
            leftStop();
        }
        if(key == KeyEvent.VK_RIGHT ){
            rightStop();
        }
    }
    public void leftStop(){dx = 0;}
    public void rightStop(){dx = 0;}
    public void left(){ dx = -10; }
    public void right(){ dx = 10; }






}


import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * Desc: This program will run the Gun Runner game. It is a single player
 * roguelike 2d platformer.
 * 
 * @author Richard Yang, Raymond Zeng-Xu
 * @version June 2022
 */

public class Animation {
    JFrame gameWindow;
    GraphicsPanel canvas;
    ArrayList<Entity> entities = new ArrayList<Entity>();
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    Camera camera = new Camera(0); // this will focus the screen on the player
    Map map; // this will spawn the walls and enemies on the map
    SlowmoTracker slowmoTracker; // this will be used to keep track of time in slow motion
    MovementKeyListener keyListener;
    BasicMouseListener mouseListener;
    long lastUpdateTime = System.currentTimeMillis(); // this is used only to help with displaying fps
    int fps, displayFps; // display frames per second
    Music backgroundMusic;
    Font font = new Font("TimesRoman", Font.PLAIN, 20);
    int gameState = Const.MENU;
    Menu menu;
    Help help;

    Animation() {
        // Create the game window itself
        gameWindow = new JFrame("Game Window");
        gameWindow.setSize(Const.WIDTH, Const.HEIGHT);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new GraphicsPanel();
        gameWindow.add(canvas);

        keyListener = new MovementKeyListener();
        canvas.addKeyListener(keyListener);

        mouseListener = new BasicMouseListener();
        canvas.addMouseListener(mouseListener);

        // Add the menu
        menu = new Menu();
        help = new Help();

        // Add the player
        entities.add(new Player(100, 100, 34, 44, "Player/"));

        // Make a map (it will automatically generate walls and rooms)
        map = new Map(0, 0, entities, 10);
        // This is used to slow down time
        slowmoTracker = new SlowmoTracker();

        // play background music
        backgroundMusic = new Music();
        backgroundMusic.start();

        gameWindow.setVisible(true);
    }

    // ------------------------------------------------------------------------------
    public void runGameLoop() {
        while (true) {
            // 1. and 2. Clear the game window and draw everything
            gameWindow.repaint();
            // 3. Wait enough time, so human eye can perceive the drawing
            try {
                Thread.sleep(Const.FRAME_PERIOD);
            } catch (Exception e) {
            }

            if (gameState == Const.PLAY) {

                // add 1 to gametime, or less depending on how slowed time is
                slowmoTracker.increaseGameTime(1 * slowmoTracker.getActiveSlowAmount());

                // update all bullets
                for (int i = 0; i < bullets.size(); i++) {
                    bullets.get(i).update(entities, bullets, slowmoTracker);
                }

                // update all entities
                for (int i = 0; i < entities.size(); i++) {
                    if (entities.get(i).checkInRange(camera.getXRange(), camera.getYRange())) {
                        entities.get(i).update(entities, bullets, slowmoTracker);
                    }
                }
                // refocus the camera onto the player
                camera.update(entities);

                // update fps
                if (System.currentTimeMillis() - lastUpdateTime > 1000) {
                    displayFps = fps;
                    fps = 0;
                    lastUpdateTime = System.currentTimeMillis();
                }
            }
        } // 5. Repeat
    }

    // ------------------------------------------------------------------------------
    public class GraphicsPanel extends JPanel {
        public GraphicsPanel() {
            setFocusable(true);
            requestFocusInWindow();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            //------------------MENU-----------------------------
            if (gameState == Const.MENU) {
                menu.draw(g);
            } else if (gameState == Const.HELP){
                help.draw(g);
            }
            //--------------------PLAY---------------------------
            else if (gameState == Const.PLAY) {
                // draw background colors. Colors change if time is slowed
                slowmoTracker.drawBackground(g);

                // draw all of the rooms
                for (int i = map.getRooms().size() - 1; i >= 0; i--) {
                    map.getRooms().get(i).draw(g, camera.getXRange(), camera.getYRange());
                }

                // draw all of the entities
                for (int i = entities.size() - 1; i >= 0; i--) {
                    entities.get(i).draw(g, camera.getXRange(), camera.getYRange(), slowmoTracker);
                }

                // draw all of the bullets
                for (int i = bullets.size() - 1; i >= 0; i--) {
                    bullets.get(i).draw(g, camera.getXRange(), camera.getYRange());
                }

                // draw the foreground, changes depending on how much time is slowed
                slowmoTracker.drawForeground(g);
                // if the player is dead, display the game over screen.
                camera.draw(g, entities);

                // Display fps
                g.setFont(font);
                g.setColor(Color.white);
                g.drawString(Integer.toString(displayFps), 10, 30);
                // Add 1 to the fps counter
                fps++;

                // draw the loading screen when the map is loading
                if (map.getMapLoading()) {
                    if (map.getLoadingScreenDrawn()) {
                        entities.get(0).checkInteract(entities, map, backgroundMusic);
                        map.setLoadingScreenDrawn(false);
                    } else {
                        map.drawLoadingScreen(g);
                        map.setLoadingScreenDrawn(true);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------------
    public class BasicMouseListener implements MouseListener {
        public void mouseClicked(MouseEvent e) { // moves the box at the mouse location
        }

        public void mousePressed(MouseEvent e) { // MUST be implemented even if not used!
            int mx = e.getX();
            int my = e.getY();

//            public Rectangle playButton = new Rectangle(Const.WIDTH / 2 - 13, 150, 100, 50);
//            public Rectangle helpButton = new Rectangle(Const.WIDTH / 2 - 13, 250, 100, 50);
//            public Rectangle quitButton = new Rectangle(Const.WIDTH / 2 - 13, 350, 100, 50);
            if (gameState == Const.MENU) {
                if (mx >= 200 && mx <= 300) {
                    if (my >= 150 && my <= 200) {
                        gameState = Const.PLAY;
                    }
                    if (my >= 250 && my <= 300) {
                        gameState = Const.HELP;
                    }
                    if (my >= 350 && my <= 400) {
                        System.exit(1);
                    }
                }
            } else if (gameState == Const.HELP){
                if (mx >= 200 && mx <= 300) {
                    if (my >= 400 && my <= 450) {
                        gameState = Const.PLAY;
                    }
                }
            } else if (gameState == Const.PLAY) {
                if (entities.get(0).getInteractingWith() == null) {
                    entities.get(0).attack(e.getX() + camera.getXRange(),
                            e.getY() + camera.getYRange(), entities, bullets, slowmoTracker);
                } else {
                    entities.get(0).mouseInteract(e.getX(), e.getY());
                }
            }
        }

        public void mouseReleased(MouseEvent e) { // MUST be implemented even if not used!
        }

        public void mouseEntered(MouseEvent e) { // MUST be implemented even if not used!
        }

        public void mouseExited(MouseEvent e) { // MUST be implemented even if not used!
        }
    }

    // ---------------------------------------------------------------------------------
    public class MovementKeyListener implements KeyListener {
        public void keyPressed(KeyEvent e) {
            char keyChar = e.getKeyChar();
            // WASD keys to move the character around
            if (keyChar == 'a') {
                entities.get(0).move("left");
            } else if (keyChar == 'd') {
                entities.get(0).move("right");
            } else if (keyChar == 'w') {
                entities.get(0).jump();
            } else if (keyChar == 'e') { // e to interact with entities
                entities.get(0).checkInteract(entities, map, backgroundMusic);
            }

            int key = e.getKeyCode();
            if (key == 16) { // SHIFT key activates slow motion
                slowmoTracker.activateSlow(0.1);
            } else if (key == 32) { // SPACE key allows for the player to dash to mouse location
                Point p = MouseInfo.getPointerInfo().getLocation();
                entities.get(0).dash(p.getX() + camera.getXRange(),
                        p.getY() + camera.getYRange(), entities, bullets, slowmoTracker);
            }
        }

        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ESCAPE) { // close the game
                gameWindow.dispose();
            }
            // stop the player from moving if A or D key is lifted
            else if (key == KeyEvent.VK_A && entities.get(0).getDirection().equals("left")) {
                entities.get(0).move("none");
            } else if (key == KeyEvent.VK_D && entities.get(0).getDirection().equals("right")) {
                entities.get(0).move("none");
            }
        }

        public void keyTyped(KeyEvent e) {
        }
    }

    // -----------------------------------------------
    public static void main(String[] args) {
        Animation game = new Animation();
        game.runGameLoop();
    }

    // randint gives a random number from min to max
    public static int randInt(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }
}

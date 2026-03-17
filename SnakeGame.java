import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;
import java.awt.geom.*;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;

public class SnakeGame extends JPanel implements ActionListener {
    static final int WIDTH = 800;
    static final int HEIGHT = 600;
    static final int SIZE = 20;

    LinkedList<Point> snake = new LinkedList<>();
    Point food;
    char dir = 'R'; // U, D, L, R
    boolean running = true;
    int score = 0;
    Timer timer;

    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> { if (dir != 'D') dir = 'U'; }
                    case KeyEvent.VK_DOWN -> { if (dir != 'U') dir = 'D'; }
                    case KeyEvent.VK_LEFT -> { if (dir != 'R') dir = 'L'; }
                    case KeyEvent.VK_RIGHT -> { if (dir != 'L') dir = 'R'; }
                    case KeyEvent.VK_P -> running = !running;
                }
            }
        });
        startGame();
    }

    public void startGame() {
        snake.clear();
        snake.add(new Point(200, 200));
        spawnFood();
        timer = new Timer(200, this);
        timer.start();
    }

    public void spawnFood() {
        Random rand = new Random();
        food = new Point(rand.nextInt(WIDTH / SIZE) * SIZE, rand.nextInt(HEIGHT / SIZE) * SIZE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable Anti-aliasing for smooth circles and corners
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Draw Background Grid
        g2d.setColor(new Color(30, 30, 30));
        for (int i = 0; i < WIDTH; i += SIZE) g2d.drawLine(i, 0, i, HEIGHT);
        for (int i = 0; i < HEIGHT; i += SIZE) g2d.drawLine(0, i, WIDTH, i);

        if (running) {
            // 2. Draw Glowing Food
            float[] dist = {0.0f, 1.0f};
            Color[] colors = {Color.RED, new Color(0, 0, 0, 0)};
            RadialGradientPaint rp = new RadialGradientPaint(
                food.x + (SIZE/2), food.y + (SIZE/2), SIZE, dist, colors);
            g2d.setPaint(rp);
            g2d.fillOval(food.x - 5, food.y - 5, SIZE + 10, SIZE + 10); // Glow
            g2d.setColor(Color.RED);
            g2d.fillOval(food.x, food.y, SIZE, SIZE); // Core

            // 3. Draw Gradient Snake
            for (int i = 0; i < snake.size(); i++) {
                // Calculate color: fades from Bright Green (head) to Dark Green (tail)
                float ratio = (float) i / (snake.size());
                int green = (int) (255 * (1 - ratio * 0.6));
                g2d.setColor(new Color(0, green, 100));
                
                // Draw rounded segments
                g2d.fillRoundRect(snake.get(i).x + 1, snake.get(i).y + 1, SIZE - 2, SIZE - 2, 8, 8);
                
                // Add eyes to the head
                if (i == 0) {
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(snake.get(i).x + 4, snake.get(i).y + 4, 4, 4);
                    g2d.fillOval(snake.get(i).x + 12, snake.get(i).y + 4, 4, 4);
                }
            }

            // 4. Modern Scoreboard
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2d.drawString("SCORE: " + score, 20, 30);

        } else {
            showGameOver(g2d);
        }
    }
    public void showGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("GAME OVER", WIDTH / 2 - 100, HEIGHT / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, WIDTH / 2 - 40, HEIGHT / 2 + 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            Point head = snake.getFirst();
            Point newHead = new Point(head.x, head.y);

            if (dir == 'U') newHead.y -= SIZE;
            if (dir == 'D') newHead.y += SIZE;
            if (dir == 'L') newHead.x -= SIZE;
            if (dir == 'R') newHead.x += SIZE;

            if (newHead.x < 0 || newHead.y < 0 || newHead.x >= WIDTH || newHead.y >= HEIGHT || snake.contains(newHead)) {
                running = false;
            } else {
                snake.addFirst(newHead);
                if (newHead.equals(food)) {
                    score++;
                    spawnFood();
                } else {
                    snake.removeLast();
                }
            }
        }
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game (Swing Version)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
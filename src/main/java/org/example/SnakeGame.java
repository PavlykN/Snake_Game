package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakeGame {
    public static void main(String[] args) {
        new GameFrame(); // створюємо вікно гри
    }
}


// Основна панель для гри
class GamePanel extends JPanel implements ActionListener {
    // Константи для налаштування гри
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25; // Розмір одного блоку
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE; // кількість блоків на екрані
    static final int DELAY = 75; // затримка таймера у мілісекундах

    // Змінні для гри
    final int x[] = new int[GAME_UNITS]; // Координати x частин тіла змійки
    final int y[] = new int[GAME_UNITS]; // Координати y частин тіла змійки
    int bodyParts = 6; // Початкова кількість частин тіла змійки
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R'; // початковий напрямок руху (R - вправо)
    boolean running = false; // чи запущена гра
    Timer timer; // таймер для оновлення гри
    Random random; // генератор випадкових чисел для створення яблук

    // конструктор панелі гри
    GamePanel() {
        random = new Random(); // ініціалізуємо генератор випадкових чисел
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT)); // встановлюємо розмір панелі
        this.setBackground(Color.black);
        this.setFocusable(true); // панель реагує на натискання клавіш
        this.addKeyListener(new MyKeyAdapter()); // додаємо обробник клавіш
        startGame();
    }

    // метод для запуску гри
    public void startGame() {
        newApple(); // створємо нове яблуко
        running = true; // увімкнення стану гри
        timer = new Timer(DELAY,this); // створюємо таймер із затримкою
        timer.start();
    }

    // метод для відображення елементів гри
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // викликаємо стандартний метод малювання
        draw(g); // викликаємо власний метод для малювання
    }

    // малюємо ігрові елементи
    public void draw (Graphics g) {
        if(running) {
            // малюємо яблуко
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // малюємо змійку
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green); // голова змійки зеленого кольору
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE); // малюємо блок тіла
                } else {
                    g.setColor(new Color(45, 180, 0)); // тіло змійки іншого відтінку
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE); // малюємо блок тіла
                }
            }

            // малюємо рахунок
            g.setColor(Color.red);
            g.setFont( new Font("Helvetica", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
        } else {
            gameOver(g); // викликаємо екран "Game Over"
        }
    }

    // генеруємо координати нового яблука
    public void newApple() {
        appleX= random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY= random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    // рух змійки
    public void move() {
        for(int i = bodyParts; i>0; i--) {
            x[i] = x[i-1]; // кожна частинаа тіла слідує за попередньою
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    // перевіряємо чи з'їла змійка яблуко
    public void checkApple () {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++; // додаємо частину тіла
            applesEaten++; // збільшуємо рахунок
            newApple(); // створюємо нове яблуко
        }
    }

    // перевіряємо зіткнення
    public void checkCollisions () {
        // checks if head collides with body
        for(int i = bodyParts; i > 0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // check if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // check if head touches right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        // check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // check if head touches bottom border
        if (y[0] > SCREEN_WIDTH) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver (Graphics g) {
        // Score
        g.setColor(Color.red);
        g.setFont( new Font("Helvetica", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

        // Game Over text
        g.setColor(Color.red);
        g.setFont( new Font("Helvetica", Font.BOLD, 70));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Nice try", (SCREEN_WIDTH - metrics2.stringWidth("Nice try"))/2, SCREEN_HEIGHT/2);


    }



    public void restart() {
        running = true;

    }

    // обробка подій таймера
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move(); // рух змійки
            checkApple(); // перевірка яблука
            checkCollisions(); // перевірка зіткнень
        }
        repaint(); // перемальовуємо екран
    }

    // клас для обробки клавіш
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        restart();
                    }
            }
        }
    }
}

// Клас для створення вікна гри
class GameFrame extends JFrame {
    GameFrame(){
        // Додаємо ігрову панель до вікна
        this.add(new GamePanel());
        this.setTitle("Snake"); // назва вікна
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Завершення програми при закритті
        this.setResizable(false); // Забороняємо зміну розміру
        this.pack(); // Підлаштовуємо розмір вікна під вміст
        this.setVisible(true); // Робимо вікно видимим
        this.setLocationRelativeTo(null); // Центруємо вікно на екрані
    }
}

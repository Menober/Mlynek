package game.engine;

import game.assets.Assets;
import game.display.Display;
import game.input.KeyManager;
import game.input.MouseManager;
import game.states.GameState;
import game.states.MenuState;
import game.states.State;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import static game.engine.Gracz.BIALY;
import static game.engine.Gracz.CZARNY;

public class Game implements Runnable {

    private boolean isRunning;
    private Thread thread;
    private Handler handler;
    private Display display;
    private BufferStrategy bufferStrategy;
    private Graphics graphics;
    private static State currentState;
    private KeyManager keyManager;
    private MouseManager mouseManager;
    public int tankColor;
    public int liczbaPionkowDoRozstawieniaBIALY = 9;
    public int liczbaPionkowDoRozstawieniaCZARNY = 9;
    public Gracz ruch = BIALY;
    public ArrayList<Pole> board = new ArrayList<>();
    long czasOdStartu;
    Pole wybranyPionek;
    boolean czyWybral;
    long opoznienie=200;
    long timeHelper;

    Game(Handler handler) {
        this.handler = handler;
        keyManager = new KeyManager();
        mouseManager = new MouseManager();
        handler.setKeyManager(keyManager);
    }

    private void init() {
        display = new Display(handler.getTitle(), handler.getWidth(), handler.getHeight());
        display.getFrame().addKeyListener(keyManager);
        display.getFrame().addMouseListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
        tankColor = 0;
        Assets.init();
        currentState = new MenuState(handler);

        Pole A1 = new Pole("A", 1);
        Pole A4 = new Pole("A", 4);
        Pole A7 = new Pole("A", 7);

        Pole B2 = new Pole("B", 2);
        Pole B4 = new Pole("B", 4);
        Pole B6 = new Pole("B", 6);

        Pole C3 = new Pole("C", 3);
        Pole C4 = new Pole("C", 4);
        Pole C5 = new Pole("C", 5);

        Pole D1 = new Pole("D", 1);
        Pole D2 = new Pole("D", 2);
        Pole D3 = new Pole("D", 3);
        Pole D5 = new Pole("D", 5);
        Pole D6 = new Pole("D", 6);
        Pole D7 = new Pole("D", 7);

        Pole E3 = new Pole("E", 3);
        Pole E4 = new Pole("E", 4);
        Pole E5 = new Pole("E", 5);

        Pole F2 = new Pole("F", 2);
        Pole F4 = new Pole("F", 4);
        Pole F6 = new Pole("F", 6);

        Pole G1 = new Pole("G", 1);
        Pole G4 = new Pole("G", 4);
        Pole G7 = new Pole("G", 7);

        board.add(A1);
        board.add(A4);
        board.add(A7);
        board.add(B2);
        board.add(B4);
        board.add(B6);
        board.add(C3);
        board.add(C4);
        board.add(C5);
        board.add(D1);
        board.add(D2);
        board.add(D3);
        board.add(D5);
        board.add(D6);
        board.add(D7);
        board.add(E3);
        board.add(E4);
        board.add(E5);
        board.add(F2);
        board.add(F4);
        board.add(F6);
        board.add(G1);
        board.add(G4);
        board.add(G7);

        dopasujPozycje();
        czasOdStartu = System.currentTimeMillis();
        //  wypelnijPola();
        rozdajPionki();
    }

    private void rozdajPionki() {
        wstawPionek(BIALY, "A", 1);
        wstawPionek(BIALY, "A", 4);
        wstawPionek(BIALY, "B", 2);
        wstawPionek(BIALY, "B", 4);
        wstawPionek(BIALY, "C", 3);
        wstawPionek(BIALY, "G", 1);
        wstawPionek(BIALY, "G", 7);
        wstawPionek(BIALY, "F", 6);

        wstawPionek(CZARNY, "F", 2);
        wstawPionek(CZARNY, "F", 4);
        wstawPionek(CZARNY, "G", 4);
        wstawPionek(CZARNY, "D", 1);
        wstawPionek(CZARNY, "D", 2);
        wstawPionek(CZARNY, "D", 5);
        wstawPionek(CZARNY, "D", 6);
        wstawPionek(CZARNY, "C", 5);
    }

    private void dopasujPozycje() {
        for (Pole p : board) {
            int x = 0, y = 0;
            switch (p.number) {
                case 1:
                    x = 8;
                    break;
                case 2:
                    x = 126;
                    break;
                case 3:
                    x = 248;
                    break;
                case 4:
                    x = 368;
                    break;
                case 5:
                    x = 500;
                    break;
                case 6:
                    x = 610;
                    break;
                case 7:
                    x = 728;
                    break;
            }
            switch (p.alpha) {
                case "A":
                    y = 8;
                    break;
                case "B":
                    y = 126;
                    break;
                case "C":
                    y = 248;
                    break;
                case "D":
                    y = 368;
                    break;
                case "E":
                    y = 500;
                    break;
                case "F":
                    y = 610;
                    break;
                case "G":
                    y = 728;
                    break;
            }
            p.x1 = x;
            p.y1 = y;
            p.x2 = x + 64;
            p.y2 = y + 64;
        }
    }

    private void wypelnijPola() {
        for (Pole p : board) {
            p.czyWolne = false;
            p.zajetePrzez = BIALY;
        }
    }

    private void render() {
        bufferStrategy = display.getCanvas().getBufferStrategy();
        if (bufferStrategy == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        graphics = bufferStrategy.getDrawGraphics();

        graphics.clearRect(0, 0, handler.getWidth(), handler.getHeight());

        if (currentState != null)
            currentState.render(graphics);

        if (currentState.getClass() != MenuState.class) {
            drawBoard(graphics);
            Font font = new Font("",1,20);
            graphics.setFont(font);
            graphics.drawString("Czas gry: " + (System.currentTimeMillis() - czasOdStartu) / 1000 + " [s]", 950, 20);
            graphics.drawString("Gracz 1",950,50);
            graphics.drawString("Gracz 2",950+64+50,50);
            graphics.drawString("Ruch gracza:",950,800);

            if (ruch == BIALY)
                graphics.drawImage(Assets.player1, 1100, 766, 64, 64, null);
            else if (ruch == CZARNY)
                graphics.drawImage(Assets.player2, 1100, 766, 64, 64, null);

            if(wybranyPionek!=null)
                graphics.drawString("Wybrany pionek: " + wybranyPionek.alpha+wybranyPionek.number, 950, 900);

        }

        bufferStrategy.show();
        graphics.dispose();
    }

    private void drawBoard(Graphics graphics) {
        for (Pole p : board) {
            if (!p.czyWolne) {
                int x = 0, y = 0;
                switch (p.number) {
                    case 1:
                        x = 8;
                        break;
                    case 2:
                        x = 126;
                        break;
                    case 3:
                        x = 248;
                        break;
                    case 4:
                        x = 368;
                        break;
                    case 5:
                        x = 500;
                        break;
                    case 6:
                        x = 610;
                        break;
                    case 7:
                        x = 728;
                        break;
                }
                switch (p.alpha) {
                    case "A":
                        y = 8;
                        break;
                    case "B":
                        y = 126;
                        break;
                    case "C":
                        y = 248;
                        break;
                    case "D":
                        y = 368;
                        break;
                    case "E":
                        y = 500;
                        break;
                    case "F":
                        y = 610;
                        break;
                    case "G":
                        y = 728;
                        break;
                }
                if (p.czyWybrany) {
                    graphics.setColor(Color.RED);
                    graphics.fillRect(p.x1, p.y1, 64, 64);
                    graphics.setColor(Color.BLACK);
                    graphics.drawRect(p.x1, p.y1, 64, 64);
                }
                if (p.zajetePrzez == BIALY) {
                    graphics.drawImage(Assets.player1, x, y, 64, 64, null);
                } else if (p.zajetePrzez == CZARNY) {
                    graphics.drawImage(Assets.player2, x, y, 64, 64, null);
                }

            }
        }
    }

    private void update() {
        currentState.update();
        keyManager.update();
        if (currentState.getClass() != MenuState.class)
            if (mouseManager.isLeftPressed()) {
                //System.out.println(mouseManager.getMouseX() + ":" + mouseManager.getMouseY());
                if (ruch == BIALY) {
                    if (liczbaPionkowDoRozstawieniaBIALY > 0)
                        postawPionek(BIALY, mouseManager.getMouseX(), mouseManager.getMouseY());
                    else {
                        Pole zaznaczonyPionek = polePodXY(mouseManager.getMouseX(), mouseManager.getMouseY());
                        if (zaznaczonyPionek != null)
                            if (wybranyPionek != null)
                            {
                                if (zaznaczonyPionek == wybranyPionek && czyWybral) {
                                    wybranyPionek.czyWybrany = false;
                                    czyWybral = false;
                                    wybranyPionek = null;
                                }
                        }
                        else if (zaznaczonyPionek != null && zaznaczonyPionek.zajetePrzez == BIALY && !czyWybral) {
                            czyWybral = true;
                            wybranyPionek = zaznaczonyPionek;
                            wybranyPionek.czyWybrany = true;
                        }

                    }
                } else {
                    if (liczbaPionkowDoRozstawieniaCZARNY > 0)
                        postawPionek(CZARNY, mouseManager.getMouseX(), mouseManager.getMouseY());
                    else {

                    }
                }
                //delay
                timeHelper=System.currentTimeMillis();
                while(System.currentTimeMillis()-timeHelper<opoznienie){

                }
            }
    }

    private void wstawPionek(Gracz gracz, String alpha, int number) {
        Pole pole = polePodAlphaNumber(alpha, number);
        if (pole != null) {
            if (pole.czyWolne) {
                pole.czyWolne = false;
                pole.zajetePrzez = gracz;
                if (gracz == BIALY) {
                    ruch = CZARNY;
                    if (liczbaPionkowDoRozstawieniaBIALY > 0)
                        liczbaPionkowDoRozstawieniaBIALY -= 1;
                } else if (gracz == CZARNY) {
                    ruch = BIALY;
                    if (liczbaPionkowDoRozstawieniaCZARNY > 0)
                        liczbaPionkowDoRozstawieniaCZARNY -= 1;
                }
            }

        }

    }

    private void postawPionek(Gracz gracz, int mouseX, int mouseY) {
        Pole pole = polePodXY(mouseX, mouseY);
        if (pole != null) {
            if (pole.czyWolne) {
                pole.czyWolne = false;
                pole.zajetePrzez = gracz;
                if (gracz == BIALY) {
                    ruch = CZARNY;
                    if (liczbaPionkowDoRozstawieniaBIALY > 0)
                        liczbaPionkowDoRozstawieniaBIALY -= 1;
                } else if (gracz == CZARNY) {
                    ruch = BIALY;
                    if (liczbaPionkowDoRozstawieniaCZARNY > 0)
                        liczbaPionkowDoRozstawieniaCZARNY -= 1;
                }
            }

        }


    }

    private Pole polePodAlphaNumber(String alpha, int number) {
        //  System.out.println(mouseX+";"+mouseY);
        for (Pole p : board) {
            if (p.alpha.equals(alpha) && p.number == number) {
                return p;
            }
        }
        return null;
    }

    private Pole polePodXY(int mouseX, int mouseY) {
        //  System.out.println(mouseX+";"+mouseY);
        for (Pole p : board) {
            if (between(mouseX, p.x1, p.x2) && between(mouseY, p.y1, p.y2)) {
                return p;
            }
        }
        return null;
    }


    public void setCurrentState(State state) {
        currentState = state;
    }

    @Override
    public void run() {
        init();

        int fps = 60;
        double timePerUpdate = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        long timer = 0;
        int ticks = 0;

        while (isRunning) {
            now = System.nanoTime();
            delta += (now - lastTime) / timePerUpdate;
            timer += now - lastTime;
            lastTime = now;

            if (delta >= 1) {
                update();
                render();
                ticks++;
                delta--;
            }

            if (timer >= 1000000000) {
                //   System.out.println("Ticks and frames: "+ticks);

                ticks = 0;
                timer = 0;
            }

        }

        stop();
    }


    public synchronized void start() {
        if (isRunning)
            return;
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private synchronized void stop() {

        System.exit(1);

    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public void changeTankColor() {
        tankColor++;
        if (tankColor > 3)
            tankColor = 0;
    }

    public void startNewGame() {
        GameState newGame = new GameState(handler);
        setCurrentState(newGame);
    }

    public static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        return (i >= minValueInclusive && i <= maxValueInclusive);
    }
}

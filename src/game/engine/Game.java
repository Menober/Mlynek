package game.engine;

import game.assets.Assets;
import game.display.Display;
import game.input.KeyManager;
import game.input.MouseManager;
import game.states.EndgameState;
import game.states.GameState;
import game.states.MenuState;
import game.states.State;
import javafx.util.Pair;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.lang.reflect.Array;
import java.math.BigDecimal;
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
    public int liczbaPionkowBIALY = 0;
    public int liczbaPionkowCZARNY = 0;
    public Gracz ruch = BIALY;
    public ArrayList<Pole> board = new ArrayList<>();
    public ArrayList<Mlynek> mlynki = new ArrayList<Mlynek>();
    long czasOdStartu;
    Pole wybranyPionek;
    boolean czyWybral;
    long opoznienie = 200;
    long opoznienieMiedzyRuchami = 200;
    long timehelperDWA;
    long timeHelper;
    boolean czyCzarnyZbija = false;
    boolean czyBialyZbija = false;
    int ileBialyZbija = 0;
    int ileCzarnyZbija = 0;
    Gracz ktoWygral = null;
    Pole ostatniePoleBialego = new Pole("start", 0);
    Pole ostatniePoleCzarnego = new Pole("start", 0);
    Pole blokowanyBialyPionek = new Pole("test", 0);
    Pole blokowanyCzarnyPionek = new Pole("test", 0);
    int liczbaRuchowBialego = 0;
    int liczbaRuchowCzarnego = 0;
    boolean czyCzarnyAI = true;
    boolean czyBialyAI = true;
    Ruch ruchCzarnego = null;
    Ruch ruchBialego = null;
    boolean debug=true;

    Game(Handler handler) {
        this.handler = handler;
        keyManager = new KeyManager();
        mouseManager = new MouseManager();
        handler.setKeyManager(keyManager);
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
            Font font = new Font("", 1, 20);
            graphics.setFont(font);
            graphics.drawString("Czas gry: " + (System.currentTimeMillis() - czasOdStartu) / 1000 + " [s]", 950, 20);
            graphics.drawString("Gracz 1 [" + liczbaPionkowBIALY + "]", 950, 50);
            graphics.drawString("Gracz 2 [" + liczbaPionkowCZARNY + "]", 950 + 64 + 50, 50);
            graphics.drawString("Ruch gracza:", 950, 800);
            graphics.drawString("zakazane pole białego:" + ostatniePoleBialego.pozycja() + " dla pionka:" + blokowanyBialyPionek.pozycja(), 450, 920);
            graphics.drawString("zakazane pole czarnego:" + ostatniePoleCzarnego.pozycja() + " dla pionka:" + blokowanyCzarnyPionek.pozycja(), 450, 950);
            graphics.drawString("Liczba dostępnych ruchów:" + liczbaDostepnychRuchowDlaPola(wybranyPionek), 450, 975);
            graphics.drawString("Aktualne punkty za młynki:" + policzPunktyZaMlynki(ruch), 10, 920);
            graphics.drawString("Czy B zbija:" + czyBialyZbija+" ile:"+ileBialyZbija, 10, 940);
            graphics.drawString("Czy C zbija:" + czyCzarnyZbija+" ile:"+ileCzarnyZbija, 10, 960);
            graphics.drawString("Liczba ruchów B:C-"+liczbaRuchowBialego+":"+liczbaRuchowCzarnego, 10, 980);


            if (ruch == BIALY)
                graphics.drawImage(Assets.player1, 1100, 766, 64, 64, null);
            else if (ruch == CZARNY)
                graphics.drawImage(Assets.player2, 1100, 766, 64, 64, null);


            if (wybranyPionek != null) {
                graphics.drawString("Wybrany pionek: " + wybranyPionek.alpha + wybranyPionek.number, 920, 860);
                graphics.drawString("Dostępni sąsiedzi: ", 920, 890);
                graphics.drawString(wybranyPionek.dajSasiadow(), 920, 920);

            }
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
                if (wybranyPionek == p) {
//                    if(wybranyPionek.zajetePrzez==BIALY){
                    if ((ruch == BIALY && liczbaPionkowBIALY == 3) || (ruch == CZARNY && liczbaPionkowCZARNY == 3)) {
                        for (Pole f : board) {
                            if (f.czyWolne) {
                                if (ruch == BIALY) {
                                    if (wybranyPionek.czyToToSamoPole(blokowanyBialyPionek) && ostatniePoleBialego.czyToToSamoPole(f)) {
                                        oznaczPoleGraficznie(graphics, f, true);
                                    } else {
                                        oznaczPoleGraficznie(graphics, f, false);
                                    }

                                } else if (ruch == CZARNY) {

                                    if (wybranyPionek.czyToToSamoPole(blokowanyCzarnyPionek) && ostatniePoleCzarnego.czyToToSamoPole(f)) {
                                        oznaczPoleGraficznie(graphics, f, true);
                                    } else {
                                        oznaczPoleGraficznie(graphics, f, false);
                                    }


                                }
                            }

                        }
                    } else {
                        for (Pole f : wybranyPionek.sasiedzi) {
                            if (f.czyWolne) {
                                if (ruch == BIALY) {
                                    if (wybranyPionek.czyToToSamoPole(blokowanyBialyPionek) && ostatniePoleBialego.czyToToSamoPole(f)) {
                                        oznaczPoleGraficznie(graphics, f, true);
                                    } else {
                                        oznaczPoleGraficznie(graphics, f, false);
                                    }

                                } else if (ruch == CZARNY) {
                                    if (wybranyPionek.czyToToSamoPole(blokowanyCzarnyPionek) && ostatniePoleCzarnego.czyToToSamoPole(f)) {
                                        oznaczPoleGraficznie(graphics, f, true);
                                    } else {
                                        oznaczPoleGraficznie(graphics, f, false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (currentState.getClass() == EndgameState.class) {
            Font font = new Font("", Font.BOLD, 100);
            graphics.setFont(font);
            graphics.drawString("KONIEC GRY", 100, 400);
            graphics.drawString("WYGRAŁ " + ktoWygral, 100, 600);
        }
    }

    private void oznaczPoleGraficznie(Graphics graphics, Pole f, boolean czyZablokowane) {
        int alpha = 127; // 50% transparent
        Color myColour = new Color(255, 200, 100, alpha);
        if (czyZablokowane)
            myColour = new Color(255, 0, 0, alpha);
        graphics.setColor(myColour);
        graphics.fillRect(f.x1, f.y1, 64, 64);
        graphics.setColor(Color.BLACK);
        graphics.drawRect(f.x1, f.y1, 64, 64);
    }


    private void minmax(Gracz gracz) {

        Ruch max = new Ruch();
        max.rodzajRuchu="brak";
        max.liczbaPunktowPoRuchu = -10000;
        if (gracz == CZARNY) {
            ArrayList<Ruch> dostepneRuchy = coMozeZrobic(gracz);
            for (Ruch r : dostepneRuchy) {
                if (r.liczbaPunktowPoRuchu > max.liczbaPunktowPoRuchu){
                    max = r;
                }
                if(debug)
                    System.out.println(CZARNY + ":" + r.rodzajRuchu + ":" + r.skad.pozycja() + ":" + r.dokad.pozycja() + ":PKT:" + r.liczbaPunktowPoRuchu);
            }
            ruchCzarnego = max;
        } else if (gracz == BIALY) {
            ArrayList<Ruch> dostepneRuchy = coMozeZrobic(gracz);
            for (Ruch r : dostepneRuchy) {
                if (r.liczbaPunktowPoRuchu > max.liczbaPunktowPoRuchu){
                    max = r;
                }
                if(debug)
                    System.out.println(BIALY + ":" + r.rodzajRuchu + ":" + r.skad.pozycja() + ":" + r.dokad.pozycja() + ":PKT:" + r.liczbaPunktowPoRuchu);
            }
            ruchBialego = max;
        }
    }

    private ArrayList<Ruch> coMozeZrobic(Gracz gracz) {
        ArrayList<Ruch> ruchy = new ArrayList<>();
        if (gracz == CZARNY) { //CZARNY
            if (liczbaPionkowDoRozstawieniaCZARNY > 0) { //rozstawianie
                for (Pole pole : board) {
                    if (pole.czyWolne) {
                        Ruch r = new Ruch();
                        r.gracz = CZARNY;
                        r.rodzajRuchu = "postaw";
                        r.skad = new Pole("", 0);
                        r.dokad = pole.copy();
                        r.stanGry = stanGryPoRuchu(r);
                        r.czyZbija = r.stanGry.czyCzarnyZbija;
                        if (r.czyZbija) { //możliwy nullpointer
                            r.coZbic = r.stanGry.coZbicBialego(null);
                            if (r.ileZbija == 2) {
                                r.coZbic2 = r.stanGry.coZbicBialego(r.coZbic);
                            }
                        }
                        r.liczbaPunktowPoRuchu = r.stanGry.punktyCzarnego;
                        ruchy.add(r);
                    }
                }
            } else if (liczbaPionkowCZARNY > 3) {//przesuwanie po sasiadach
                for (Pole pole : board) {
                    if (pole.zajetePrzez == CZARNY) {
                        ArrayList<Pole> dostepnePola = dostepneSasiady(pole);
                        for (Pole p : dostepnePola) {
                            Ruch r = new Ruch();
                            r.gracz = CZARNY;
                            r.rodzajRuchu = "rusz";
                            r.skad = pole.copy();
                            r.dokad = p.copy();
                            r.stanGry = stanGryPoRuchu(r);
                            r.czyZbija = r.stanGry.czyCzarnyZbija;
                            if (r.czyZbija) { //możliwy nullpointer
                                r.coZbic = r.stanGry.coZbicBialego(null);
                                if (r.ileZbija == 2) {
                                    r.coZbic2 = r.stanGry.coZbicBialego(r.coZbic);
                                }
                            }
                            r.liczbaPunktowPoRuchu = r.stanGry.punktyCzarnego;
                            ruchy.add(r);
                        }
                    }
                }
            } else if (liczbaPionkowCZARNY == 3) { //poruszanie CZARNEGO po całej planszy
                for (Pole poleGracza : board) {
                    if (poleGracza.zajetePrzez == CZARNY) {
                        for (Pole poleWolne : board) {
                            if (poleWolne.czyWolne) {
                                if (!czyToCofanie(poleGracza, poleWolne)) {
                                    Ruch r = new Ruch();
                                    r.gracz = CZARNY;
                                    r.rodzajRuchu = "rusz";
                                    r.skad = poleGracza.copy();
                                    r.dokad = poleWolne.copy();
                                    r.stanGry = stanGryPoRuchu(r);
                                    r.czyZbija = r.stanGry.czyCzarnyZbija;
                                    if (r.czyZbija) { //możliwy nullpointer
                                        r.coZbic = r.stanGry.coZbicBialego(null);
                                        if (r.ileZbija == 2) {
                                            r.coZbic2 = r.stanGry.coZbicBialego(r.coZbic);
                                        }
                                    }
                                    r.liczbaPunktowPoRuchu = r.stanGry.punktyCzarnego;
                                    ruchy.add(r);
                                }
                            }
                        }
                    }
                }
            }
        } else if (gracz == BIALY) { //BIALY
            if (liczbaPionkowDoRozstawieniaBIALY > 0) { //rozstawianie
                for (Pole pole : board) {
                    if (pole.czyWolne) {
                        Ruch r = new Ruch();
                        r.gracz = BIALY;
                        r.rodzajRuchu = "postaw";
                        r.skad = new Pole("", 0);
                        r.dokad = pole.copy();
                        r.stanGry = stanGryPoRuchu(r);
                        r.czyZbija = r.stanGry.czyBialyZbija;
                        if (r.czyZbija) { //możliwy nullpointer
                            r.coZbic = r.stanGry.coZbicCzarnego(null);
                            if (r.ileZbija == 2) {
                                r.coZbic2 = r.stanGry.coZbicCzarnego(r.coZbic);
                            }
                        }
                        r.liczbaPunktowPoRuchu = r.stanGry.punktyBialego;
                        ruchy.add(r);
                    }
                }
            } else if (liczbaPionkowBIALY > 3) {//przesuwanie po sasiadach
                for (Pole pole : board) {
                    if (pole.zajetePrzez == BIALY) {
                        ArrayList<Pole> dostepnePola = dostepneSasiady(pole);
                        for (Pole p : dostepnePola) {
                            Ruch r = new Ruch();
                            r.gracz = BIALY;
                            r.rodzajRuchu = "rusz";
                            r.skad = pole.copy();
                            r.dokad = p.copy();
                            r.stanGry = stanGryPoRuchu(r);
                            r.czyZbija = r.stanGry.czyBialyZbija;
                            if (r.czyZbija) { //możliwy nullpointer
                                r.coZbic = r.stanGry.coZbicCzarnego(null);
                                if (r.ileZbija == 2) {
                                    r.coZbic2 = r.stanGry.coZbicCzarnego(r.coZbic);
                                }
                            }
                            r.liczbaPunktowPoRuchu = r.stanGry.punktyBialego;
                            ruchy.add(r);
                        }
                    }
                }
            } else if (liczbaPionkowBIALY == 3) { //poruszanie CZARNEGO po całej planszy
                for (Pole poleGracza : board) {
                    if (poleGracza.zajetePrzez == BIALY) {
                        for (Pole poleWolne : board) {
                            if (poleWolne.czyWolne) {
                                if (!czyToCofanie(poleGracza, poleWolne)) {
                                    Ruch r = new Ruch();
                                    r.gracz = BIALY;
                                    r.rodzajRuchu = "rusz";
                                    r.skad = poleGracza.copy();
                                    r.dokad = poleWolne.copy();
                                    r.stanGry = stanGryPoRuchu(r);
                                    r.czyZbija = r.stanGry.czyBialyZbija;
                                    if (r.czyZbija) { //możliwy nullpointer
                                        r.coZbic = r.stanGry.coZbicCzarnego(null);
                                        if (r.ileZbija == 2) {
                                            r.coZbic2 = r.stanGry.coZbicCzarnego(r.coZbic);
                                        }
                                    }
                                    r.liczbaPunktowPoRuchu = r.stanGry.punktyBialego;
                                    ruchy.add(r);
                                }
                            }
                        }
                    }
                }
            }
        }
        return ruchy;
    }

    private boolean czyToCofanie(Pole poleGracza, Pole poleWolne) {
        if (poleGracza.zajetePrzez == CZARNY) {
            if (poleGracza.czyToToSamoPole(blokowanyCzarnyPionek)) {
                if (poleWolne.czyToToSamoPole(ostatniePoleCzarnego))
                    return true;
            }
            return false;
        } else if (poleGracza.zajetePrzez == BIALY) {
            if (poleGracza.czyToToSamoPole(blokowanyBialyPionek)) {
                if (poleWolne.czyToToSamoPole(ostatniePoleBialego))
                    return true;
            }
            return false;
        }
        return false;
    }

    private ArrayList<Pole> dostepneSasiady(Pole pole) {
        ArrayList<Pole> dostepne = new ArrayList<>();
        for (Pole p : pole.sasiedzi) {
            if (p.czyWolne) {
                if (pole.zajetePrzez == CZARNY) { //blokowanie cofania czarnego
                    if (pole.czyToToSamoPole(blokowanyCzarnyPionek)) {
                        if (!p.czyToToSamoPole(ostatniePoleCzarnego))
                            dostepne.add(p);
                    } else {
                        dostepne.add(p);
                    }
                } else if (pole.zajetePrzez == BIALY) { //blokowanie cofania bialego
                    if (pole.czyToToSamoPole(blokowanyBialyPionek)) {
                        if (!p.czyToToSamoPole(ostatniePoleBialego))
                            dostepne.add(p);
                    } else {
                        dostepne.add(p);
                    }
                }
            }
        }
        return dostepne;

    }

    private StanGry stanGryPoRuchu(Ruch r) {
        StanGry stan = new StanGry();
        stan.board = new ArrayList<>();
        for (Pole p : board) {
            stan.board.add(p.copy());
        }
        for (Mlynek m : mlynki)
            stan.mlynki.add(m.copy());


        stan.wykonajRuch(r, r.gracz);
        //funkcjeOceny
        stan.punktyCzarnego = 10*stan.policzLiczbeRuchowGracza(CZARNY)+stan.policzPunktyZaMlynki(CZARNY, stan.board)-(stan.liczbaPionkowBIALY-liczbaPionkowBIALY);
        stan.punktyBialego = 10*stan.policzLiczbeRuchowGracza(BIALY)+stan.policzPunktyZaMlynki(BIALY, stan.board)-(stan.liczbaPionkowCZARNY-liczbaPionkowCZARNY);
        if(r.gracz==BIALY)
            r.ileZbija=stan.ileBialyZbija;
        else
            r.ileZbija= stan.ileCzarnyZbija;
        //stan.aktywujMlynki(r.dokad);

        return stan;
    }

    private void update() {
        currentState.update();
        keyManager.update();


        if (currentState.getClass() != MenuState.class && currentState.getClass() != EndgameState.class) {

            if (mouseManager.isLeftPressed()) {

                if (czyBialyZbija || czyCzarnyZbija) { //zbijanie
                    if (czyBialyZbija && !czyBialyAI) { //zbijanie przez BIALEGO
                        Pole kliknietePole = polePodXY(mouseManager.getMouseX(), mouseManager.getMouseY());
                        if (kliknietePole != null) {
                            if (kliknietePole.zajetePrzez == CZARNY) {
                                kliknietePole.zajetePrzez = null;
                                kliknietePole.czyWolne = true;
                                liczbaPionkowCZARNY -= 1;
                                ileBialyZbija -= 1;
                                if (ileBialyZbija == 0) {
                                    ruch = CZARNY;
                                    czyBialyZbija = false;
                                }
                                liczbaRuchowBialego += 1;
                                log("BIALY zbija pionek:" + kliknietePole.pozycja());
                            }
                        }
                    } else if (czyCzarnyZbija && !czyCzarnyAI) { //zbijanie przez CZARNEGO
                        Pole kliknietePole = polePodXY(mouseManager.getMouseX(), mouseManager.getMouseY());
                        if (kliknietePole != null) {
                            if (kliknietePole.zajetePrzez == BIALY) {
                                kliknietePole.zajetePrzez = null;
                                kliknietePole.czyWolne = true;
                                liczbaPionkowBIALY -= 1;
                                ileCzarnyZbija -= 1;
                                if (ileCzarnyZbija == 0) {
                                    ruch = BIALY;
                                    czyCzarnyZbija = false;
                                }
                                liczbaRuchowCzarnego += 1;
                                log("CZARNY zbija pionek:" + kliknietePole.pozycja());
                            }
                        }
                    }
                } else {
                    if (liczbaPionkowDoRozstawieniaBIALY > 0 && ruch == BIALY) { //rozstawianie białych
                        timeHelper = System.currentTimeMillis();//todel
//                        postawPionek(BIALY, mouseManager.getMouseX(), mouseManager.getMouseY());
                        Pole kliknietePole = polePodXY(mouseManager.getMouseX(), mouseManager.getMouseY());
                        if (kliknietePole != null && kliknietePole.czyWolne) {
                            kliknietePole.czyWolne = false;
                            kliknietePole.zajetePrzez = BIALY;
                            liczbaPionkowDoRozstawieniaBIALY -= 1;
                            liczbaPionkowBIALY += 1;
                            aktywujMlynki(kliknietePole);
                            if (!czyBialyZbija)
                                ruch = CZARNY;
                            liczbaRuchowBialego += 1;
                            log("BIALY rozstawia pionek:" + kliknietePole.pozycja());
                        }


                    } else if (liczbaPionkowDoRozstawieniaCZARNY > 0 && ruch == CZARNY) { //rozstawianie czarnych
                        timeHelper = System.currentTimeMillis();//todel
//                        postawPionek(CZARNY, mouseManager.getMouseX(), mouseManager.getMouseY());
                        Pole kliknietePole = polePodXY(mouseManager.getMouseX(), mouseManager.getMouseY());
                        if (kliknietePole != null && kliknietePole.czyWolne) {
                            kliknietePole.czyWolne = false;
                            kliknietePole.zajetePrzez = CZARNY;
                            liczbaPionkowDoRozstawieniaCZARNY -= 1;
                            liczbaPionkowCZARNY += 1;
                            aktywujMlynki(kliknietePole);
                            if (!czyCzarnyZbija)
                                ruch = BIALY;
                            liczbaRuchowCzarnego += 1;
                            log("CZARNY rozstawia pionek:" + kliknietePole.pozycja());
                        }
                    } else {
                        Pole kliknietePole = polePodXY(mouseManager.getMouseX(), mouseManager.getMouseY());
                        if (kliknietePole != null) {
                            if (wybranyPionek != null) {
                                if (kliknietePole == wybranyPionek && System.currentTimeMillis() - timeHelper > opoznienie) { // odznaczenie pionka
                                    timeHelper = System.currentTimeMillis();//todel

                                    wybranyPionek.czyWybrany = false;
                                    czyWybral = false;
                                    wybranyPionek = null;
                                } else if (kliknietePole.czyWolne) { //ruch
                                    if (ruch == BIALY) {
                                        if (liczbaPionkowBIALY == 3) { //poruszanie po całej planszy
                                            if (wybranyPionek.czyToToSamoPole(blokowanyBialyPionek) && kliknietePole.czyToToSamoPole(ostatniePoleBialego)) {
                                            } else {
                                                ostatniePoleBialego.alpha = wybranyPionek.alpha; //pole z ktorego ruszyl pionek
                                                ostatniePoleBialego.number = wybranyPionek.number;
                                                blokowanyBialyPionek.alpha = kliknietePole.alpha; // pole na ktory ruszyl pionek
                                                blokowanyBialyPionek.number = kliknietePole.number;
                                                ruszPionek(kliknietePole);
                                                liczbaRuchowBialego += 1;
                                            }
                                        } else { //poruszanie po sąsiadach
                                            if (wybranyPionek.czyPodanePoleJestSasiadem(kliknietePole)) {
                                                if (wybranyPionek.czyToToSamoPole(blokowanyBialyPionek) && kliknietePole.czyToToSamoPole(ostatniePoleBialego)) {
                                                } else {
                                                    ostatniePoleBialego.alpha = wybranyPionek.alpha; //pole z ktorego ruszyl pionek
                                                    ostatniePoleBialego.number = wybranyPionek.number;
                                                    blokowanyBialyPionek.alpha = kliknietePole.alpha; // pole na ktory ruszyl pionek
                                                    blokowanyBialyPionek.number = kliknietePole.number;
                                                    ruszPionek(kliknietePole);
                                                    liczbaRuchowBialego += 1;
                                                }
                                            }
                                        }
                                        timeHelper = System.currentTimeMillis();//todel
                                    } else if (ruch == CZARNY) {
                                        timeHelper = System.currentTimeMillis();//todel
                                        if (liczbaPionkowCZARNY == 3) { //poruszanie po całej planszy
                                            if (wybranyPionek.czyToToSamoPole(blokowanyCzarnyPionek) && kliknietePole.czyToToSamoPole(ostatniePoleCzarnego)) {
                                            } else {
                                                ostatniePoleCzarnego.alpha = wybranyPionek.alpha; //pole z ktorego ruszyl pionek
                                                ostatniePoleCzarnego.number = wybranyPionek.number;
                                                blokowanyCzarnyPionek.alpha = kliknietePole.alpha; // pole na ktory ruszyl pionek
                                                blokowanyCzarnyPionek.number = kliknietePole.number;
                                                ruszPionek(kliknietePole);
                                                liczbaRuchowCzarnego += 1;
                                            }
                                        } else { //poruszanie po sąsiadach
                                            if (wybranyPionek.czyPodanePoleJestSasiadem(kliknietePole)) {
                                                if (wybranyPionek.czyToToSamoPole(blokowanyCzarnyPionek) && kliknietePole.czyToToSamoPole(ostatniePoleCzarnego)) {
                                                } else {
                                                    ostatniePoleCzarnego.alpha = wybranyPionek.alpha; //pole z ktorego ruszyl pionek
                                                    ostatniePoleCzarnego.number = wybranyPionek.number;
                                                    blokowanyCzarnyPionek.alpha = kliknietePole.alpha; // pole na ktory ruszyl pionek
                                                    blokowanyCzarnyPionek.number = kliknietePole.number;
                                                    ruszPionek(kliknietePole);
                                                    liczbaRuchowCzarnego += 1;
                                                }
                                            }
                                        }
                                    }

                                }

                            } else if (kliknietePole.zajetePrzez == ruch && System.currentTimeMillis() - timeHelper > opoznienie) { //zaznaczenie pionka
                                timeHelper = System.currentTimeMillis(); //TODO


                                czyWybral = true;
                                wybranyPionek = kliknietePole;
                                wybranyPionek.czyWybrany = true;
                            }
                        }
                    }


                    //delay

                }

                timeHelper = System.currentTimeMillis();
                while (System.currentTimeMillis() - timeHelper < opoznienie) {

                }
            }

            if (ruch == CZARNY && czyCzarnyAI) {//estymacja ruchu
                minmax(CZARNY);
            }
            if (ruch == BIALY && czyBialyAI) {//estymacja ruchu
                minmax(BIALY);
            }
            //implai
            if (czyCzarnyAI && ruch == CZARNY) {
                wykonajRuch(ruchCzarnego, ruch);

                timehelperDWA=System.currentTimeMillis(); //opoznienie miedzy ruchami botow
                while(System.currentTimeMillis()-timehelperDWA<opoznienieMiedzyRuchami){

                }

                ruchCzarnego = null;

            } else if (czyBialyAI && ruch == BIALY) {
                wykonajRuch(ruchBialego, ruch);

                timehelperDWA=System.currentTimeMillis(); //opoznienie miedzy ruchami botow
                while(System.currentTimeMillis()-timehelperDWA<opoznienieMiedzyRuchami){

                }

                ruchBialego = null;

            }


            if (ktoWygral==BIALY||(liczbaPionkowCZARNY < 3 && liczbaPionkowDoRozstawieniaCZARNY == 0)) { //kto wygrał
                ktoWygral = BIALY;
                log("BIALY wygrywa");
                log("Czas rozgrywki:"+(System.currentTimeMillis() - czasOdStartu) / 1000 + " [s]");
                log("Liczba ruchów białego:"+liczbaRuchowBialego);
                log("Liczba ruchów czarnego:"+liczbaRuchowCzarnego);
                EndgameState endgameState = new EndgameState(handler);
                setCurrentState(endgameState);
            } else if (ktoWygral==CZARNY||(liczbaPionkowBIALY < 3 && liczbaPionkowDoRozstawieniaBIALY == 0)) {
                ktoWygral = CZARNY;
                log("CZARNY wygrywa");
                log("Czas rozgrywki:"+(System.currentTimeMillis() - czasOdStartu) / 1000 + " [s]");
                EndgameState endgameState = new EndgameState(handler);
                setCurrentState(endgameState);
            }else if(liczbaRuchowCzarnego>200||liczbaRuchowBialego>200){
                ktoWygral = null;
                log("REMIS");
                log("Czas rozgrywki:"+(System.currentTimeMillis() - czasOdStartu) / 1000 + " [s]");
                EndgameState endgameState = new EndgameState(handler);
                setCurrentState(endgameState);
            }
        }
    }

    private void zbijPionek(Pole pionek) {
        if (pionek.zajetePrzez == BIALY && ileCzarnyZbija>0) {
            liczbaPionkowBIALY -= 1;
            ileCzarnyZbija -= 1;
            liczbaRuchowCzarnego+=1;
            log("AI:CZARNY zbija:"+pionek.pozycja());
            zwolnijPole(polePodAlphaNumber(pionek.alpha, pionek.number));
        } else if (pionek.zajetePrzez == CZARNY&&ileBialyZbija>0) {
            liczbaPionkowCZARNY -= 1;
            ileBialyZbija -= 1;
            liczbaRuchowBialego+=1;
            log("AI:BIALY zbija:"+pionek.pozycja());
            zwolnijPole(polePodAlphaNumber(pionek.alpha, pionek.number));
        }

    }

    private void wykonajRuch(Ruch r, Gracz ruch) {
        if(r!=null&&r.rodzajRuchu.equals("brak")){
            if(ruch==BIALY)
                ktoWygral=CZARNY;
            else
                ktoWygral=BIALY;
        }else if (ruch == CZARNY && r != null) {//CZARNY
            if (r.rodzajRuchu.equals("postaw")) {
                log("AI:Czarny wstawia pionek na:" + r.dokad.pozycja());
                wstawPionek(CZARNY, r.dokad.alpha, r.dokad.number);
                aktywujMlynki(polePodAlphaNumber(r.dokad.alpha, r.dokad.number));
                if (r.czyZbija) { //breakpoint
                    ileCzarnyZbija=r.ileZbija;
                    zbijPionek(r.coZbic);
                    if (r.ileZbija == 2) {
                        zbijPionek(r.coZbic2);
                    }
                    ileCzarnyZbija=0;
                    czyCzarnyZbija = false;
                    this.ruch = BIALY;
                }
            } else if (r.rodzajRuchu.equals("rusz")) {
                Pole tmp = polePodAlphaNumber(r.skad.alpha, r.skad.number);
                ostatniePoleCzarnego = new Pole(tmp, false);
                log("AI:Czarny rusza pionek z:" + r.skad.pozycja() + " na:" + r.dokad.pozycja());
                zwolnijPole(tmp);
                wstawPionek(CZARNY, r.dokad.alpha, r.dokad.number);
                blokowanyCzarnyPionek = polePodAlphaNumber(r.dokad.alpha, r.dokad.number);
                aktywujMlynki(polePodAlphaNumber(r.dokad.alpha, r.dokad.number));
                if (r.czyZbija) {
                    ileCzarnyZbija=r.ileZbija;
                    zbijPionek(r.coZbic);
                    if (r.ileZbija == 2) {
                        zbijPionek(r.coZbic2);
                    }
                    ileCzarnyZbija=0;
                    czyCzarnyZbija = false;
                    this.ruch = BIALY;
                }
            }

        } else if (ruch == BIALY && r != null) {//BIALY
            if (r.rodzajRuchu.equals("postaw")) {
                log("AI:BIALY wstawia pionek na:" + r.dokad.pozycja());
                wstawPionek(BIALY, r.dokad.alpha, r.dokad.number);
                aktywujMlynki(polePodAlphaNumber(r.dokad.alpha, r.dokad.number));
                if (r.czyZbija) { //breakpoint
                    ileBialyZbija=r.ileZbija;
                    zbijPionek(r.coZbic);
                    if (r.ileZbija == 2) {
                        zbijPionek(r.coZbic2);
                    }
                    ileBialyZbija=0;
                    czyBialyZbija = false;
                    this.ruch = CZARNY;
                }
            } else if (r.rodzajRuchu.equals("rusz")) {
                Pole tmp = polePodAlphaNumber(r.skad.alpha, r.skad.number);
                ostatniePoleBialego = new Pole(tmp, false);
                log("AI:BIALY rusza pionek z:" + r.skad.pozycja() + " na:" + r.dokad.pozycja());
                zwolnijPole(tmp);
                wstawPionek(BIALY, r.dokad.alpha, r.dokad.number);
                blokowanyBialyPionek = polePodAlphaNumber(r.dokad.alpha, r.dokad.number);
                aktywujMlynki(polePodAlphaNumber(r.dokad.alpha, r.dokad.number));
                if (r.czyZbija) {
                    ileBialyZbija=r.ileZbija;
                    zbijPionek(r.coZbic);
                    if (r.ileZbija == 2) {
                        zbijPionek(r.coZbic2);
                    }
                    ileBialyZbija=0;
                    czyBialyZbija = false;
                    this.ruch = CZARNY;
                }
            }

        }
    }

    private void zwolnijPole(Pole tmp) {
        tmp.zajetePrzez = null;
        tmp.czyWolne = true;
        tmp.czyWybrany = false;

    }

    private int policzPunktyZaMlynki(Gracz gracz) {
        int liczbaPunktow = 0;
        for (Mlynek m : mlynki) {
            liczbaPunktow += m.ilePunktowZaMlynek(gracz);
        }
        return liczbaPunktow;
    }

    private int policzLiczbeRuchowGracza(Gracz gracz) {
        int liczbaRuchow = 0;
        ArrayList<Pole> polaGracza = polaGracza(gracz);
        for (Pole pole : polaGracza) {
            liczbaRuchow += liczbaDostepnychRuchowDlaPola(pole);


        }

        return liczbaRuchow;
    }

    private int liczbaDostepnychRuchowDlaPola(Pole pole) {
        int liczba = 0;
        if (pole != null)
            for (Pole sasiad : pole.sasiedzi) {
                if (sasiad.czyWolne) {
                    if (pole.zajetePrzez == BIALY) {
                        if (blokowanyBialyPionek.czyToToSamoPole(pole)) {
                            if (!sasiad.czyToToSamoPole(ostatniePoleBialego)) {
                                liczba++;
                            }
                        } else {
                            liczba++;
                        }
                    } else if (pole.zajetePrzez == CZARNY) {
                        if (blokowanyCzarnyPionek.czyToToSamoPole(pole)) {
                            if (!sasiad.czyToToSamoPole(ostatniePoleCzarnego)) {
                                liczba++;
                            }
                        } else {
                            liczba++;
                        }
                    }
                }
            }


        return liczba;
    }

    private ArrayList polaGracza(Gracz gracz) {
        ArrayList<Pole> pola = new ArrayList<>();
        for (Pole pole : board) {
            if (pole.zajetePrzez == gracz) {
                pola.add(pole);
            }
        }
        return pola;
    }


    private void log(String log) {
        System.out.println(log);
    }

    private void ruszPionek(Pole kliknietePole) {
        if (ruch == BIALY) { //logowanie
            log("BIALY przesuwa pionek z:" + wybranyPionek.pozycja() + " na:" + kliknietePole.pozycja());
            ruchBialego = null;
        } else if (ruch == CZARNY) {
            log("CZARNY przesuwa pionek z:" + wybranyPionek.pozycja() + " na:" + kliknietePole.pozycja());
            ruchCzarnego = null;
        }
        wybranyPionek.czyWolne = true;
        wybranyPionek.czyWybrany = false;
        wybranyPionek.zajetePrzez = null;
        wybranyPionek = null;
        kliknietePole.czyWolne = false;
        kliknietePole.zajetePrzez = ruch;
        aktywujMlynki(kliknietePole);
        if (!(czyCzarnyZbija || czyBialyZbija)) {
            if (ruch == BIALY)
                ruch = CZARNY;
            else
                ruch = BIALY;
        }
    }


    private void aktywujMlynki(Pole pole) {
        ileBialyZbija = 0;
        ileCzarnyZbija = 0;
        for (Mlynek mlynek : mlynki) {
            if (mlynek.czyZawieraPole(pole)) {
                if (mlynek.czyMlynek(board)) {
                    mlynek.czyAktywny = true;
                    if (pole.zajetePrzez == BIALY) {
                        czyBialyZbija = true;
                        ileBialyZbija += 1;
                    } else if (pole.zajetePrzez == CZARNY) {
                        czyCzarnyZbija = true;
                        ileCzarnyZbija += 1;
                    }
                }
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
                    if (liczbaPionkowDoRozstawieniaBIALY > 0) {
                        liczbaPionkowDoRozstawieniaBIALY -= 1;
                        liczbaPionkowBIALY += 1;
                    }
                    liczbaRuchowBialego+=1;
                } else if (gracz == CZARNY) {
                    ruch = BIALY;
                    if (liczbaPionkowDoRozstawieniaCZARNY > 0) {
                        liczbaPionkowDoRozstawieniaCZARNY -= 1;
                        liczbaPionkowCZARNY += 1;
                    }
                    liczbaRuchowCzarnego+=1;
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
                    if (liczbaPionkowDoRozstawieniaBIALY > 0) {
                        liczbaPionkowDoRozstawieniaBIALY -= 1;
                        liczbaPionkowBIALY += 1;
                    }
                } else if (gracz == CZARNY) {
                    ruch = BIALY;
                    if (liczbaPionkowDoRozstawieniaCZARNY > 0) {
                        liczbaPionkowDoRozstawieniaCZARNY -= 1;
                        liczbaPionkowCZARNY += 1;
                    }
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
        switch (tankColor){
            case 0:czyBialyAI=true; czyCzarnyAI=true; break;
            case 1:czyBialyAI=false; czyCzarnyAI=true; break;
            case 2:czyBialyAI=true; czyCzarnyAI=false; break;
            case 3:czyBialyAI=false; czyCzarnyAI=false; break;

        }
    }

    public static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        return (i >= minValueInclusive && i <= maxValueInclusive);
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

        A1.sasiedzi.add(A4);
        A1.sasiedzi.add(D1);

        A4.sasiedzi.add(A1);
        A4.sasiedzi.add(A7);
        A4.sasiedzi.add(B4);

        A7.sasiedzi.add(A4);
        A7.sasiedzi.add(D7);

        B2.sasiedzi.add(D2);
        B2.sasiedzi.add(B4);

        B4.sasiedzi.add(A4);
        B4.sasiedzi.add(B2);
        B4.sasiedzi.add(B6);
        B4.sasiedzi.add(C4);

        B6.sasiedzi.add(B4);
        B6.sasiedzi.add(D6);

        C3.sasiedzi.add(C4);
        C3.sasiedzi.add(D3);

        C4.sasiedzi.add(B4);
        C4.sasiedzi.add(C3);
        C4.sasiedzi.add(C5);

        C5.sasiedzi.add(C4);
        C5.sasiedzi.add(D5);

        D1.sasiedzi.add(A1);
        D1.sasiedzi.add(G1);
        D1.sasiedzi.add(D2);

        D2.sasiedzi.add(B2);
        D2.sasiedzi.add(D1);
        D2.sasiedzi.add(D3);
        D2.sasiedzi.add(F2);

        D3.sasiedzi.add(C3);
        D3.sasiedzi.add(D2);
        D3.sasiedzi.add(E3);

        D5.sasiedzi.add(C5);
        D5.sasiedzi.add(D6);
        D5.sasiedzi.add(E5);

        D6.sasiedzi.add(B6);
        D6.sasiedzi.add(D5);
        D6.sasiedzi.add(D7);
        D6.sasiedzi.add(F6);

        D7.sasiedzi.add(A7);
        D7.sasiedzi.add(D6);
        D7.sasiedzi.add(G7);

        E3.sasiedzi.add(D3);
        E3.sasiedzi.add(E4);

        E4.sasiedzi.add(E3);
        E4.sasiedzi.add(E5);
        E4.sasiedzi.add(F4);

        E5.sasiedzi.add(D5);
        E5.sasiedzi.add(E4);

        F2.sasiedzi.add(D2);
        F2.sasiedzi.add(F4);

        F4.sasiedzi.add(E4);
        F4.sasiedzi.add(F2);
        F4.sasiedzi.add(F6);
        F4.sasiedzi.add(G4);

        F6.sasiedzi.add(D6);
        F6.sasiedzi.add(F4);

        G1.sasiedzi.add(D1);
        G1.sasiedzi.add(G4);

        G4.sasiedzi.add(F4);
        G4.sasiedzi.add(G1);
        G4.sasiedzi.add(G7);

        G7.sasiedzi.add(D7);
        G7.sasiedzi.add(G4);

        mlynki.add(new Mlynek(A1, A4, A7));
        mlynki.add(new Mlynek(B2, B4, B6));
        mlynki.add(new Mlynek(C3, C4, C5));
        mlynki.add(new Mlynek(D1, D2, D3));
        mlynki.add(new Mlynek(D5, D6, D7));
        mlynki.add(new Mlynek(E3, E4, E5));
        mlynki.add(new Mlynek(F2, F4, F6));
        mlynki.add(new Mlynek(G1, G4, G7));

        mlynki.add(new Mlynek(A1, D1, G1));
        mlynki.add(new Mlynek(B2, D2, F2));
        mlynki.add(new Mlynek(C3, D3, E3));
        mlynki.add(new Mlynek(A4, B4, C4));
        mlynki.add(new Mlynek(E4, F4, G4));
        mlynki.add(new Mlynek(C5, D5, E5));
        mlynki.add(new Mlynek(B6, D6, F6));
        mlynki.add(new Mlynek(A7, D7, G7));


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
        //  rozdajPionki();
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

}

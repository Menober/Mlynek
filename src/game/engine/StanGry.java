package game.engine;

import java.util.ArrayList;

import static game.engine.Gracz.BIALY;
import static game.engine.Gracz.CZARNY;

public class StanGry {
    public int liczbaPionkowDoRozstawieniaBIALY ;
    public int liczbaPionkowDoRozstawieniaCZARNY;
    public int liczbaPionkowBIALY ;
    public int liczbaPionkowCZARNY ;
    public Gracz ruch ;
    public ArrayList<Pole> board = new ArrayList<>();
    public ArrayList<Mlynek> mlynki = new ArrayList<Mlynek>();
    long czasOdStartu;
    Pole wybranyPionek;
    boolean czyWybral;
    boolean czyCzarnyZbija = false;
    boolean czyBialyZbija = false;
    int ileBialyZbija ;
    int ileCzarnyZbija;
    Gracz ktoWygral = null;
    Pole ostatniePoleBialego ;
    Pole ostatniePoleCzarnego ;
    Pole blokowanyBialyPionek ;
    Pole blokowanyCzarnyPionek ;
    int liczbaRuchowBialego ;
    int liczbaRuchowCzarnego ;
    int punktyCzarnego;
    int punktyBialego;

    public int policzPunktyZaMlynki(Gracz gracz,ArrayList<Pole> board) {
        int liczbaPunktow = 0;
        for (Mlynek m : mlynki) {
            liczbaPunktow += m.ilePunktowZaMlynek(gracz,board);
        }
        return liczbaPunktow;
    }


    public void wykonajRuch(Ruch r,Gracz ruch) {
        if (ruch == CZARNY && r != null) {//CZARNY
            if (r.rodzajRuchu.equals("postaw")) {
                wstawPionek(CZARNY, r.dokad.alpha, r.dokad.number);
            } else if (r.rodzajRuchu.equals("rusz")) {
                Pole tmp = polePodAlphaNumber(r.skad.alpha, r.skad.number);
                ostatniePoleCzarnego = new Pole(tmp, false);
                zwolnijPole(tmp);
                wstawPionek(CZARNY, r.dokad.alpha, r.dokad.number);
                blokowanyCzarnyPionek = polePodAlphaNumber(r.dokad.alpha, r.dokad.number);
            }

        } else if (ruch == BIALY && r != null) {//BIALY
            if (r.rodzajRuchu.equals("postaw")) {
                wstawPionek(BIALY, r.dokad.alpha, r.dokad.number);
                r = r.nastepnyRuch;
            } else if (r.rodzajRuchu.equals("rusz")) {
                Pole tmp = polePodAlphaNumber(r.skad.alpha, r.skad.number);
                ostatniePoleBialego = new Pole(tmp, false);
//                log("AI:Bialy rusza pionek z:" + r.skad.pozycja() + " na:" + r.dokad.pozycja());
                zwolnijPole(tmp);
                wstawPionek(BIALY, r.dokad.alpha, r.dokad.number);
                blokowanyBialyPionek = polePodAlphaNumber(r.dokad.alpha, r.dokad.number);
                r = r.nastepnyRuch;
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

    private void zwolnijPole(Pole tmp) {
        tmp.zajetePrzez = null;
        tmp.czyWolne = true;
        tmp.czyWybrany = false;
    }

}

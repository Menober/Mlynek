package game.engine;

import java.util.ArrayList;

import static game.engine.Gracz.BIALY;
import static game.engine.Gracz.CZARNY;

public class StanGry {
    public int liczbaPionkowDoRozstawieniaBIALY;
    public int liczbaPionkowDoRozstawieniaCZARNY;
    public int liczbaPionkowBIALY;
    public int liczbaPionkowCZARNY;
    public Gracz ruch;
    public ArrayList<Pole> board = new ArrayList<>();
    public ArrayList<Mlynek> mlynki = new ArrayList<Mlynek>();
    long czasOdStartu;
    Pole wybranyPionek;
    boolean czyWybral;
    boolean czyCzarnyZbija = false;
    boolean czyBialyZbija = false;
    int ileBialyZbija;
    int ileCzarnyZbija;
    Gracz ktoWygral = null;
    Pole ostatniePoleBialego;
    Pole ostatniePoleCzarnego;
    Pole blokowanyBialyPionek;
    Pole blokowanyCzarnyPionek;
    int liczbaRuchowBialego;
    int liczbaRuchowCzarnego;
    int punktyCzarnego;
    int punktyBialego;
    int glebokosc = 2;
    Ruch ruchCzarnego = null;
    Ruch ruchBialego = null;


    public int policzPunktyZaMlynki(Gracz gracz, ArrayList<Pole> board) {
        int liczbaPunktow = 0;
        for (Mlynek m : mlynki) {
            liczbaPunktow += m.ilePunktowZaMlynek(gracz, board);
        }
        return liczbaPunktow;
    }


    public void wykonajRuch(Ruch r, Gracz ruch) {
        if (ruch == CZARNY && r != null) {//CZARNY
            if (r.rodzajRuchu.equals("postaw")) {
                wstawPionek(CZARNY, r.dokad.alpha, r.dokad.number);
                aktywujMlynki(polePodAlphaNumber(r.dokad.alpha, r.dokad.number));
                if (r.czyZbija) {
//                    zbijPionek(r);
                    if (r.ileZbija == 2) {
//                        zbijPionek(r);
                    }
//                    czyCzarnyZbija=false;
//                    this.ruch=BIALY;
                }
            } else if (r.rodzajRuchu.equals("rusz")) {
                Pole tmp = polePodAlphaNumber(r.skad.alpha, r.skad.number);
                ostatniePoleCzarnego = new Pole(tmp, false);
                zwolnijPole(tmp);
                wstawPionek(CZARNY, r.dokad.alpha, r.dokad.number);
                blokowanyCzarnyPionek = polePodAlphaNumber(r.dokad.alpha, r.dokad.number);
                aktywujMlynki(polePodAlphaNumber(r.dokad.alpha, r.dokad.number));
                if (r.czyZbija) {
//                    zbijPionek(r);
                    if (r.ileZbija == 2) {
//                        zbijPionek(r);
                    }
                }
            }

        } else if (ruch == BIALY && r != null) {//BIALY
            if (r.rodzajRuchu.equals("postaw")) {
                wstawPionek(BIALY, r.dokad.alpha, r.dokad.number);
                aktywujMlynki(polePodAlphaNumber(r.dokad.alpha, r.dokad.number));
                if (r.czyZbija) {
//                    zbijPionek(r);
                    if (r.ileZbija == 2) {
//                        zbijPionek(r);
                    }
//                    czyCzarnyZbija=false;
//                    this.ruch=BIALY;
                }
            } else if (r.rodzajRuchu.equals("rusz")) {
                Pole tmp = polePodAlphaNumber(r.skad.alpha, r.skad.number);
                ostatniePoleBialego = new Pole(tmp, false);
                zwolnijPole(tmp);
                wstawPionek(BIALY, r.dokad.alpha, r.dokad.number);
                blokowanyBialyPionek = polePodAlphaNumber(r.dokad.alpha, r.dokad.number);
                aktywujMlynki(polePodAlphaNumber(r.dokad.alpha, r.dokad.number));
                if (r.czyZbija) {
//                    zbijPionek(r);
                    if (r.ileZbija == 2) {
//                        zbijPionek(r);
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

    public Pole coZbicBialego(Pole pierwszeZbicie) {
        if (pierwszeZbicie == null) {
            for (Pole p : board) {
                if (p.zajetePrzez == BIALY) {
                    return p;
                }
            }
        } else {
            for (Pole p : board) {
                if (p.zajetePrzez == BIALY && !p.czyToToSamoPole(pierwszeZbicie)) {
                    return p;
                }
            }
        }
        return null;
    }

    public Pole coZbicCzarnego(Pole pierwszeZbicie) {
        if (pierwszeZbicie == null) {
            for (Pole p : board) {
                if (p.zajetePrzez == CZARNY) {
                    return p;
                }
            }
        } else {
            for (Pole p : board) {
                if (p.zajetePrzez == CZARNY && !p.czyToToSamoPole(pierwszeZbicie)) {
                    return p;
                }
            }
        }
        return null;
    }

    public void aktywujMlynki(Pole pole) {
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

    public int policzLiczbeRuchowGracza(Gracz gracz) {
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
                        if (blokowanyBialyPionek != null && blokowanyBialyPionek.czyToToSamoPole(pole)) {
                            if (!sasiad.czyToToSamoPole(ostatniePoleBialego)) {
                                liczba++;
                            }
                        } else {
                            liczba++;
                        }
                    } else if (pole.zajetePrzez == CZARNY) {
                        if (blokowanyCzarnyPionek != null && blokowanyCzarnyPionek.czyToToSamoPole(pole)) {
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
}

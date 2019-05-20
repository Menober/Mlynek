package game.engine;

import java.util.ArrayList;

public class Pole {
    boolean czyWolne = true;
    Gracz zajetePrzez;
    String alpha;
    int number;
    int x1, x2, y1, y2;
    boolean czyWybrany = false;
    ArrayList<Pole> sasiedzi = new ArrayList<>();

    public Pole(String alpha, int number) {
        this.alpha = alpha;
        this.number = number;
    }
    public Pole(Pole pole,boolean czyKopiowacSasiadow){
        this.alpha=pole.alpha;
        this.number=pole.number;
        this.czyWolne=pole.czyWolne;
        this.zajetePrzez=pole.zajetePrzez;
        this.x1=pole.x1;
        this.x2=pole.x2;
        this.y2=pole.y2;
        this.y1=pole.y1;
        this.czyWybrany=pole.czyWybrany;
        if(czyKopiowacSasiadow)
            this.sasiedzi=kopiujSasiadow(pole);
    }

    private ArrayList<Pole> kopiujSasiadow(Pole pole) {
        ArrayList<Pole> kopia=new ArrayList<>();
        for(Pole p:pole.sasiedzi){
            kopia.add(new Pole(p,false));
        }
        return kopia;
    }

    public void setRect(int x1, int x2, int y1, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public boolean czyPodanePoleJestSasiadem(Pole p) {
        for (Pole sasiad : sasiedzi) {
            if (p.alpha.equals(sasiad.alpha) && p.number == sasiad.number)
                return true;
        }
        return false;
    }

    public String dajSasiadow() {
        String tekst = "";
        for (Pole p : sasiedzi) {
            if (p.czyWolne) {
                tekst += p.alpha + p.number + ", ";
            }
        }
        return tekst;
    }

    public boolean czyToToSamoPole(Pole pole){
        return pole.number==this.number&&this.alpha.equals(pole.alpha);
    }

    public String pozycja() {
        return alpha+number;
    }

    public Pole copy() {
        return new Pole(this,true);
    }
}

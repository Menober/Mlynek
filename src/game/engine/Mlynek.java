package game.engine;

import java.util.ArrayList;

public class Mlynek {
    public Pole p1, p2, p3;
    public boolean czyAktywny = true;

    public Mlynek(Pole p1, Pole p2, Pole p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Mlynek copy(){
        Mlynek newMlynek=new Mlynek(p1.copy(),p2.copy(),p3.copy());
        newMlynek.czyAktywny=czyAktywny;
        return newMlynek;
    }

    public boolean czyMlynek() {
        return p1.zajetePrzez == p2.zajetePrzez && p2.zajetePrzez == p3.zajetePrzez && p2.zajetePrzez != null;
    }

    public boolean czyMlynek(ArrayList<Pole> board) {
        return polePodAlphaNumber(p1.alpha,p1.number,board).zajetePrzez == polePodAlphaNumber(p2.alpha,p2.number,board).zajetePrzez && polePodAlphaNumber(p2.alpha,p2.number,board).zajetePrzez == polePodAlphaNumber(p3.alpha,p3.number,board).zajetePrzez && polePodAlphaNumber(p2.alpha,p2.number,board).zajetePrzez != null;
    }

    private Pole polePodAlphaNumber(String alpha, int number,ArrayList<Pole> board) {
        //  System.out.println(mouseX+";"+mouseY);
        for (Pole p : board) {
            if (p.alpha.equals(alpha) && p.number == number) {
                return p;
            }
        }
        return null;
    }


    public boolean czyZawieraPole(Pole pole) {
        if (p1.czyToToSamoPole(pole) || p2.czyToToSamoPole(pole) || p3.czyToToSamoPole(pole))
            return true;
        return false;
    }

    public int ilePunktowZaMlynek(Gracz gracz) {
        int liczbaPunktow = 0;
        int liczbaZajetychPol=0;
        if (p1.zajetePrzez == gracz)
            liczbaZajetychPol+=1;
        if (p2.zajetePrzez == gracz)
            liczbaZajetychPol+=1;
        if (p3.zajetePrzez == gracz)
            liczbaZajetychPol+=1;

        switch (liczbaZajetychPol){
            case 0:liczbaPunktow=0;break;
            case 1:liczbaPunktow=10;break;
            case 2:liczbaPunktow=50;break;
            case 3:liczbaPunktow=100;break;

        }

        return liczbaPunktow;
    }

    public int ilePunktowZaMlynek(Gracz gracz,ArrayList<Pole> board) {
        int liczbaPunktow = 0;
        int liczbaZajetychPol=0;
        if (polePodAlphaNumber(p1.alpha,p1.number,board).zajetePrzez == gracz)
            liczbaZajetychPol+=1;
        if (polePodAlphaNumber(p2.alpha,p2.number,board).zajetePrzez == gracz)
            liczbaZajetychPol+=1;
        if (polePodAlphaNumber(p3.alpha,p3.number,board).zajetePrzez == gracz)
            liczbaZajetychPol+=1;

        switch (liczbaZajetychPol){
            case 0:liczbaPunktow=0;break;
            case 1:liczbaPunktow=10;break;
            case 2:liczbaPunktow=50;break;
            case 3:liczbaPunktow=100;break;

        }

        return liczbaPunktow;
    }
}

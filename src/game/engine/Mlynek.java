package game.engine;

public class Mlynek {
    public Pole p1,p2,p3;
    public boolean czyAktywny=true;
    public Mlynek(Pole p1,Pole p2, Pole p3){
        this.p1=p1;
        this.p2=p2;
        this.p3=p3;
    }

    public boolean czyMlynek(){
        return p1.zajetePrzez==p2.zajetePrzez&&p2.zajetePrzez==p3.zajetePrzez&&p2.zajetePrzez!=null;
    }

    public boolean czyZawieraPole(Pole pole) {
        if(p1==pole||p2==pole||p3==pole)
            return true;
        return false;
    }
}

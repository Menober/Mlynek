package game.engine;

public class Pole {
    boolean czyWolne=true;
    Gracz zajetePrzez;
    String alpha;
    int number;
    int x1,x2,y1,y2;
    boolean czyWybrany=false;

    public Pole(String alpha, int number){
        this.alpha=alpha;
        this.number=number;
    }
    public void setRect(int x1,int x2,int y1,int y2){
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
    }
}

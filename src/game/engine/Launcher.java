package game.engine;

public class Launcher {
    public static void main(String [] args){
        Handler handler=new Handler();
        handler.setTitle("Tank 2018");
        handler.setWidth(1200);
        handler.setHeight(1000);
        Game game = new Game(handler);
        handler.setGame(game);
        handler.getGame().start();


    }
}

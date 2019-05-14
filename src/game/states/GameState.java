package game.states;

import game.entities.Player;
import game.assets.Assets;
import game.engine.Handler;
import game.tiles.Tile;
import game.world.Map;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GameState extends State {

    private Handler handler;
    private Player player;
    private Map map;
    public GameState(Handler handler){
        this.handler=handler;
        BufferedImage[] skins = null;
        switch(handler.getGame().tankColor){
            case 0:skins=Assets.tankYellow;break;
            case 1:skins=Assets.tankRed;break;
            case 2:skins=Assets.tankGreen;break;
            case 3:skins=Assets.tankGrey;break;
        }
        map=new Map(handler);
        this.player=new Player(skins,800,800);
    }
    @Override
    public void update() {

        if(handler.getKeyManager().aUp)
            player.moveUp();
        if(handler.getKeyManager().aDown)
            player.moveDown();
        if(handler.getKeyManager().aLeft)
            player.moveLeft();
        if(handler.getKeyManager().aRight)
            player.moveRight();
        player.update();

//        if( map.getTile((int)(player.getX()+32)/64,(int)(player.getY()+32)/64).equals(Tile.dirtTile))
//            player.setSpeed(1.5);
//        else if(map.getTile((int)(player.getX()+32)/64,(int)(player.getY()+32)/64).equals(Tile.grassTile))
//            player.setSpeed(2);

        //  Tymczasowo// Koordynaty środka postaci podzielone przez szerowkosc/wysokosc aby uzyskac klocek pod postacią
        //System.out.println(map.getTile((int)(player.getX()+32)/64,(int)(player.getY()+32)/64).getClass());
    }

    @Override
    public void render(Graphics g) {
        map.render(g);
        player.render(g);

    }



}

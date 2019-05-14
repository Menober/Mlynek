package game.world;

import game.assets.Assets;
import game.engine.Handler;
import game.tiles.Tile;

import java.awt.*;

public class Map {

    int xCount=12,yCount=9;
    int [][] tiles={
            {3,3,3,3,3,3,3,3,3,3,3,3},
            {3,2,2,2,2,2,2,2,2,2,2,3},
            {3,4,4,4,4,4,4,4,4,4,4,3},
            {3,4,1,1,1,1,1,1,1,1,4,3},
            {3,4,1,1,1,1,4,4,4,4,4,3},
            {3,4,1,1,4,4,4,2,2,2,2,3},
            {3,4,1,1,1,1,4,2,2,2,2,3},
            {3,4,4,4,4,4,4,2,2,2,0,3},
            {3,3,3,3,3,3,3,3,3,3,3,3}
    };
    Handler handler;
    public Map(Handler handler){
        this.handler=handler;
    }


    public Tile getTile(int x, int y){
        if(x < 0 || y < 0 || x >= handler.getWidth() || y >= handler.getHeight())
            return Tile.grassTile;

        Tile t = Tile.tiles[tiles[y][x]];
        if(t == null)
            return Tile.dirtTile;
        return t;
    }

    public void render(Graphics graphics){
//        for(int i=0;i<yCount;i++)
//            for(int j=0;j<xCount;j++)
//                Tile.tiles[tiles[i][j]].render(graphics,j*64,i*64);
        graphics.drawImage(Assets.tlo,0,0,900,900,null);



        for(int i = 1; i<=handler.getGame().liczbaPionkowDoRozstawieniaBIALY; i++){
            graphics.drawImage(Assets.player1,950,i*70,64,64,null);
        }
        for(int i = 1; i<=handler.getGame().liczbaPionkowDoRozstawieniaCZARNY; i++){
            graphics.drawImage(Assets.player2,950+64+50,i*70,64,64,null);
        }

    }
}

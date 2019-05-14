package game.tiles;

import game.assets.Assets;

import java.awt.image.BufferedImage;

public class BrickTile extends Tile{

    public BrickTile(int id) {
        super(Assets.brick, id);
    }

    @Override
    public boolean isSolid(){
        return true;
    }
}

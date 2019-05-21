package game.assets;

import java.awt.image.BufferedImage;

public class Assets {
    private static final int width = 32, height = 32;


    public static BufferedImage brick, water,dirt,stone,grass, tlo,player1,player2;
    public static BufferedImage[] tankRed,tankGreen,tankGrey,tankYellow;
    public static BufferedImage start,starth,color,colorh,exit,exith,czlowiekAI,czlowiekczlowiek,AIczlowiek,AIAI;

    public static void init(){

        SpriteSheet textures=new SpriteSheet(ImageLoader.loadImage("/textures/textures.png"));
        brick=textures.crop(0,0,32,32);
        water=textures.crop(32,0,32,32);
        dirt=textures.crop(64,0,32,32);
        stone=textures.crop(96,0,32,32);
        grass=textures.crop(128,0,32,32);


        tankYellow=new BufferedImage[4];    //0
        tankRed=new BufferedImage[4];       //1
        tankGreen=new BufferedImage[4];     //2
        tankGrey=new BufferedImage[4];      //3

        start=new SpriteSheet(ImageLoader.loadImage("/textures/start.png")).crop(0,0,256,128);
        starth=new SpriteSheet(ImageLoader.loadImage("/textures/starth.png")).crop(0,0,256,128);
        color=new SpriteSheet(ImageLoader.loadImage("/textures/color.png")).crop(0,0,256,128);
        colorh=new SpriteSheet(ImageLoader.loadImage("/textures/colorh.png")).crop(0,0,256,128);
        exit=new SpriteSheet(ImageLoader.loadImage("/textures/exit.png")).crop(0,0,256,128);
        exith=new SpriteSheet(ImageLoader.loadImage("/textures/exith.png")).crop(0,0,256,128);
        czlowiekAI=new SpriteSheet(ImageLoader.loadImage("/textures/czlowiekAI.png")).crop(0,0,256,128);
        czlowiekczlowiek=new SpriteSheet(ImageLoader.loadImage("/textures/czlowiekczlowiek.png")).crop(0,0,256,128);
        AIczlowiek=new SpriteSheet(ImageLoader.loadImage("/textures/AIczlowiek.png")).crop(0,0,256,128);
        AIAI=new SpriteSheet(ImageLoader.loadImage("/textures/AIAI.png")).crop(0,0,256,128);
        tlo =new SpriteSheet(ImageLoader.loadImage("/textures/tlo.png")).crop(0,0,900,900);

        SpriteSheet tank=new SpriteSheet(ImageLoader.loadImage("/textures/tankYellow.png"));
        tankYellow[0]=tank.crop(0,0,32,32); //up
        tankYellow[1]=tank.crop(32,0,32,32); //down
        tankYellow[2]=tank.crop(0,32,32,32);//left
        tankYellow[3]=tank.crop(32,32,32,32);//right

        tank=new SpriteSheet(ImageLoader.loadImage("/textures/tankRed.png"));
        tankRed[0]=tank.crop(0,0,32,32); //up
        tankRed[1]=tank.crop(32,0,32,32); //down
        tankRed[2]=tank.crop(0,32,32,32);//left
        tankRed[3]=tank.crop(32,32,32,32);//right

        tank=new SpriteSheet(ImageLoader.loadImage("/textures/tankGreen.png"));
        tankGreen[0]=tank.crop(0,0,32,32); //up
        tankGreen[1]=tank.crop(32,0,32,32); //down
        tankGreen[2]=tank.crop(0,32,32,32);//left
        tankGreen[3]=tank.crop(32,32,32,32);//right

        tank=new SpriteSheet(ImageLoader.loadImage("/textures/tankGrey.png"));
        tankGrey[0]=tank.crop(0,0,32,32); //up
        tankGrey[1]=tank.crop(32,0,32,32); //down
        tankGrey[2]=tank.crop(0,32,32,32);//left
        tankGrey[3]=tank.crop(32,32,32,32);//right

        player1=new SpriteSheet(ImageLoader.loadImage("/textures/player1.png")).crop(0,0,32,32);
        player2=new SpriteSheet(ImageLoader.loadImage("/textures/player2.png")).crop(0,0,32,32);
    }



}

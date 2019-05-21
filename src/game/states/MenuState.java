package game.states;

import game.assets.Assets;
import game.engine.Handler;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MenuState extends State {

    private Handler handler;
    private int option;

    public MenuState(Handler handler){
        this.handler=handler;
        option=0;
    }

    @Override
    public void update() {
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_UP)&&option!=0)
            option--;
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_DOWN)&&option!=2)
            option++;
        if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER))
            switch(option){
                case 0:handler.getGame().startNewGame();break;
                case 1:handler.getGame().changeTankColor();break;
                case 2:handler.getGame().setRunning(false);break;
            }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.yellow);
        g.fillRect(20,20,800-40,600-40);

        if(option==0)
            g.drawImage(Assets.starth,50,50,null);
        else
            g.drawImage(Assets.start,50,50,null);
        if(option==1)
            g.drawImage(Assets.colorh,50,180,null);
        else
            g.drawImage(Assets.color,50,180,null);
        if(option==2)
            g.drawImage(Assets.exith,50,310,null);
        else
            g.drawImage(Assets.exit,50,310,null);


        int tmpTankX=350,tmpTankY=180;
        switch(handler.getGame().tankColor){
            case 0:g.drawImage(Assets.AIAI,tmpTankX,tmpTankY,256,128,null);break;
            case 1:g.drawImage(Assets.czlowiekAI,tmpTankX,tmpTankY,256,128,null);break;
            case 2:g.drawImage(Assets.AIczlowiek,tmpTankX,tmpTankY,256,128,null);break;
            case 3:g.drawImage(Assets.czlowiekczlowiek,tmpTankX,tmpTankY,256,128,null);break;
        }



    }
}

package it.uniba.dib.sms22238.game.views.LViews;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import it.uniba.dib.sms22238.R;

public class Character {
    private Bitmap stopAnimation;
    private Bitmap moveAnimation1;
    private Bitmap moveAnimation2;
    boolean isRight=true;
    boolean wasMirroredLeft=false;
    boolean wasMirroredRight=true;
    boolean isPressed=false;
    boolean isJumping=false;
    Bitmap alienStop;
    Bitmap alienAnimation1;
    Bitmap alienAnimation2;
    int counter=1;
    int charSpeed=0;
    int viewIdentificator;
    float density;
    private int screenX, screenY;
    private float screenRatioX, screenRatioY;
    int y;
    boolean isPoweringUp=false;
    private Resources res;


    public Character(int viewIdentificator, Resources res, int screenX, int screenY , float screenRatioX, float screenRatioY)
    {
        this.viewIdentificator = viewIdentificator;
        setAnimation(viewIdentificator, res);
        this.screenX = screenX;
        this.screenY = screenY;
        this.screenRatioX = screenRatioX;
        this.screenRatioY = screenRatioY;
        this.res=res;
       // setBitmapResolution(res, viewIdentificator);

        //y=screenY-floorHeight-this.stopAnimation.getHeight()+1;

    }

    public void setPoweringUp(){
        if(viewIdentificator==0){
            this.stopAnimation=BitmapFactory.decodeResource(res, R.drawable.astronaut_powerup1);
            this.stopAnimation=Bitmap.createScaledBitmap(stopAnimation,stopAnimation.getWidth()/7,stopAnimation.getHeight()/7,false);
            this.moveAnimation1 = BitmapFactory.decodeResource(res, R.drawable.astronaut_powerup2);
            this.moveAnimation2 = BitmapFactory.decodeResource(res, R.drawable.astronaut_powerup3);

            this.moveAnimation1= Bitmap.createScaledBitmap(moveAnimation1,moveAnimation1.getWidth()/7, moveAnimation1.getHeight()/7,false);
            this.moveAnimation2 =Bitmap.createScaledBitmap(moveAnimation2,moveAnimation2.getWidth()/7, moveAnimation2.getHeight()/7,false);
            if(!isRight){ //se sta andando a sinistra gira subito la bitmap
                this.stopAnimation=Bitmap.createScaledBitmap(stopAnimation,-stopAnimation.getWidth(),stopAnimation.getHeight(),false);
                this.moveAnimation1=Bitmap.createScaledBitmap(moveAnimation1,-moveAnimation1.getWidth(),moveAnimation1.getHeight(),false);
                this.moveAnimation2=Bitmap.createScaledBitmap(moveAnimation2,-moveAnimation2.getWidth(),moveAnimation2.getHeight(),false);
            }
        }
        else if(viewIdentificator==1){
            this.stopAnimation=BitmapFactory.decodeResource(res,R.drawable.rover_powerup1);
            this.stopAnimation=Bitmap.createScaledBitmap(stopAnimation,stopAnimation.getWidth()/7,stopAnimation.getHeight()/7,false);
            this.moveAnimation1 = BitmapFactory.decodeResource(res, R.drawable.rover_powerup2);
            this.moveAnimation2 = BitmapFactory.decodeResource(res, R.drawable.rover_powerup1);

            this.moveAnimation1= Bitmap.createScaledBitmap(moveAnimation1,moveAnimation1.getWidth()/7, moveAnimation1.getHeight()/7,false);
            this.moveAnimation2 =Bitmap.createScaledBitmap(moveAnimation2,moveAnimation2.getWidth()/7, moveAnimation2.getHeight()/7,false);
            if(!isRight){ //se sta andando a sinistra gira subito la bitmap
                this.stopAnimation=Bitmap.createScaledBitmap(stopAnimation,-stopAnimation.getWidth(),stopAnimation.getHeight(),false);
                this.moveAnimation1=Bitmap.createScaledBitmap(moveAnimation1,-moveAnimation1.getWidth(),moveAnimation1.getHeight(),false);
                this.moveAnimation2=Bitmap.createScaledBitmap(moveAnimation2,-moveAnimation2.getWidth(),moveAnimation2.getHeight(),false);
            }
        }



    }
    public void resetPoweringUp(){
        setAnimation(viewIdentificator,res);
    }

    public Bitmap getStopAnimation()
    {
        return stopAnimation;
    }
    public Bitmap getMoveAnimation1()
    {
        return moveAnimation1;
    }
    public Bitmap getMoveAnimation2()
    {
        return moveAnimation2;
    }

    private void setAnimation(int flag, Resources res) {

        if(flag == 2) // museo , metodo di prova
        {
          this.stopAnimation = BitmapFactory.decodeResource(res, R.drawable.hirooki_fermo);
          this.moveAnimation1 = BitmapFactory.decodeResource(res, R.drawable.hirooki1);
          this.moveAnimation2 = BitmapFactory.decodeResource(res, R.drawable.hirooki2);

          this.stopAnimation = Bitmap.createScaledBitmap(stopAnimation,stopAnimation.getWidth()/7, stopAnimation.getHeight()/7,false);
          this.moveAnimation1= Bitmap.createScaledBitmap(moveAnimation1,moveAnimation1.getWidth()/7, moveAnimation1.getHeight()/7,false);
          this.moveAnimation2 =Bitmap.createScaledBitmap(moveAnimation2,moveAnimation2.getWidth()/7, moveAnimation2.getHeight()/7,false);
        }
        else if(flag==0){ //personaggio luna
            this.stopAnimation = BitmapFactory.decodeResource(res, R.drawable.astronaut);
            this.moveAnimation1 = BitmapFactory.decodeResource(res, R.drawable.astronaut1);
            this.moveAnimation2 = BitmapFactory.decodeResource(res, R.drawable.astronaut2);

            this.stopAnimation = Bitmap.createScaledBitmap(stopAnimation,stopAnimation.getWidth()/7, stopAnimation.getHeight()/7,false);
            this.moveAnimation1= Bitmap.createScaledBitmap(moveAnimation1,moveAnimation1.getWidth()/7, moveAnimation1.getHeight()/7,false);
            this.moveAnimation2 =Bitmap.createScaledBitmap(moveAnimation2,moveAnimation2.getWidth()/7, moveAnimation2.getHeight()/7,false);

        }
        else if(flag==1){ //personaggio marte
            this.stopAnimation = BitmapFactory.decodeResource(res, R.drawable.rover1);
            this.moveAnimation1 = BitmapFactory.decodeResource(res, R.drawable.rover2);
            this.moveAnimation2 = BitmapFactory.decodeResource(res, R.drawable.rover1);

            this.stopAnimation = Bitmap.createScaledBitmap(stopAnimation,stopAnimation.getWidth()/7, stopAnimation.getHeight()/7,false);
            this.moveAnimation1= Bitmap.createScaledBitmap(moveAnimation1,moveAnimation1.getWidth()/7, moveAnimation1.getHeight()/7,false);
            this.moveAnimation2 =Bitmap.createScaledBitmap(moveAnimation2,moveAnimation2.getWidth()/7, moveAnimation2.getHeight()/7,false);

        }

        else if(flag==4){ //è un alieno
            this.stopAnimation=BitmapFactory.decodeResource(res,R.drawable.alien1);
            this.moveAnimation1=BitmapFactory.decodeResource(res,R.drawable.alien2);
            this.moveAnimation2=BitmapFactory.decodeResource(res,R.drawable.alien3);

            this.stopAnimation = Bitmap.createScaledBitmap(stopAnimation,stopAnimation.getWidth()/8, stopAnimation.getHeight()/8,false);
            this.moveAnimation1= Bitmap.createScaledBitmap(moveAnimation1,moveAnimation1.getWidth()/8, moveAnimation1.getHeight()/8,false);
            this.moveAnimation2 =Bitmap.createScaledBitmap(moveAnimation2,moveAnimation2.getWidth()/8, moveAnimation2.getHeight()/8,false);
        }


    }


    public void setIsRight(boolean isRight)
    {
        this.isRight = isRight;
    }
    public void setIsPressed(boolean isPressed)
    {
        this.isPressed = isPressed;
    }
    public void setWasMirroredLeft(boolean wasMirroredLeft)
    {
        this.wasMirroredLeft = wasMirroredLeft;
    }
    public void setWasMirroredRight(boolean wasMirroredRight)
    {
        this.wasMirroredRight = wasMirroredRight;
    }
    public void setIsJumping(boolean isJumping)
    {
        this.isJumping = isJumping;
    }



    public Bitmap charOrientation(){
        if(isRight&&wasMirroredLeft){ //se l'utente sta premendo per spostarsi verso destra e la bitmap è flippata restituisce la bitmap normale
            stopAnimation=Bitmap.createScaledBitmap(stopAnimation,-(stopAnimation.getWidth()),stopAnimation.getHeight(),false);
            moveAnimation1=Bitmap.createScaledBitmap(moveAnimation1,-(moveAnimation1.getWidth()),moveAnimation1.getHeight(),false);
            moveAnimation2=Bitmap.createScaledBitmap(moveAnimation2,-(moveAnimation2.getWidth()),moveAnimation2.getHeight(),false);
            wasMirroredRight=true;
            wasMirroredLeft=false;
        }
        else if((!isRight)&&wasMirroredRight){  //altrimenti l'utente sta premendo per spostarsi verso sinistra e la bitmap è normale la restituisce flippata
            stopAnimation=Bitmap.createScaledBitmap(stopAnimation,-(stopAnimation.getWidth()),stopAnimation.getHeight(),false);
            moveAnimation1=Bitmap.createScaledBitmap(moveAnimation1,-(moveAnimation1.getWidth()),moveAnimation1.getHeight(),false);
            moveAnimation2=Bitmap.createScaledBitmap(moveAnimation2,-(moveAnimation2.getWidth()),moveAnimation2.getHeight(),false);
            wasMirroredLeft=true;
            wasMirroredRight=false;
        }
        if(isPressed){
            if(counter==1){
                counter++;
                return moveAnimation1;
            }
            if(counter==2){
                counter++;
                return moveAnimation2;
            }
            counter--;
        }
        return stopAnimation;
    }

    public float charMovement()
    {
        if(isPressed){  //se il background è arrivato alla fine
            if(viewIdentificator==4){
                if(isRight&&(charSpeed<screenX-stopAnimation.getWidth()-5)){ //se l'utente sta premendo verso destra //e se x è minore di questo punto sull'asse delle x
                    charSpeed+=  (25); //aumenta ancora la x finchè il personaggio non arriva alla fine dello schermo quindi isEnd diventa false
                }
                else if((!isRight)&&(charSpeed>5)){ //se l'utente sta premendo verso sinistra
                    charSpeed-=  (20); //diminuisce ancora la x finchè il personaggio non arriva alla fine dello schermo quindi isEnd diventa false
                }
            }
            else{
                if(isRight&&(charSpeed<screenX-stopAnimation.getWidth()-5)){ //se l'utente sta premendo verso destra //e se x è minore di questo punto sull'asse delle x
                    charSpeed+=  (15); //aumenta ancora la x finchè il personaggio non arriva alla fine dello schermo quindi isEnd diventa false
                }
                else if((!isRight)&&(charSpeed>5)){ //se l'utente sta premendo verso sinistra
                    charSpeed-=  (15); //diminuisce ancora la x finchè il personaggio non arriva alla fine dello schermo quindi isEnd diventa false
                }
            }
        }
        return charSpeed;

    }

    public float alienMovement(){
        charSpeed-= (int) (25);
        return charSpeed;
    }


    Rect collisionShape(){
        return  new Rect(charSpeed,y,charSpeed+charOrientation().getWidth(),y+charOrientation().getHeight());
    }

}

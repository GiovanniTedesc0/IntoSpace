package it.uniba.dib.sms22238.game.views;

import static it.uniba.dib.sms22238.game.views.LViews.ViewMuseum.screenRatioX;
import static it.uniba.dib.sms22238.game.views.LViews.ViewMuseum.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import it.uniba.dib.sms22238.R;

public class Bullet {
    int x,y,width,height;
    Bitmap bullet;
    Bullet(Resources res){
        bullet= BitmapFactory.decodeResource(res, R.drawable.bullet);

        width=bullet.getWidth();
        height=bullet.getHeight();

        width /=5;
        height /=5;

        width *=screenRatioX;
        height *=screenRatioY;

        bullet=Bitmap.createScaledBitmap(bullet,width,height,false); //per ridimensionare il proiettile

    }

    Rect getCollisionShape(){  //definisco i margini di collisione dell'immagine
        return new Rect(x,y,x+width,y+height);
    }
}

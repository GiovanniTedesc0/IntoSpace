package it.uniba.dib.sms22238.game.views.LViews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import it.uniba.dib.sms22238.GameActivityLandscape;
import it.uniba.dib.sms22238.HallActivity;
import it.uniba.dib.sms22238.R;


public class ViewMuseum extends SurfaceView implements Runnable {

    private Thread thread;
    private Bitmap museum_background = BitmapFactory.decodeResource(getResources(), R.drawable.background_museo);
    private Bitmap floor;
    private Bitmap pause,apollo11,roverMars,buttonLeft,buttonRight,turnIcon,history;
    private Bitmap ranking;
    public int x=0;
    public int screenX, screenY;
    int distance;
    private HallActivity hallactivity;
    public static float screenRatioX, screenRatioY;
    public boolean isPlaying;
    private Paint paint;
    boolean isSwitchingToMoon,isSwitchingToMars;
    private Bitmap interfaceBackground;
    private SharedPreferences prefs;
    private Bitmap gem;
    private long gemTot;
    private Bitmap login;
    Rect rectGround;
    Character hiroki;
    FirebaseFirestore db;
    private String email;
    Toast toast;
    private boolean showHistory=false;
    private int xApollo,xRover;
    private SoundPool soundPool;
    private int soundTeche;
    private boolean isCollidingApollo=false,isCollidingRover=false;


    public ViewMuseum(HallActivity activity, int screenX, int screenY) {
        super(activity);
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        prefs=activity.getSharedPreferences("game",activity.MODE_PRIVATE);


        //prefs.edit().clear().commit();  //per pulire il prefs

        int tempGems = prefs.getInt("tempGems", 0);
        int gameCounter = prefs.getInt("gameCounter", 0);
        int xPosition = prefs.getInt("xPosition", 0);
        int flagLevel = prefs.getInt("flagLevel", -1);
        email = prefs.getString("email", "").toString();

        this.hallactivity=activity;
        this.screenX=screenX;
        this.screenY=screenY;
        screenRatioX=1920F/screenX;
        screenRatioY=1080F/screenY;
        paint=new Paint();
        hiroki = new Character(2,getResources(),screenX,screenY,screenRatioX,screenRatioY);



        if( prefs.getString("email", "") != "")
        {
            if(connectivityManager.getActiveNetwork() != null )
            {

                db = FirebaseFirestore.getInstance();
                Map<String, Object> appoggio = (Map<String, Object>) prefs.getAll();
                appoggio.remove("email");
                appoggio.remove("xPosition");
                appoggio.remove("tempGems");
                appoggio.remove("gameCounter");
                appoggio.remove("flagLevel");
                appoggio.remove("isMute");

                //per l'account admin imposta le gemme a 50
                if(prefs.getString("email", "").equals("Admin@gmail.com"))
                {
                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putLong("moonGem",50);
                    editor.putLong("marsGem",50);
                    editor.commit();
                }

                db.collection("gems").document(prefs.getString("email", "")).set(appoggio).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                         toast = Toast.makeText(getContext(), R.string.Failed_update, Toast.LENGTH_SHORT);
                         toast.show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        toast = Toast.makeText(getContext(), R.string.Account_succ, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });



            }else
            {
                toast = Toast.makeText(getContext(), R.string.Off, Toast.LENGTH_SHORT);
                toast.show();

            }


        }else
        {
            toast = Toast.makeText(getContext(), R.string.Guest, Toast.LENGTH_SHORT);
            toast.show();
            SharedPreferences.Editor editor=prefs.edit();
            editor.putInt("tempGems",0);
            editor.putInt("xPosition",0);
            editor.putInt("gameCounter",0);
            editor.putInt("flagLevel",-1);
            editor.commit();
        }

        gemTot = prefs.getLong("moonGem", 0) + prefs.getLong("marsGem", 0);

        museum_background = Bitmap.createScaledBitmap(museum_background,screenX,screenY,false); //lo screen x deve essere uguale allo schermo

        isSwitchingToMoon=false;
        isSwitchingToMars=false;

        floor= BitmapFactory.decodeResource(getResources(), R.drawable.pavimento_museo);
        floor=Bitmap.createScaledBitmap(floor,screenX+2,floor.getHeight(),false);

        pause=BitmapFactory.decodeResource(getResources(),R.drawable.pause);
        pause=Bitmap.createScaledBitmap(pause,pause.getWidth()/30,pause.getHeight()/30,false);

        login=BitmapFactory.decodeResource(getResources(),R.drawable.login);
        login=Bitmap.createScaledBitmap(login,login.getWidth()/30,login.getHeight()/30,false);

        apollo11=BitmapFactory.decodeResource(getResources(),R.drawable.apollo11);
        roverMars=BitmapFactory.decodeResource(getResources(),R.drawable.teca_rover);
        apollo11=Bitmap.createScaledBitmap(apollo11,apollo11.getWidth()/2,apollo11.getHeight()/2,false);
        roverMars=Bitmap.createScaledBitmap(roverMars,roverMars.getWidth()/2,roverMars.getHeight()/2,false);

        interfaceBackground=BitmapFactory.decodeResource(getResources(),R.drawable.history_background);
        interfaceBackground=Bitmap.createScaledBitmap(interfaceBackground,-screenX,-screenY,false);

        gem=BitmapFactory.decodeResource(getResources(),R.drawable.gem);
        gem=Bitmap.createScaledBitmap(gem,gem.getWidth()/2,gem.getHeight()/2,false);

        ranking = BitmapFactory.decodeResource(getResources(), R.drawable.ranking);
        ranking = Bitmap.createScaledBitmap(ranking,ranking.getWidth()/30,ranking.getHeight()/30,false);

        turnIcon= BitmapFactory.decodeResource(getResources(),R.drawable.turn_image);
        turnIcon = Bitmap.createScaledBitmap(turnIcon,turnIcon.getWidth()/15,turnIcon.getHeight()/15,false);

        history = BitmapFactory.decodeResource(getResources(),R.drawable.history);
        history = Bitmap.createScaledBitmap(history,history.getWidth()/2,history.getHeight()/2,false);

        buttonLeft=BitmapFactory.decodeResource(getResources(),R.drawable.left_button);
        buttonLeft=Bitmap.createScaledBitmap(buttonLeft,buttonLeft.getWidth()/30,buttonLeft.getHeight()/30,false);

        buttonRight=BitmapFactory.decodeResource(getResources(),R.drawable.right_button);
        buttonRight=Bitmap.createScaledBitmap(buttonRight,buttonRight.getWidth()/30,buttonRight.getHeight()/30,false);


        rectGround = new Rect(0,screenX-floor.getHeight(),screenX, screenY);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            AudioAttributes audioAttributes= new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool=new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes).setMaxStreams(7)
                    .build();

        }
        else {
            soundPool =new SoundPool(7, AudioManager.STREAM_MUSIC,0);
        }
        soundTeche=soundPool.load(activity,R.raw.teche,1);

    }

    Rect collisionShapeApollo11(){
        return new Rect(xApollo,0,xApollo+apollo11.getWidth(),apollo11.getHeight());
    }

    Rect collisionShapeRover(){
        return new Rect(xRover,0,xRover+roverMars.getWidth(),roverMars.getHeight());
    }

    private void update(){
        if(Rect.intersects(hiroki.collisionShape(),collisionShapeApollo11())){
            apollo11=BitmapFactory.decodeResource(getResources(),R.drawable.apollo11_shade);
            apollo11=Bitmap.createScaledBitmap(apollo11,apollo11.getWidth()/2,apollo11.getHeight()/2,false);
            if(!prefs.getBoolean("isMute",false)&&!isCollidingApollo)
                soundPool.play(soundTeche,1,1,0,0,1); //riproduce il suono di collisione con le teche
            isCollidingApollo=true;
        }
        else{
            isCollidingApollo=false;
            apollo11=BitmapFactory.decodeResource(getResources(),R.drawable.apollo11);
            apollo11=Bitmap.createScaledBitmap(apollo11,apollo11.getWidth()/2,apollo11.getHeight()/2,false);
        }

        if(Rect.intersects(hiroki.collisionShape(),collisionShapeRover())){
            roverMars=BitmapFactory.decodeResource(getResources(),R.drawable.rover_shade);
            roverMars=Bitmap.createScaledBitmap(roverMars,roverMars.getWidth()/2,roverMars.getHeight()/2,false);
            if(!prefs.getBoolean("isMute",false)&&!isCollidingRover)
                soundPool.play(soundTeche,1,1,0,0,1); //riproduce il suono di collisione con le teche
            isCollidingRover=true;

        }else{
            isCollidingRover=false;
            roverMars=BitmapFactory.decodeResource(getResources(),R.drawable.teca_rover);
            roverMars=Bitmap.createScaledBitmap(roverMars,roverMars.getWidth()/2,roverMars.getHeight()/2,false);
        }
    }

    protected void draw(){
        if(getHolder().getSurface().isValid()){  //se il nostro oggetto surface è stato correttamente istanziato
            Canvas canvas=getHolder().lockCanvas(); //blocca il canva per poterlo usare
            canvas.drawBitmap(museum_background,0,0,paint);
            canvas.drawBitmap(roverMars,canvas.getWidth()/2+300,screenY-floor.getHeight()-roverMars.getHeight()+5,paint);
            canvas.drawBitmap(floor,0,screenY-floor.getHeight(),paint);
            canvas.drawBitmap(apollo11,canvas.getWidth()/2-300-apollo11.getWidth(),0,paint);
            xRover=canvas.getWidth()/2+300;
            distance = canvas.getWidth();
            canvas.drawBitmap(hiroki.charOrientation(),hiroki.charMovement(),screenY-floor.getHeight()-hiroki.getStopAnimation().getHeight(),paint); //screenY-floor.getHeight()-character.getHeight()
            //canvas.drawBitmap(ranking,screenX-ranking.getWidth()-10*screenRatioX,10*screenRatioY,paint);
            canvas.drawBitmap(ranking,canvas.getWidth()/2-ranking.getWidth()/2,10*screenRatioX,paint);
            canvas.drawBitmap(history,(screenX-pause.getWidth()-10*screenRatioX+canvas.getWidth()/2+ranking.getWidth()/2)/2-history.getWidth()/2,0,paint);
            canvas.drawBitmap(pause,screenX-pause.getWidth()-10*screenRatioX,10*screenRatioX,paint);
            canvas.drawBitmap(login,10*screenRatioX,screenY-10*screenRatioY-login.getHeight(),paint);
            canvas.drawBitmap(gem,10*screenRatioX,10*screenRatioY,paint);
            Paint text=new Paint();
            text.setColor(Color.WHITE);
            text.setTextSize(gem.getHeight());
            canvas.drawText(gemTot+"",10*screenRatioX+gem.getWidth()+10,gem.getHeight()+5*screenRatioX,text);
            if(isSwitchingToMoon){  //se sta passando all'activity portrait chiama il fragment con la storia
                short c=0;
                canvas.drawBitmap(interfaceBackground,0,0,paint);
                TextPaint textPaint = new TextPaint();
                //textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTypeface(Typeface.create("sans-serif-condensed-medium",Typeface.NORMAL));
                canvas.drawBitmap(turnIcon,10*screenRatioX,10*screenRatioX,paint);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
                String message= hallactivity.getString(R.string.MoonStory);
                StaticLayout.Builder builder=StaticLayout.Builder.obtain(message,0,message.length(), textPaint,700)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL);

                StaticLayout staticLayout=builder.build();


                int xPos = (canvas.getWidth() / 2) - (staticLayout.getWidth()/2);
                int yPos = (int) ((canvas.getHeight() / 2) - (staticLayout.getHeight()/2));
                canvas.save();
                canvas.translate(xPos, yPos);
                staticLayout.draw(canvas);
                canvas.restore();
                getHolder().unlockCanvasAndPost(canvas);  //dopo aver disegnato le bitmap sblocca il canvas
                sleep(7000);  //imposta questo tempo per far comparire l'interfaccia con la storia e poi passare all'activity per il viaggio
                hallactivity.callTravel(c); //passa 0 come flag perché sta andando verso la luna
                isSwitchingToMoon= false;
                return;
            }
            if(isSwitchingToMars){  //se sta passando all'activity portrait disegna la storia per il viaggio verso marte
                short c=1;
                canvas.drawBitmap(interfaceBackground,0,0,paint);
                TextPaint textPaint = new TextPaint();

                canvas.drawBitmap(turnIcon,10*screenRatioX,10*screenRatioX,paint);
                textPaint.setTypeface(Typeface.create("sans-serif-condensed-medium",Typeface.NORMAL));
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
                String message=hallactivity.getString(R.string.MarsStory);;
                StaticLayout.Builder builder=StaticLayout.Builder.obtain(message,0,message.length(), textPaint,700)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL);

                StaticLayout staticLayout=builder.build();

                int xPos = (canvas.getWidth() / 2) - (staticLayout.getWidth()/2);
                int yPos = (int) ((canvas.getHeight() / 2) - (staticLayout.getHeight()/2));
                canvas.save();
                canvas.translate(xPos, yPos);
                staticLayout.draw(canvas);
                canvas.restore();
                getHolder().unlockCanvasAndPost(canvas);  //dopo aver disegnato le bitmap sblocca il canvas
                sleep(7000);  //imposta questo tempo per far comparire l'interfaccia con la storia e poi passare all'activity per il viaggio
                hallactivity.callTravel(c); //passa 0 come flag perché sta andando verso la luna
                isSwitchingToMars=false;
                return;
            }
            if(showHistory){
                canvas.drawBitmap(interfaceBackground,0,0,paint);
                TextPaint textPaint = new TextPaint();
                textPaint.setTypeface(Typeface.create("sans-serif-condensed-medium",Typeface.NORMAL));
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
                String message=hallactivity.getString(R.string.Startstory);;
                StaticLayout.Builder builder=StaticLayout.Builder.obtain(message,0,message.length(), textPaint,700)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL);

                StaticLayout staticLayout=builder.build();

                int xPos = (canvas.getWidth() / 2) - (staticLayout.getWidth()/2);
                int yPos = (int) ((canvas.getHeight() / 2) - (staticLayout.getHeight()/2));
                canvas.save();
                canvas.translate(xPos, yPos);
                staticLayout.draw(canvas);
                canvas.restore();
                getHolder().unlockCanvasAndPost(canvas);  //dopo aver disegnato le bitmap sblocca il canvas
                sleep(5000);  //imposta questo tempo per far comparire l'interfaccia con la storia e poi passare all'activity per il viaggio
                showHistory=false;
                getHolder().lockCanvas();
            }
            canvas.drawBitmap(buttonLeft,screenX-10*screenRatioX-buttonLeft.getWidth(),screenY-10*screenRatioX-buttonLeft.getHeight(),paint);
            canvas.drawBitmap(buttonRight,screenX-buttonRight.getWidth()-buttonRight.getWidth()-20*screenRatioX,screenY-10*screenRatioX-buttonRight.getHeight(),paint);
            getHolder().unlockCanvasAndPost(canvas);  //dopo aver disegnato le bitmap sblocca il canvas
        }
    }

    @Override
    public void run() {
        while (isPlaying){
            update();
            draw();
            sleep(36); //setta la pausa del thread ogni 36 milli secondi così che il gioco runni a 36fps
        }
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            hallactivity.finish();
            e.printStackTrace();
        }

    }

    public void resume(){  //quando il gioco riprende viene richiamato

        isPlaying=true;
        thread=new Thread(this);
        thread.start();  //il run del thread

    }

    public void pause(){

        isPlaying=false;
        try{
            thread.join();
        }catch (InterruptedException e){
            hallactivity.finish();
            e.printStackTrace();
        }

    }

    public void checkLevelStatus( short flag){

        if(prefs.getInt("xPosition",0) != 0)// o landscape
        {

            Bundle bundle = new Bundle(1);
            bundle.putShort("flagPlanet", flag); //setta il flag nel boundle per chiamare il pianeta
            Intent i = new Intent(getContext(), GameActivityLandscape.class);
            i.putExtras(bundle);
            getContext().startActivity(i);

        }else if(prefs.getInt("gameCounter", 0) != 0)// controllo se si sta riferendo al portrait
        {
            hallactivity.callTravel(flag); //passa il flag per chiamare il viaggo

        }else if(prefs.getInt("flagLevel",-1) == 0&&prefs.getInt("xPosition",0) != 0){
            Bundle bundle = new Bundle(1);
            bundle.putShort("flagPlanet", flag); //setta il flag nel boundle per chiamare il pianeta
            Intent i = new Intent(getContext(), GameActivityLandscape.class);
            i.putExtras(bundle);
            getContext().startActivity(i);

        }else if(prefs.getInt("flagLevel",-1) == 1&&prefs.getInt("xPosition",0) != 0){
            Bundle bundle = new Bundle(1);
            bundle.putShort("flagPlanet", flag); //setta il flag nel boundle per chiamare il pianeta
            Intent i = new Intent(getContext(), GameActivityLandscape.class);
            i.putExtras(bundle);
            getContext().startActivity(i);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX()>10*screenRatioX&&event.getX()<10*screenRatioX+login.getWidth()&&event.getY()<screenY-10*screenRatioX&&event.getY()>screenY-10*screenRatioY-login.getHeight()){
                    hallactivity.callLogin();
                    break;
                }
                if(event.getX()>screenX-pause.getWidth()-10*screenRatioX&&event.getY()<10*screenRatioX+pause.getHeight()){
                    //se il tocco avviene nel quadrato in cui si trova il bottone di pausa
                    short flag=0,flagActivity=1;
                    hallactivity.callManager(flag,flagActivity);
                    break;
                }
                if((distance/2-ranking.getWidth()/2 < event.getX() && event.getX() < distance/2 + ranking.getWidth()/2)
                        &&
                     event.getY() < ranking.getHeight()+10*screenRatioX&&event.getY()>10*screenRatioX){
                    short flag=1,flagActivity=1;// flagActivity in questo caso e inutile
                    hallactivity.callManager(flag,flagActivity);
                    break;
                }

                if(event.getX()>distance/2-300-apollo11.getWidth()&&event.getX()<distance/2-300&&event.getY()>0&&event.getY()<apollo11.getHeight()){

                    if(prefs.getInt("flagLevel",-1) == 0) {
                        Toast.makeText(getContext(), R.string.Recap, Toast.LENGTH_SHORT).show();
                        checkLevelStatus((short) 0);
                    }else {
                        isSwitchingToMoon = true;
                    }
                    break;
                    //quando tocchi sulla teca dell'apollo 11 richiamare l'activity portrait
                }
                if((event.getX()>distance/2+300)&&(event.getX()<distance/2+300+roverMars.getWidth())&&event.getY()>screenY-floor.getHeight()-roverMars.getHeight()+5&&event.getY()<screenY-floor.getHeight()+5)
                {
                    if( gemTot>= 50)
                    {
                        if(prefs.getInt("flagLevel",-1) == 1) {
                            Toast.makeText(getContext(), R.string.Recap, Toast.LENGTH_SHORT).show();
                            checkLevelStatus((short) 1);
                        }else{
                            isSwitchingToMars=true;
                        }

                    }else
                    {

                        toast = Toast.makeText(getContext(), R.string.Gemmcap, Toast.LENGTH_SHORT);
                        toast.show();

                    }
                    break;
                }
                if(event.getX()>(screenX-pause.getWidth()-10*screenRatioX+distance/2+ranking.getWidth()/2)/2-history.getWidth()/2
                        &&event.getX()<(screenX-pause.getWidth()-10*screenRatioX+distance/2+ranking.getWidth()/2)/2+history.getWidth()/2
                        &&event.getY()>0&&event.getY()<history.getHeight()){
                    showHistory=true;
                }
                break;
            case MotionEvent.AXIS_PRESSURE: //se l'utente sta tenendo premuto lo schermo
                if(event.getX()>screenX-buttonLeft.getWidth()-buttonLeft.getWidth()-20*screenRatioX
                        &&event.getX()<screenX-20*screenRatioX-buttonLeft.getWidth()-10*screenRatioX
                        &&event.getY()>screenY-10*screenRatioX-buttonLeft.getHeight()
                        &&event.getY()<screenY-10*screenRatioX) {
                    hiroki.setIsPressed(true);
                    hiroki.setIsRight(false);
                    return true;
                }
                else if(event.getX()>screenX-10*screenRatioX-buttonRight.getWidth()
                        &&event.getX()<screenX-10*screenRatioX&&event.getY()>screenY-10*screenRatioX-buttonRight.getHeight()
                        &&event.getY()<screenY-10*screenRatioX){//quando questo succede il tocco arriva dalla parte destra
                    hiroki.setIsPressed(true);
                    hiroki.setIsRight(true);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:     //se l'utente ha lasciato lo schermo
                hiroki.setIsPressed(false);
                break;

        }

        return true;
    }


}

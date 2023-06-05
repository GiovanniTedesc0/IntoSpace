package it.uniba.dib.sms22238.game.views;

import static it.uniba.dib.sms22238.game.views.LViews.ViewMuseum.screenRatioX;
import static it.uniba.dib.sms22238.game.views.LViews.ViewMuseum.screenRatioY;

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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import it.uniba.dib.sms22238.GameActivityPortrait;
import it.uniba.dib.sms22238.HallActivity;
import it.uniba.dib.sms22238.R;
import it.uniba.dib.sms22238.game.views.PViews.Background;
import it.uniba.dib.sms22238.game.views.PViews.Ship;

public class ViewTravel extends SurfaceView implements Runnable, SensorEventListener {

    private Thread thread;
    private boolean isPlaying=true;
    private GameActivityPortrait gameActivityPortrait;
    private Paint paint;
    private SensorManager sManager;
    public int screenX,screenY;
    private Sensor accelerometer;
    private boolean isPressed=false;
    public Random random;
    public List<Bullet> bullets;
    public List<Enemy> enemies;
    boolean isGameOver;
    private Bitmap pause,turnIcon;
    public static int gameCounter;
    public static int gemCounter;
    private Paint text;
    private int enemyCounter;
    public Background background_1,background_2;
    public Ship ship;
    private  SharedPreferences prefs = getContext().getSharedPreferences("game",getContext().MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    short flagPlanet;
    private SoundPool soundPool;
    private int soundShoot,soundExplosion,soundGem,soundPowerup,soundDeath;
    private boolean isPlayingSong=false;


    public ViewTravel(GameActivityPortrait activity, short flag,int screenY,int screenX) {

        super(activity);
        this.flagPlanet =flag;
        this.gameActivityPortrait=activity;
        this.screenX=screenX;
        this.screenY=screenY;

        gameCounter = prefs.getInt("gameCounter", 0);
        gemCounter = prefs.getInt("tempGems", 0);


        paint=new Paint();
        text=new Paint();
        text.setColor(Color.WHITE);
        text.setTextSize(50);
        text.setTextAlign(Paint.Align.CENTER);

        background_1=new Background(getResources());
        background_2=new Background(getResources());

        pause=BitmapFactory.decodeResource(getResources(),R.drawable.pause);
        pause=Bitmap.createScaledBitmap(pause,pause.getWidth()/30,pause.getHeight()/30,false);

        turnIcon= BitmapFactory.decodeResource(getResources(),R.drawable.turn_image);
        turnIcon = Bitmap.createScaledBitmap(turnIcon,-turnIcon.getWidth()/15,turnIcon.getHeight()/15,false);

        background_2.y=-screenY;  //inizializza il secondo background sopra il primo

        ship=new Ship(getResources(),screenX,screenY,flagPlanet);

        bullets=new ArrayList<>();

        enemies=new ArrayList<>();

        random=new Random();

        // imposta accelerometro per input
        sManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
        soundShoot=soundPool.load(activity,R.raw.shoot,1);
        soundExplosion=soundPool.load(activity,R.raw.explosion,1);
        soundGem=soundPool.load(activity,R.raw.gem,1);
        soundPowerup=soundPool.load(activity,R.raw.powerup,1);
        soundDeath=soundPool.load(activity,R.raw.death,1);

    }

    @Override
    public void run() {
            while (isPlaying){   //il gioco si ferma all'incirca dopo 1 minuto e mezzo di gioco
                update();
                draw();
                sleep(36);
                if(isGameOver){
                    waitBeforeExiting();
                    return;
                }
                gameCounter++;
            }
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*questa funzione genera randomicamente la posizione di un asteroide sullo schermo*/

    public void generateEnemies(){
        enemyCounter=random.nextInt(4)+1; //sceglie casualmente quanti nemici aggiungere allo schermo

        for(int i=0; i<enemyCounter;i++){
            Enemy enemy=new Enemy(getResources(),screenX);
            enemy.getEnemy(); //genera casualmente un nemico per ogni elemento dell'array
            enemy.y=0;
            enemy.x=random.nextInt(screenX-enemy.width); //l'asse delle x viene scelto randomicamente
            enemies.add(enemy);
        }

    }

    /*questa funzione genera randomicamente la posizione di un powerup sullo schermo*/

    public void generatePowerup(){
        if(!ship.isPowerup){  //se non ci sono altri power up sullo schermo
            int i=random.nextInt(10)+1;
            if(i==5){
                ship.isPowerup=true; //se la random genera 5 in un range da 1 a 10 genera il powerup
                ship.yPowerup=0;
                ship.xPoweup=random.nextInt(screenX-ship.powerup.getWidth());
            }
        }
    }

    private void draw(){
        if(getHolder().getSurface().isValid()){
            Canvas canvas=getHolder().lockCanvas();
            canvas.drawBitmap(background_1.background,background_1.x,background_1.y,paint);
            canvas.drawBitmap(background_2.background,background_2.x,background_2.y,paint);
            if(ship.isPowerup){
                canvas.drawBitmap(ship.powerup,ship.xPoweup,ship.yPowerup,paint);
            }
            text.setColor(Color.WHITE);
            text.setTextSize(68);
            canvas.drawBitmap(pause,screenX-pause.getWidth()-10*screenRatioX,10*screenRatioX,paint);
            for(Enemy enemy:enemies){
                canvas.drawBitmap(enemy.enemy,enemy.x,enemy.y,paint);
                if(enemy.gemShot){
                    canvas.drawBitmap(enemy.gem,enemy.xGem,enemy.yGem,paint);
                }
                if(enemy.shot){
                    canvas.drawBitmap(enemy.getExplosion(),enemy.xExplosion,enemy.yExplosion,paint);
                }
            }

            canvas.drawBitmap(ship.getShip(),ship.xShip,ship.yShip,paint);
            for(Iterator<Bullet> iterator=bullets.iterator(); iterator.hasNext();){
                Bullet bullet=iterator.next();
                canvas.drawBitmap(bullet.bullet,bullet.x,bullet.y,paint);
            }
            canvas.drawText(""+gemCounter,canvas.getWidth()/2,50*screenRatioY,text);
            if(gameCounter>1000){  //se il gioco finisce salva i dati delle gemme


                Log.d("flag",prefs.getAll().toString());
                if(prefs.getString("email", "") != "")
                {
                 if(flagPlanet == 0) // luna
                 {
                     long appoggio = prefs.getLong("moonGem",0);
                     appoggio += gemCounter;
                     editor.putLong("moonGem", appoggio);
                     editor.commit();

                 }else if(flagPlanet == 1)// marte
                 {
                     long appoggio = prefs.getLong("marsGem",0);
                     appoggio += gemCounter;
                     editor.putLong("marsGem", appoggio);
                     editor.commit();
                 }

                }
                gemCounter = 0;
                gameCounter = 0;

                editor.putInt("tempGems",gemCounter);
                editor.putInt("gameCounter", gameCounter);
                editor.commit();

                Paint textPaint = new Paint();
                textPaint.setTextAlign(Paint.Align.CENTER);
                int xPos = (canvas.getWidth() / 2);
                int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(50);
                textPaint.setTypeface(Typeface.create("sans-serif-condensed-medium",Typeface.NORMAL));
                canvas.drawBitmap(turnIcon,canvas.getWidth()/2-turnIcon.getWidth()/2,yPos-turnIcon.getWidth(),paint);
                canvas.drawText(gameActivityPortrait.getString(R.string.A), xPos,yPos,textPaint);

                Log.d("flag",prefs.getAll().toString());
                getHolder().unlockCanvasAndPost(canvas);
                gameActivityPortrait.callPlanet(flagPlanet);
                return;

            }

            if(isGameOver){ //se hai perso
                // scrive nel DB del telefono
                isPlaying=false; //il gioco si blocca
                Paint textPaint = new Paint();
                textPaint.setTextAlign(Paint.Align.CENTER);
                int xPos = (canvas.getWidth() / 2);
                int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(100);
                textPaint.setTypeface(Typeface.create("sans-serif-condensed-medium",Typeface.NORMAL));
                canvas.drawText( gameActivityPortrait.getString(R.string.GO), xPos,yPos,textPaint);
                getHolder().unlockCanvasAndPost(canvas);

                return;
            }
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void waitBeforeExiting(){

        try {
            Thread.sleep(3000);

            gemCounter = 0;
            gameCounter = 0;
            flagPlanet = -1;

            editor.putInt("tempGems",gemCounter);
            editor.putInt("gameCounter", gameCounter);
            editor.putInt("flagLevel", flagPlanet);
            editor.commit();

            Intent i=new Intent(gameActivityPortrait, HallActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            gameActivityPortrait.startActivity(i);
            gameActivityPortrait.finish();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void update(){

        background_1.y+=10*screenRatioY;
        background_2.y+=10*screenRatioY;

        if(background_1.y>screenY){ //se il background è sceso fuori dallo schermo
            background_1.y=-screenY;
        }
        if(background_2.y>screenY){ //se il background è sceso fuori dallo schermo
            background_2.y=-screenY;
        }

        if(flagPlanet==0){ //sulla luna i nemici spuntano più lentamente
            if(gameCounter%300==0){
                generateEnemies();
                generatePowerup();
            }
        }
        else if(flagPlanet==1){ //su marte i nemici spuntano più velocemente
            if(gameCounter%200==0){
                generateEnemies();
                generatePowerup();
            }
        }

        List<Enemy> enemiesTrash=new ArrayList<>();

        if(!bullets.isEmpty()) {
            for (Iterator<Bullet> iterator=bullets.iterator(); iterator.hasNext();) {
                Bullet bullet=iterator.next();
                bullet.y -= 50 * screenRatioY;  //muovere il proiettile di 50 pixel

                if (!enemies.isEmpty()) {
                    for (Enemy enemy : enemies) {
                        if (Rect.intersects(enemy.getCollisionShape(), bullet.getCollisionShape())) { //se un proiettile colpisce l'asteroide
                            if(!prefs.getBoolean("isMute",false))
                                soundPool.play(soundExplosion,1,1,0,0,1); //riproduce il suono dell'esplosione
                            if (enemy.isGem) {
                                enemy.gemShot = true;
                                enemy.xGem = enemy.x + enemy.enemy.getWidth() / 2 - enemy.gem.getWidth() / 2;
                                enemy.yGem = enemy.y + enemy.enemy.getHeight() / 2 - enemy.gem.getHeight() / 2;
                            }
                            enemy.shot=true;
                            enemy.xExplosion=enemy.x;
                            enemy.yExplosion=enemy.y;
                            enemy.y -= screenY + 500; //l'asteroide si sposta di 500+screenY
                            bullet.y = -500;
                        }
                    }
                }

            }
        }

        for(Iterator<Bullet> iterator=bullets.iterator(); iterator.hasNext();){
            Bullet bullet=iterator.next();
            if(bullet.y<0){   //l'elemento viene rimosso se esce dallo schermo
                iterator.remove();
                if(bullets.isEmpty()&&isPressed){  //se l'array list rimane vuoto mentre si sta sparando ne aggiunge uno per evitare che venga disegnato un bullet vuoto
                    setBullet();
                }
            }
        }


        if(ship.isPowerup&&ship.yPowerup<=screenY){   //controllo per la velocità casuale del power up
            ship.yPowerup+=ship.speedPowerup;
            int bound=(int)(30*screenRatioY);
            ship.speedPowerup=random.nextInt(bound);
            if(ship.speedPowerup<10*screenRatioY){
                ship.speedPowerup=(int) (10*screenRatioY);
            }
            if(Rect.intersects(ship.getCollisionShapePowerUp(),ship.getCollisionShape())){  //se la navicella colpisce il powerup
                if(!prefs.getBoolean("isMute",false))
                    soundPool.play(soundPowerup,1,1,0,0,1); //riproduce il suono del powerup
                ship.isPoweringup=true; //cambia la bitmap della navicella
                ship.yPowerup+=screenY;
                ship.counterPouwerup=gameCounter; //inizializza il contatore del powerup
            }
        }

        if(ship.isPoweringup&&gameCounter>=ship.counterPouwerup+200){  //se il contatore di gioco incrementa di 833 cioè all'incirca 30 secondi
            ship.isPoweringup=false;  //termina il power up
        }

        else if(ship.isPowerup&&ship.yPowerup>=screenY){  //se il power up esce dallo schermo la variabile cambia
            ship.isPowerup=false;
        }

        if(!enemies.isEmpty()){
            for(Enemy enemy:enemies){ //per ogni asteroide
                if(flagPlanet==0){  //sulla luna il background scorre più lentamente
                    enemy.y+=enemy.speed; //assegna alla y una certa velocità

                }
                else if(flagPlanet==1){ //su marte il background scorre più lentamente
                    enemy.y+=enemy.speed+5; //assegna alla y una certa velocità che è più veloce su Marte
                }

                if(enemy.y<screenY){  //finchè l'asteroide rimane sullo schermo aumenta la sua posizione lungo l'asse delle y
                    int bound=(int) (30*screenRatioY);
                    enemy.speed=random.nextInt(bound);
                    if(enemy.speed<10*screenRatioY){
                        enemy.speed=(int)(10*screenRatioY);
                    }
                }
                else{
                    enemiesTrash.add(enemy);
                }
                if(Rect.intersects(enemy.getCollisionShape(),ship.getCollisionShape())){
                    if(ship.isPoweringup){  //se è il powerup non perdi
                        if(!prefs.getBoolean("isMute",false))
                            soundPool.play(soundExplosion,1,1,0,0,1); //riproduce il suono dell'esplosione
                        enemy.shot=true;
                        enemy.xExplosion=enemy.x;
                        enemy.yExplosion=enemy.y;
                        if(enemy.isGem){
                            enemy.gemShot=true;
                            enemy.xGem=enemy.x+enemy.enemy.getWidth()/2-enemy.gem.getWidth()/2;
                            enemy.yGem=enemy.y+enemy.enemy.getHeight()/2-enemy.gem.getHeight()/2;
                        }
                        enemy.y-=screenY+500; //l'asteroide si sposta di 500+screenY
                    }
                    else{
                        if(!prefs.getBoolean("isMute",false))
                            soundPool.play(soundDeath,1,1,0,0,1); //riproduce il suono di aver perso
                        isGameOver=true;
                        return;
                    }
                }
                if(enemy.isGem&&enemy.gemShot&&enemy.yGem<=screenY){ //se l'elemento dell'array list era un asteroide con gemma ed è stato colpito quindi c'è la gemma sullo schermo e non è ancora scomparsa
                    enemy.yGem+=enemy.speed;
                    if(Rect.intersects(enemy.getCollisionShapeGem(),ship.getCollisionShape())){
                        if(!prefs.getBoolean("isMute",false))
                            soundPool.play(soundGem,1,1,0,0,1); //riproduce il suono dell'acquisizione di una gemma
                        gemCounter++;
                        enemy.yGem+=screenY; //fa scomparire la gemma
                    }
                }
                else{  //quando la gemma scompare dallo schermo la variabile torna falsa così da non farla disegnare più
                    enemy.gemShot=false;
                }

            }
        }

        if(!enemiesTrash.isEmpty()){
            for(Enemy enemy:enemiesTrash){
                enemies.remove(enemy);
            }
        }
    }

    public void resume(){
        accelerometerReset();
        isPlaying=true;
        thread=new Thread(this);
        thread.start();
    }

    public void pause(){

        editor.putInt("tempGems",gemCounter);
        editor.putInt("gameCounter", gameCounter);
        editor.putInt("flagLevel", flagPlanet);
        editor.commit();

        accelerometerOut();
        try {

            isPlaying=false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){  //se l'input proviene dall'accellerometro
            ship.xShip=(int)(ship.xShip-sensorEvent.values[0]-sensorEvent.values[0]);
            if(ship.xShip+sensorEvent.values[0]+ship.getShip().getWidth()>screenX-10*screenRatioX){  //la navicella si sposta verso destra se rimane in questo range
                ship.xShip=(int)(screenX-10*screenRatioX-ship.getShip().getWidth());
            }
            else if(ship.xShip-sensorEvent.values[0]<0){ //la navicella si sposta verso sinistra se rimane in questo range
                ship.xShip=(int)(10*screenRatioX);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
    //stacca l'acelerometro, dopo che va in onPause
    public void accelerometerOut() {
        sManager.unregisterListener(this);
    }

    // ri-attacca l'acelerometro, dopo che va in onResume
    public void accelerometerReset() {
        sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getX()>screenX-pause.getWidth()-10*screenRatioX&&event.getY()<10*screenRatioX+pause.getHeight()){
                    //se il tocco avviene nel quadrato in cui si trova il bottone di pausa
                    short flag=0,flagActivity=0;
                    gameActivityPortrait.callManager(flag,flagActivity);
                }
                if(!prefs.getBoolean("isMute",false))
                    soundPool.play(soundShoot,1,1,0,0,1);
                isPressed=true;
                setBullet();
                break;
            case MotionEvent.ACTION_UP:
                isPressed=false;
                break;

            case MotionEvent.BUTTON_BACK:
                Intent i=new Intent(getContext(),HallActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
                gameActivityPortrait.finish();

        }
        return true;
    }

    /*questa funzione aggiunge un proiettile all'arraylist di proiettili*/

    public void setBullet()
    {

        Bullet bullet=new Bullet(getResources());
        bullet.x=ship.xShip+ship.shipShoot.getWidth()/2-bullet.bullet.getWidth()/2;
        bullet.y=screenY-ship.shipShoot.getHeight()-bullet.bullet.getHeight();
        bullets.add(bullet);

    }
}

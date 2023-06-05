package it.uniba.dib.sms22238;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import it.uniba.dib.sms22238.game.views.LViews.ViewMuseum;

public class HallActivity extends AppCompatActivity {

    private ViewMuseum viewMuseum;
    private short flag;
    private Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
        //imposta l'orientamento in orizzontale come obbligatorio

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //imposta lo schermo in full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getOnBackPressedDispatcher();

        //ottiene le dimensioni dello schermo a run time
        Point point=new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        //e le passa al costruttore della view
        viewMuseum = new ViewMuseum(this,point.x,point.y);

        sound=new Sound(this,0);

        //setta la view del gioco
        setContentView(viewMuseum);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewMuseum.resume();
        sound.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewMuseum.pause();
        sound.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sound.stop();
    }

    public void callTravel(short flag){
        Bundle bundle=new Bundle(1);
        bundle.putShort("flag", flag); //setta il flag nel boundle uguale a quello che gli arriva dalla view per capire se sta andando su marte o sulla luna
        Intent i = new Intent(HallActivity.this, GameActivityPortrait.class);
        i.putExtras(bundle);
        this.startActivity(i);
    }
    public void callManager(short flag, short flagActivity){
        Bundle bundle=new Bundle(2);
        bundle.putShort("flag", flag); //setta il flag nel boundle uguale a 0 perchè sta chiamando il fragment di pausa
        bundle.putShort("flagActivity",flagActivity);
        Intent i = new Intent(HallActivity.this, ManagerActivity.class);
        i.putExtras(bundle);
        this.startActivity(i);
    }

    public void callLogin(){  //chiama l'activity login
        Intent i=new Intent(HallActivity.this,MainActivity.class);
        this.startActivity(i);
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this,
                        new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission}, requestCode);
            }
        }
    }


}
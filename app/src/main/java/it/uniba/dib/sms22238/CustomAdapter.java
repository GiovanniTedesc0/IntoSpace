package it.uniba.dib.sms22238;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import it.uniba.dib.sms22238.ingress.Player;

public class CustomAdapter extends BaseAdapter {
    ArrayList<Player> giocatori = new ArrayList<Player>();
    Context mContext;



    public CustomAdapter(Context context, ArrayList<Player> giocatori){
    this.giocatori = giocatori;
    mContext = context;
    }
    public int getCount() {
        return giocatori.size();
    }

    @Override
    public Object getItem(int position) {
        return giocatori.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null)
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_view_players,viewGroup, false);
        }
        Player tempPlayer = (Player) getItem(position);
        TextView nome_player = (TextView)view.findViewById(R.id.nome_player);
        TextView numero_gemme = (TextView)view.findViewById(R.id.numero_gemme);


        nome_player.setText(tempPlayer.getName());
        numero_gemme.setText(""+tempPlayer.getPunteggio());

        return view;
    }
}

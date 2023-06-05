package it.uniba.dib.sms22238.ingress;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import it.uniba.dib.sms22238.MainActivity;
import it.uniba.dib.sms22238.R;

public class HomeFragment extends Fragment {

    //bottoni del fragment d'ingresso che servono per fare login, registrazione e entrare come ospite

    private Button loginButton;
    private Button singupButton;
    private TextView account;
    private ImageView backBtn;
    private SharedPreferences prefs;



    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        loginButton = v.findViewById(R.id.loginBtn);
        singupButton = v.findViewById(R.id.singupBtn);
        account = v.findViewById(R.id.textView3);
        backBtn=v.findViewById(R.id.back_arrow);

        prefs=getActivity().getSharedPreferences("game",getActivity().MODE_PRIVATE);
        if(!prefs.getString("email","").isEmpty()){
            account.setText("Account: " + prefs.getString("email",""));
        }

        backBtn.setOnClickListener(view -> {
                getActivity().onBackPressed();
                getActivity().finish();
        });

        loginButton.setOnClickListener(v14 -> {
            AppCompatActivity activity = (MainActivity) getActivity();
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new LoginFragment());
            ft.addToBackStack(null);
            ft.commit();
        });

        singupButton.setOnClickListener(v13 -> {
            AppCompatActivity activity = (MainActivity) getActivity();
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new SingUpFragment());
            ft.addToBackStack(null);
            ft.commit();
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
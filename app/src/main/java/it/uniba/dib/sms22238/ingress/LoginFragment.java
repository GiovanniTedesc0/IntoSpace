package it.uniba.dib.sms22238.ingress;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import it.uniba.dib.sms22238.HallActivity;
import it.uniba.dib.sms22238.R;

public class LoginFragment extends Fragment {

    private EditText email;
    private EditText password;
    private Button loginBtn;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private FirebaseAuth auth;
    DocumentReference docRef;
    private Toast toast;
    private ImageView backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        prefs=getActivity().getSharedPreferences("game",getActivity().MODE_PRIVATE);

        editor= prefs.edit();
        auth = FirebaseAuth.getInstance();
        email=v.findViewById(R.id.email);
        password=v.findViewById(R.id.password);
        loginBtn=v.findViewById(R.id.loginBtn);
        backBtn=v.findViewById(R.id.back_arrow);

        backBtn.setOnClickListener(view -> getActivity().onBackPressed());

        loginBtn.setOnClickListener(view -> {

            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();


            if(TextUtils.isEmpty(txt_email)  || TextUtils.isEmpty(txt_password)){
                Toast toast=Toast.makeText(getActivity(), R.string.Fill_login, Toast.LENGTH_SHORT);
                toast.show();
            }else
            {
                loginUser(txt_email,txt_password);

            }

        });

       return v;
    }

    private void loginUser(String email, String password) {


        if(auth.getCurrentUser() != null)
        {
            FirebaseAuth.getInstance().signOut();
        }
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {


                toast=Toast.makeText(getActivity(), R.string.loggin_successful, Toast.LENGTH_SHORT);


                docRef = FirebaseFirestore.getInstance().collection("gems").document(email);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.exists())
                            {
                                Map<String, Object> localGems = doc.getData();
                                editor.putString("email", email);
                                editor.putLong("moonGem", (long )localGems.get("moonGem"));
                                editor.putLong("marsGem", (long)localGems.get("marsGem"));

                                editor.putInt("tempGems", 0);
                                editor.putInt("gameCounter", 0);
                                editor.putInt("xPosition", 0);
                                editor.putInt("flagLevel", -1);

                                editor.commit();

                                Intent i = new Intent(getActivity(), HallActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                getActivity().finish();

                            }


                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        toast=Toast.makeText(getActivity(), R.string.Doc_error, Toast.LENGTH_SHORT);
                        toast.show();

                    }
                });




            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toast=Toast.makeText(getActivity(), R.string.Loggin_failed, Toast.LENGTH_SHORT);
                toast.show();
                Log.w(TAG ,"login failed",e);


            }
        });

    }
}
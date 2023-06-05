package it.uniba.dib.sms22238;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import it.uniba.dib.sms22238.ingress.Player;

public class RankingFragment extends Fragment {

    FirebaseFirestore db;
    Player player;
    long totGems = 0;
    ListView rankList;
    ArrayAdapter<String> arr;
    String[] appoggio;
    int[] appoggio2;
    Button go_back;
    Button export;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ranking, container, false);
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getActiveNetwork() != null)
        {
            rankList = v.findViewById(R.id.list);
            go_back = v.findViewById(R.id.go_back1);
            export = v.findViewById(R.id.export);

            go_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });


            ArrayList<Player> list= new ArrayList<Player>();
            db = FirebaseFirestore.getInstance();
            db.collection("gems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {

                        for(QueryDocumentSnapshot document : task.getResult()) {

                            totGems = (long)(document.getData()).get("marsGem") + (long)(document.getData()).get("moonGem");
                            list.add(new Player(document.getId(), totGems));

                        }
                        Collections.sort(list);
                        appoggio = new String[list.size()];
                        appoggio2 = new int[list.size()];

                        for(int i= 0; i< list.size(); i++)
                        {
                          player = list.get(i);
                          appoggio[i] = player.getName();
                          appoggio2[i] = (int) player.getPunteggio();

                         }
                        CustomAdapter myCustomAdapter = new CustomAdapter(v.getContext(),list);
                        rankList.setAdapter(myCustomAdapter);


                    }else
                    {
                        Toast.makeText(getContext(),R.string.FetchErr,Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else
        {
            Toast.makeText(getContext(),R.string.ConnRankErr,Toast.LENGTH_SHORT).show();
        }


        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Toast.makeText(getContext(),R.string.SuccesExport,Toast.LENGTH_SHORT).show();
                    CreateExcel(appoggio,appoggio2);
                } catch (IOException e) {
                    Toast.makeText(getContext(),R.string.ErrorExport,Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });




        return v;
    }

    public void CreateExcel(String[] list,int[] list2) throws IOException {

        File path = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(path, "file.xls");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row;
        HSSFCell cell ;

             for(int i = 0; i < list.length; i++)
            {

                row =  sheet.createRow(i);
                cell  = row.createCell(0);
                cell.setCellValue(list[i]);
                cell  = row.createCell(1);
                cell.setCellValue(list2[i]);

            }

        FileOutputStream fileOut = new FileOutputStream(file);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();


    }



}
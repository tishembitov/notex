package com.example.notex_release;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    ListTaskAdapter listTaskAdapter;

    DBHelper mydb;
    LinearLayout empty;
    NestedScrollView scrollView;
    LinearLayout activeContainer, doneContainer;
    NoScrollListView taskListActive, taskListDone;
    ArrayList<HashMap<String, String>> activeList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> doneList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);
        empty = findViewById(R.id.empty);
        scrollView = findViewById(R.id.scrollView);
        activeContainer = findViewById(R.id.activeContainer);
        doneContainer = findViewById(R.id.doneContainer);

        taskListActive= findViewById(R.id.taskListActive);
        taskListDone = findViewById(R.id.taskListDone);


    }

    public void openAddModifyTask(View view) {
        startActivity(new Intent(this, AddModifyTask.class));
    }


//    private void updateUI() {
//        if (listTaskAdapter == null) {
//            listTaskAdapter = new ListTaskAdapter();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        populateData();
    }

/*    @Override
    public void onStart() {
        super.onStart();
        populateData();
    }*/


    public void populateData() {
        mydb = new DBHelper(this);

        runOnUiThread(new Runnable() {
            public void run() {
                fetchDataFromDB();
            }
        });
    }


    public void fetchDataFromDB() {
        activeList.clear();
        doneList.clear();
        //ListTaskAdapter adapter = new ListTaskAdapter(this, activeList, mydb);

        Cursor active = mydb.getActiveTask();
        Cursor done = mydb.getDoneTask();


        loadDataList(active, activeList);
        loadDataList(done, doneList);
        //adapter.notifyDataSetChanged();

        //отображение/неотображение категорий
        if (activeList.isEmpty() && doneList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            if (activeList.isEmpty()) {
                activeContainer.setVisibility(View.GONE);
            } else {
                activeContainer.setVisibility(View.VISIBLE);
                loadListView(taskListActive, activeList);
            }

            if (doneList.isEmpty()) {
                doneContainer.setVisibility(View.GONE);
            } else {
                doneContainer.setVisibility(View.VISIBLE);
                loadListView(taskListDone, doneList);
            }

        }
    }


    public void loadDataList(Cursor cursor, ArrayList<HashMap<String, String>> dataList) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                HashMap<String, String> mapActive = new HashMap<String, String>();
                mapActive.put("id", cursor.getString(0).toString());
                mapActive.put("task", cursor.getString(1).toString());
                mapActive.put("date", cursor.getString(2).toString());
                mapActive.put("status", cursor.getString(3).toString());
                dataList.add(mapActive);

                cursor.moveToNext();
            }
        }
    }

    public void loadListView(NoScrollListView listView, final ArrayList<HashMap<String, String>> dataList) {
        ListTaskAdapter adapter = new ListTaskAdapter(this, dataList, mydb);
        listView.setAdapter(adapter);
        ///! не влияет
        //adapter.notifyDataSetChanged();

        //создаем для списка слушатель
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(com.example.notex_release.MainActivity.this, AddModifyTask.class);
                i.putExtra("isModify", true);
                i.putExtra("id", dataList.get(+position).get("id"));
                startActivity(i);
            }
        });
    }
}

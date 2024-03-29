package com.blt.mockgps.Main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.ObjectBoxLiveData;
import com.blt.mockgps.Application.MyApp;
import com.blt.mockgps.Database.Entitiy.PeriodEntitiy;
import com.blt.mockgps.Database.Entitiy.PositionsEntitiy;
import com.blt.mockgps.Database.Setting.ImportDataClass;
import com.blt.mockgps.Location.AddLocationDialog;
import com.blt.mockgps.R;
import com.blt.mockgps.Services.BackGroundTaskService;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler_main;
    private Box<PositionsEntitiy> positionBox;
    private Box<PeriodEntitiy> PeriodBox;
    private FloatingActionButton btnAddNew;
    private List<PositionsEntitiy> list_poses;
    private MenuItem btnStart, btnPause;
    private TextInputEditText etTime;
    private boolean blStart = false;


    private PeriodEntitiy periodEntitiy;
    private ImportDataClass importDataClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getViews();
        setSetting();
        getData();
        setData();
        setSweepCommand();
    }


    private void getViews() {
        recycler_main = findViewById(R.id.recycler_main);
        btnAddNew = findViewById(R.id.btnAddNewRow);
        etTime = findViewById(R.id.etTime);
    }

    private void setSetting() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_main.setLayoutManager(layoutManager);


        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddLocationDialog addLocationDialog = new AddLocationDialog();
                addLocationDialog.show(getSupportFragmentManager().beginTransaction(), "");
            }
        });

        etTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null && editable.length()>0){
                    if(periodEntitiy != null)
                        periodEntitiy.setPeriod(Long.valueOf(editable.toString()));
                    else
                        periodEntitiy = new PeriodEntitiy(0,Long.valueOf(editable.toString()));

                    PeriodBox.put(periodEntitiy);
                }
            }
        });

    }

    private void getData() {
        BoxStore boxStore = ((MyApp) getApplication()).getBoxStore();
        positionBox = boxStore.boxFor(PositionsEntitiy.class);
        PeriodBox = boxStore.boxFor(PeriodEntitiy.class);
    }

    private void setData() {

        periodEntitiy = PeriodBox.get(1);

        if(periodEntitiy==null)
            periodEntitiy= new PeriodEntitiy(0,5);



        etTime.setText(String.valueOf(periodEntitiy.Period));

        ObjectBoxLiveData<PositionsEntitiy> live_pos = new ObjectBoxLiveData<>(positionBox.query().build());
        live_pos.observe(this, new Observer<List<PositionsEntitiy>>() {
            @Override
            public void onChanged(@Nullable List<PositionsEntitiy> positionsEntitiys) {
                list_poses = positionsEntitiys;
                PositionAdapter adapter = new PositionAdapter(MainActivity.this, positionsEntitiys);
                recycler_main.setAdapter(adapter);
            }
        });
    }

    private void setSweepCommand() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (list_poses == null)
                    return;

                if (swipeDir == 4 || swipeDir == 8)//Sweep to Left or Right
                {
                    PositionsEntitiy positionsEntitiy = list_poses.get(viewHolder.getAdapterPosition());
                    positionBox.remove(positionsEntitiy.getId());
                }

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycler_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        btnStart = menu.findItem(R.id.btnStart);
        btnPause = menu.findItem(R.id.btnPause);
        checkPlayPasue();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnPause:
                pauseCommand();
                return true;
            case R.id.btnStart:
                startCommand();
                return true;
            case R.id.btnImport:
                ImportCommand();
                return true;
            case R.id.btnClear:
                btnClearCommand();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void btnClearCommand(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        positionBox.removeAll();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    private void ImportCommand(){
        importDataClass = new ImportDataClass(this);
        importDataClass.ChoiceFileDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayPasue();
    }

    private void checkPlayPasue() {

        if(btnStart==null || btnPause==null)
            return;

        if (blStart) {
            btnPause.setVisible(true);
            btnStart.setVisible(false);
        } else {
            btnPause.setVisible(false);
            btnStart.setVisible(true);
        }
    }

    private void startCommand(){
        blStart = true;
        startService(new Intent(this, BackGroundTaskService.class));
        checkPlayPasue();
    }
    private void pauseCommand(){
        blStart = false;
        stopService(new Intent(this, BackGroundTaskService.class));
        checkPlayPasue();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.RESULT_LOAD_IMAGE) && resultCode == RESULT_OK && null != data) {
             Uri selectedJson = data.getData();
            List<PositionsEntitiy> list_pos = importDataClass.ReadFile(selectedJson);
            if(list_pos!=null){
                positionBox.put(list_pos);
            }
        }
    }
}

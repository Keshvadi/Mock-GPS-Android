package ir.babak.mockgps.Main;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.ObjectBoxLiveData;
import ir.babak.mockgps.Application.MyApp;
import ir.babak.mockgps.Database.Entitiy.PeriodEntitiy;
import ir.babak.mockgps.Database.Entitiy.PositionsEntitiy;
import ir.babak.mockgps.Location.AddLocationDialog;
import ir.babak.mockgps.R;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler_main;
    private Box<PositionsEntitiy> positionBox;
    private Box<PeriodEntitiy> PeriodBox;
    private FloatingActionButton btnAddNew;
    private List<PositionsEntitiy> list_poses;
    private MenuItem btnStart, btnPause;
    private TextInputEditText etTime;
    private boolean blStart = false;
    private Timer timer = new Timer();
    private View parentview;
    private MockLocationClass mockLocationClass;
    private int pos = -1;
    private long mPeriod ;
    private Handler mHandler = new Handler();
    private PeriodEntitiy periodEntitiy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        parentview = findViewById(android.R.id.content);
        mockLocationClass = new MockLocationClass(this);
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

        mPeriod = periodEntitiy.Period * 1000;

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
        }
        return super.onOptionsItemSelected(item);
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
        ToggleCommand();
        checkPlayPasue();
    }
    private void pauseCommand(){
        blStart = false;
        ToggleCommand();
        checkPlayPasue();
    }

    private void ToggleCommand(){
        if(list_poses==null || list_poses.size()<1){
            Snackbar.make(parentview,R.string.YouNeedAddSomeLocationBeforStartAp,Snackbar.LENGTH_LONG);
            blStart = false;
            return;
        }



        if(blStart){
           TimerTask updateLocation = new UpdateLocationTask();
           timer.scheduleAtFixedRate(updateLocation,200,1*1000);
            start();
        }else {
            timer.cancel();
            LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                locMgr.removeTestProvider(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                Snackbar.make(parentview,e.toString(),Snackbar.LENGTH_LONG);
                e.printStackTrace();
            }
        }
    }

    private void start(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(pos >= (list_poses.size()-1))
                    pos=0;
                else
                    pos++;

                mHandler.postDelayed(this, mPeriod);
            }
        });
    }

    class UpdateLocationTask extends TimerTask {

        @Override
        public void run() {
            Log.i("Timer","Tick");
            if(list_poses==null || list_poses.size()<1)
                return;

            PositionsEntitiy positionsEntitiy = list_poses.get(pos);
            Log.i("Timer",positionsEntitiy.toString());

            mockLocationClass.setMock(positionsEntitiy.getLat(),positionsEntitiy.getLng(),20);
        }
    }



}

package xyz.techrelation.lookingforasantaclaus;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {
    private int place_targetA, place_targetB, place_targetC, place_targetD;
    private int type_targetA, type_targetB, type_targetC, type_targetD;
    private int bonus_number;
    private int game_count;
    private int countlv1, countlv2, countlv3;
    private int score;
    private int timerM;
    private int no_target;

    private static final int NOTFOUND = 404;

    private boolean hasStarted = false;
    private boolean first_click = false;
    private boolean click_bonus = false;
    private boolean isBonusStage = false;


    private TextView statusView;
    private TextView scoreView;
    private ImageView[] imageViews = new ImageView[16];

    private Timer mainTimer;
    private MainTimerTask MainTimerTask;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        TypefaceProvider.registerDefaultIconSets();
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        setContentView(R.layout.activity_main);
        scoreView = (TextView) findViewById(R.id.ScoreView);
        statusView = (TextView) findViewById(R.id.Status);

        setup();

    }

    public void setup() {
        for (int i = 0; i < 16; i++) {
            imageViews[i] = (ImageView) findViewById(getResources()
                    .getIdentifier("image" + i, "id", getPackageName()));
            imageViews[i].setImageResource(R.drawable.question);
        }
    }

    public class MainTimerTask extends TimerTask {
        @Override
        public void run() {
            first_click = true;
            mHandler.post(new Runnable() {
                public void run() {
                    setup();
                    Random rnd = new Random();
                    if (game_count <= 10) {
                        game_count += 1;
                        timerM = 3000;
                        place_targetA = rnd.nextInt(15);
                        imageViews[place_targetA].setImageResource(R.drawable.present_box1);
                        first_click = true;
                    } else if (game_count > 10 && game_count <= 21) {
                        mainTimer.cancel();
                        game_count += 1;
                        timerM = 2000;
                        statusView.setText(R.string.lv2_st);
                        timers();
                        place_targetA = rnd.nextInt(15);
                        imageViews[place_targetA].setImageResource(R.drawable.present_box2);
                        first_click = true;
                    } else if (game_count > 21 && game_count < 31) {
                        mainTimer.cancel();
                        game_count += 1;
                        timerM = 1500;
                        statusView.setText(R.string.lv3_st);
                        timers();
                        place_targetA = rnd.nextInt(15);
                        imageViews[place_targetA].setImageResource(R.drawable.present_box3);
                        first_click = true;
                        //3分の1の確率でbonus_numberをセット
                        while (place_targetA == bonus_number) {
                            bonus_number = rnd.nextInt(47);
                        }
                        if (bonus_number <= 15) {
                            click_bonus = false;
                            imageViews[bonus_number].setImageResource(R.drawable.santa);
                            first_click = true;
                        }
                    } else {
                        mainTimer.cancel();
                        //BonusStage開始条件
                        if (game_count < 41 && countlv1 > 7 && countlv2 > 7 && countlv3 > 7) {
                            isBonusStage = true;
                            game_count += 1;
                            timerM = 1300;
                            statusView.setText(R.string.bnstage_st);
                            timers();
                            place_targetA = rnd.nextInt(15);
                            type_targetA = 4;
                            type_targetB = 0;
                            type_targetC = 0;
                            type_targetD = 0;
                            no_target = 1;
                            //set数と時間を決定：1→100％（1300）,2→70％（1800）,3→40％（2400）,4→20％（3300）
                            int decide_time = rnd.nextInt(10) + 1;
                            //それぞれの場所と種類を決定
                            if (decide_time >= 4) {
                                timerM = 1800;
                                no_target++;
                                while (place_targetB == place_targetA) {
                                    place_targetB = rnd.nextInt(15);
                                    type_targetB = rnd.nextInt(2) + 1;
                                    setTarget(place_targetB, type_targetB);
                                }

                            }
                            if (decide_time >= 7) {
                                timerM = 2400;
                                no_target++;
                                while (place_targetC == place_targetA || place_targetC == place_targetB) {
                                    place_targetC = rnd.nextInt(15);
                                    type_targetC = rnd.nextInt(2) + 1;
                                    setTarget(place_targetC, type_targetC);
                                }
                            }
                            if (decide_time >= 9) {
                                timerM = 3300;
                                no_target++;
                                while (place_targetD == place_targetA || place_targetD == place_targetB || place_targetD == place_targetC) {
                                    place_targetD = rnd.nextInt(15);
                                    type_targetD = rnd.nextInt(2) + 1;
                                }
                            }

                            //3分の1の確率でbonus_numberをセット
                            while (place_targetA == bonus_number || place_targetB == bonus_number || place_targetC == bonus_number || place_targetD == bonus_number) {
                                bonus_number = rnd.nextInt(47);
                            }
                            if (bonus_number <= 15) {
                                click_bonus = false;
                                imageViews[bonus_number].setImageResource(R.drawable.santa);
                            }
                        }
                        first_click = true;
                        statusView.setText(R.string.finish_st);
                        setup();
                    }
                }
            });
        }
    }

    public void timers() {
        this.mainTimer = new Timer();
        this.MainTimerTask = new MainTimerTask();
        this.mainTimer.schedule(MainTimerTask, 1000, 3000);
    }

    public void setTarget(int _place_target, int _type_target) {
        switch (_type_target) {
            case 1:
                imageViews[_place_target].setImageResource(R.drawable.present_box1);
                break;
            case 2:
                imageViews[_place_target].setImageResource(R.drawable.present_box2);
                break;
            case 3:
                imageViews[_place_target].setImageResource(R.drawable.present_box3);
                break;
            case 4:
                imageViews[_place_target].setImageResource(R.drawable.present_box4);
                break;
        }
    }

    public void Start(View view) {
        changeScore(0,NOTFOUND);
        timerM = 3000;
        mainTimer = new Timer();
        MainTimerTask = new MainTimerTask();
        mainTimer.schedule(MainTimerTask, 3000, timerM);
        statusView.setText(R.string.lv1_st);
        hasStarted = true;
    }

    public void Reset(View view) {
        if (hasStarted) {
            mainTimer.cancel();
            MainTimerTask.cancel();
            changeScore(0,NOTFOUND);
            game_count = 0;
            hasStarted = false;

            for (int i = 0; i < 16; i++) {
                imageViews[i] = (ImageView) findViewById(getResources()
                        .getIdentifier("image" + i, "id", getPackageName()));
                imageViews[i].setImageResource(R.drawable.question);

            }
        }
    }

    public void find(View view) {
        int click_number = Integer.parseInt((String) view.getTag());
        //レベル1でtarget_numberを押したとき
        if (first_click && hasStarted && game_count < 11 && click_number == place_targetA) {
            changeScore(15, click_number);
            countlv1 = countlv1++;
            first_click = false;
        }

        //レベル1でtarget_number以外を押したとき
        else if (hasStarted && game_count < 11 && click_number != place_targetA) {
            changeScore(-10, click_number);
        }

        //レベル2でtarget_numberを押したとき
        else if (first_click && game_count > 10 && game_count < 21 && click_number == place_targetA) {
            changeScore(20, click_number);
            countlv2 = countlv2++;
            first_click = false;
        }

        //レベル2でtarget_number以外を押したとき
        else if (game_count > 10 && game_count < 21 && click_number != place_targetA) {
            changeScore(-15, click_number);
        }

        //レベル3でbonus_numberを押さずtarget_numberを押したとき
        else if (first_click && game_count > 21 && click_number == place_targetA && !click_bonus) {
            changeScore(25, click_number);
            countlv3 = countlv3++;
            first_click = false;
        }

        //レベル3とBonusStageでbonus_numberを押したとき
        if (game_count > 21 && click_number == bonus_number) {
            click_bonus = true;
        }

        //レベル3でbonus_numberを押したあとtarget_numberを押したとき
        else if (game_count > 21 && click_number == place_targetA && click_bonus) {
            changeScore(40, click_number);
            if (!isBonusStage) {
                countlv3 = countlv3++;
            }
        }

        //レベル3でtarget_numberとbonus_number以外を押したとき
        else if (game_count > 21 && click_number != place_targetA && click_number != bonus_number) {
            changeScore(-20, click_number);

        }

        //BonusStageでtarget_numberAを押したとき
        else if (isBonusStage && click_number == place_targetA) {
            if (!click_bonus) {
                switch (type_targetA) {
                    case 1:
                        changeScore(15,NOTFOUND);
                        imageViews[place_targetA].setImageResource(R.drawable.plus15);
                        break;
                    case 2:
                        changeScore(20,NOTFOUND);
                        imageViews[place_targetA].setImageResource(R.drawable.plus20);
                        break;
                    case 3:
                        changeScore(25,NOTFOUND);
                        imageViews[place_targetA].setImageResource(R.drawable.plus25);
                        break;
                    case 4:
                        changeScore(30,NOTFOUND);
                        imageViews[place_targetA].setImageResource(R.drawable.plus30);
                        break;
                }
            } else {
                changeScore(40,NOTFOUND);
                imageViews[place_targetA].setImageResource(R.drawable.plus40);
                click_bonus = false;
            }
        }

        //BonusStageでtarget_numberBを押したとき
        else if (isBonusStage && click_number == place_targetB) {
            if (!click_bonus) {
                switch (type_targetB) {
                    case 1:
                        changeScore(15,NOTFOUND);
                        imageViews[place_targetB].setImageResource(R.drawable.plus15);
                        break;
                    case 2:
                        changeScore(20,NOTFOUND);
                        imageViews[place_targetB].setImageResource(R.drawable.plus20);
                        break;
                    case 3:
                        changeScore(25,NOTFOUND);
                        imageViews[place_targetB].setImageResource(R.drawable.plus25);
                        break;
                    case 4:
                        changeScore(30,NOTFOUND);
                        imageViews[place_targetB].setImageResource(R.drawable.plus30);
                        break;

                }
            } else {
                changeScore(40,NOTFOUND);
                imageViews[place_targetB].setImageResource(R.drawable.plus40);
                click_bonus = false;
            }
        }
        //BonusStageでtarget_numberCを押したとき
        else if (isBonusStage && click_number == place_targetC) {
            if (!click_bonus) {
                switch (type_targetC) {
                    case 1:
                        changeScore(15,NOTFOUND);
                        imageViews[place_targetC].setImageResource(R.drawable.plus15);
                        break;
                    case 2:
                        changeScore(20,NOTFOUND);
                        imageViews[place_targetC].setImageResource(R.drawable.plus20);
                        break;
                    case 3:
                        changeScore(25,NOTFOUND);
                        imageViews[place_targetC].setImageResource(R.drawable.plus25);
                        break;
                    case 4:
                        changeScore(30,NOTFOUND);
                        imageViews[place_targetC].setImageResource(R.drawable.plus30);
                        break;
                }
            } else {
                changeScore(40,NOTFOUND);
                imageViews[place_targetC].setImageResource(R.drawable.plus40);
                click_bonus = false;
            }

        }
        //BonusStageでtarget_numberDを押したとき
        else if (isBonusStage && click_number == place_targetD) {
            if (!click_bonus) {
                switch (type_targetD) {
                    case 1:
                        changeScore(15,NOTFOUND);
                        imageViews[place_targetD].setImageResource(R.drawable.plus15);
                        break;
                    case 2:
                        changeScore(20,NOTFOUND);
                        imageViews[place_targetD].setImageResource(R.drawable.plus20);
                        break;
                    case 3:
                        changeScore(25,NOTFOUND);
                        imageViews[place_targetD].setImageResource(R.drawable.plus25);
                        break;
                    case 4:
                        changeScore(30,NOTFOUND);
                        imageViews[place_targetD].setImageResource(R.drawable.plus30);
                        break;
                }
            } else {
                changeScore(40,NOTFOUND);
                imageViews[place_targetD].setImageResource(R.drawable.plus40);
                click_bonus = false;
            }
        }

        //BonusStageでtarget以外を押したとき
        else if (isBonusStage && (click_number != place_targetA || click_number != place_targetB || click_number != place_targetC)) {
            switch (no_target) {
                case 1:
                    changeScore(-30, click_number);
                    break;
                case 2:
                    changeScore(-20, click_number);
                    break;
                case 3:
                    changeScore(-15, click_number);
                    break;
                case 4:
                    changeScore(-10, click_number);
                    break;

            }
        }


    }

    public void changeScore(int getScore, int click_number) {
        score += getScore;
        scoreView.setText(score + R.string.unit_sc);
        if (click_number!= NOTFOUND) {
            switch (getScore) {
                case -30:
                    imageViews[click_number].setImageResource(R.drawable.minus30);
                    break;
                case -20:
                    imageViews[click_number].setImageResource(R.drawable.minus20);
                    break;
                case -10:
                    imageViews[click_number].setImageResource(R.drawable.minus10);
                    break;
                case 15:
                    imageViews[click_number].setImageResource(R.drawable.plus15);
                    break;
                case 20:
                    imageViews[click_number].setImageResource(R.drawable.plus20);
                    break;
                case 30:
                    imageViews[click_number].setImageResource(R.drawable.plus30);
                    break;
                case 40:
                    imageViews[click_number].setImageResource(R.drawable.plus40);
                    break;

            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

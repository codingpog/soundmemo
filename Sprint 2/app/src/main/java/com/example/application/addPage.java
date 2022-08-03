package com.example.application;

//import android.graphics.Color;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.application.utils.Utils;
import com.google.firebase.database.FirebaseDatabase;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.luck.picture.lib.language.LanguageConfig;

import java.util.ArrayList;

// Page for users to add a task to their to-do list
public class addPage extends AppCompatActivity {
    Button button_done;
    Button button_back;
    Button button_camera;
    Button button_galley;
    EditText send_acts;
    EditText send_tips;
    //EditText send_duration;
    TimePicker time;
    String mSelectTime;
    ImageView iv;
    private String picUrl;
    String myDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        // Initialize variables
        send_acts = findViewById(R.id.set_activity);
        send_tips = findViewById(R.id.set_tips);
        button_done = findViewById(R.id.addDone);
        button_back = findViewById(R.id.back_button);
        //send_duration = findViewById(R.id.duration1);
        time = findViewById(R.id.timePicker);

        // Done button clicked
        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = getIntent().getStringExtra("date");
                myDay = day;
                Log.i(TAG, "onClick: "+day);
                String activity = send_acts.getText().toString();
                String tips = send_tips.getText().toString();
                //String stringDuration = send_duration.getText().toString();
                //int duration;
                if(activity.isEmpty()){
                    Toast.makeText(addPage.this, "Info missing!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int hour = time.getHour();
                int minute = time.getMinute();
                /* String task_time = hour + ":" + minute; */
                Task task = new Task(activity, tips, hour, minute, false);

                // Push task input to Firebase, the chile node will always be "NotComplete"
                FirebaseDatabase.getInstance().getReference()
                        .child("Tasks and Dates")
                        .child(day)
                        .child("NotComplete")
                        .push().setValue(task);

                //add calender
                Utils.writeToCalendar(getApplicationContext(),activity,tips,day+" "+hour+":"+minute);

                Intent intent = new Intent(addPage.this, CalenderActivity.class);
                Toast.makeText(addPage.this, "Task set successfully!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        // Go back to previous activity
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        iv = findViewById(R.id.iv);

        // Click to take picture
        findViewById(R.id.iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureSelector.create(addPage.this)
                        .openGallery(SelectMimeType.ofImage())
                        .setLanguage(LanguageConfig.ENGLISH)
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                if(null != result && !result.isEmpty()) {
                                    LocalMedia localMedia = result.get(0);
                                    Glide.with(addPage.this)
                                            .load(localMedia.getPath())
                                            .apply(RequestOptions.centerCropTransform()).into(iv);
                                }
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        /*结果回调*/
//        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
//            if (data != null) {
//                PictureBean pictureBean = data.getParcelableExtra(PictureSelector.PICTURE_RESULT);
//
//                //使用 Glide 加载图片
//                Glide.with(this)
//                        .load(pictureBean.getUri())
//                        .apply(RequestOptions.centerCropTransform()).into(iv);
//                picUrl = pictureBean.getUri().toString();            }
//        }
//    }
}

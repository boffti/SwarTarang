package com.melkoa.swartarang;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO = 0;
    private static final String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath();
    private FloatingActionButton student_record_fab, teacher_record_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar_main);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Line 52-63 Initialize UI Elements
        TextView login_logo_text = findViewById(R.id.toolbar_title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/pecita.otf");
        login_logo_text.setTypeface(custom_font);

        //Request permissions at runtime
        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Util.requestPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);

        final TextView teacherText = findViewById(R.id.textView2);
        student_record_fab = findViewById(R.id.student_fab);
        teacher_record_fab = findViewById(R.id.teacher_fab);

        //Dropdown for selecting Type of Recording
        MaterialSpinner spinner = findViewById(R.id.typesSpinner);
        spinner.setItems("CUSTOM", "SA PA SA", "SARI RI GA MA PA DHA NI SA", "SASA RIRI");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                switch(position){
                    case 0:
                        //Code for 1st Option
                        if(teacher_record_fab.getVisibility() == View.INVISIBLE) {
                            teacher_record_fab.setVisibility(View.VISIBLE);
                            teacherText.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getApplicationContext(), "Record teacher's melody.", Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        //Code for 2nd Option
                        teacher_record_fab.setVisibility(View.INVISIBLE);
                        teacherText.setVisibility(View.INVISIBLE);
                        student_record_fab.setPadding(0,0,100,0);
                        Toast.makeText(getApplicationContext(), "Inbuilt Raga is loaded. ("+item+")", Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        //Code for 3rd Option
                        teacher_record_fab.setVisibility(View.INVISIBLE);
                        teacherText.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Inbuilt Raga is loaded. ("+item+")", Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        //Code for 4th Option
                        teacher_record_fab.setVisibility(View.INVISIBLE);
                        teacherText.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Inbuilt Raga is loaded. ("+item+")", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Student record action
        student_record_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showRecordDialog("Student Recording");
                recordAudio("Student Recording");
            }
        });

        //Teacher record action
        teacher_record_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showRecordDialog("Teacher Recording");
                recordAudio("Teacher Recording");
            }
        });

        //Student Record Button Long Click
        student_record_fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Playback recorded Wav file
                //TODO Playback not working. Need fix.
                AssetManager am = getApplicationContext().getAssets();
                Toast.makeText(getApplicationContext(), "Playing student recording", Toast.LENGTH_SHORT).show();
                String path = Environment.getExternalStorageDirectory() + "/student.wav";
                Uri uri = Uri.parse(path);
                final MediaPlayer player = new MediaPlayer();
                try {
                    AssetFileDescriptor afd = am.openFd(Environment.getExternalStorageDirectory() + "/student.wav");
                    player.setDataSource(path);
                    player.prepare();
                    player.start();

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Exception of type : " + e.toString());
                    e.printStackTrace();
                }

                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                    }
                });
                return true;
            }
        });

        //Teacher Record Button Long Click
        teacher_record_fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Playback recorded Wav file
                //TODO Playback not working. Need fix.
                Toast.makeText(getApplicationContext(), "Playing teacher recording", Toast.LENGTH_SHORT).show();
                String path = Environment.getExternalStorageDirectory() + "/teacher.wav";
                MediaPlayer player = new MediaPlayer();
                try {
                    player.setDataSource(path);
                    player.prepare();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Exception of type : " + e.toString());
                    e.printStackTrace();
                }
                player.start();
                return true;
            }
        });
    }

    //On audio recording result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Record Audio method (EXT LIB)
    public void recordAudio(String type) {
        int color, requestCode = 0;
        String fileName;
        if (type.equals("Student Recording")){
            color = getResources().getColor(R.color.studentColor);
            fileName = "/student.wav";
        } else {
            color = getResources().getColor(R.color.teacherColor);
            fileName = "/teacher.wav";
        }

        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(AUDIO_FILE_PATH + fileName)
                .setColor(color)
                .setRequestCode(REQUEST_RECORD_AUDIO)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(true)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
    }

    //EXT LIB to find pitch
    public void findPitch(View v){
        Toast.makeText(this, "Finding Pitch...", Toast.LENGTH_SHORT).show();
        String student_recording_path =
                Environment.getExternalStorageDirectory().getPath() + "/student.wav";

        String teacher_recording_path =
                Environment.getExternalStorageDirectory().getPath() + "/teacher.wav";
        // create a wave object
        Wave student_recording = new Wave(student_recording_path);
        Wave teacher_recording = new Wave(teacher_recording_path);
        FingerprintSimilarity fingerprintSimilarity = student_recording.getFingerprintSimilarity(teacher_recording);
        float score = fingerprintSimilarity.getScore();
        float similarity = fingerprintSimilarity.getSimilarity();
        Log.d("Similar sound :", "Score : " + score + "\n  Similarity : "+ similarity);
        Toast.makeText(this, "Score : " + score + "\n  Similarity : " + similarity, Toast.LENGTH_SHORT).show();
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            Intent i = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}

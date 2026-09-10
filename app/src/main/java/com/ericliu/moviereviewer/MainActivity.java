package com.ericliu.moviereviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView btnMic;
    private String review = "";
    private TextClassificationClient client;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new TextClassificationClient(getApplicationContext());
        handler = new Handler();

        btnMic = (ImageView) findViewById(R.id.imageView2);

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    /**
     * Show Google Speech Input Dialog.
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");

        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "Speech not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && data != null) {
                    TextView textView = (TextView) findViewById(R.id.textView);
                    ArrayList<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    String paragraph = "";
                    for (String result : results) {
                        paragraph += result;
                    }
                    review = paragraph;
                    textView.setText(paragraph);
                }
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        handler.post(
            () -> {
                client.load();
            }
        );
    }

    @Override
    protected void onStop(){
        super.onStop();
        handler.post(
            () -> {
                client.unload();
            }
        );
    }

    public void classifyTriggeredByButton(View view) {
        TextView textView4 = findViewById(R.id.textView);
        String input = textView4.getText().toString();
        handler.post(
                () -> {
                    List<Result> results = client.classify(input);

                    showResults(results);
                }
        );
    }

    private void showResults(List<Result> results) {
        runOnUiThread(
                () -> {
                    EditText editText = findViewById(R.id.editText3);
                    String movie = editText.getText().toString();
                    String textToShow = String.format("Score of the movie %s: \n ", movie);
                    double score = 0.5;
                    for (int i = 0; i < results.size(); i++) {
                        Result result = results.get(i);
                        textToShow += String.format("  %s: %s\n", result.getTitle(), result.getConfidence());
                        if (result.getTitle().equals("Positive")) {
                            score = result.getConfidence();
                        }
                    }
                    TextView textView7 = findViewById(R.id.textView7);
                    textView7.setText(textToShow);

                    String para = "";

//                    try {
//                        BufferedReader dataIO = new BufferedReader(new InputStreamReader(openFileInput("reviewhistory")));
//                        String line;
//
//                        while ((line = dataIO.readLine()) != null) {
//                            para += line + "\n";
//                        }
//
//                        dataIO.close();
//                    }
//                    catch(Exception e){
//                        e.printStackTrace();
//                    }
//
//                    try {
//                        OutputStreamWriter fos = new OutputStreamWriter(openFileOutput("reviewhistory", Context.MODE_PRIVATE));
//                        fos.write(para);
//                        fos.write(movie  + "\n");
//                        fos.write(score + "\n");
//                        fos.write(review + "\n");
//                        fos.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
////                        textView7.setText(e.getMessage());
//                    }

                }
        );
    }






}
package com.lasante.ryan.wikipediaguesser;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class GuessingActivity extends AppCompatActivity {

    ArrayList<WikipediaEntry> entries;
    ArrayList<Button> answerButtons;
    TextView hintTextView;
    TextView streakTextView;
    static String STREAK_TEXT = "Current Streak: ";
    int currentStreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guessing);
        hintTextView = (TextView)findViewById(R.id.hint);
        streakTextView = (TextView)findViewById(R.id.streak);
        answerButtons = new ArrayList<Button>();
        answerButtons.add((Button)findViewById(R.id.answer1));
        answerButtons.add((Button)findViewById(R.id.answer2));
        answerButtons.add((Button)findViewById(R.id.answer3));

        EventBus.getDefault().register(this);

        entries = new ArrayList<WikipediaEntry>();

        currentStreak = 0;
        updateStreakText();
        fetchNewQuestion();
    }

    private void fetchNewQuestion() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            // Show text saying please connect network
            Toast.makeText(this, "Please connect to the internet to play", Toast.LENGTH_LONG).show();
            return;
        }
        // Fetch three entries
        entries.clear();
        fetchRandomEntry();
        fetchRandomEntry();
        fetchRandomEntry();
    }

    protected void fetchRandomEntry() {
        new DownloadJSONTask().execute("https://en.wikipedia.org/w/api.php?action=query&generator=random&grnnamespace=0&prop=extracts&exchars=1000&format=json");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guessing, menu);
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

    public void onEvent(JSONObject jsonObject) {
        try {

            WikipediaEntry entry = new WikipediaEntry(jsonObject);
            entries.add(entry);
            if (entries.size() > 2) {
                // Now we can show the question
                askQuestion();
            }
        } catch (Exception ex) {
            Log.e("GuessingActivity", "Failed parsing json");
        }
    }

    protected void askQuestion() {
        final int correctIndex = (int)(Math.random()*8) % 3;
        String hint = entries.get(correctIndex).getClueSentence();

        View.OnClickListener correctListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStreak++;
                updateStreakText();
                Toast.makeText(v.getContext(), generateCorrectText(), Toast.LENGTH_LONG).show();
                for (Button button : answerButtons) {
                    button.setOnClickListener(null);
                    button.setEnabled(false);
                }
                fetchNewQuestion();
            }
        };

        View.OnClickListener wrongListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStreak = 0;
                updateStreakText();
                Toast.makeText(v.getContext(), "Nope Try again", Toast.LENGTH_SHORT).show();
                v.setEnabled(false);
            }
        };

        for (int i = 0; i < entries.size(); i++) {
            WikipediaEntry entry = entries.get(i);
            Button button = answerButtons.get(i);
            button.setText(entry.getTitle());
            if (i == correctIndex) {
                button.setOnClickListener(correctListener);
            } else {
                button.setOnClickListener(wrongListener);
            }
            button.setEnabled(true);
        }

        hintTextView.setText(hint);
    }

    protected void updateStreakText() {
        streakTextView.setText(STREAK_TEXT + currentStreak);
    }

    protected String generateCorrectText() {
        String value = "Ding Ding Ding! You got it";
        if (currentStreak == 5 || currentStreak == 17) {
            value = "Look at you go! That's " + currentStreak + " in a row!!";
        } else if (currentStreak == 11) {
            value = "Incredible! You're on a roll!";
        }

        return value;
    }
}

package com.lasante.ryan.wikipediaguesser;

import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by ryanlasante on 11/6/15.
 */
public class WikipediaEntry {
    protected String title;
    protected int pageid;
    protected String extract;
    protected static int TOO_FEW_WORDS = 5;
    protected static int TOO_MANY_WORDS = 30;
    protected static int RATING_PER_WORD = TOO_FEW_WORDS;
    protected static double RATING_FOR_FEW_WORDS = 1 / TOO_FEW_WORDS;
    protected static double RATING_FOR_TOO_MANY_WORDS = 1 / TOO_MANY_WORDS;


    public WikipediaEntry(int pageid, String title, String extract) {
        this.pageid = pageid;
        this.title = title;
        this.extract = extract;
    }

    public WikipediaEntry(JSONObject jsonObject) throws JSONException {
        JSONObject query = jsonObject.getJSONObject("query");
        JSONObject pages = query.getJSONObject("pages");
        JSONObject page = pages.getJSONObject(pages.keys().next());
        title = page.getString("title");
        pageid = page.getInt("pageid");
        extract = page.getString("extract");
    }

    public String getTitle() {
        return title.replaceAll("\\(.*\\)", "");
    }

    public String getDescription() {
        // First remove all html from the extract
        String plainText = new HtmlToPlainText().getPlainText(Jsoup.parse(extract));
        plainText = plainText.replace("...", "");
        plainText = plainText.replace(".\n", ". ");
        plainText = plainText.replace("\n", ". ");
        return plainText;
    }

    public String getClueSentence() {
        String description = getDescription();
        String title = getTitle();
        // Let's find the first sentence that doesn't have the title in it
        String[] sentences = description.split("(\\. |\\*)");
        int lowestClueRating = 100000;
        String bestSentence = null;
        for (String sentence : sentences) {
            int clueRating = clueRatingOfString(title, sentence, lowestClueRating);
            if (clueRating == 0) {
                return sentence.trim();
            } else if (clueRating < lowestClueRating) {
                bestSentence = sentence;
                lowestClueRating = clueRating;
            }
        }

        return bestSentence;
    }

    protected static int clueRatingOfString(String answerString, String sentence, int currentMin) {
        String[] splitAnswer = answerString.toLowerCase().split(" ");
        String normalizedSentence = sentence.toLowerCase();
        int rating = 0;
        for (String titleComponent : splitAnswer) {
            // Dumb way of doing it for now, check to see how many times each clue is in the sentence
            while (normalizedSentence.contains(titleComponent)) {
                normalizedSentence = normalizedSentence.replaceFirst(titleComponent, "");
                rating += RATING_PER_WORD;
                if (rating >= currentMin) {
                    return currentMin + 1;
                }
            }
        }

        int wordCount = normalizedSentence.split(" ").length;
        if (wordCount <= TOO_FEW_WORDS) {
            rating += RATING_FOR_FEW_WORDS * (TOO_FEW_WORDS - wordCount);
        } else if (wordCount >= TOO_MANY_WORDS) {
            rating += RATING_FOR_TOO_MANY_WORDS * wordCount;
        }
        Log.d("WikipediaEntry", "\nSentence: " + sentence + "\nRating: " + rating);
        return rating;
    }
}

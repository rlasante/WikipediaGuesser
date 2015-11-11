package com.lasante.ryan.wikipediaguesser;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.json.JSONObject;

/**
 * Created by ryanlasante on 11/6/15.
 */
public class DownloadJSONTaskTest extends TestCase {

    public void testDownloadValidJSON() throws Exception {
        DownloadJSONTask task = new DownloadJSONTask();
        task.execute("https://en.wikipedia.org/w/api.php?action=query&generator=random&grnnamespace=0&prop=extracts&exchars=1000&format=json");
//        wait(5000);
        JSONObject json = task.get();

        Assert.assertNotNull(json.optJSONObject("query"));
    }

    public void testDownloadBadJSON() throws Exception {
        DownloadJSONTask task = new DownloadJSONTask();
        task.execute("http://google.com");
//        wait(5000);
        JSONObject json = task.get();

        Assert.assertNull(json.optJSONObject("query"));
    }
}
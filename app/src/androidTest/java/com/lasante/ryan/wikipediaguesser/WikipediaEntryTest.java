package com.lasante.ryan.wikipediaguesser;

import junit.framework.TestCase;

import org.json.JSONObject;

/**
 * Created by ryanlasante on 11/6/15.
 */
public class WikipediaEntryTest extends TestCase {

    public void testCreateNullWikipediaEntry() throws Exception {
        try {
            new WikipediaEntry(null);
            assertTrue("Should never get here", false);
        } catch (Exception ex) {
            assertNotNull(ex);
        }
    }

    public void testCreateEmptyObjectWikipediaEntry() throws Exception {
        try {
            new WikipediaEntry(new JSONObject());
            assertTrue("Should never get here", false);
        } catch (Exception ex) {
            assertNotNull(ex);
        }
    }

    public void testCreateWikipediaEntry() throws Exception {
        try {
            WikipediaEntry entry = new WikipediaEntry(new JSONObject("\n" +
                    "{\"batchcomplete\":\"\",\"continue\":{\"grncontinue\":\"0.409141017924|0.409141528061|14642901|0\",\"continue\":\"grncontinue||\"},\"query\":{\"pages\":{\"41297167\":{\"pageid\":41297167,\"ns\":0,\"title\":\"John Power (director)\",\"extract\":\"<p><b>John Power</b> (b 1930) is an Australian film director best known for his work in television.</p>\\n<h2><span id=\\\"Select_Filmography\\\">Select Filmography</span></h2>\\n<ul>\\n<li><i>Escape from Singapore</i> (1974) (TV movie)</li>\\n<li><i>Billy and Percy</i> (1974) (TV movie)</li>\\n<li><i>They Don't Clap Losers</i> (1975) (TV movie)</li>\\n<li><i>The Picture Show Man</i> (1977)</li>\\n<li><i>The Sound of Love</i> (1978) (TV movie)</li>\\n<li><i>A Single Life</i> (1986) (TV movie)</li>\\n<li><i>The Dismissal</i> (1983) (TV mini series)</li>\\n<li><i>The Great Gold Swindle</i> (1984)</li>\\n<li><i>Alice to Nowhere</i> (1986) (TV mini series)</li>\\n<li><i>The Dirtwater Dynasty</i> (1988)</li>\\n<li><i>Father</i> (1990)</li>\\n<li><i>Sky Trackers</i> (1990) (TV movie)</li>\\n<li><i>All the Rivers Run 2</i> (1990) (TV mini series)</li>\\n<li><i>The Tommyknockers</i> (1993) (TV mini series)</li>\\n<li><i>Someone Else's Child</i> (1994) (TV movie)</li>\\n<li><i>Betrayed by Love</i> (1994) (TV movie)</li>\\n<li><i>Fatal Vows</i></li>\\n</ul>...\"}}}}"));

            assertEquals(41297167, entry.pageid);
            assertEquals("John Power", entry.getTitle());
            assertNotNull(entry.getDescription());

        } catch (Exception ex) {
            assertFalse(true);
        }
    }

    public void testCreatClue() throws Exception {
        try {
            WikipediaEntry entry = new WikipediaEntry(new JSONObject("\n" +
                    "{\"batchcomplete\":\"\",\"continue\":{\"grncontinue\":\"0.409141017924|0.409141528061|14642901|0\",\"continue\":\"grncontinue||\"},\"query\":{\"pages\":{\"41297167\":{\"pageid\":41297167,\"ns\":0,\"title\":\"John Power (director)\",\"extract\":\"<p><b>John Power</b> (b 1930) is an Australian film director best known for his work in television.</p>\\n<h2><span id=\\\"Select_Filmography\\\">Select Filmography</span></h2>\\n<ul>\\n<li><i>Escape from Singapore</i> (1974)</li></ul>...\"}}}}"));

            assertEquals("Escape from Singapore (1974)", entry.getClueSentence());

        } catch (Exception ex) {
            assertFalse(true);
        }
    }

    static int clueMAX = 10000;

    public void testClueRatingOf0() {
        String answer = "Nope";
        String sentence = "Test of rating 0 clues in this string";

        assertEquals(0, WikipediaEntry.clueRatingOfString(answer, sentence, clueMAX));
    }

    public void testClueRatingOfShortSentence() {
        String answer = "Nope";
        String sentence = "Short Sentence";

        assertEquals(WikipediaEntry.RATING_FOR_FEW_WORDS, WikipediaEntry.clueRatingOfString(answer, sentence, clueMAX));
    }

    public void testClueRatingOfLongSentence() {
        String answer = "Nope";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < WikipediaEntry.TOO_MANY_WORDS + 1; i++) {
            stringBuffer.append(" Word" + i);
        }
        String sentence = stringBuffer.toString().trim();
        assertEquals(WikipediaEntry.RATING_FOR_TOO_MANY_WORDS, WikipediaEntry.clueRatingOfString(answer, sentence, clueMAX));
    }

    public void testClueRatingOf1() {
        String answer = "Test";
        String sentence = "Test of rating 1 word clue";

        assertEquals(1 * WikipediaEntry.RATING_PER_WORD, WikipediaEntry.clueRatingOfString(answer, sentence, clueMAX));
    }

    public void testClueShortCircuiting() {
        String answer = "Test";
        String sentence = "Test of short circuiting Test Test";

        int minFoundValue = 1;

        assertEquals(minFoundValue + 1, WikipediaEntry.clueRatingOfString(answer, sentence, minFoundValue));
    }

    public void testClueMultiplesInSentence() {
        String answer = "Test";
        String sentence = "Test Test multiword sentence five six";

        assertEquals(2 * WikipediaEntry.RATING_PER_WORD, WikipediaEntry.clueRatingOfString(answer, sentence, clueMAX));
    }

    public void testClueMultiAnswerAndSentence() {
        String answer = "Test this";
        String sentence = "Test this multiword answer and sentence Test this";

        assertEquals(4 * WikipediaEntry.RATING_PER_WORD, WikipediaEntry.clueRatingOfString(answer, sentence, clueMAX));
    }

    public void testSample1() {
        String clue = "The album peaked at number 57 on the Billboard Top Country Albums chart";
        assertEquals(clue, sampleEntries[0].getClueSentence());
    }

    static WikipediaEntry[] sampleEntries = new WikipediaEntry[]{
            new WikipediaEntry(31075000, "Dwight's Used Records", "<p><i><b>Dwight's Used Records</b></i> is the fifteenth studio album, and the third covers album by American country music artist Dwight Yoakam. It was released by Audium Records on June 29, 2004. The album peaked at number 57 on the <i>Billboard</i> Top Country Albums chart.</p> <h2><span id=\"Track_listing\">Track listing</span></h2> <h2><span id=\"Personnel\">Personnel</span></h2> <ul> <li>Deana Carter- duet vocals on \"Waiting\"</li> <li>Skip Edwards- percussion, piano, Wurlitzer piano</li> <li>Keith Gattis- baritone guitar, electric guitar</li> <li>Mitch Marine- drums, percussion</li> <li>Heather Myles- duet vocals on \"Little Chapel\"</li> <li>The Nitty Gritty Dirt Band- vocals on \"Some Dark Holler\" and \"Wheels\"</li> <li>Dave Roe- bass guitar, upright bass, background vocals</li> <li>Ralph Stanley- duet vocals on \"Down Where the River Bends\" and \"Miner's Prayer\"</li> <li>Kay Walker- background vocals</li> <li>Ray C. Walker- background vocals</li> <li>Gabe Witcher- fiddle, mandolin</li> </ul>..."),
            new WikipediaEntry(33261227, "Celia (As You Like It)", "<p><b>Celia</b> is one of the important characters of Shakespeare's <i>As You Like It</i>.</p> <p>Celia is the daughter of Duke Frederick. Celia and Rosalind are cousins but they have sisterly affection.</p> <h2><span id=\"Physical_appearance\">Physical appearance</span></h2> <p>Celia is beautiful, but with a beauty less sparkling than that of Rosalind. Orlando describes both of them as 'fair and excellent ladies'.</p> <p>Celia is shorter than her cousin and less majestic in appearance. She has a gentle expression combined with a habitual serious appearance. Hence Rosalind addresses her at one time as 'my pretty little coz', and at another as, 'sad brow and true maid'.</p> <h2><span id=\"Love_for_Rosalind\">Love for Rosalind</span></h2> <p>Celia's love for Rosalind knows no limits and is frequently referred to in the play. Charles, the wrestler, relates that Celia loves her cousin so much that she would have followed Rosalind into exile in case Rosalind too had been banished along with</p>..."),
            new WikipediaEntry(41605910, "2006 FIBA Africa Under-18 Championship squads", "<p>This article displays the rosters for the participating teams at the 2008 FIBA Africa Under-18 Championship.</p> <h2><span id=\".C2.A0Angola_.5B1.5D\"><span>&#160;</span>Angola</span></h2> <h2><span id=\".C2.A0Congo_.5B2.5D\"><span>&#160;</span>Congo</span></h2> <h2><span id=\".C2.A0Guinea_.5B3.5D\"><span>&#160;</span>Guinea</span></h2> <h2><span id=\".C2.A0Ivory_Coast_.5B4.5D\"><span>&#160;</span>Ivory Coast</span></h2> <h2><span id=\".C2.A0Kenya_.5B5.5D\"><span>&#160;</span>Kenya</span></h2> <h2><span id=\".C2.A0Mali_.5B6.5D\"><span>&#160;</span>Mali</span></h2> <h2><span id=\".C2.A0Mozambique_.5B7.5D\"><span>&#160;</span>Mozambique</span></h2> <h2><span id=\".C2.A0Nigeria_.5B8.5D\"><span>&#160;</span>Nigeria</span></h2> <h2><span id=\".C2.A0South_Africa_.5B9.5D\"><span>&#160;</span>South Africa</span></h2> <h2><span id=\"See_also\">See also</span></h2> <ul> <li>2007 FIBA Africa Championship squads</li> </ul> <h2><span id=\"References\">References</span></h2> <h2><span id=\"External_links\">External links</span></h2>..."),
            new WikipediaEntry(31109540, "Bill Bailey (surfer)", "<p><b>Bill Bailey</b> (27 September 1933 - 28 April 2009) was known as \"the father of British surfing\" for the crucial role he played in the development of the sport in the United Kingdom. He set up the first surf company in Britain.</p> <p>Bailey grew up in Inglesbatch, in Somerset where his father withdrew him from school at age 14 due to his behaviour, and placed him in the Royal Air Force. There he trained as an engineer working on Short Sunderland flying boats and taking postings overseas. It was while working on air-sea rescue in Sri Lanka that he developed a love of the sea, quitting the air-force at the end of the 1950s and moving to Newquay to work as a lifeguard.</p> <p>Bailey began building life-saving equipment including a surf ski in 1961, designed to be used by lifeguards with paddles. While tinkering with the design he met two Australians on their way to the US, and was impressed with the foam core and fibre-glass construction of their surf boards. He bought one and learned</p>..."),
            new WikipediaEntry(44895765, "Tepava", "<p><b>Tepava</b> is a village in Lovech Municipality, Lovech Province, northern Bulgaria.</p> <h2><span id=\"References\">References</span></h2>..."),
            new WikipediaEntry(11635902, "North York Ski Centre", "<p><b>North York Ski Centre</b> (also known as the Earl Bales Ski &amp; Snowboard Centre) is a small alpine skiing hill located in Earl Bales Park, close to the intersection of Bathurst Street and Sheppard Avenue in Toronto. It features one quad chair and a rope tow, serving three intermediate slopes and one beginner slope. It is one of two ski hills located within the boundaries of Toronto, the other being the Centennial Park.</p> <p>In 2011 and 2012, repeated failures of the now-retired double chair brought up talks of closing the centre. Instead, the city decided to revitalize the club by adding a quad chair.</p> <p>The main entrance to Earl Bales Park is named after Raoul Wallenberg.</p> <h2><span id=\"See_also\">See also</span></h2> <ul> <li>Centennial Park</li> <li>Uplands Ski Area</li> <li>List of ski areas and resorts in Canada</li> </ul> <h2><span id=\"External_links\">External links</span></h2> <ul> <li>[1]</li> </ul> <p><br /></p>..."),
            new WikipediaEntry(45478889, "The Imperial OPA", "<p>The <b>Imperial OPA Circus</b> is a modern circus headquartered and operated out of Atlanta, Georgia. Founded in 2009 by Timothy Mack, it currently contains a core company of 15 performers. In conjugation with its core philosophy, to be creative, to be inspirational, and to do good, the Imperial OPA Circus performs at local events to benefit nonprofit organizations such as Wish for Wendy, which benefits those with cystic fibrosis, and the Atlanta Hunger Walk, which benefits the Atlanta Community Food Bank. Their first performance was an Atlanta local fundraiser to supply the East Atlanta Village with recycling cans. Their second performance, entitled \"Cirque du Beaute\", was on October 3, 2009 with Jyl Craven, a local hair studio. This was a fundraiser for the St. Jude's Children's Hospital of Atlanta.</p> <h2><span id=\"References\">References</span></h2>..."),
            new WikipediaEntry(42923353, "Kaan Önder", "<p><b>Kaan Önder</b>, (born 10 January 1997 in Istanbul) is a Turkish racing driver from Istanbul, Turkey. He is mentored by triple world champion Andy Priaulx.</p> <p>He holds titles of CIK SEEKZ (South East European Karting Zone) KF3 champion (2010), two times Turkish Karting Championship champion ( KF3 in 2009 and ICA-J in 2008) and winner of the Rotax Max International Open Junior Category in 2011. He is the youngest Turkish race driver who attended the FIA ETCC in 2014.</p> <h2><span id=\"Racing_career\">Racing career</span></h2> <p>Kaan's motorsports career started in 2007 with TOSFED's PO Cup project and he participated over 400 races and is the 1st Turkish driver to follow whole European Rotax Max Championship season in 2010. Kaan also raced for British Super 1 Championship, Belgian Championship, BNL Series, SEEKZ (South East European Karting Championship), and many other international trophies and race events.</p> <p>He is the first Turkish kart driver who holds</p>..."),
            new WikipediaEntry(6204062, "Southland Independent School District", "<p><b>Southland Independent School District</b> is a public school district based in the community of Southland, Texas (USA).</p> <p>Located in Garza County, portions of the district extend into Lynn and Lubbock counties.</p> <h2><span id=\"Academic_achievement\">Academic achievement</span></h2> <p>In 2009, the school district was rated \"academically acceptable\" by the Texas Education Agency.</p> <h2><span id=\"Special_programs\">Special programs</span></h2> <h3><span id=\"Athletics\">Athletics</span></h3> <p>Southland High School plays six-man football.</p> <h2><span id=\"See_also\">See also</span></h2> <ul> <li>List of school districts in Texas</li> </ul> <h2><span id=\"References\">References</span></h2> <h2><span id=\"External_links\">External links</span></h2> <ul> <li>Southland ISD</li> </ul> <p><br /></p>...")
    };
}
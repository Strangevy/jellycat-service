package com.jellycat.jellycatservice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractEpisode {

    public static void main(String[] args) {
        String[] inputs = {
                "Jade.The QUEEN Of News.Ep19.HDTV.1080p.H264-CNHK.ts",
                "追光的日子.CCTV4K.Ray.of.Light.2023.E01.2160p.UHDTV.H265.AC3-HaresTV.ts",
                "WATCHER.S01E01.2160p.TVING.WEB-DL.AAC2.0.H.265-CHDWEB.mkv",
                "我可能遇到了救星.Hi.Venus.第一季第一集.2022.2160p.WEB-DL.H265.AAC-ADWeb.mp4"
        };

        for (String input : inputs) {
            // Extract episode number
            Matcher episodeMatcher = Pattern.compile("(Ep|E)(\\d+)").matcher(input);
            int episodeNumber = -1;
            if (episodeMatcher.find()) {
                episodeNumber = Integer.parseInt(episodeMatcher.group(2));
            }

            // Extract season number (if available)
            Matcher seasonMatcher = Pattern.compile("S(\\d+)").matcher(input);
            int seasonNumber = -1;
            if (seasonMatcher.find()) {
                seasonNumber = Integer.parseInt(seasonMatcher.group(1));
            }

            System.out.println("Episode Number: " + episodeNumber);
            System.out.println("Season Number: " + seasonNumber);
            System.out.println();
        }
    }
}

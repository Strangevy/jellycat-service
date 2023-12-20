package com.jellycat.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.jellycat.dto.MedieFileRecord;

public class MedieFileUtils {

    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{4})");
    private static final Pattern RESOLUTION_PATTERN = Pattern.compile("(\\d+p)");
    private static final Pattern SEASON_PATTERN = Pattern.compile("S(\\d{1,2})");
    private static final Pattern EPISODE_PATTERN = Pattern.compile("E[p]?(\\d{1,2})");
    private static final Pattern CLEAN_FILENAME_PATTERN = Pattern
            .compile("^(.*?)(\\d{4}|S\\d{1,2}E\\d{1,2}|Ep\\d{1,2}).*");

    public static void main(String[] args) {
        List<String> filenames = Arrays.asList(
                "「万里归途」.Home.Coming.2022.2160p.WEB-DL.HEVC.DDP5.1-QHstudlo.mp4",
                "「深海」.Deep.Sea.2023.2160p.60fps.WEB-DL.HEVC.10bit.DDP5.1.Atmos.2Audios-QHstudlo.mp4",
                "「深海」.Deep.Sea.2023.2160p.V2.60fps.WEB-DL.HEVC.10bit.DDP5.1.Atmos.2Audios-QHstudlo.mp4",
                "Barbie.2023.BluRay.1080p.AVC.Atmos.TrueHD7.1-MTeam.mp4",
                "Barbie.2023.BluRay.1080p.DD5.1.x264-BMDru.mp4",
                "Dungeons.and.Dragons.Honor.Among.Thieves.2023.BluRay.2160p.TrueHD.Atmos.7.1.x265.10bit.HDR-BeiTai Love Rosie 2014 Repack 1080p BluRay DTS x264-LHD.mp4",
                "Marry.My.Dead.Body.2023.1080p.NF.WEB-DLDDP5.1.H264-HHWEB Missing.2023.2160p.WEB-DL.x265.10bit.HDR.DDP5.1-FLUX.mp4",
                "Oppenheimer.2023.IMAX.BluRay.1080p.DD5.1.x264-BMDru.mp4",
                "Puss.in.Boots.The.Last.Wish.2022.BluRay.1080p.AVC.Atmos.TrueHD7.1-MTeam Puss.in.Boots.The.Last.Wish.2022.REPACK.BluRay.1080p.DD5.1.x264-BMDru Searching.2018.BluRay.1080p.DTS-HD.MA.5.1.x265.10bit-BeiTai.mp4",
                "The.Midnight.After.2014.BluRay.1080p.TrueHD.5.1.x265.10bit-BeiTai.mp4",
                "The.Secret.Life.of.Walter.Mitty.2013.BluRay.1080p.DTS-HD.MA.7.1.x265.10bit-BeiTai The.Sparring.Partner.2022.1080p.BluRay.x265.10bit-WiKi.mp4",
                "东北告别天团.Goodbye.2022.2160p.WEB-DL.H265.AAC-LeagueWEB.mp4",
                "东北告别天团.mp4",
                "Jade.The QUEEN Of News.Ep19.HDTV.1080p.H264-CNHK.ts",
                "追光的日子.CCTV4K.Ray.of.Light.2023.E01.2160p.UHDTV.H265.AC3-HaresTV.ts",
                "WATCHER.S01E01.2160p.TVING.WEB-DL.AAC2.0.H.265-CHDWEB.mkv",
                "我可能遇到了救星.Hi.Venus.第一季第一集.2022.2160p.WEB-DL.H265.AAC-ADWeb.mp4",
                "满江红.Full.River.Red.2023.60FPS.2160p.WEB-DL.H265.10bit.DTS.5.1-OurTV.mp4",
                "神出鬼没.Ghosted.2023.ATVP.WEB-DL.2160p.HEVC.HDR.DV.Atmos.DDP5.1-HDSWEB.mp4");

        List<MedieFileRecord> cleanedFilenames = filenames.stream()
                .map(MedieFileUtils::cleanFilename)
                .collect(Collectors.toList());

        cleanedFilenames.forEach(System.out::println);
    }

    public static MedieFileRecord cleanFilename(String filename) {
        String modifiedFilename = filename.substring(0, filename.lastIndexOf('.')); // Use a temporary variable
        String year = extractInfo(modifiedFilename, YEAR_PATTERN).orElse(null);
        String resolution = extractInfo(modifiedFilename, RESOLUTION_PATTERN).orElse(null);
        Optional<Integer> season = extractInfo(modifiedFilename, SEASON_PATTERN).map(Integer::parseInt);
        Optional<Integer> episode = extractInfo(modifiedFilename, EPISODE_PATTERN).map(Integer::parseInt);

        Optional<String> cleanedResult = extractInfo(modifiedFilename, CLEAN_FILENAME_PATTERN);
        if (cleanedResult.isPresent()) {
            modifiedFilename = cleanedResult.get();
        }

        modifiedFilename = String.join(" ", modifiedFilename.replaceAll("[\\p{P}\\s]+", " ").trim());
        return new MedieFileRecord(modifiedFilename, year, resolution, season, episode);
    }

    private static Optional<String> extractInfo(String filename, Pattern pattern) {
        Matcher matcher = pattern.matcher(filename);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }
}

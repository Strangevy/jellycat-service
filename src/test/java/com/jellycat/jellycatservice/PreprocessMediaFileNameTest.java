package com.jellycat.jellycatservice;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.jellycat.dto.MedieFileRecord;

public class PreprocessMediaFileNameTest {
        private static final String SEPARATOR_REGEX = "[\\.\\-_\\s]";
        private static final String YEAR_REGEX = "\\d{4}";
        private static final String RESOLUTION_REGEX = "\\d{3,4}p";
        private static final String BRACKET_REGEX = "\\(|\\)|【|】|［|］|\\[|\\]|\\.[^.]*$";

        private static final Pattern separatorPattern = Pattern.compile(SEPARATOR_REGEX);
        private static final Pattern yearPattern = Pattern.compile(YEAR_REGEX);
        private static final Pattern resolutionPattern = Pattern.compile(RESOLUTION_REGEX);
        private static final Pattern bracketPattern = Pattern.compile(BRACKET_REGEX);

        @Test
        void test() {
                Arrays.asList("［万里归途］.Home.Coming.2022.2160p.WEB-DL.HEVC.DDP5.1-QHstudlo.mp4",
                                "［深海］.Deep.Sea.2023.2160p.60fps.WEB-DL.HEVC.10bit.DDP5.1.Atmos.2Audios-QHstudlo.mp4",
                                "［深海］.Deep.Sea.2023.2160p.V2.60fps.WEB-DL.HEVC.10bit.DDP5.1.Atmos.2Audios-QHstudlo.mp4",
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
                                "满江红.Full.River.Red.2023.60FPS.2160p.WEB-DL.H265.10bit.DTS.5.1-OurTV.mp4",
                                "神出鬼没.Ghosted.2023.ATVP.WEB-DL.2160p.HEVC.HDR.DV.Atmos.DDP5.1-HDSWEB.mp4 ")
                                .stream().map(PreprocessMediaFileNameTest::preprocessMediaFileName)
                                .filter(Optional::isPresent).forEach(System.out::println);

        }

        // A method to preprocess the media file name
        static Optional<MedieFileRecord> preprocessMediaFileName(final String mediaFileName) {
                // 使用Optional来处理可能为空的情况，避免空指针异常
                return Optional.ofNullable(mediaFileName)
                                // 去除括号和后缀
                                .map(name -> bracketPattern.matcher(name).replaceAll(""))
                                // 分割文件名
                                .map(name -> separatorPattern.split(name))
                                .flatMap(parts -> {
                                        // 找到年份部分的索引，使用filter和findFirst方法
                                        Optional<Integer> index = IntStream.range(0, parts.length)
                                                        .filter(i -> yearPattern.matcher(parts[i]).matches())
                                                        .boxed()
                                                        .findFirst();
                                        // 获取第一个分辨率部分，使用findFirst方法
                                        String resolution = Arrays.stream(parts)
                                                        .filter(part -> resolutionPattern.matcher(part).matches())
                                                        .findFirst().orElse("");
                                        // 获取处理后的文件名部分，使用join和trim方法
                                        String name = String.join(" ",
                                                        index.filter(i -> i > 0).map(i -> Arrays.copyOfRange(parts, 0, i)).orElse(parts))
                                                        .trim();
                                        // 返回处理后的文件名，年份，和分辨率，使用ofNullable方法
                                        return Optional.ofNullable(new MedieFileRecord(name,
                                                        index.map(i -> parts[i]).orElse(null), resolution));
                                });
        }

}
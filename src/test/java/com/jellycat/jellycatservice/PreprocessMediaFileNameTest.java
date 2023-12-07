package com.jellycat.jellycatservice;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class PreprocessMediaFileNameTest {
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
                "满江红.Full.River.Red.2023.60FPS.2160p.WEB-DL.H265.10bit.DTS.5.1-OurTV.mp4",
                "神出鬼没.Ghosted.2023.ATVP.WEB-DL.2160p.HEVC.HDR.DV.Atmos.DDP5.1-HDSWEB.mp4 ")
                .forEach(PreprocessMediaFileNameTest::preprocessMediaFileName);

    }

    // A method to preprocess the media file name
  static void preprocessMediaFileName(String mediaFileName) {
    String[] separators = { "\\.", "_", "-", "\\s" };

    for (String separator : separators) {
      String processedMediaFileName = Arrays.stream(mediaFileName.split(separator))
          .takeWhile(part -> !part.matches("\\d{4}"))
          .map(part -> part.replaceAll("[［］\\[\\]\\(\\)]", "")) // remove brackets
          .collect(Collectors.joining(" "));

      if (!processedMediaFileName.trim().isEmpty()) {
        System.out.println("Processed media file name: " + processedMediaFileName.trim());
        break;
      }
    }
  }
}
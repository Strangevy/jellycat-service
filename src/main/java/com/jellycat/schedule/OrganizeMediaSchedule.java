package com.jellycat.schedule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jellycat.api.TMDBApi;
import com.jellycat.dto.MedieFileRecord;
import com.jellycat.dto.SystemConfig;
import com.jellycat.dto.TMDBSearchResp;
import com.jellycat.dto.TMDBSearchResult;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrganizeMediaSchedule {
        private static final String SEPARATOR_REGEX = "[\\.\\-_\\s]";
        private static final String YEAR_REGEX = "\\d{4}";
        private static final String RESOLUTION_REGEX = "\\d{3,4}p";
        private static final String BRACKET_REGEX = "\\(|\\)|【|】|［|］|\\[|\\]|\\.[^.]*$";
        private static final String SEASON_REGEX = "S(\\d+)";
        private static final String EPISODE_REGEX = "(Ep|E)(\\d+)";

        private static final Pattern separatorPattern = Pattern.compile(SEPARATOR_REGEX);
        private static final Pattern yearPattern = Pattern.compile(YEAR_REGEX);
        private static final Pattern resolutionPattern = Pattern.compile(RESOLUTION_REGEX);
        private static final Pattern bracketPattern = Pattern.compile(BRACKET_REGEX);
        private static final Pattern seasonPattern = Pattern.compile(SEASON_REGEX);
        private static final Pattern episodePattern = Pattern.compile(EPISODE_REGEX);

        // The media file extensions to filter
        private static final List<String> MEDIA_FILE_EXTENSIONS = Arrays.asList(".mp4", ".mkv", ".avi", ".mov", ".wmv");

        // The media file size threshold in bytes
        private static final long MEDIA_FILE_SIZE_THRESHOLD = 256 * 1024 * 1024;
        @Autowired
        private SystemConfig systemConfig;
        @Autowired
        private TMDBApi tmdbApi;

        @Scheduled(fixedDelay = 60000)
        public void organizeMedia() {
                log.info("start task: organizeMedia");
                String sourcePath = systemConfig.getSourcePath();
                // traverse the source directory to identify unorganized media
                try (Stream<Path> paths = Files.walk(Paths.get(sourcePath))) {
                        // Filter the media files that are larger than the threshold
                        paths.filter(Files::isRegularFile)
                                        .map(Path::toFile)
                                        .filter(file -> isMediaFile(file) && file.length() > MEDIA_FILE_SIZE_THRESHOLD
                                                        && isNotModifiedInLastMinute(file))
                                        .forEach(file -> {
                                                // preprocess media file name
                                                Optional<MedieFileRecord> medieFileRecord = preprocessMediaFileName(
                                                                file.getName());
                                                if (!medieFileRecord.isPresent()) {
                                                        return;
                                                }
                                                // search media with TMDB API
                                                TMDBSearchResp tmdbResp = tmdbApi
                                                                .searchMulti(medieFileRecord.get().name());

                                                if (Objects.isNull(tmdbResp)
                                                                || CollectionUtils.isEmpty(tmdbResp.results())) {
                                                        return;
                                                }
                                                TMDBSearchResult result;
                                                if (StringUtils.hasText(medieFileRecord.get().year())) {
                                                        result = tmdbResp.results().stream().takeWhile(e -> {
                                                                return medieFileRecord.get().year().equals(
                                                                                e.firstAirDate().substring(0, 4));
                                                        }).findFirst().orElse(null);
                                                } else {
                                                        result = tmdbResp.results().getFirst();
                                                }
                                                // create target directory
                                                // Paths.get(systemConfig.getTargetPath()+"/"+result.mediaType()+"/"+);
                                                // move or create link media to target directory
                                                // move or create link TODO
                                                if (true) {

                                                        return;
                                                }
                                                return;
                                        });

                } catch (IOException e) {
                }

                log.info("end task: organizeMedia");
        }

        // Check if a file is a media file by its extension
        private static boolean isMediaFile(File file) {
                String fileName = file.getName().toLowerCase();
                for (String extension : MEDIA_FILE_EXTENSIONS) {
                        if (fileName.endsWith(extension)) {
                                return true;
                        }
                }
                return false;
        }

        // Check if a file is not modified in the last minute
        private static boolean isNotModifiedInLastMinute(File file) {
                // Get the current time and the file's last modified time as Instant objects
                Instant currentTime = Instant.now();
                Instant fileTime = Instant.ofEpochMilli(file.lastModified());

                // Check if the file's last modified time is before the current time minus one
                // minute
                return fileTime.isBefore(currentTime.minus(1, ChronoUnit.MINUTES));
        }

        public static Optional<MedieFileRecord> preprocessMediaFileName(final String mediaFileName) {
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
                                                        index.filter(i -> i > 0)
                                                                        .map(i -> Arrays.copyOfRange(parts, 0, i))
                                                                        .orElse(parts))
                                                        .trim();

                                        // Extract episode number
                                        Optional<Integer> seasonNumber = IntStream.range(0, parts.length)
                                                        .filter(i -> seasonPattern.matcher(parts[i]).matches())
                                                        .boxed()
                                                        .findFirst();

                                        // Extract season number 
                                        Optional<Integer> episodeNumber = IntStream.range(0, parts.length)
                                                        .filter(i -> episodePattern.matcher(parts[i]).matches())
                                                        .boxed()
                                                        .findFirst();

                                        // 返回处理后的文件名，年份，和分辨率，使用ofNullable方法
                                        return Optional.ofNullable(new MedieFileRecord(name,
                                                        index.map(i -> parts[i]).orElse(null), resolution, seasonNumber,
                                                        episodeNumber));
                                });
        }

}

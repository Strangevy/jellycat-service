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
import java.util.regex.Pattern;
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
import com.jellycat.util.MedieFileUtils;

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
                                                MedieFileRecord medieFileRecord = MedieFileUtils.cleanFilename(
                                                                file.getName());
                                                // search media with TMDB API
                                                TMDBSearchResp tmdbResp;
                                                if (medieFileRecord.episode().isPresent()) {
                                                        // tv
                                                        tmdbResp = tmdbApi.searchTV(medieFileRecord.name());

                                                } else {
                                                        // movie
                                                        tmdbResp = tmdbApi.searchMovie(medieFileRecord.name());
                                                }

                                                if (Objects.isNull(tmdbResp)|| CollectionUtils.isEmpty(tmdbResp.results())) {
                                                        return;
                                                }
                                                TMDBSearchResult result;
                                                if (StringUtils.hasText(medieFileRecord.year())) {
                                                        result = tmdbResp.results().stream().takeWhile(e -> {
                                                                return medieFileRecord.year().equals(
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
}

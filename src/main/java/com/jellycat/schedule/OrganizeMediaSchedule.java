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
import com.jellycat.util.ExceptionUtils;
import com.jellycat.util.MedieFileUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrganizeMediaSchedule {
        // The media file extensions to filter
        private static final List<String> MEDIA_FILE_EXTENSIONS = Arrays.asList(".mp4", ".mkv", ".avi", ".mov", ".wmv");

        // The media file size threshold in bytes
        private static final long MEDIA_FILE_SIZE_THRESHOLD = 256 * 1024 * 1024;

        private static final String MOVIE_NAME_STANDARD = "%s (%d).%s";
        private static final String TV_NAME_STANDARD = "%s - S%02dE%02d - %s.%s";

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
                                                MedieFileRecord medieFileRecord = MedieFileUtils
                                                                .cleanFilename(file.getName());
                                                // search media with TMDB API
                                                TMDBSearchResp tmdbResp;
                                                String seasonPath = "";
                                                if (medieFileRecord.episode().isPresent()) {
                                                        // tv
                                                        tmdbResp = tmdbApi.searchTV(medieFileRecord.name());
                                                        seasonPath = "/S" + medieFileRecord.season();
                                                } else {
                                                        // movie
                                                        tmdbResp = tmdbApi.searchMovie(medieFileRecord.name());
                                                }

                                                if (Objects.isNull(tmdbResp)
                                                                || CollectionUtils.isEmpty(tmdbResp.results())) {
                                                        return;
                                                }
                                                // match
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
                                                Path targetPath = Paths.get(systemConfig.getTargetPath() + "/"
                                                                + (medieFileRecord.episode().isPresent() ? "tv"
                                                                                : "movie")
                                                                + "/" + result.name()
                                                                + seasonPath);
                                                File targetFile = targetPath.toFile();
                                                if (!targetFile.exists()) {
                                                        targetFile.mkdirs();
                                                }
                                                // move or create link media to target directory

                                                // move flag TODO
                                                boolean moveFlag = true;
                                                if (moveFlag) {
                                                        try {
                                                                Files.move(file.toPath(),
                                                                                Paths.get(targetPath.toString() + "/"
                                                                                                + ""));
                                                        } catch (IOException e) {
                                                                throw ExceptionUtils.buildException(log, e,
                                                                                "show sub list error");
                                                        }

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

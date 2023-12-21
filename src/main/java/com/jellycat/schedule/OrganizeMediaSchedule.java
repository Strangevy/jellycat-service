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
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.jellycat.api.TMDBApi;
import com.jellycat.dto.MedieFileRecord;
import com.jellycat.dto.SystemConfig;
import com.jellycat.dto.TMDBMovieResult;
import com.jellycat.dto.TMDBSearchResp;
import com.jellycat.dto.TMDBTVResult;
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
        // private static final long MEDIA_FILE_SIZE_THRESHOLD = 0;

        private static final String MOVIE_DIR_NAME_STANDARD = "%s (%s)";
        private static final String MOVIE_NAME_STANDARD = "%s (%s) - %s.%s";
        private static final String TV_DIR_NAME_STANDARD = "%s (%d)/Season %02d";
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
                                        .filter(file -> isMediaFile(file) && file.length() >= MEDIA_FILE_SIZE_THRESHOLD
                                                        && isNotModifiedInLastMinute(file))
                                        .forEach(file -> {
                                                // preprocess media file name
                                                MedieFileRecord medieFileRecord = MedieFileUtils
                                                                .cleanFilename(file.getName());
                                                Optional<Path> targetFilePath;
                                                if (medieFileRecord.episode().isPresent()) {
                                                        // tv
                                                        targetFilePath = this.tvMediaHandle(medieFileRecord);
                                                } else {
                                                        // movie
                                                        targetFilePath = this.movieMediaHandle(medieFileRecord);
                                                }

                                                if (!targetFilePath.isPresent()) {
                                                        throw ExceptionUtils.buildException("handle media error");
                                                }

                                                if (Files.exists(targetFilePath.get())) {
                                                        return;
                                                }

                                                // move flag TODO
                                                boolean moveFlag = false;
                                                log.info("source:{} ---> target:{}", file.getAbsolutePath(),
                                                                targetFilePath.get().toString());
                                                try {
                                                        if (moveFlag) {
                                                                Files.move(file.toPath(), targetFilePath.get());
                                                        } else {
                                                                // create link
                                                                Files.createLink(targetFilePath.get(), file.toPath());
                                                        }
                                                } catch (IOException e) {
                                                        throw ExceptionUtils.buildException(log, e,
                                                                        "move media error");
                                                }
                                        });

                } catch (IOException e) {
                        log.error("organizeMedia task has an error", e);
                }

                log.info("end task: organizeMedia");
        }

        private Optional<Path> tvMediaHandle(MedieFileRecord medieFileRecord) {
                // search media with TMDB API
                TMDBSearchResp<TMDBTVResult> tmdbResp = tmdbApi.searchTV(medieFileRecord.name());

                if (Objects.isNull(tmdbResp)
                                || CollectionUtils.isEmpty(tmdbResp.results())) {
                        return Optional.empty();
                }
                // match
                TMDBTVResult result;
                if (StringUtils.hasText(medieFileRecord.year())) {
                        result = tmdbResp.results().stream().filter(e -> {
                                return medieFileRecord.year().equals(
                                                e.firstAirDate().substring(0, 4));
                        }).findFirst().orElse(null);
                } else {
                        result = tmdbResp.results().getFirst();
                }
                // create target directory
                Path targetDirPath = Paths.get(systemConfig.getTargetPath() + "/tv/"
                                + String.format(TV_DIR_NAME_STANDARD, result.name(),
                                                result.firstAirDate().substring(0, 4),
                                                medieFileRecord.season().orElse(1)));
                File targetDir = targetDirPath.toFile();
                if (!targetDir.exists()) {
                        targetDir.mkdirs();
                }
                String targetFileName = String
                                .format(TV_NAME_STANDARD, result.name(), medieFileRecord
                                                .season()
                                                .orElse(1),
                                                medieFileRecord.episode().get(), "",
                                                medieFileRecord.suffix());

                Path targetFilePath = Paths.get(targetDirPath.toString() + "/" + targetFileName);

                return Optional.of(targetFilePath);
        }

        private Optional<Path> movieMediaHandle(MedieFileRecord medieFileRecord) {
                TMDBSearchResp<TMDBMovieResult> tmdbResp = tmdbApi.searchMovie(medieFileRecord.name());

                if (Objects.isNull(tmdbResp)
                                || CollectionUtils.isEmpty(tmdbResp.results())) {
                        return Optional.empty();
                }
                // match
                TMDBMovieResult result;
                if (StringUtils.hasText(medieFileRecord.year())) {
                        result = tmdbResp.results().stream().filter(e -> {
                                return medieFileRecord.year().equals(
                                                e.releaseDate().substring(0, 4));
                        }).findFirst().orElse(tmdbResp.results().get(0));
                } else {
                        result = tmdbResp.results().getFirst();
                }
                // create target directory
                String year = result.releaseDate().substring(0, 4);
                Path targetDirPath = Paths.get(systemConfig.getTargetPath() + "/movie"
                                + "/" + String.format(MOVIE_DIR_NAME_STANDARD, result.title(),
                                                year));
                File targetDir = targetDirPath.toFile();
                if (!targetDir.exists()) {
                        targetDir.mkdirs();
                }
                String targetFileName = String.format(MOVIE_NAME_STANDARD, result.title(),
                                year, medieFileRecord.resolution(),
                                medieFileRecord.suffix());

                Path targetFilePath = Paths
                                .get(targetDirPath.toString() + "/" + targetFileName);

                return Optional.of(targetFilePath);
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

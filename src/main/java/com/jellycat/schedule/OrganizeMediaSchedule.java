package com.jellycat.schedule;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrganizeMediaSchedule {

    private static final String SEPARATOR_REGEX = "[\\.\\-_\\s]";
    private static final String YEAR_REGEX = "\\d{4}";
    private static final String RESOLUTION_REGEX = "\\d{3,4}p";
    private static final String BRACKET_REGEX = "\\(|\\)|【|】|［|］|\\[|\\]";

    private static final Pattern separatorPattern = Pattern.compile(SEPARATOR_REGEX);
    private static final Pattern yearPattern = Pattern.compile(YEAR_REGEX);
    private static final Pattern resolutionPattern = Pattern.compile(RESOLUTION_REGEX);
    private static final Pattern bracketPattern = Pattern.compile(BRACKET_REGEX);

    @Scheduled(fixedDelay = 60000)
    public void organizeMedia() {
        log.info("start task: organizeMedia");

        log.info("end task: organizeMedia");
    }

    static void preprocessMediaFileName(String mediaFileName) {
        // 使用Optional来处理可能为空的情况，避免空指针异常
        Optional<String> processedMediaFileName = Optional.ofNullable(mediaFileName)
                // 去除括号
                .map(name -> bracketPattern.matcher(name).replaceAll(""))
                // 分割文件名
                .map(name -> separatorPattern.split(name))
                .map(parts -> {
                    // 找到年份部分的索引，使用takeWhile方法
                    int index = Arrays.stream(parts).takeWhile(part -> !yearPattern.matcher(part).matches())
                            .count() < parts.length
                                    ? (int) Arrays.stream(parts).takeWhile(part -> !yearPattern.matcher(part).matches())
                                            .count()
                                    : -1;
                    // 获取第一个分辨率部分，使用findFirst方法
                    String resolution = Arrays.stream(parts).filter(part -> resolutionPattern.matcher(part).matches())
                            .findFirst().orElse("");
                    // 获取处理后的文件名部分，使用trim和replaceAll方法
                    String name = String.join(" ", index == -1 ? parts : Arrays.copyOfRange(parts, 0, index)).trim()
                            .replaceAll("\\s+", " ");
                    // 返回处理后的文件名，年份，和分辨率
                    return "Processed media file name: " + name + ", Year: " + (index == -1 ? "" : parts[index])
                            + ", Resolution: "
                            + resolution;
                });
        // 使用orElse方法提供一个默认值，如果处理后的文件名为空
        System.out.println(processedMediaFileName.orElse("Invalid media file name"));
    }
}

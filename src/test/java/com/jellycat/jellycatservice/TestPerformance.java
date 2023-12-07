package com.jellycat.jellycatservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestPerformance {

    @Test
    void testPerformance() throws IOException {
        // 创建测试数据
        Path targetPath = Files.createTempDirectory("test");
        Path linkPath = Files.createFile(targetPath.resolve("link.txt"));
        Path hardLink1 = targetPath.resolve("hardlink1.txt");
        Path hardLink2 = targetPath.resolve("hardlink2.txt");
        Path regularFile1 = Files.createFile(targetPath.resolve("regularfile1.txt"));
        Path regularFile2 = Files.createFile(targetPath.resolve("regularfile2.txt"));

        // 创建硬链接
        if (!Files.exists(hardLink1)) {
            Files.createLink(hardLink1, linkPath);
        }
        if (!Files.exists(hardLink2)) {
            Files.createLink(hardLink2, linkPath);
        }

        // 请求垃圾回收
        System.gc();

        // 测试 `Files.isSameFile()` 方法
        long isSameFileTime = measureTime(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    Files.isSameFile(linkPath, hardLink1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Files.isSameFile() 方法的执行时间：" + isSameFileTime + " 纳秒");

        // 请求垃圾回收
        System.gc();

        // 测试 `Files.getAttribute()` 方法
        long getAttributeTime = measureTime(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    Files.getAttribute(linkPath, "unix:ino");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Files.getAttribute() 方法的执行时间：" + getAttributeTime + " 纳秒");

        // 请求垃圾回收
        System.gc();

        // 测试 `Files.isRegularFile()` 方法
        long isRegularFileTime = measureTime(() -> {
            for (int i = 0; i < 10000; i++) {
                Files.isRegularFile(linkPath);
            }
        });

        System.out.println("Files.isRegularFile() 方法的执行时间：" + isRegularFileTime + " 纳秒");

        // 请求垃圾回收
        System.gc();

        // 删除测试数据
        try {
            Files.delete(linkPath);
            Files.delete(hardLink1);
            Files.delete(hardLink2);
            Files.delete(regularFile1);
            Files.delete(regularFile2);
            Files.delete(targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long measureTime(Runnable task) throws IOException {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }
}

package com.jellycat.vo;

public record ResponseVo<T>(int status, String msg, T data) {
}
package com.jellycat.dto;

public record SearchMultiReqRecord(String query, boolean include_adult, String language, int page) {

}

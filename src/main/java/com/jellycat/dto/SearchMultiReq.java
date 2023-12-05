package com.jellycat.dto;

import lombok.Data;

@Data
public class SearchMultiReq {
   private String query;
   private boolean include_adult;
   private String language;
   private int page;
}

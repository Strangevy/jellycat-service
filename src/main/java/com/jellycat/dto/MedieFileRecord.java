package com.jellycat.dto;

import java.util.Optional;

public record MedieFileRecord(String name, String year, String resolution, Optional<Integer> season, Optional<Integer> episode,String suffix ) {

}

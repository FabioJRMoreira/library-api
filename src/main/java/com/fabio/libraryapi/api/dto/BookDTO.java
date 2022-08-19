package com.fabio.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long    id;
    private String  title;
    private String  author;
    private String  isbn;

    public BookDTO(){}

}

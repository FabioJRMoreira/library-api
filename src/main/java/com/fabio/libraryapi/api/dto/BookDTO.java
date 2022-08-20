package com.fabio.libraryapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long    id;
    @NotEmpty
    private String  title;
    @NotEmpty
    private String  author;
    @NotEmpty
    private String  isbn;

    public BookDTO(){}

}

package com.fabio.libraryapi.api.resouce;

import com.fabio.libraryapi.api.dto.BookDTO;
import com.fabio.libraryapi.api.exception.ApiErros;
import com.fabio.libraryapi.entity.Book;
import com.fabio.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {


    private BookService service;
    private ModelMapper modelMapper;


    public BookController(BookService service,ModelMapper modelMapper){
        this.service=service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto){
        Book entity = modelMapper.map(dto,Book.class);
        entity = service.save(entity);
        return modelMapper.map(entity,BookDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationExceptions(MethodArgumentNotValidException ex){
        BindingResult bindingResult =ex.getBindingResult();
        return new ApiErros(bindingResult);
    }
}

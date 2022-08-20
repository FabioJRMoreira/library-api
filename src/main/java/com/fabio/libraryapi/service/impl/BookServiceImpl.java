package com.fabio.libraryapi.service.impl;

import com.fabio.libraryapi.entity.Book;
import com.fabio.libraryapi.model.repository.BookRepository;
import com.fabio.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {


    BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}

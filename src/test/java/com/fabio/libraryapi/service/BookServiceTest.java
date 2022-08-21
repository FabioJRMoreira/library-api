package com.fabio.libraryapi.service;

import com.fabio.libraryapi.entity.Book;
import com.fabio.libraryapi.exception.BusinessException;
import com.fabio.libraryapi.model.repository.BookRepository;
import com.fabio.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    //moca o comportamento do repository
    @MockBean
    BookRepository repository;

    //executa o metodo antes de cada teste
    @BeforeEach
    public void setUp(){
        this.service=new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //cenario
        Book book = createNewBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        //simulando o conportamento do repository
        Mockito.when(repository.save(book)).thenReturn(Book
                .builder()
                .id(1l)
                .isbn("123")
                .title("As aventuras")
                .author("Fulano")
                .build()
        );
        //execucao
        Book savedBook = service.save(book);
        //verificacao
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }



    @Test
    @DisplayName("Deve lancar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWinthDuplicatedISBN(){
        //CENARIO
        Book book = createNewBook();
        //quando meu repository executar o metodo passando qualquer string ele vai retornar true
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));
        //vefificacoes
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn ja cadastrado.");
        //nao pode salvar de forma alguma
        //verifica que meu repository nunca vai executar o save
        Mockito.verify(repository,Mockito.never()).save(book);


    }

    private Book createNewBook() {
        return Book.builder().isbn("123").title("As aventuras").author("Fulano").build();
    }

}

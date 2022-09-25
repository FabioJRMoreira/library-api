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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por id ")
    public void getByIdTest(){
        //cenario
        Long id =1l;
        Book book = createNewBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());

    }

    @Test
    @DisplayName("Deve retonar vazio ao obter um livro por id quando ele nao existe na base ")
    public void bookNorFoundByIdTest(){
        //cenario
        Long id =1l;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Book> book = service.getById(id);

        assertThat(book.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){
        //cenario
        Book book = createNewBook();
        book.setId(1l);
        //execucao
            //VERIFICA SE NAO TEVE NENHUM ERRO
            org.junit.jupiter.api.Assertions.assertDoesNotThrow(()->service.delete(book));
        //verificacao
        Mockito.verify(repository,Mockito.times(1)).delete(book);

    }
    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro enexistente")
    public void deleteInvalidBookTeste(){
        Book book = new Book();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->service.delete(book));
        Mockito.verify(repository,Mockito.never()).delete(book);
    }


    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro enexistente")
    public void updateInvalidBookTeste(){
        Book book = new Book();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->service.update(book));
        Mockito.verify(repository,Mockito.never()).save(book);
    }

    @Test
    @DisplayName("deve atualizar um livro")
    public void updateBookTest(){
        long id =1l;
        //livro a ser atualizado
        Book bookUpdating = Book.builder().id(id).build();
        //simulacao de livro atualizado
        Book bookAtualizado = createNewBook();
        bookAtualizado.setId(id);

        Mockito.when(repository.save(bookUpdating)).thenReturn(bookAtualizado);

        //execucao
        Book book= service.update(bookUpdating);

        //verificacoes
        assertThat(book.getId()).isEqualTo(bookAtualizado.getId());
        assertThat(book.getTitle()).isEqualTo(bookAtualizado.getTitle());
        assertThat(book.getAuthor()).isEqualTo(bookAtualizado.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(bookAtualizado.getIsbn());

    }

    @Test
    @DisplayName("Deve filtrar livro pelas propriedades")
    public void findBookTest(){
        //cenario
        Book book = createNewBook();
        PageRequest pageRequest = PageRequest.of(0,10);
        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest,1);
        Mockito.when(repository.findAll(Mockito.any(Example.class),Mockito.any(PageRequest.class)))
                .thenReturn(page);
        //execucacao
        Page<Book> result = service.find(book,pageRequest);
        //verificacao
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);


    }

    private Book createNewBook() {
        return Book.builder().isbn("123").title("As aventuras").author("Fulano").build();
    }

}

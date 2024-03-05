package com.alibou.security.book;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;

    public void save(BookRequest request) {
        var book = Book.builder()
                .id(request.getId())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .build();
        repository.save(book);
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Book update(Integer id, BookRequest request) {
        // TODO: define runtime exception
        var book = repository.findById(id).orElseThrow();
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        return repository.save(book);
    }
}

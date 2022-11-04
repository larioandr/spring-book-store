package com.example.bookstore.model;

import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ModelTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("basicH2PU");
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }

    @SneakyThrows
    @Test
    public void testDeletingReaderSetNullBookReaderField() {
        var formatter = new SimpleDateFormat("yyyy-MM-dd");

        // Create a reader and a couple of books, save them.
        var reader  = new Reader();
        reader.setName("Ivan Doe");
        reader.setLastBookTakenDate(new Date());

        var book1 = new Book();
        book1.setName("Timequake");
        book1.setAuthor("Kurt Vonnegut");
        book1.setPublishDate(formatter.parse("2000-01-01"));

        var book2 = new Book();
        book2.setName("War and Peace");
        book2.setAuthor("Leo Tolstoy");
        book2.setPublishDate(formatter.parse("1980-05-08"));

        persist(book1, book2);
        persist(reader);

        // Set reader for books
        book1 = em.find(Book.class, book1.getId());
        book1.setReader(reader);
        persist(book1);

        System.out.printf("Reader: ID=%d\n", reader.getId());
        System.out.printf("Book 1: ID=%d, reader=%s\n", book1.getId(), book1.getReader() == null ? "null" : book1.getReader().getName());
        System.out.printf("Book 2: ID=%d, reader=%s\n", book2.getId(), book2.getReader() == null ? "null" : book2.getReader().getName());

        System.out.println("---------- BEFORE DELETION -------");
        for (var book: listBooks()) {
            System.out.printf("%s\n", book.toString());
        }

        // Delete reader
        reader = em.find(Reader.class, reader.getId());
        em.getTransaction().begin();
        em.remove(reader);
        em.getTransaction().commit();
        em.clear();

        book1 = em.find(Book.class, book1.getId());
        book2 = em.find(Book.class, book2.getId());

        System.out.println("---------- AFTER DELETION -------");
        for (var book: listBooks()) {
            System.out.printf("%s\n", book.toString());
        }
    }

    private void persist(Object... models) {
        var tx = em.getTransaction();
        tx.begin();
        for (Object model: models) {
            em.persist(model);
        }
        tx.commit();
        em.clear();
    }

    private List<Book> listBooks() {
        return em.createQuery("SELECT book FROM Book book", Book.class).getResultList();
    }
}

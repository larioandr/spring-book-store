package com.example.bookstore.model;

import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ModelTest {

//    private EntityManagerFactory emf;
    @Autowired
    private TestEntityManager em;

    public static DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");

    @SneakyThrows
    @Test
    public void testCreateBook() {
        var book = new Book();
        book.setName("abc");
        book.setAuthor("def");
        book.setPublishDate(dateFmt.parse("2000-01-05"));
        em.persist(book);
//        em.persist();
    }


//    @SneakyThrows
//    @Test
//    public void testDeletingReaderSetNullBookReaderField() {
//        var formatter = new SimpleDateFormat("yyyy-MM-dd");
//
//        // Create a reader and a couple of books, save them.
//        var reader  = new Reader();
//        reader.setName("Ivan Doe");
//        reader.setLastBookTakenDate(new Date());
//
//        var book1 = new Book();
//        book1.setName("Timequake");
//        book1.setAuthor("Kurt Vonnegut");
//        book1.setPublishDate(formatter.parse("2000-01-01"));
//
//        var book2 = new Book();
//        book2.setName("War and Peace");
//        book2.setAuthor("Leo Tolstoy");
//        book2.setPublishDate(formatter.parse("1980-05-08"));
//
//        em.persist(book1);
//        em.persist(book2);
//        em.persist(reader);
//
////        persist(book1, book2);
////        persist(reader);
//
//        // Set reader for books
//        book1 = em.find(Book.class, book1.getId());
//        book1.setReader(reader);
//
//        em.persist(book1);
////        persist(book1);
//
//        System.out.printf("Reader: ID=%d\n", reader.getId());
//        System.out.printf("Book 1: ID=%d, reader=%s\n", book1.getId(), book1.getReader() == null ? "null" : book1.getReader().getName());
//        System.out.printf("Book 2: ID=%d, reader=%s\n", book2.getId(), book2.getReader() == null ? "null" : book2.getReader().getName());
//
//        System.out.println("---------- BEFORE DELETION -------");
//        for (var book: listBooks()) {
//            System.out.printf("%s\n", book.toString());
//        }
//
//        // Delete reader
//        reader = em.find(Reader.class, reader.getId());
//        em.remove(reader);
//        em.flush();
//
//        book1 = em.find(Book.class, book1.getId());
//        book2 = em.find(Book.class, book2.getId());
//
//        System.out.println("---------- AFTER DELETION -------");
//        for (var book: listBooks()) {
//            System.out.printf("%s\n", book.toString());
//        }
//    }
//
////    private void persist(Object... models) {
////        em.getEntityManager().getTransaction().begin();
////        for (Object model: models) {
////            em.persist(model);
////        }
////        em.getEntityManager().getTransaction().commit();
////        em.clear();
////    }
//
//    private List<Book> listBooks() {
//        return em.getEntityManager().createQuery("SELECT book FROM Book book", Book.class).getResultList();
//    }
}

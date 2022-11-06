package com.example.bookstore.model;

import net.sf.cglib.core.Local;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookTest {

    @Autowired
    private TestEntityManager em;

    private long warAndPeaceId;  // book id
    private long timequakeId;  // book id
    private long solarisId;  // book id

    private long ivanId;  // reader id

    @Before
    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        // Create some books
        warAndPeaceId = createBook("War and Peace", "Lev Tolstoy", "1868-01-01");
        timequakeId = createBook("Timequake", "Kurt Vonnegut", "1997-01-01");
        solarisId = createBook("Solaris", "Stanislaw Lem", "1961-01-01");

        // Create a reader
        ivanId = em.persistAndFlush(new Reader("Ivan")).getId();
        em.clear();
    }

    @Test
    public void testLoadBook() {
        var book = em.find(Book.class, warAndPeaceId);
        assertEquals("Lev Tolstoy", book.getAuthor());
        assertEquals("War and Peace", book.getTitle());
        assertEquals(LocalDate.parse("1868-01-01"), book.getPublishDate());
        assertNull(book.getReader());
        assertNull(book.getReaderUpdatedDate());
    }

    @Test
    public void testUpdateBook() {
        var book = em.find(Book.class, warAndPeaceId);

        book.setAuthor("Kurt Vonnegut");
        book.setTitle("Cat's cradle");
        book.setPublishDate(LocalDate.parse("1963-01-01"));
        em.flush();
        em.clear();

        var updatedBook = em.find(Book.class, warAndPeaceId);
        assertEquals("Kurt Vonnegut", updatedBook.getAuthor());
        assertEquals("Cat's cradle", updatedBook.getTitle());
        assertEquals(LocalDate.parse("1963-01-01"), updatedBook.getPublishDate());
        assertNull(updatedBook.getReader());
    }

    @Test
    public void testDeleteBook() {
        var book = em.find(Book.class, warAndPeaceId);

        em.remove(book);
        em.flush();
        em.clear();

        assertNull(em.find(Book.class, warAndPeaceId));

        var books = em.getEntityManager()
                .createQuery("SELECT b FROM Book b", Book.class)
                .getResultList();
        assertEquals(2, books.size());
    }

    @Test
    public void testSettingReaderUpdatesLastReaderUpdatedDate() {
        var book = em.find(Book.class, warAndPeaceId);
        assertNull(book.getReaderUpdatedDate());

        // Set the reader
        var reader = em.find(Reader.class, ivanId);
        book.setReader(reader);
        em.flush();
        em.clear();

        book = em.find(Book.class, warAndPeaceId);
        assertEquals(LocalDate.now(), book.getReaderUpdatedDate());
    }

    @Test
    public void testDeletingReaderSetNullBookReaderField() {
        var reader = em.find(Reader.class, ivanId);

        // Let Ivan take some science fiction
        List<Book> sciFiBooks = em.getEntityManager()
                .createQuery("SELECT b FROM Book b WHERE b.id IN (?1)", Book.class)
                .setParameter(1, Arrays.asList(solarisId, timequakeId))
                .getResultList();
        assertEquals(2, sciFiBooks.size());
        for (var b: sciFiBooks) {
            b.setReader(reader);
        }
        em.flush();
        em.clear();

        // Check that Ivan now read these two books:
        reader = em.find(Reader.class, reader.getId());
        assertEquals(new HashSet<>(sciFiBooks), reader.getAllBooksOnHands());
        assertEquals(reader, em.find(Book.class, timequakeId).getReader());
        assertEquals(reader, em.find(Book.class, solarisId).getReader());
        assertNull(em.find(Book.class, warAndPeaceId).getReader());
        em.clear();

        // Remove Ivan
        reader = em.find(Reader.class, ivanId);
        em.remove(reader);
        em.flush();
        em.clear();

        // Make sure that all books now don't have reader
        var allBooksList = em.getEntityManager()
                .createQuery("SELECT b FROM Book b", Book.class)
                .getResultList();
        assertEquals(3, allBooksList.size());
        for (var book: allBooksList) {
            assertNull(book.getReader());
        }
    }

    private Long createBook(String title, String author, String dateString) {
        var book = new Book(title, author, LocalDate.parse(dateString));
        em.persistAndFlush(book);
        return book.getId();
    }

}

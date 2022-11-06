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
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReaderTest {

    @Autowired
    private TestEntityManager em;

    private long ivanId;

    private long trialId;
    private long catsCradleId;
    private long solarisId;

    @Before
    public void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        // Create several books
        trialId = createBook("The Trial", "Franz Kafka", "1925-04-26");
        catsCradleId = createBook("Cat's cradle", "Kurt Vonnegut", "1963-01-01");
        solarisId = createBook("Solaris", "Stanislaw Lem", "1961-01-01");
        // Create reader
        ivanId = em.persistAndFlush(new Reader("Ivan")).getId();
        em.clear();
    }

    @Test
    public void testUpdateReader() {
        var reader = em.find(Reader.class, ivanId);
        assertEquals("Ivan", reader.getName());
        assertNull(reader.getLastBookTakenDate());

        reader.setName("Johan");
        reader.setLastBookTakenDate(LocalDate.parse("2022-05-04"));
        em.flush();
        em.clear();

        var updatedReader = em.find(Reader.class, ivanId);
        assertEquals("Johan", updatedReader.getName());
        assertEquals(LocalDate.parse("2022-05-04"), updatedReader.getLastBookTakenDate());
    }

    @Test
    public void testDeleteReader() {
        var reader = em.find(Reader.class, ivanId);
        assertNotNull(reader);
        em.remove(reader);
        em.flush();
        em.clear();

        assertEquals(0, listReaders().size());
    }

    @Test
    public void testLastBookTakenOnHandsDate() {
        // Let these be the readerUpdatedDate values
        var trialDate = LocalDate.parse("2022-05-30");
        var catsCradleDate = LocalDate.parse("2022-10-20");
        var solarisDate = LocalDate.parse("2021-01-02");

        // List books (manually, preserving order)
        var trial = em.find(Book.class, trialId);
        var catsCradle = em.find(Book.class, catsCradleId);
        var solaris = em.find(Book.class, solarisId);

        // Originally, Ivan doesn't have any books on hands
        var ivan = em.find(Reader.class, ivanId);
        assertNull(ivan.getLastBookTakenOnHandsDate());

        // Ivan decided to frustrate on reading Trial
        trial.setReader(ivan);
        trial.setReaderUpdatedDate(trialDate);
        em.flush();
        em.refresh(ivan);
        assertEquals(trialDate, ivan.getLastBookTakenOnHandsDate());

        // Afterwards, Ivan has taken Cat's Cradle for some apocalyptic fun
        catsCradle.setReader(ivan);
        catsCradle.setReaderUpdatedDate(catsCradleDate);
        em.flush();
        em.refresh(ivan);
        assertEquals(catsCradleDate, ivan.getLastBookTakenOnHandsDate());

        // We also remember, that on January holidays last year Ivan read Lem
        // (it shouldn't change lastBookTakenDate, since it is in past)
        solaris.setReader(ivan);
        solaris.setReaderUpdatedDate(solarisDate);
        em.flush();
        em.refresh(ivan);
        assertEquals(catsCradleDate, ivan.getLastBookTakenOnHandsDate());

        // Ivan gave back Cat's Cradle
        catsCradle.setReader(null);
        em.flush();
        em.refresh(ivan);
        assertEquals(2, ivan.getAllBooksOnHands().size());
        assertEquals(trialDate, ivan.getLastBookTakenOnHandsDate());

        // Ivan gave back Trial, now the last book is Solaris
        trial.setReader(null);
        em.flush();
        em.refresh(ivan);
        assertEquals(solarisDate, ivan.getLastBookTakenOnHandsDate());

        // Ivan finally gave back Solaris, so he doesn't have any books anymore
        solaris.setReader(null);
        em.flush();
        em.refresh(ivan);
        assertNull(ivan.getLastBookTakenOnHandsDate());
    }

    private List<Reader> listReaders() {
        return em.getEntityManager()
                .createQuery("SELECT r FROM Reader r", Reader.class)
                .getResultList();
    }

    private List<Book> listBooks() {
        return em.getEntityManager()
                .createQuery("SELECT b FROM Book b", Book.class)
                .getResultList();
    }

    private long createBook(String title, String author, String publishDate) {
        var book = new Book(title, author, LocalDate.parse(publishDate));
        return em.persistAndFlush(book).getId();
    }
}

package com.example.bookstore.model;

import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Table(name = "reader")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, message = "Every reader must have a name")
    @Setter
    private String name;


    /*
     * This field indicates the last date when the book was taken by this reader.
     * Since readers assignment to books is managed on the Book side,
     * we do NOT update this field automatically.
     *
     * It is expected that this field will be updated from the controller side.
     */
    @Column(name = "last_book_taken_date", columnDefinition = "DATE", nullable = true)
    @Setter
    private LocalDate lastBookTakenDate;

    /*
     * Computed field. Indicates the last date when any book that is currently
     * owned by the reader was taken.
     */
    @Formula("SELECT b.reader_updated_date " +
             "FROM book b " +
             "WHERE b.reader_id = id " +
             "ORDER BY b.reader_updated_date DESC " +
             "LIMIT 1")
    LocalDate lastBookTakenOnHandsDate;

    @OneToMany(mappedBy = "reader")
    private Set<Book> allBooksOnHands;

    public Reader(String name) {
        this.name = name;
    }

    @PreRemove
    public void preRemove() {
        for (var book: allBooksOnHands) {
            book.setReader(null);
        }
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hashCode(id);
        } else {
            return Objects.hash(name);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Reader) {
            var other = (Reader) obj;
            if (id == null) {
                return other.getId() == null && hashCode() == other.hashCode();
            } else {
                return other.getId() != null && id.equals(other.getId());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format(
                "<Reader #%d: name=\"%s\" lastBookTakenDate=%s>",
                id, name,
                lastBookTakenOnHandsDate == null ? "(null)" :
                        new SimpleDateFormat("yyyy-MM-dd").format(lastBookTakenOnHandsDate));
    }
}

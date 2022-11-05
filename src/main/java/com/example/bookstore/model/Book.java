package com.example.bookstore.model;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, message = "Every book must have a name")
    @Setter
    private String title;

    @NotNull
    @Column(name = "publish_date", columnDefinition = "DATE")
    @Setter
    private LocalDate publishDate;

    @Column(name = "reader_updated_date", columnDefinition = "DATE", nullable = true)
    @Setter(AccessLevel.PROTECTED)
    private LocalDate readerUpdatedDate;

    @NotNull
    @Size(min = 1, message = "Every book must have an author")
    @Setter
    private String author;

    @ManyToOne()
    @JoinColumn(name = "reader_id", nullable = true)
    private Reader reader;

    public Book(String title, String author, LocalDate publishDate) {
        this.title = title;
        this.author = author;
        this.publishDate = publishDate;
        this.reader = null;
        this.readerUpdatedDate = null;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
        this.readerUpdatedDate = LocalDate.now();
    }

    @Override
    public String toString() {
        return String.format(
                "<Book #%d: name=\"%s\" publishDate=%s author=\"%s\" reader=%s>",
                id,
                title,
                new SimpleDateFormat("yyyy-MM-dd").format(publishDate),
                author, reader == null ? "(null)" : reader.getName());
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hashCode(id);
        } else {
            return Objects.hash(title, publishDate, author);
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
        if (obj instanceof Book) {
            var other = (Book) obj;
            if (id != null) {
                return other.getId() != null && other.getId().equals(id);
            } else {
                return other.getId() == null && hashCode() == obj.hashCode();
            }
        };
        return false;
    }
}

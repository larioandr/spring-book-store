package com.example.bookstore.model;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "book")
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Size(min = 1, message = "Every book must have a name")
    private String name;

    @NotNull
    private Date publishDate;

    @NotNull
    @Size(min = 1, message = "Every book must have an author")
    private String author;

    @ManyToOne()
    @JoinColumn(name = "reader_id", nullable = true)
    private Reader reader;

    public String toString() {
        return String.format(
                "<Book #%d: name=\"%s\" publishDate=%s author=\"%s\" reader=%s>",
                id,
                name,
                new SimpleDateFormat("yyyy-MM-dd").format(publishDate),
                author, reader == null ? "(null)" : reader.getName());
    }
}

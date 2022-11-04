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
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "reader")
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Size(min = 1, message = "Every reader must have a name")
    private String name;

    Date lastBookTakenDate;

    @OneToMany(mappedBy = "reader")
    private Set<Book> books;

    public String toString() {
        return String.format(
                "<Reader #%d: name=\"%s\" lastBookTakenDate=%s>",
                id, name,
                lastBookTakenDate == null ? "(null)" :
                        new SimpleDateFormat("yyyy-MM-dd").format(lastBookTakenDate));
    }

    @PreRemove
    public void preRemove() {
        for (var book: books) {
            book.setReader(null);
        }
    }

    //    @Formula("SELECT ")
//    List<Book> allBooksOnHands;
}

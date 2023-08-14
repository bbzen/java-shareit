package ru.practicum.shareit.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @Column(name = "comment_text")
    private String text;
    @OneToOne
    @JoinColumn(name = "comment_item")
    private Item item;
    @OneToOne
    @JoinColumn(name = "comment_author")
    private User author;
    @Column(name = "comment_created")
    private LocalDateTime created;

    public Comment(String text, LocalDateTime created) {
        this.text = text;
        this.created = created;
    }
}

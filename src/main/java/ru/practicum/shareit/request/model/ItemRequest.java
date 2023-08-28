package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    @Column(name = "request_description")
    private String description;
    @Column(name = "requester")
    private Long requester;
    @Column(name = "request_created")
    private LocalDateTime created;
}

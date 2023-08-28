package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor

public class ItemRequestRespDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<Item> items;

    public ItemRequestRespDto(Long id, String description, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.created = created;
    }
}

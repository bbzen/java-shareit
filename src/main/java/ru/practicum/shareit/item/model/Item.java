package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private Long sharerUserId;
    private String name;
    private String description;
    private Boolean available;
    private String request;

    public Item(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}

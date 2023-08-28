package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class ItemIncomeDto {
    private String name;
    private String description;
    private Boolean available;
}

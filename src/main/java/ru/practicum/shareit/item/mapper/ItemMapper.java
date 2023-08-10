package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
        public static Item toItem(ItemDto itemDto) {
            return new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        }
}

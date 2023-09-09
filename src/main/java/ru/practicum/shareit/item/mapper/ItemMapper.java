package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInputDto;
import ru.practicum.shareit.item.model.ItemTransferDto;

public class ItemMapper {
        public static Item toItem(ItemInputDto itemInputDto) {
            return new Item(itemInputDto.getName(), itemInputDto.getDescription(), itemInputDto.getAvailable(), itemInputDto.getRequestId());
        }

        public static ItemTransferDto toTransferDto(Item item) {
            return new ItemTransferDto(item.getId(), item.getSharerUserId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequestId());
        }

    public static ItemInputDto toItemInputDto(Item item) {
            return new ItemInputDto(item.getName(), item.getDescription(), item.getAvailable(), item.getRequestId());
    }
}

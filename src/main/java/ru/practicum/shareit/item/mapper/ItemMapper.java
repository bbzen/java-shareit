package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemCommonDto;
import ru.practicum.shareit.item.model.ItemIncomeDto;
import ru.practicum.shareit.item.model.ItemTransferDto;

public class ItemMapper {
        public static Item toItem(ItemIncomeDto itemIncomeDto) {
            return new Item(itemIncomeDto.getName(), itemIncomeDto.getDescription(), itemIncomeDto.getAvailable());
        }

        public static ItemTransferDto toTransferDto(Item item) {
            return new ItemTransferDto(item.getId(), item.getSharerUserId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequest());
        }

    public static ItemCommonDto toItemCommon(Item item) {
        return new ItemCommonDto(item.getId(), item.getSharerUserId(), item.getName(), item.getDescription(), item.getAvailable(), item.getRequest());
    }
}

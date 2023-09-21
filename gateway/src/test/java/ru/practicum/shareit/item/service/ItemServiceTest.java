package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.ItemInvalidException;
import ru.practicum.shareit.item.model.ItemInputDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemServiceTest {
    private ItemService itemService;
    private ItemInputDto itemInputDto;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl();
        itemInputDto = new ItemInputDto("itemName", "itemDescription", true, 1L);
    }

    @Test
    public void doAllChecksNormal() {
                itemService.doAllChecks(itemInputDto);
    }

    @Test
    public void doAllChecksFailNullAvailable() {
        itemInputDto.setAvailable(null);
        Exception thrown = assertThrows(ItemInvalidException.class, () -> itemService.doAllChecks(itemInputDto));
        assertEquals("Для предмета " + itemInputDto.getName() + " не задан статус! Невозможно добавить предмет.", thrown.getMessage());
    }

    @Test
    public void doAllChecksFailNullName() {
        itemInputDto.setName(null);
        Exception thrown = assertThrows(ItemInvalidException.class, () -> itemService.doAllChecks(itemInputDto));
        assertEquals("Для предмета не задано название! Невозможно добавить предмет.", thrown.getMessage());
    }

    @Test
    public void doAllChecksFailBlancName() {
        itemInputDto.setName("");
        Exception thrown = assertThrows(ItemInvalidException.class, () -> itemService.doAllChecks(itemInputDto));
        assertEquals("Для предмета не задано название! Невозможно добавить предмет.", thrown.getMessage());
    }

    @Test
    public void doAllChecksFailNullDescription() {
        itemInputDto.setDescription(null);
        Exception thrown = assertThrows(ItemInvalidException.class, () -> itemService.doAllChecks(itemInputDto));
        assertEquals("Для предмета не задано описание! Невозможно добавить предмет.", thrown.getMessage());
    }
}
package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.ItemRequestBadRequestException;
import ru.practicum.shareit.request.model.ItemRequestInputDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemRequestServiceTest {
    private ItemRequestService itemRequestService;
    private ItemRequestInputDto itemRequestInputDto;
    private Integer from;
    private Integer size;

    @BeforeEach
    public void setUp() {
        from = 0;
        size = 5;
        itemRequestService = new ItemRequestServiceImpl();
        itemRequestInputDto = new ItemRequestInputDto("description");
    }

    @Test
    public void checkCreateParamNormal() {
        itemRequestService.checkCreateParam(itemRequestInputDto);
    }

    @Test
    public void checkCreateParamFailNullDescription() {
        itemRequestInputDto.setDescription(null);

        Exception thrown = assertThrows(ItemRequestBadRequestException.class, () -> itemRequestService.checkCreateParam(itemRequestInputDto));
        assertEquals("Описание запроса не может быть пустым.", thrown.getMessage());
    }

    @Test
    public void checkCreateParamFailBlancDescription() {
        itemRequestInputDto.setDescription("");

        Exception thrown = assertThrows(ItemRequestBadRequestException.class, () -> itemRequestService.checkCreateParam(itemRequestInputDto));
        assertEquals("Описание запроса не может быть пустым.", thrown.getMessage());
    }

    @Test
    public void checkGetParamsNormal() {
        itemRequestService.checkGetParams(from, size);
    }

    @Test
    public void checkGetParamsFailNegativeFrom() {
        from = -1;

        Exception thrown = assertThrows(ItemRequestBadRequestException.class, () -> itemRequestService.checkGetParams(from, size));
        assertEquals("Не верно заданы параметры поиска.", thrown.getMessage());

    }

    @Test
    public void checkGetParamsFailZeroSize() {
        size = 0;

        Exception thrown = assertThrows(ItemRequestBadRequestException.class, () -> itemRequestService.checkGetParams(from, size));
        assertEquals("Не верно заданы параметры поиска.", thrown.getMessage());

    }
}
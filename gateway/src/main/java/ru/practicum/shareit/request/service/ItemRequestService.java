package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequestInputDto;


public interface ItemRequestService {
    void checkCreateParam(ItemRequestInputDto itemRequestInputDto);

    void checkGetParams(Integer from, Integer size);
}

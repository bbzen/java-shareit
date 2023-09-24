package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequestRespDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestInputDto itemRequestInputDto, User itemRequester) {
        return new ItemRequest(itemRequestInputDto.getDescription(), itemRequester, LocalDateTime.now());
    }

    public static ItemRequestRespDto toItemReqRespDto(ItemRequest ir) {
        return new ItemRequestRespDto(ir.getId(), ir.getDescription(), ir.getCreated());
    }

    public static ItemRequestInputDto toItemRequestInputDto(ItemRequestRespDto irrDto) {
        return new ItemRequestInputDto(irrDto.getDescription());
    }
}

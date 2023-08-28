package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequestRespDto;

import java.util.List;


public interface ItemRequestService {

    ItemRequestRespDto createRequest(Long requesterUserId, ItemRequestInputDto itemRequestInputDto);

    List<ItemRequestRespDto> findOwnItemRequest(Long requesterUserId);

    ItemRequestRespDto findById(Long requesterUserId, Long requestId);

    List<ItemRequestRespDto> findAll(Long requesterUserId, Integer from, Integer size);
}

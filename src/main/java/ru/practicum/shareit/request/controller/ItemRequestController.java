package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestRespDto createItemRequest(@RequestHeader(value = X_SHARER_USER_ID) Long requesterUserId, @RequestBody ItemRequestInputDto itemRequestInputDto) {
        return itemRequestService.createRequest(requesterUserId, itemRequestInputDto);
    }

    @GetMapping
    public List<ItemRequestRespDto> findOwnerRequests(@RequestHeader(value = X_SHARER_USER_ID) Long requesterUserId) {
        return itemRequestService.findOwnItemRequest(requesterUserId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestRespDto findById(@RequestHeader(value = X_SHARER_USER_ID) Long userId, @PathVariable(required = false) Long requestId) {
        return itemRequestService.findById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestRespDto> findAllPaging(@RequestHeader(value = X_SHARER_USER_ID) Long requesterUserId, @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        return itemRequestService.findAll(requesterUserId, from, size);
    }
}

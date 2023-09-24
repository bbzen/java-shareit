package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.model.ItemRequestInputDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(value = X_SHARER_USER_ID) Long requesterUserId,
            @RequestBody ItemRequestInputDto itemRequestInputDto) {
        itemRequestService.checkCreateParam(itemRequestInputDto);
        return itemRequestClient.createItemRequest(requesterUserId, itemRequestInputDto);
    }

    @GetMapping
    public ResponseEntity<Object> findOwnerRequests(@RequestHeader(value = X_SHARER_USER_ID) Long requesterUserId) {
        return itemRequestClient.findOwnerRequests(requesterUserId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable(required = false) Long requestId) {
        return itemRequestClient.findById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllPaging(
            @RequestHeader(value = X_SHARER_USER_ID) Long requesterUserId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        itemRequestService.checkGetParams(from, size);
        return itemRequestClient.findAllPaging(requesterUserId, from, size);
    }
}

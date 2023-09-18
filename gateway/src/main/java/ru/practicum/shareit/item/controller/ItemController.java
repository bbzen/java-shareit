package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.model.CommentInputDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.model.ItemInputDto;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @RequestBody ItemInputDto itemInputDto) {
        itemService.doAllChecks(itemInputDto);
        return itemClient.createItem(sharerUserId, itemInputDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @PathVariable Long itemId, @RequestBody ItemInputDto item) {
        return itemClient.updateItem(sharerUserId, itemId, item);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAllMatchesText(@RequestParam("text") String text) {
        return itemClient.findAllMatchesText(text);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @PathVariable Long itemId) {
        return itemClient.findItem(sharerUserId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @PathVariable Long itemId, @RequestBody CommentInputDto commentDto) {
        return itemClient.saveComment(sharerUserId, itemId, commentDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUserItems(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId) {
        return itemClient.findAllUserItems(sharerUserId);
    }
}

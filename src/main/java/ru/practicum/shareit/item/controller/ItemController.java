package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.model.CommentInputDto;
import ru.practicum.shareit.comment.model.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInputDto;
import ru.practicum.shareit.item.model.ItemTransferDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private ItemService itemService;

    @PostMapping
    public Item createItem(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @RequestBody ItemInputDto itemInputDto) {
        return itemService.create(sharerUserId, itemInputDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @PathVariable Long itemId, @RequestBody ItemInputDto item) {
        return itemService.update(sharerUserId, itemId, item);
    }

    @GetMapping("/search")
    public List<Item> findAllMatchesText(@RequestParam("text") String text) {
        return itemService.findAllMatchesText(text);
    }

    @GetMapping("/{itemId}")
    public ItemTransferDto findItem(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @PathVariable Long itemId) {
        return itemService.findItem(sharerUserId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto saveComment(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @PathVariable Long itemId, @RequestBody CommentInputDto commentDto) {
        return itemService.saveCommentToItem(sharerUserId, itemId, commentDto);
    }

    @GetMapping
    public List<ItemTransferDto> findAllUserItems(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId) {
        return itemService.findAllUserItems(sharerUserId);
    }
}

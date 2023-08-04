package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item createItem(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @RequestBody ItemDto itemDto) {
        return itemService.createItem(sharerUserId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId, @PathVariable Long itemId, @RequestBody ItemDto item) {
        return itemService.updateItem(sharerUserId, itemId, item);
    }

    @GetMapping("/search")
    public List<Item> findAllMatchesText(@RequestParam("text") String text) {
        return itemService.findAllMatchesText(text);
    }

    @GetMapping("/{itemId}")
    public Item findItem(@PathVariable Long itemId) {
        return itemService.findItem(itemId);
    }

    @GetMapping
    public List<Item> findAllUserItems(@RequestHeader(value = SHARER_USER_ID_HEADER) Long sharerUserId) {
        return itemService.findAllUserItems(sharerUserId);
    }
}

package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item findItem(Long itemId);

    public List<Item> findAllUserItems(Long sharerUserId);

    public List<Item> findAllMatchText(String tag);

    Item updateItem(Item item);

    void removeItem(Long itemId);

    boolean containsItem(Long itemId);
}

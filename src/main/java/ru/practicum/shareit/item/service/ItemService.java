package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.ItemInvalidException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ItemService {
    private ItemStorage itemStorage;
    private UserStorage userStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public Item createItem(Long sharerUserId, ItemDto itemDto) {
        doAllChecks(itemDto);
        if (userStorage.containsUser(sharerUserId)) {
            Item item = ItemMapper.toItem(itemDto);
            item.setSharerUserId(sharerUserId);
            itemStorage.createItem(item);
            return item;
        }
        log.debug("Пользователь {} не найден! Невозможно добавить предмет.", sharerUserId);
        throw new UserNotFoundException("Пользователь " + sharerUserId + " не найден! Невозможно добавить предмет.");
    }

    public Item updateItem(Long sharerUserId, Long itemId, ItemDto itemDto) {
        Item updateItem = itemStorage.findItem(itemId);
        if (userStorage.containsUser(sharerUserId) && itemStorage.containsItem(itemId) && sharerUserId.equals(updateItem.getSharerUserId())) {
            if (itemDto.getAvailable() != null) {
                updateItem.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getName() != null) {
                updateItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                updateItem.setDescription(itemDto.getDescription());
            }
            itemStorage.updateItem(updateItem);
            return updateItem;
        }
        log.debug("Невозможно обновить предмет.");
        throw new UserNotFoundException("Невозможно обновить предмет.");
    }

    public Item findItem(Long itemId) {
        if (itemStorage.containsItem(itemId)) {
            return itemStorage.findItem(itemId);
        }
        log.debug("Предмета {} нет в базе.", itemId);
        throw new UserNotFoundException("Предмета " + itemId + " нет в базе.");
    }

    public List<Item> findAllUserItems(Long sharerUserId) {
        return itemStorage.findAllUserItems(sharerUserId);
    }

    public List<Item> findAllMatchesText(String tag) {
        return itemStorage.findAllMatchText(tag);
    }

    private boolean doAllChecks(ItemDto itemDto) {
        checkIsAvailable(itemDto);
        checkItemDescription(itemDto);
        checkItemName(itemDto);
        return true;
    }

    private boolean checkIsAvailable(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            log.debug("Для предмета {} не задан статус! Невозможно добавить предмет.", itemDto.getName());
            throw new ItemInvalidException("Для предмета " + itemDto.getName() + " не задан статус! Невозможно добавить предмет.");
        }
        return true;
    }

    private boolean checkItemName(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.debug("Для предмета не задано название! Невозможно добавить предмет.");
            throw new ItemInvalidException("Для предмета не задано название! Невозможно добавить предмет.");
        }
        return true;
    }

    private boolean checkItemDescription(ItemDto itemDto) {
        if (itemDto.getDescription() == null) {
            log.debug("Для предмета не задано описание! Невозможно добавить предмет.");
            throw new ItemInvalidException("Для предмета не задано описание! Невозможно добавить предмет.");
        }
        return true;
    }
}

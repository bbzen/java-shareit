package ru.practicum.shareit.item.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Repository
public class ItemStorageInMemory implements ItemStorage {
    private Long itemId;
    private Map<Long, Item> itemStorage;

    public ItemStorageInMemory() {
        this.itemId = 0L;
        this.itemStorage = new HashMap<>();
    }

    @Override
    public Item createItem(Item item) {
        item.setId(++itemId);
        itemStorage.put(itemId, item);
        return item;
    }

    @Override
    public Item findItem(Long itemId) {
        return itemStorage.get(itemId);
    }

    @Override
    public List<Item> findAllUserItems(Long sharerUserId) {
        return itemStorage.values().stream()
                .filter(o -> o.getSharerUserId().equals(sharerUserId))
                .collect(Collectors.toList());
    }

    public List<Item> findAllMatchText(String tag) {
        List<Item> nameSearch = new ArrayList<>();
        if (tag != null && !tag.isBlank()) {
            nameSearch.addAll(itemStorage.values().stream()
                    .filter(o -> o.getName().toLowerCase().contains(tag.toLowerCase()) && o.getAvailable())
                    .collect(Collectors.toList()));
            List<Item> descriptionSearch = itemStorage.values().stream()
                    .filter(o -> o.getDescription().toLowerCase().contains(tag.toLowerCase()) && o.getAvailable())
                    .collect(Collectors.toList());
            for (Item searchItem : descriptionSearch) {
                if (!nameSearch.contains(searchItem)) {
                    nameSearch.add(searchItem);
                }
            }
        }
        return nameSearch;
    }

    @Override
    public Item updateItem(Item item) {
        return itemStorage.put(item.getId(), item);
    }

    @Override
    public void removeItem(Long itemId) {
        itemStorage.remove(itemId);
    }

    @Override
    public boolean containsItem(Long itemId) {
        return itemStorage.containsKey(itemId);
    }
}

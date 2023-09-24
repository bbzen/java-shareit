package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User sharerUser;
    private User requesterUser;
    private Item itemSrc;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        sharerUser = new User("sharerUserName", "sharer@mail.ru");
        sharerUser = userRepository.save(sharerUser);
        requesterUser = new User("requesterUserName", "requester@mail.ru");
        requesterUser = userRepository.save(requesterUser);
        itemRequest = new ItemRequest("requestDescription", requesterUser, LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        itemSrc = new Item(1L, sharerUser.getId(), "itemOne", "itemOneDescription", true, itemRequest.getId());
        itemSrc = itemRepository.save(itemSrc);
    }

    @Test
    public void findAllByUserIdTest() {
        Item resultItem = itemRepository.save(itemSrc);
        List<Item> resultList = itemRepository.findAllByUserId(sharerUser.getId());

        assertEquals(resultItem.getId(), resultList.get(0).getId());
        assertEquals(resultItem.getSharerUserId(), resultList.get(0).getSharerUserId());
        assertEquals(resultItem.getName(), resultList.get(0).getName());
        assertEquals(resultItem.getDescription(), resultList.get(0).getDescription());
        assertEquals(resultItem.getAvailable(), resultList.get(0).getAvailable());
    }

    @Test
    public void findAllMatchStringDescOrderTest() {
        Item resultItem = itemRepository.save(itemSrc);
        List<Item> resultList = itemRepository.findAllMatchStringDescOrder(itemSrc.getDescription());

        assertEquals(resultItem.getId(), resultList.get(0).getId());
        assertEquals(resultItem.getSharerUserId(), resultList.get(0).getSharerUserId());
        assertEquals(resultItem.getName(), resultList.get(0).getName());
        assertEquals(resultItem.getDescription(), resultList.get(0).getDescription());
        assertEquals(resultItem.getAvailable(), resultList.get(0).getAvailable());
    }

    @Test
    public void findAllByRequestIdTest() {
        Item resultItem = itemRepository.save(itemSrc);
        List<Item> resultList = itemRepository.findAllByRequestId(itemRequest.getId());

        assertEquals(resultItem.getId(), resultList.get(0).getId());
        assertEquals(resultItem.getSharerUserId(), resultList.get(0).getSharerUserId());
        assertEquals(resultItem.getName(), resultList.get(0).getName());
        assertEquals(resultItem.getDescription(), resultList.get(0).getDescription());
        assertEquals(resultItem.getAvailable(), resultList.get(0).getAvailable());
    }
}
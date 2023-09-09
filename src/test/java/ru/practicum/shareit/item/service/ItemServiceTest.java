package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentResponseDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.model.ItemInvalidException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInputDto;
import ru.practicum.shareit.item.model.ItemTransferDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private static ItemRepository itemRepository;
    @Mock
    private static UserRepository userRepository;
    @Mock
    private static BookingRepository bookingRepository;
    @Mock
    private static CommentRepository commentRepository;
    @InjectMocks
    private static ItemService itemService;
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Booking bookingOne;
    private Booking bookingNext;
    private Booking bookingLast;
    private Comment commentOne;

    @BeforeAll
    public static void initial() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @BeforeEach
    public void setUp() {
        userOne = new User("OwnerUser", "owner@user.com");
        userOne.setId(1L);
        userTwo = new User("BookerUser", "booker@user.com");
        userTwo.setId(2L);
        itemOne = new Item(1L, 1L, "ItemOne", "ItemDescription", true, null);
        bookingOne = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemOne, userTwo, BookingStatus.APPROVED);
        bookingNext = new Booking(2L, LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(8), itemOne, userTwo, BookingStatus.APPROVED);
        bookingLast = new Booking(3L, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(1), itemOne, userTwo, BookingStatus.APPROVED);
        commentOne = new Comment(1L, "commentText", itemOne, userTwo, LocalDateTime.now().plusDays(3));
    }

    @Test
    public void createItem() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOne));
        when(itemRepository.save(Mockito.any()))
                .thenReturn(itemOne);

        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);
        Item result = itemService.create(userOne.getId(), itemInputDto);

        assertEquals(itemOne.getId(), result.getId());
        verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    public void createItemNoUserFail() {
        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);

        Exception thrown = assertThrows(UserNotFoundException.class, () -> itemService.create(itemOne.getId(), itemInputDto));

        assertEquals("Пользователь " + userOne.getId() + " не найден! Невозможно добавить предмет.", thrown.getMessage());
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    public void createItemNullAvailAbleFail() {
        itemOne.setAvailable(null);
        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);

        Exception thrown = assertThrows(ItemInvalidException.class, () -> itemService.create(itemOne.getId(), itemInputDto));

        assertEquals("Для предмета " + itemInputDto.getName() + " не задан статус! Невозможно добавить предмет.", thrown.getMessage());
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    public void createItemNullItemNameFail() {
        itemOne.setName(null);
        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);

        Exception thrown = assertThrows(ItemInvalidException.class, () -> itemService.create(itemOne.getId(), itemInputDto));

        assertEquals("Для предмета не задано название! Невозможно добавить предмет.", thrown.getMessage());
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    public void createItemBlancItemNameFail() {
        itemOne.setName("");
        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);

        Exception thrown = assertThrows(ItemInvalidException.class, () -> itemService.create(itemOne.getId(), itemInputDto));

        assertEquals("Для предмета не задано название! Невозможно добавить предмет.", thrown.getMessage());
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    public void createItemNullItemDescriptionFail() {
        itemOne.setDescription(null);
        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);

        Exception thrown = assertThrows(ItemInvalidException.class, () -> itemService.create(itemOne.getId(), itemInputDto));

        assertEquals("Для предмета не задано описание! Невозможно добавить предмет.", thrown.getMessage());
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    public void updateItem() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOne));
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemOne));
        when(itemRepository.save(Mockito.any()))
                .thenReturn(itemOne);

        String itemNameUpd = "itemUpdatedName";
        String itemDescriptionUpd = "itemUpdatedDescription";
        Long itemRequestIdUpd = 7L;
        Boolean itemAvailableUpdated = false;

        itemOne.setName(itemNameUpd);
        itemOne.setDescription(itemDescriptionUpd);
        itemOne.setRequestId(itemRequestIdUpd);
        itemOne.setAvailable(itemAvailableUpdated);

        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);
        Item result = itemService.update(userOne.getId(), itemOne.getId(), itemInputDto);
        assertEquals(itemOne.getId(), result.getId());
        assertEquals(itemNameUpd, result.getName());
        assertEquals(itemDescriptionUpd, result.getDescription());
        assertEquals(itemRequestIdUpd, result.getRequestId());
        assertEquals(itemAvailableUpdated, result.getAvailable());
        verify(itemRepository).save(Mockito.any(Item.class));
    }

    @Test
    public void updateItemNoItemFail() {
        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);

        Exception thrown = assertThrows(ItemNotFoundException.class, () -> itemService.update(userOne.getId(), itemOne.getId(), itemInputDto));

        assertEquals("Предмет " + itemOne.getId() + " не найден.", thrown.getMessage());
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    public void updateItemNoUserFail() {
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemOne));
        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);

        Exception thrown = assertThrows(UserNotFoundException.class, () -> itemService.update(userOne.getId(), itemOne.getId(), itemInputDto));

        assertEquals("Пользователь " + userOne.getId() + " не найден", thrown.getMessage());
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    public void updateItemNotSameUserFail() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userOne));
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemOne));
        ItemInputDto itemInputDto = ItemMapper.toItemInputDto(itemOne);

        Exception thrown = assertThrows(UserNotFoundException.class, () -> itemService.update(userTwo.getId(), itemOne.getId(), itemInputDto));

        assertEquals("Невозможно обновить предмет.", thrown.getMessage());
        verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    public void findItem() {
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemOne));
        when(commentRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(List.of(commentOne));
        when(bookingRepository.findNextBooking(Mockito.anyLong()))
                .thenReturn(bookingNext);
        when(bookingRepository.findLastBooking(Mockito.anyLong()))
                .thenReturn(bookingLast);

        ItemTransferDto result = itemService.findItem(userOne.getId(), itemOne.getId());

        assertEquals(itemOne.getId(), result.getId());
        assertEquals(itemOne.getSharerUserId(), result.getSharerUserId());
        assertEquals(itemOne.getName(), result.getName());
        assertEquals(itemOne.getDescription(), result.getDescription());
        assertEquals(bookingNext.getId(), result.getNextBooking().getId());
        assertEquals(bookingLast.getId(), result.getLastBooking().getId());
        assertEquals(commentOne.getId(), result.getComments().get(0).getId());
    }

    @Test
    public void findAllUserItemsNormal() {
        when(itemRepository.findAllByUserId(Mockito.anyLong()))
                .thenReturn(List.of(itemOne));
        when(commentRepository.findAllByItemId(Mockito.anyLong()))
                .thenReturn(List.of(commentOne));
        when(bookingRepository.findNextBooking(Mockito.anyLong()))
                .thenReturn(bookingNext);
        when(bookingRepository.findLastBooking(Mockito.anyLong()))
                .thenReturn(bookingLast);

        List<ItemTransferDto> result = itemService.findAllUserItems(userOne.getId());
        assertEquals(itemOne.getId(), result.get(0).getId());
        assertEquals(itemOne.getSharerUserId(), result.get(0).getSharerUserId());
        assertEquals(itemOne.getName(), result.get(0).getName());
        assertEquals(itemOne.getDescription(), result.get(0).getDescription());
        assertEquals(bookingNext.getId(), result.get(0).getNextBooking().getId());
        assertEquals(bookingLast.getId(), result.get(0).getLastBooking().getId());
        assertEquals(commentOne.getId(), result.get(0).getComments().get(0).getId());
    }

    @Test
    public void findAllMatchesText() {
        when(itemRepository.findAllMatchStringDescOrder(Mockito.anyString()))
                .thenReturn(List.of(itemOne));

        List<Item> result = itemService.findAllMatchesText(itemOne.getDescription());
        assertEquals(itemOne.getId(), result.get(0).getId());
        assertEquals(itemOne.getDescription(), result.get(0).getDescription());
    }

    @Test
    public void saveCommentToItem() {
        when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(itemOne));
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userTwo));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusApproved(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(bookingOne));
        when(commentRepository.save(Mockito.any()))
                .thenReturn(commentOne);

        CommentResponseDto result = itemService.saveCommentToItem(userTwo.getId(), itemOne.getId(), CommentMapper.toCommentInputDto(commentOne));
        assertEquals(commentOne.getId(), result.getId());
    }

    @Test
    public void saveCommentToItemNoBookingFail() {
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusApproved(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Collections.emptyList());

        Exception thrown = assertThrows(ItemInvalidException.class, () -> itemService.saveCommentToItem(userTwo.getId(), itemOne.getId(), CommentMapper.toCommentInputDto(commentOne)));
        assertEquals("Для предмета " + itemOne.getId() + " отсутствует заявка на бронирование! Невозможно добавить комментарий.", thrown.getMessage());
    }
}
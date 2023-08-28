package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentIncomeDto;
import ru.practicum.shareit.comment.model.CommentResponseDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.model.ItemInvalidException;
import ru.practicum.shareit.exception.model.ItemNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemIncomeDto;
import ru.practicum.shareit.item.model.ItemTransferDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    @Override
    public Item createItem(Long sharerUserId, ItemIncomeDto itemIncomeDto) {
        doAllChecks(itemIncomeDto);
        if (userRepository.findById(sharerUserId).isPresent()) {
            Item item = ItemMapper.toItem(itemIncomeDto);
            item.setSharerUserId(sharerUserId);
            itemRepository.save(item);
            return item;
        }
        log.debug("Пользователь {} не найден! Невозможно добавить предмет.", sharerUserId);
        throw new UserNotFoundException("Пользователь " + sharerUserId + " не найден! Невозможно добавить предмет.");
    }

    @Override
    public Item updateItem(Long sharerUserId, Long itemId, ItemIncomeDto itemIncomeDto) {
        Item updateItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет " + itemId + " не найден."));
        User sharer = userRepository.findById(sharerUserId).orElseThrow(() -> new UserNotFoundException("Пользователь " + sharerUserId + " не найден"));
        if (sharerUserId.equals(updateItem.getSharerUserId())) {
            if (itemIncomeDto.getAvailable() != null) {
                updateItem.setAvailable(itemIncomeDto.getAvailable());
            }
            if (itemIncomeDto.getName() != null) {
                updateItem.setName(itemIncomeDto.getName());
            }
            if (itemIncomeDto.getDescription() != null) {
                updateItem.setDescription(itemIncomeDto.getDescription());
            }
            itemRepository.save(updateItem);
            return updateItem;
        }
        log.debug("Невозможно обновить предмет.");
        throw new UserNotFoundException("Невозможно обновить предмет.");
    }

    @Override
    public ItemTransferDto findItem(Long sharerUserId, Long itemId) {
        ItemTransferDto current = ItemMapper.toTransferDto(itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмета " + itemId + " нет в базе.")));
        List<Comment> itemCommentsList = new ArrayList<>(commentRepository.findAllByItemId(itemId));
        current.setComments(itemCommentsList.stream().map(CommentMapper::toCommentRespDto).collect(Collectors.toList()));
        if (current.getSharerUserId().equals(sharerUserId)) {
            Booking nextBooking = bookingRepository.findNextBooking(itemId);
            Booking lastBooking = bookingRepository.findLastBooking(itemId);
            if (nextBooking != null) {
                current.setNextBooking(BookingMapper.toBookingShort(nextBooking));
            }
            if (lastBooking != null) {
                current.setLastBooking(BookingMapper.toBookingShort(lastBooking));
            }
        }
        return current;
    }

    @Override
    public List<ItemTransferDto> findAllUserItems(Long sharerUserId) {
        List<ItemTransferDto> currentITD = itemRepository.findAllByUserId(sharerUserId).stream().map(ItemMapper::toTransferDto).collect(Collectors.toList());
        for (ItemTransferDto itemDto : currentITD) {
            List<CommentResponseDto> commentRespDtos = commentRepository.findAllByItemId(itemDto.getId()).stream().map(CommentMapper::toCommentRespDto).collect(Collectors.toList());
            itemDto.setComments(commentRespDtos);
            Booking nextBooking = bookingRepository.findNextBooking(itemDto.getId());
            Booking lastBooking = bookingRepository.findLastBooking(itemDto.getId());
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingShort(nextBooking));
            }
            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingShort(lastBooking));
            }

        }
        return currentITD;
    }

    @Override
    public List<Item> findAllMatchesText(String tag) {
        return itemRepository.findAllMatchStringDescOrder(tag);
    }

    @Override
    public CommentResponseDto saveCommentToItem(Long userId, Long itemId, CommentIncomeDto commentIncomeDto) {
        if (!bookingRepository.existsByBookerIdAndItemIdAndStatusApproved(userId, itemId).isEmpty() && !commentIncomeDto.getText().isBlank()) {
            User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь " + userId + " не найден"));
            Item current = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет " + itemId + " не найден."));
            Comment result = CommentMapper.toComment(commentIncomeDto);
            result.setAuthor(booker);
            result.setItem(current);
            commentRepository.save(result);
            return CommentMapper.toCommentRespDto(result);
        }
        log.debug("Для предмета {} отсутствует заявка на бронирование! Невозможно добавить комментарий.", itemId);
        throw new ItemInvalidException("Для предмета " + itemId + " отсутствует заявка на бронирование! Невозможно добавить комментарий.");
    }

    private boolean doAllChecks(ItemIncomeDto itemIncomeDto) {
        checkIsAvailable(itemIncomeDto);
        checkItemDescription(itemIncomeDto);
        checkItemName(itemIncomeDto);
        return true;
    }

    private boolean checkIsAvailable(ItemIncomeDto itemIncomeDto) {
        if (itemIncomeDto.getAvailable() == null) {
            log.debug("Для предмета {} не задан статус! Невозможно добавить предмет.", itemIncomeDto.getName());
            throw new ItemInvalidException("Для предмета " + itemIncomeDto.getName() + " не задан статус! Невозможно добавить предмет.");
        }
        return true;
    }

    private boolean checkItemName(ItemIncomeDto itemIncomeDto) {
        if (itemIncomeDto.getName() == null || itemIncomeDto.getName().isBlank()) {
            log.debug("Для предмета не задано название! Невозможно добавить предмет.");
            throw new ItemInvalidException("Для предмета не задано название! Невозможно добавить предмет.");
        }
        return true;
    }

    private boolean checkItemDescription(ItemIncomeDto itemIncomeDto) {
        if (itemIncomeDto.getDescription() == null) {
            log.debug("Для предмета не задано описание! Невозможно добавить предмет.");
            throw new ItemInvalidException("Для предмета не задано описание! Невозможно добавить предмет.");
        }
        return true;
    }
}

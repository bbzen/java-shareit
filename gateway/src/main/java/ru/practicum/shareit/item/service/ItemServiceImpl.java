package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentInputDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Item create(Long sharerUserId, ItemInputDto itemInputDto) {
        doAllChecks(itemInputDto);
        if (userRepository.findById(sharerUserId).isPresent()) {
            Item item = ItemMapper.toItem(itemInputDto);
            item.setSharerUserId(sharerUserId);
            item.setId(itemRepository.save(item).getId());
            return item;
        }
        log.debug("Пользователь {} не найден! Невозможно добавить предмет.", sharerUserId);
        throw new UserNotFoundException("Пользователь " + sharerUserId + " не найден! Невозможно добавить предмет.");
    }

    @Override
    public Item update(Long sharerUserId, Long itemId, ItemInputDto itemInputDto) {
        Item updateItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет " + itemId + " не найден."));
        User sharer = userRepository.findById(sharerUserId).orElseThrow(() -> new UserNotFoundException("Пользователь " + sharerUserId + " не найден"));
        if (sharerUserId.equals(updateItem.getSharerUserId())) {
            if (itemInputDto.getAvailable() != null) {
                updateItem.setAvailable(itemInputDto.getAvailable());
            }
            if (itemInputDto.getName() != null) {
                updateItem.setName(itemInputDto.getName());
            }
            if (itemInputDto.getDescription() != null) {
                updateItem.setDescription(itemInputDto.getDescription());
            }
            if (itemInputDto.getRequestId() != null) {
                updateItem.setRequestId(itemInputDto.getRequestId());
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
                current.setNextBooking(BookingMapper.toBookingEntityDataKeeping(nextBooking));
            }
            if (lastBooking != null) {
                current.setLastBooking(BookingMapper.toBookingEntityDataKeeping(lastBooking));
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
                itemDto.setNextBooking(BookingMapper.toBookingEntityDataKeeping(nextBooking));
            }
            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingEntityDataKeeping(lastBooking));
            }

        }
        return currentITD;
    }

    @Override
    public List<Item> findAllMatchesText(String tag) {
        return itemRepository.findAllMatchStringDescOrder(tag);
    }

    @Override
    public CommentResponseDto saveCommentToItem(Long userId, Long itemId, CommentInputDto commentInputDto) {
        if (!bookingRepository.existsByBookerIdAndItemIdAndStatusApproved(userId, itemId).isEmpty() && !commentInputDto.getText().isBlank()) {
            User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь " + userId + " не найден"));
            Item current = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Предмет " + itemId + " не найден."));
            Comment result = CommentMapper.toComment(commentInputDto);
            result.setAuthor(booker);
            result.setItem(current);
            return CommentMapper.toCommentRespDto(commentRepository.save(result));
        }
        log.debug("Для предмета {} отсутствует заявка на бронирование! Невозможно добавить комментарий.", itemId);
        throw new ItemInvalidException("Для предмета " + itemId + " отсутствует заявка на бронирование! Невозможно добавить комментарий.");
    }

    private boolean doAllChecks(ItemInputDto itemInputDto) {
        checkIsAvailable(itemInputDto);
        checkItemDescription(itemInputDto);
        checkItemName(itemInputDto);
        return true;
    }

    private boolean checkIsAvailable(ItemInputDto itemInputDto) {
        if (itemInputDto.getAvailable() == null) {
            log.debug("Для предмета {} не задан статус! Невозможно добавить предмет.", itemInputDto.getName());
            throw new ItemInvalidException("Для предмета " + itemInputDto.getName() + " не задан статус! Невозможно добавить предмет.");
        }
        return true;
    }

    private boolean checkItemName(ItemInputDto itemInputDto) {
        if (itemInputDto.getName() == null || itemInputDto.getName().isBlank()) {
            log.debug("Для предмета не задано название! Невозможно добавить предмет.");
            throw new ItemInvalidException("Для предмета не задано название! Невозможно добавить предмет.");
        }
        return true;
    }

    private boolean checkItemDescription(ItemInputDto itemInputDto) {
        if (itemInputDto.getDescription() == null) {
            log.debug("Для предмета не задано описание! Невозможно добавить предмет.");
            throw new ItemInvalidException("Для предмета не задано описание! Невозможно добавить предмет.");
        }
        return true;
    }
}

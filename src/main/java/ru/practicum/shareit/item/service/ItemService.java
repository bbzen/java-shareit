package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.model.CommentIncomeDto;
import ru.practicum.shareit.comment.model.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemIncomeDto;
import ru.practicum.shareit.item.model.ItemTransferDto;

import java.util.List;

@Service
public interface ItemService {

    Item createItem(Long sharerUserId, ItemIncomeDto itemIncomeDto);

    Item updateItem(Long sharerUserId, Long itemId, ItemIncomeDto itemIncomeDto);

    ItemTransferDto findItem(Long sharerUserId, Long itemId);

    List<ItemTransferDto> findAllUserItems(Long sharerUserId);

    List<Item> findAllMatchesText(String tag);

    CommentResponseDto saveCommentToItem(Long userId, Long itemId, CommentIncomeDto commentIncomeDto);
}

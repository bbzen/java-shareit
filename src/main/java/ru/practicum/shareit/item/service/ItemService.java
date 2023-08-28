package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.model.CommentInputDto;
import ru.practicum.shareit.comment.model.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInputDto;
import ru.practicum.shareit.item.model.ItemTransferDto;

import java.util.List;

@Service
public interface ItemService {

    Item create(Long sharerUserId, ItemInputDto itemInputDto);

    Item update(Long sharerUserId, Long itemId, ItemInputDto itemInputDto);

    ItemTransferDto findItem(Long sharerUserId, Long itemId);

    List<ItemTransferDto> findAllUserItems(Long sharerUserId);

    List<Item> findAllMatchesText(String tag);

    CommentResponseDto saveCommentToItem(Long userId, Long itemId, CommentInputDto commentInputDto);
}

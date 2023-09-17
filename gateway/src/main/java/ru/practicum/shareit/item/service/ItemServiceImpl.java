package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.model.ItemInvalidException;
import ru.practicum.shareit.item.model.ItemInputDto;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private CommentRepository commentRepository;




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

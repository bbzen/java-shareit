package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.ItemRequestBadRequestException;
import ru.practicum.shareit.request.model.ItemRequestInputDto;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public void checkCreateParam(ItemRequestInputDto itemRequestInputDto) {
        if (itemRequestInputDto.getDescription() == null || itemRequestInputDto.getDescription().isBlank()) {
            log.debug("Описание запроса не может быть пустым.");
            throw new ItemRequestBadRequestException("Описание запроса не может быть пустым.");
        }
    }

    @Override
    public void checkGetParams(Integer from, Integer size) {
        if (from != null || size != null) {
            if (from < 0 || size <= 0) {
                log.debug("Не верно заданы параметры поиска.");
                throw new ItemRequestBadRequestException("Не верно заданы параметры поиска.");
            }
        }
    }
}

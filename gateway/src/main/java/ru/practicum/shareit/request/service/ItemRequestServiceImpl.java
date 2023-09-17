package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.ItemRequestBadRequestException;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

public void checkGetParams(int from, int size) {
    if (from < 0 || size <= 0) {
        log.debug("Не верно заданы параметры поиска.");
        throw new ItemRequestBadRequestException("Не верно заданы параметры поиска.");
    }
}
}

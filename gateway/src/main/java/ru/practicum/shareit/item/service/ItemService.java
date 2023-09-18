package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.ItemInputDto;

@Service
public interface ItemService {

    boolean doAllChecks(ItemInputDto itemInputDto);
}

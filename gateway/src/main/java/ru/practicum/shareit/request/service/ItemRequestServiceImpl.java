package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.ItemRequestBadRequestException;
import ru.practicum.shareit.exception.model.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.model.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequestRespDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Override
    public ItemRequestRespDto createRequest(Long requesterUserId, ItemRequestInputDto itemRequestInputDto) {
        User currentUser = userRepository.findById(requesterUserId).orElseThrow(() -> new UserNotFoundException("Пользователь " + requesterUserId + " не найден."));
        if (itemRequestInputDto.getDescription() == null || itemRequestInputDto.getDescription().isBlank()) {
            log.debug("Описание запроса не может быть пустым.");
            throw new ItemRequestBadRequestException("Описание запроса не может быть пустым.");
        }
        ItemRequest resultIR = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestInputDto, currentUser));
        return ItemRequestMapper.toItemReqRespDto(resultIR);
    }

    @Override
    public List<ItemRequestRespDto> findOwnItemRequest(Long requesterUserId) {
        checkUserExists(requesterUserId);
        return itemRequestRepository.findAllByRequesterId(requesterUserId)
                .stream()
                .map(ItemRequestMapper::toItemReqRespDto)
                .peek(i -> {
                    List<Item> itemsByRequest = itemRepository.findAllByRequestId(i.getId());
                    i.setItems(itemsByRequest);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestRespDto findById(Long requesterUserId, Long requestId) {
        checkUserExists(requesterUserId);
            ItemRequest currentIr = itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException("Запрос предмета " + requestId + " не найден"));
            ItemRequestRespDto resultIrRespDto = ItemRequestMapper.toItemReqRespDto(currentIr);
            resultIrRespDto.setItems(itemRepository.findAllByRequestId(requestId));
            return resultIrRespDto;
    }

    @Override
    public List<ItemRequestRespDto> findAll(Long requesterUserId, Integer from, Integer size) {
        checkUserExists(requesterUserId);
        if (from == null && size == null) {
            return itemRequestRepository.findAll().stream()
                    .map(ItemRequestMapper::toItemReqRespDto)
                    .peek(i -> i.setItems(itemRepository.findAllByRequestId(i.getId())))
                    .collect(Collectors.toList());
        }
        if (from < 0 || size <= 0) {
            log.debug("Не верно заданы параметры поиска.");
            throw new ItemRequestBadRequestException("Не верно заданы параметры поиска.");
        }
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(Math.abs(from / size), size, sortById);
        return itemRequestRepository.findAll(page).stream()
                .filter(i -> !i.getRequester().getId().equals(requesterUserId))
                .map(ItemRequestMapper::toItemReqRespDto)
                .peek(i -> i.setItems(itemRepository.findAllByRequestId(i.getId())))
                .collect(Collectors.toList());
    }

    private void checkUserExists(Long requesterUserId) {
        if (!userRepository.existsById(requesterUserId)) {
            throw new UserNotFoundException("Пользователь " + requesterUserId + " не найден.");
        }
    }
}

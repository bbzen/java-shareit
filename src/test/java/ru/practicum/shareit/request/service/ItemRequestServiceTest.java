package ru.practicum.shareit.request.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    ItemRequestService itemRequestService;
    private final EasyRandom generator = new EasyRandom();
    private User userRequester;
    private ItemRequestInputDto itemRequestInputDto;

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        userRequester = generator.nextObject(User.class);
        itemRequestInputDto = generator.nextObject(ItemRequestInputDto.class);
    }

    @Test
    public void createRequestNormal() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userRequester));
        when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(ItemRequestMapper.toItemRequest(itemRequestInputDto, userRequester));

        ItemRequestRespDto result = itemRequestService.createRequest(userRequester.getId(), itemRequestInputDto);

        assertEquals(itemRequestInputDto.getDescription(), result.getDescription());
        assertNotNull(result.getCreated());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    public void createRequestNoUserFail() {
        Exception thrown = assertThrows(UserNotFoundException.class, () -> itemRequestService.createRequest(userRequester.getId(), itemRequestInputDto));

        assertEquals("Пользователь " + userRequester.getId() + " не найден.", thrown.getMessage());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    public void createRequestNullDescriptionFail() {
        itemRequestInputDto.setDescription(null);
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userRequester));

        Exception thrown = assertThrows(ItemRequestBadRequestException.class, () -> itemRequestService.createRequest(userRequester.getId(), itemRequestInputDto));

        assertEquals("Описание запроса не может быть пустым.", thrown.getMessage());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    public void createRequestBlancDescriptionFail() {
        itemRequestInputDto.setDescription("");
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(userRequester));

        Exception thrown = assertThrows(ItemRequestBadRequestException.class, () -> itemRequestService.createRequest(userRequester.getId(), itemRequestInputDto));

        assertEquals("Описание запроса не может быть пустым.", thrown.getMessage());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    public void findOwnItemRequest() {
        List<ItemRequest> listIR = generator.objects(ItemRequest.class, 5).collect(Collectors.toList());
        List<ItemRequestRespDto> srcList = listIR.stream().map(ItemRequestMapper::toItemReqRespDto).collect(Collectors.toList());
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findAllByRequesterId(Mockito.anyLong()))
                .thenReturn(listIR);

        List<ItemRequestRespDto> resultList = itemRequestService.findOwnItemRequest(userRequester.getId());

        assertEquals(srcList.stream().map(ItemRequestRespDto::getDescription).collect(Collectors.toList()).toString(),
                resultList.stream().map(ItemRequestRespDto::getDescription).collect(Collectors.toList()).toString());
    }

    @Test
    public void findOwnItemRequestNoUserFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        Exception thrown = assertThrows(UserNotFoundException.class, () -> itemRequestService.findOwnItemRequest(userRequester.getId()));

        assertEquals("Пользователь " + userRequester.getId() + " не найден.", thrown.getMessage());

    }

    @Test
    public void findById() {
        ItemRequest ir = generator.nextObject(ItemRequest.class);
        List<Item> itemsList = generator.objects(Item.class, 5).collect(Collectors.toList());
        ItemRequestRespDto srcIRRDto = ItemRequestMapper.toItemReqRespDto(ir);
        srcIRRDto.setItems(itemsList);
        when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(ir));
        when(itemRepository.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(itemsList);
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        ItemRequestRespDto result = itemRequestService.findById(userRequester.getId(), ir.getId());
        assertEquals(srcIRRDto, result);
    }

    @Test
    public void findByIdNoIRFail() {
        ItemRequest ir = generator.nextObject(ItemRequest.class);
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Exception thrown = assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.findById(userRequester.getId(), ir.getId()));
        assertEquals("Запрос предмета " + ir.getId() + " не найден", thrown.getMessage());
    }

    @Test
    public void findAllPageable() {
        List<ItemRequest> listIR = generator.objects(ItemRequest.class, 5).collect(Collectors.toList());
        List<ItemRequestRespDto> srcList = listIR.stream().map(ItemRequestMapper::toItemReqRespDto).collect(Collectors.toList());
        Page<ItemRequest> page = new PageImpl<>(listIR, PageRequest.of(0, 5), 10);
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        List<ItemRequestRespDto> resultList = itemRequestService.findAll(userRequester.getId(), 0, 5);
        assertEquals(srcList.stream().map(ItemRequestRespDto::getDescription).collect(Collectors.toList()).toString(),
                resultList.stream().map(ItemRequestRespDto::getDescription).collect(Collectors.toList()).toString());
    }

    @Test
    public void findAllNormal() {
        List<ItemRequest> listIR = generator.objects(ItemRequest.class, 5).collect(Collectors.toList());
        List<ItemRequestRespDto> srcList = listIR.stream().map(ItemRequestMapper::toItemReqRespDto).collect(Collectors.toList());
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findAll())
                .thenReturn(listIR);

        List<ItemRequestRespDto> resultList = itemRequestService.findAll(userRequester.getId(), null, null);
        assertEquals(srcList.stream().map(ItemRequestRespDto::getDescription).collect(Collectors.toList()).toString(),
                resultList.stream().map(ItemRequestRespDto::getDescription).collect(Collectors.toList()).toString());
    }

    @Test
    public void findAllInvalidFromFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Exception thrown = assertThrows(ItemRequestBadRequestException.class, () -> itemRequestService.findAll(userRequester.getId(), -1, null));
        assertEquals("Не верно заданы параметры поиска.", thrown.getMessage());
    }

    @Test
    public void findAllInvalidSizeFail() {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Exception thrown = assertThrows(ItemRequestBadRequestException.class, () -> itemRequestService.findAll(userRequester.getId(), 0, -5));
        assertEquals("Не верно заданы параметры поиска.", thrown.getMessage());
    }
}
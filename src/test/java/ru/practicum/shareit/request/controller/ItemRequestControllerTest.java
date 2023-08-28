package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequestInputDto;
import ru.practicum.shareit.request.model.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final EasyRandom generator = new EasyRandom();
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void createItemRequest() throws Exception {
        User user = generator.nextObject(User.class);
        ItemRequestRespDto itemRequestRespDto = generator.nextObject(ItemRequestRespDto.class);
        ItemRequestInputDto itemRequestInputDto = ItemRequestMapper.toItemRequestInputDto(itemRequestRespDto);
        when(itemRequestService.createRequest(Mockito.anyLong(), Mockito.any(ItemRequestInputDto.class)))
                .thenReturn(itemRequestRespDto);

        mvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, user.getId())
                        .content(mapper.writeValueAsString(itemRequestInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestRespDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestRespDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(DateTimeFormatter.ISO_DATE_TIME.format(itemRequestRespDto.getCreated()))))
                .andExpect(jsonPath("$.items.[0].id", is(itemRequestRespDto.getItems().get(0).getId())))
                .andReturn();
    }

    @Test
    public void findOwnerRequests() throws Exception {
        List<ItemRequestRespDto> irList = generator.objects(ItemRequestRespDto.class, 5).collect(Collectors.toList());
        User user = generator.nextObject(User.class);
        when(itemRequestService.findOwnItemRequest(Mockito.anyLong()))
                .thenReturn(irList);

        mvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id", is(irList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(irList.get(1).getDescription()), String.class))
                .andExpect(jsonPath("$[1].created", is(irList.get(1).getCreated().toString())))
                .andExpect(jsonPath("$[1].items.[0].id", is(irList.get(1).getItems().get(0).getId())))
                .andReturn();
    }

    @Test
    public void findById() throws Exception {
        User user = generator.nextObject(User.class);
        ItemRequestRespDto itemRequestRespDto = generator.nextObject(ItemRequestRespDto.class);
        when(itemRequestService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestRespDto);

        mvc.perform(get("/requests/{requestId}", itemRequestRespDto.getId())
                        .header(X_SHARER_USER_ID, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestRespDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestRespDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(itemRequestRespDto.getCreated().toString())))
                .andExpect(jsonPath("$.items.[0].id", is(itemRequestRespDto.getItems().get(0).getId())))
                .andReturn();
    }

    @Test
    public void findAllPaging() throws Exception {
        List<ItemRequestRespDto> irList = generator.objects(ItemRequestRespDto.class, 5).collect(Collectors.toList());
        User user = generator.nextObject(User.class);
        when(itemRequestService.findAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(irList);

        mvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, user.getId())
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id", is(irList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(irList.get(1).getDescription()), String.class))
                .andExpect(jsonPath("$[1].created", is(irList.get(1).getCreated().toString())))
                .andExpect(jsonPath("$[1].items.[0].id", is(irList.get(1).getItems().get(0).getId())))
                .andReturn();
    }
}
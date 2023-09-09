package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentInputDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemInputDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    public static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Comment commentOne;

    @BeforeEach
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());
        userOne = new User("OwnerUser", "owner@user.com");
        userOne.setId(1L);
        userTwo = new User("BookerUser", "booker@user.com");
        userTwo.setId(2L);
        itemOne = new Item(1L, 1L, "ItemOne", "ItemDescription", true, null);
        commentOne = new Comment(1L, "commentText", itemOne, userTwo, LocalDateTime.now());
    }

    @Test
    public void createItem() throws Exception {
        when(itemService.create(Mockito.anyLong(), Mockito.any(ItemInputDto.class)))
                .thenReturn(itemOne);

        mvc.perform(post("/items").header("X-Sharer-User-Id", userOne.getId())
                        .content(mapper.writeValueAsString(ItemMapper.toItemInputDto(itemOne)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemOne.getId()), Long.class))
                .andExpect(jsonPath("name", is(itemOne.getName())))
                .andReturn();
    }

    @Test
    public void updateItem() throws Exception {
        when(itemService.update(Mockito.anyLong(), Mockito.anyLong(),any(ItemInputDto.class)))
                .thenReturn(itemOne);

        mvc.perform(patch("/items/{itemId}", itemOne.getId())
                        .header("X-Sharer-User-Id", userOne.getId())
                        .content(mapper.writeValueAsString(ItemMapper.toItemInputDto(itemOne)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemOne.getId()), Long.class))
                .andExpect(jsonPath("name", is(itemOne.getName())))
                .andReturn();

        itemOne.setName("updatedName");
        itemOne.setDescription("updatedDescription");

        mvc.perform(patch("/items/{itemId}", itemOne.getId())
                        .header("X-Sharer-User-Id", userOne.getId())
                        .content(mapper.writeValueAsString(ItemMapper.toItemInputDto(itemOne)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemOne.getId()), Long.class))
                .andExpect(jsonPath("name", is(itemOne.getName())))
                .andReturn();
    }

    @Test
    public void findAllMatchesText() throws Exception {
        when(itemService.findAllMatchesText(Mockito.anyString()))
                .thenReturn(List.of(itemOne));

        mvc.perform(get("/items/search")
                        .param("text", itemOne.getDescription()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemOne.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemOne.getDescription())))
                .andExpect(jsonPath("$[0].name", is(itemOne.getName())))
                .andReturn();
    }

    @Test
    public void findItem() throws Exception {
        when(itemService.findItem(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(ItemMapper.toTransferDto(itemOne));

        mvc.perform(get("/items/{itemId}", itemOne.getId())
                .header("X-Sharer-User-Id", userOne.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(itemOne.getId()), Long.class))
                .andExpect(jsonPath("name", is(itemOne.getName())))
                .andReturn();
    }

    @Test
    public void saveComment() throws Exception {
        when(itemService.saveCommentToItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentInputDto.class)))
                .thenReturn(CommentMapper.toCommentRespDto(commentOne));

        mvc.perform(post("/items/{itemId}/comment", itemOne.getId())
                        .header("X-Sharer-User-Id", userTwo.getId())
                        .content(mapper.writeValueAsString(CommentMapper.toCommentInputDto(commentOne)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(commentOne.getId()), Long.class))
                .andExpect(jsonPath("text", is(commentOne.getText())))
                .andReturn();
    }

    @Test
    public void findAllUserItems() throws Exception {
        when(itemService.findAllUserItems(Mockito.anyLong()))
                .thenReturn(List.of(ItemMapper.toTransferDto(itemOne)));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userOne.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemOne.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemOne.getDescription())))
                .andExpect(jsonPath("$[0].name", is(itemOne.getName())))
                .andReturn();
    }
}
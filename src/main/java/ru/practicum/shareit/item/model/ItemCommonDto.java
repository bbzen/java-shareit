package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

@Data
public class ItemCommonDto {
        private Long id;
        private Long sharerUserId;
        private String name;
        private String description;
        private Boolean available;
        private Long request;
        private List<Comment> comments;

        public ItemCommonDto(Long id, Long sharerUserId, String name, String description, Boolean available, Long request) {
            this.id = id;
            this.sharerUserId = sharerUserId;
            this.name = name;
            this.description = description;
            this.available = available;
            this.request = request;
        }
    }

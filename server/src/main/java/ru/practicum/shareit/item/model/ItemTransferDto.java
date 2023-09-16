package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingEntityDataKeeping;
import ru.practicum.shareit.comment.model.CommentResponseDto;

import java.util.List;

@Data
public class ItemTransferDto {
    private Long id;
    private Long sharerUserId;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private BookingEntityDataKeeping lastBooking;
    private BookingEntityDataKeeping nextBooking;
    private List<CommentResponseDto> comments;

    public ItemTransferDto(Long id, Long sharerUserId, String name, String description, Boolean available, Long request) {
        this.id = id;
        this.sharerUserId = sharerUserId;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}

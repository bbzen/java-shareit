package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select * from items as it where sharer_user_id = ?1  order by item_id asc", nativeQuery = true)
    List<Item> findAllByUserId(Long sharerUserId);

    @Query(value = "SELECT * FROM items i WHERE (lower(item_name) LIKE lower(concat('%',?1,'%')) OR lower(item_description) LIKE lower(concat('%',?1,'%'))) and item_available  = true AND (CASE WHEN ?1 like '' THEN false ELSE true END)", nativeQuery = true)
    List<Item> findAllMatchStringDescOrder(String tag);

    List<Item> findAllByRequestId(Long itemRequestId);
}


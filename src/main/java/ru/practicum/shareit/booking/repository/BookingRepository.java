package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT * FROM booking WHERE booker = ?1 ORDER BY booking_id DESC", nativeQuery = true)
    List<Booking> findAllByBookerIdAll(Long userId);

    @Query(value = "SELECT * FROM booking WHERE booker = ?1 AND status = ?2 ORDER BY booking_id DESC", nativeQuery = true)
    List<Booking> findAllByBookerAndStatus(Long userId, String state);

    @Query(value = "SELECT * FROM booking WHERE booker = ?1 AND CURRENT_TIMESTAMP < booking_start ORDER BY booking_id DESC", nativeQuery = true)
    List<Booking> findAllByBookerFuture(Long userId);

    @Query(value = "SELECT * FROM booking WHERE booker = ?1 AND booking_end < CURRENT_TIMESTAMP ORDER BY booking_id DESC", nativeQuery = true)
    List<Booking> findAllByBookerPast(Long userId);

    @Query(value = "SELECT * FROM booking WHERE booker = ?1 AND booking_start < CURRENT_TIMESTAMP and CURRENT_TIMESTAMP < booking_end ORDER BY booking_id asc;", nativeQuery = true)
    List<Booking> findAllByBookerCurrent(Long userId);

    @Query(value = "SELECT * FROM booking WHERE booking_item IN (SELECT item_id FROM items WHERE sharer_user_id = ?1) ORDER BY booking_id DESC", nativeQuery = true)
    List<Booking> findAllBySharerUserId(Long userId);

    @Query(value = "SELECT * FROM booking WHERE booking_item IN (SELECT item_id FROM items WHERE sharer_user_id = ?1) AND CURRENT_TIMESTAMP < booking_start ORDER BY booking_id DESC", nativeQuery = true)
    List<Booking> findAllBySharerUserIdFuture(Long userId);

    @Query(value = "SELECT * FROM booking WHERE booking_item IN (SELECT item_id FROM items WHERE sharer_user_id = ?1) AND booking_end < CURRENT_TIMESTAMP ORDER BY booking_id DESC", nativeQuery = true)
    List<Booking> findAllBySharerUserIdPast(Long userId);

    @Query(value = "SELECT * FROM booking WHERE booking_item IN (SELECT item_id FROM items WHERE sharer_user_id = ?1) AND status = ?2 ORDER BY booking_id DESC", nativeQuery = true)
    List<Booking> findAllBySharerUserIdAndStatus(Long userId, String state);

    @Query(value = "SELECT * FROM booking WHERE booking_item IN (SELECT item_id FROM items WHERE sharer_user_id = ?1) AND booking_start < CURRENT_TIMESTAMP and CURRENT_TIMESTAMP < booking_end ORDER BY booking_id DESC", nativeQuery = true)
    List<Booking> findAllBySharerUserIdCurrent(Long userId);

    @Query(value = "SELECT * FROM booking where booking_item = ?1 AND current_timestamp < booking_start AND status = 'APPROVED' ORDER BY booking_start asc LIMIT 1", nativeQuery = true)
    Booking findNextBooking(Long itemId);

    @Query(value = "SELECT * FROM booking WHERE booking_item = ?1 AND booking_start < CURRENT_TIMESTAMP AND status = 'APPROVED' ORDER BY booking_start desc LIMIT 1", nativeQuery = true)
    Booking findLastBooking(Long itemId);

    @Query(value = "SELECT * FROM booking WHERE booker = ?1 and booking_item = ?2 AND status = 'APPROVED' GROUP BY booking_id HAVING MAX(booking_end) < CURRENT_TIMESTAMP ORDER BY booking_id ASC", nativeQuery = true)
    List<Booking> existsByBookerIdAndItemIdAndStatusApproved(Long bookerId, Long itemId);
}
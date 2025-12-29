package com.yeditepe.bookingservice.repository;

import com.yeditepe.bookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventId(String eventId);

    List<Booking> findByStatus(com.yeditepe.bookingservice.entity.BookingStatus status);

    List<Booking> findByUserIdAndStatus(Long userId, com.yeditepe.bookingservice.entity.BookingStatus status);
}
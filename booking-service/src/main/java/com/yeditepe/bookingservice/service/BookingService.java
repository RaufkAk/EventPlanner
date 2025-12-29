package com.yeditepe.bookingservice.service;

import com.yeditepe.bookingservice.client.EventServiceClient;
import com.yeditepe.bookingservice.client.PaymentServiceClient;
import com.yeditepe.bookingservice.client.UserServiceClient;
import com.yeditepe.bookingservice.dto.BookingRequest;
import com.yeditepe.bookingservice.dto.BookingResponse;
import com.yeditepe.bookingservice.dto.EventStockResponse;
import com.yeditepe.bookingservice.dto.PaymentRequest;
import com.yeditepe.bookingservice.dto.PaymentResponse;
import com.yeditepe.bookingservice.entity.Booking;
import com.yeditepe.bookingservice.entity.BookingStatus;
import com.yeditepe.bookingservice.event.BookingCreatedEvent;
import com.yeditepe.bookingservice.messaging.BookingEventPublisher;
import com.yeditepe.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final BookingEventPublisher eventPublisher;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for user: {} and event: {}", request.getUserId(), request.getEventId());
        
        try {
            Boolean userValid = userServiceClient.validateUser(request.getUserId());
            if (!userValid) {
                throw new RuntimeException("User validation failed");
            }
        } catch (Exception e) {
            log.error("User validation failed: {}", e.getMessage());
            throw new RuntimeException("User service unavailable or user invalid");
        }

        EventStockResponse stockResponse;
        try {
            stockResponse = eventServiceClient.checkStock(request.getEventId());
            if (!stockResponse.getHasStock()) {
                throw new RuntimeException("Event has no available seats");
            }
        } catch (Exception e) {
            log.error("Stock check failed: {}", e.getMessage());
            throw new RuntimeException("Event service unavailable or no stock");
        }

        try {
            Boolean reserved = eventServiceClient.reserveSeat(request.getEventId());
            if (!reserved) {
                throw new RuntimeException("Seat reservation failed");
            }
        } catch (Exception e) {
            log.error("Seat reservation failed: {}", e.getMessage());
            throw new RuntimeException("Failed to reserve seat");
        }

        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setEventId(request.getEventId());
        booking.setStatus(BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with ID: {}", savedBooking.getId());

        try {
            PaymentRequest paymentRequest = new PaymentRequest(
                savedBooking.getId(),
                BigDecimal.valueOf(100.0)
            );
            PaymentResponse paymentResponse = paymentServiceClient.processPayment(paymentRequest);
            
            if (!"SUCCESS".equals(paymentResponse.getStatus())) {
                rollbackBooking(savedBooking);
                throw new RuntimeException("Payment failed: " + paymentResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("Payment processing failed: {}", e.getMessage());
            rollbackBooking(savedBooking);
            throw new RuntimeException("Payment service unavailable");
        }

    savedBooking.setStatus(BookingStatus.CONFIRMED);
        savedBooking = bookingRepository.save(savedBooking);

        BookingCreatedEvent event = new BookingCreatedEvent(
            savedBooking.getId(),
            savedBooking.getUserId(),
            savedBooking.getEventId(),
            savedBooking.getStatus().name(),
            savedBooking.getBookingDate()
        );
        eventPublisher.publishBookingCreatedEvent(event);

        log.info("Booking confirmed successfully: {}", savedBooking.getId());
        return mapToResponse(savedBooking);
    }

    @Transactional
    protected void rollbackBooking(Booking booking) {
        log.warn("Rolling back booking: {}", booking.getId());
        try {
            eventServiceClient.releaseSeat(booking.getEventId());
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            log.info("Booking rollback completed: {}", booking.getId());
        } catch (Exception e) {
            log.error("Rollback failed: {}", e.getMessage());
        }
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        return mapToResponse(booking);
    }

    public List<BookingResponse> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByEventId(String eventId) {
        return bookingRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToResponse(updatedBooking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToResponse(updatedBooking);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getEventId(),
                booking.getStatus(),
                booking.getBookingDate()
        );
    }
}

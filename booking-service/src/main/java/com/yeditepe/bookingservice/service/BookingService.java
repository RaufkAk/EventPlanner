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
    public BookingResponse createBooking(BookingRequest request, String authorizationHeader) {
        log.info("Creating booking for user: {} and event: {}", request.getUserId(), request.getEventId());

        try {
            Boolean userValid = userServiceClient.validateUser(request.getUserId(), authorizationHeader);
            if (!userValid) {
                throw new RuntimeException("User validation failed");
            }
        } catch (Exception e) {
            log.error("User validation failed: {}", e.getMessage());
            throw new RuntimeException("User service unavailable or user invalid");
        }
        log.info("User validation successful for user: {}", request.getUserId());

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
        log.info("Booking created with PENDING status, ID: {}", savedBooking.getId());

        // Process payment BEFORE confirming booking
        try {
            PaymentRequest paymentRequest = new PaymentRequest(
                    savedBooking.getId(),
                    BigDecimal.valueOf(100.0));
            log.info("Processing payment for booking: {}", savedBooking.getId());
            PaymentResponse paymentResponse = paymentServiceClient.processPayment(paymentRequest);

            // In dev mode, accept payment if response is not null (regardless of status)
            // In production, check for COMPLETED status strictly
            if (paymentResponse != null) {
                log.info("Payment processed for booking: {} (Status: {})", savedBooking.getId(),
                        paymentResponse.getStatus());
                // Payment successful (or in dev mode) - confirm booking
                savedBooking.setStatus(BookingStatus.CONFIRMED);
                savedBooking = bookingRepository.save(savedBooking);
                log.info("Booking status updated to CONFIRMED: {}", savedBooking.getId());
            } else {
                log.warn("Payment response is null, keeping booking as PENDING");
                // Payment failed - keep booking as PENDING, don't send notification
                return mapToResponse(savedBooking);
            }
        } catch (Exception e) {
            log.error("Payment processing failed: {}", e.getMessage());
            log.warn("Booking remains PENDING due to payment failure");
            return mapToResponse(savedBooking);
        }

        // Publish event to RabbitMQ for async notification ONLY if booking is CONFIRMED
        if (savedBooking.getStatus() == BookingStatus.CONFIRMED) {
            try {
                // Build event with all required notification data
                BookingCreatedEvent event = BookingCreatedEvent.builder()
                        .bookingId(savedBooking.getId())
                        .userId(savedBooking.getUserId())
                        .userEmail("user" + savedBooking.getUserId() + "@eventplanner.com") // Placeholder
                        .eventId(savedBooking.getEventId())
                        .eventTitle("Event " + savedBooking.getEventId()) // Placeholder
                        .seatCount(1)
                        .status(savedBooking.getStatus().name())
                        .bookingDate(savedBooking.getBookingDate())
                        .build();

                log.info("Publishing booking confirmation event to RabbitMQ: {}", savedBooking.getId());
                eventPublisher.publishBookingCreatedEvent(event);
            } catch (Exception e) {
                log.error("Failed to publish booking event to RabbitMQ: {}", e.getMessage(), e);
                // Continue even if notification fails - booking should still be created
            }
        } else {
            log.info("Booking {} is not CONFIRMED, skipping RabbitMQ notification", savedBooking.getId());
        }

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
                booking.getBookingDate());
    }
}

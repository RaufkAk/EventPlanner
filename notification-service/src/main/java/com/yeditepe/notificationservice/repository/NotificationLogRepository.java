package com.yeditepe.notificationservice.repository;

import com.yeditepe.notificationservice.model.NotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends MongoRepository<NotificationLog, String> {

    // Belirli bir booking ID'ye ait bildirimleri bul
    List<NotificationLog> findByBookingId(String bookingId);

    // Belirli bir email adresine gönderilen bildirimleri bul
    List<NotificationLog> findByRecipientEmail(String email);

    // Başarısız bildirimleri bul
    List<NotificationLog> findByStatus(String status);
}

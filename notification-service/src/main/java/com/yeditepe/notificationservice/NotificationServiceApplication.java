package com.yeditepe.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Notification Service - Main Application
 *
 * Görevler:
 * - RabbitMQ'dan rezervasyon mesajlarını dinler
 * - Email bildirimleri gönderir
 * - MongoDB'ye bildirim loglarını kaydeder
 * - Eureka'ya kayıt olur (service discovery)
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
        System.out.println("""
                
                ╔═══════════════════════════════════════════╗
                ║   NOTIFICATION SERVICE STARTED! 🔔        ║
                ║                                           ║
                ║   Port: 8085                              ║
                ║   Listening to: notificationQueue        ║
                ║   MongoDB: notification_db                ║
                ║   Eureka: Registered                      ║
                ╚═══════════════════════════════════════════╝
                """);
    }
}

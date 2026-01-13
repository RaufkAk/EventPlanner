#!/bin/bash

# Hata durumunda durdur
set -e

echo "🚀 Deployment Başlıyor..."

# 1. En güncel kodları çek
echo "⬇️ GitHub'dan kodlar çekiliyor..."
git pull origin main

# 2. Projeyi Derle (JAR dosyalarını oluştur)
# Not: Sunucuda yeterli RAM/Swap olduğundan emin olun
echo "🛠️ Maven Build başlatılıyor..."
./mvnw clean package -DskipTests -Dlombok.version=1.18.34

# 3. Konteynerleri Güncelle
# --build: Kod değiştiği için imajları yeniden oluşturur
# -d: Arka planda çalıştırır
# Docker Compose akıllıdır; sadece değişen servisleri yeniden oluşturur.
# Veritabanı (postgres, mysql) gibi değişmeyen servisleri ELLEMEZ.
echo "🐳 Docker Compose ile servisler güncelleniyor..."
docker-compose up -d --build

echo "✅ Deployment Başarıyla Tamamlandı! Servisler güncellendi."

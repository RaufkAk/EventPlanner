#!/bin/bash

# UFW (Uncomplicated Firewall) Setup Script for EventPlanner

# 1. SSH (Sunucuya erişimin kesilmemesi için EN ÖNEMLİSİ)
ufw allow 22/tcp
echo "✅ SSH (22) portuna izin verildi."

# 2. API Gateway (Dış dünyanın uygulamaya girdiği ana kapı)
ufw allow 8000/tcp
echo "✅ API Gateway (8000) portuna izin verildi."

# 3. Eureka Dashboard (Servislerin durumunu görmek için - İsteğe bağlı)
ufw allow 8761/tcp
echo "✅ Eureka Dashboard (8761) portuna izin verildi."

# 4. RabbitMQ Management UI (İsteğe bağlı)
ufw allow 15672/tcp
echo "✅ RabbitMQ Management (15672) portuna izin verildi."

# --- GÜVENLİK UYARISI ---
# Aşağıdaki veritabanı portlarını dışarı açmak GÜVENLİ DEĞİLDİR.
# Veritabanlarına sadece diğer Docker container'ları erişmelidir.
# Eğer dışarıdan bağlanman gerekiyorsa (IntelliJ ile vb.), aşağıdaki satırların başındaki # işaretini kaldır.

ufw allow 5432/tcp  # PostgreSQL
ufw allow 3306/tcp  # MySQL
ufw allow 27017/tcp # MongoDB

# 5. Firewall'u Aktifleştir
echo "🔥 Firewall aktifleştiriliyor..."
ufw --force enable

echo "🎉 İşlem tamam! 'ufw status' ile kuralları kontrol edebilirsin."

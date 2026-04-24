# FinBot — AI Destekli Kişisel Finans Takip Sistemi

![CI](https://github.com/omerr02200/finbot/actions/workflows/ci.yml/badge.svg)

## Proje Hakkında
Microservice mimarili, AI destekli kişisel finans takip sistemi.
Kullanıcılar gelir/gider takibi yapabilir, AI destekli tasarruf önerileri alabilir.

## Teknolojiler
- **Backend:** Java 21, Spring Boot 3
- **Güvenlik:** Spring Security, JWT
- **Veritabanı:** PostgreSQL, Flyway Migration
- **Cache:** Redis
- **Mesajlaşma:** Apache Kafka, RabbitMQ
- **AI:** Google Gemini API
- **DevOps:** Docker, Docker Compose, GitHub Actions
- **Dokümantasyon:** Swagger / OpenAPI

## Servisler
| Servis | Port | Açıklama |
|--------|------|----------|
| user-service | 8080 | Kullanıcı, işlem yönetimi, AI önerileri |
| analytics-service | 8081 | Kafka ile harcama analizi |
| notification-service | 8082 | RabbitMQ ile bildirim servisi |

## Mimari
- user-service → Kafka → analytics-service
- user-service → RabbitMQ → notification-service
- user-service → Gemini API → AI önerileri

## Kurulum

### Gereksinimler
- Docker & Docker Compose
- Java 21
- Maven

### Çalıştırma
```bash
# Repoyu klonla
git clone https://github.com/omerr02200/finbot.git
cd finbot

# .env dosyası oluştur
cp .env.example .env
# GEMINI_API_KEY ve JWT_SECRET değerlerini doldur

# Tüm servisleri başlat
docker-compose up -d
```

### API Dokümantasyonu
Uygulama ayağa kalktıktan sonra: http://localhost:8080/swagger-ui/index.html

## API Endpoint'leri

### Auth
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | /api/v1/auth/login | Giriş yap, JWT token al |
| POST | /api/v1/users/register | Kayıt ol |

### Transaction
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | /api/v1/transactions/{userId} | İşlem ekle |
| GET | /api/v1/transactions/{userId} | İşlemleri listele |
| GET | /api/v1/transactions/{userId}/date-range | Tarih aralığına göre listele |
| GET | /api/v1/transactions/{userId}/type | Tipe göre listele |

### AI
| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | /api/v1/ai/advice/{userId} | AI destekli harcama önerisi al |

## Geliştirici
**Ömer Akıncı** — Backend Developer

[![LinkedIn](https://img.shields.io/badge/LinkedIn-omerr02200-blue)](https://linkedin.com/in/omerr02200)
[![GitHub](https://img.shields.io/badge/GitHub-omerr02200-black)](https://github.com/omerr02200)
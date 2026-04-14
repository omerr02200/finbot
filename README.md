# FinBot — AI Destekli Kişisel Finans Takip Sistemi

Microservice mimarili, AI destekli backend projesi.

## Teknolojiler
- Java 21 / Spring Boot 3
- PostgreSQL, Redis
- Swagger Open API
- Spring Security - JWT
- Apache Kafka, RabbitMQ
- Docker & Kubernetes
- GitHub Actions (CI/CD)
- Gemini API

## Servisler
- **user-service** — Kullanıcı, işlem yönetimi, AI önerileri
- **notification-service** — RabbitMQ ile bildirim servisi
- **analytics-service** — Kafka ile harcama analizi

## Kurulum
```bash
docker-compose up -d
```

## Geliştirici
Ömer Akıncı — [LinkedIn](https://linkedin.com/in/omerr02200) | [GitHub](https://github.com/omerr02200)

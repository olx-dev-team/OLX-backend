# 🛒 OLX-backend

<img src="src/main/resources/docs/image/olx-logo.png" alt="OLX backend image" width="300"/>

This project is a backend implementation of an OLX-style marketplace system built with **Spring Boot**.

## 📦 Features

- ✅ JWT-based Authentication and Role-based Authorization
- ✅ Modular Entity Design (User, Product, Category, Favorites, Chat, Notification)
- ✅ File upload system with `Attachment` entity
- ✅ Favorite system with product change tracking and notification
- ✅ Chat messaging between users
- ✅ Soft delete & audit fields: `createdAt`, `updatedAt`, `active`
- ✅ Clear architecture with clean entity relationships

## 🧱 Domain Entities

- `User`: Authenticated user (can post/view products)
- `Role`, `Permission`: Dynamic access control
- `Product`: Main listing entity with image attachments
- `Category`: Supports nested subcategories
- `Favorites`: Tracks users who liked a product
- `Notification`: Alerts users when a product they liked gets updated
- `Chat` & `Message`: Simple direct messaging between users
- `Attachment`: Stores image/file metadata (path, size, type)

## ⚙️ Technologies

- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- PostgreSQL
- Lombok
- JPA / Hibernate

## 🚀 How to Run

```bash
# Clone project
git clone https://github.com/olx-dev-team/OLX-backend.git

# Navigate to project
cd OLX-backend

# Build and run
./mvnw spring-boot:run

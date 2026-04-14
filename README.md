# Movie Recommendation System

This faculty project is a backend system for managing movies and users, with a focus on personalized recommendations and data-driven logic. The application is developed in Java, using JDBC for database communication, with the database designed and implemented in Microsoft SQL Server.

## Functionality
- Management of users, movies, genres, and tags
- Support for user-created watchlists
- Movie rating system with built-in constraints to encourage balanced user behavior
- Recommendation engine based on user preferences and rating patterns
- Basic user profiling and reward system based on activity

## Technologies
- Java
- JDBC
- Microsoft SQL Server
- IntelliJ IDEA
## Database

The database is designed in Microsoft SQL Server and integrated via JDBC, supporting:

- Relationships between movies, genres, and tags
- User interactions such as ratings and watchlists
- Queries for recommendations and analytics
- Implementation of business logic directly in the database using:
 - Triggers (for enforcing rating constraints)
 - Stored procedures (for reward logic and additional processing)

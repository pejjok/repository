# Repository

Backend service for managing a scientific works repository. The application provides APIs for authentication, users, groups, specialties, scientific works, work files, file access requests, and repository statistics.

## Tech Stack

- Kotlin
- Spring Boot
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL
- Flyway database migrations
- Gradle
- Google Drive storage integration for production
- SMTP email integration

## Requirements

- JDK 17
- PostgreSQL
- Gradle wrapper included in the repository

## Configuration

The application imports environment values from a local `.env` file when present:

```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/repository
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
JWT_SECRET=replace-with-a-long-secret
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
CORS_ORIGIN=http://localhost:3000
```

The production profile also expects Google Drive configuration:

```properties
GOOGLE_DRIVE_CREDENTIALS=path-or-json-credentials
GOOGLE_DRIVE_FOLDER_ID=google-drive-folder-id
GOOGLE_DRIVE_IMPERSONATE_EMAIL=service-account-user@example.com
```

Default admin credentials are configured in `src/main/resources/application.properties`:

```properties
admin.email=admin@gmail.com
admin.password=admin
```

Change these values before using the application outside local development.

## Run Locally

Start PostgreSQL and create the target database, then run:

```bash
./gradlew bootRun
```

The API starts on port `8080` by default.

## Run Tests

```bash
./gradlew test
```

Tests use the configuration in `src/test/resources/application.properties`.

## Build

```bash
./gradlew build
```

To build without tests:

```bash
./gradlew build -x test
```

## Docker

Build the image:

```bash
docker build -t repository .
```

Run the container:

```bash
docker run --rm -p 8080:8080 --env-file .env repository
```

The Docker image runs the application with the `prod` profile.

## API Overview

Base path: `/api/v1`

Authentication:

- `POST /auth/register` - Register a new user and return authentication tokens.
- `POST /auth/login` - Authenticate a user with credentials and return authentication tokens.
- `POST /auth/refresh/{refreshToken}` - Issue a new access token using a refresh token.
- `POST /auth/change-password` - Start the password reset flow for an email address.
- `POST /auth/change-password/verify` - Verify the password reset token and update the password.

Users:

- `GET /users` - Return a paginated list of users.
- `GET /users/{id}` - Return a single user by ID.
- `PATCH /users` - Update the authenticated user's full name.
- `PUT /users/{id}` - Update a user's assigned specialties.
- `PATCH /users/{id}` - Change a user's role.

Scientific works:

- `POST /works` - Create a new scientific work.
- `GET /works` - Return a paginated list of works with optional filters.
- `GET /works/{id}` - Return a single scientific work by ID.
- `PUT /works/{id}` - Update scientific work details.
- `PATCH /works/{id}` - Archive or unarchive a scientific work.
- `DELETE /works/{id}` - Delete a scientific work.

Work files:

- `POST /work-files` - Upload a work file.
- `GET /work-files/{id}` - Return metadata for a work file.
- `GET /work-files/{id}/download` - Download a work file when access is allowed.
- `DELETE /work-files/{id}` - Delete a work file.
- `GET /work-files/{id}/request` - Return access requests for a specific work file.
- `POST /work-files/{id}/request` - Create an access request for a work file.
- `GET /work-files/requests` - Return a paginated list of file access requests.
- `PUT /work-files/requests/{id}` - Update the status of a file access request.

Groups:

- `POST /groups` - Create a new group.
- `GET /groups` - Return all groups.
- `DELETE /groups/{id}` - Delete a group by ID.

Specialties:

- `POST /specialties` - Create a new specialty.
- `GET /specialties` - Return all specialties.
- `GET /specialties/{id}` - Return a single specialty by ID.
- `PUT /specialties/{id}` - Update a specialty by ID.
- `DELETE /specialties/{id}` - Delete a specialty by ID.

Statistics:

- `GET /statistic/general` - Return general repository statistics.
- `GET /statistic/by-specialty` - Return work counts grouped by specialty.
- `GET /statistic/by-year` - Return work counts grouped by year.
- `GET /statistic/by-type` - Return work counts grouped by work type.

## Database Migrations

Flyway migrations are stored in:

```text
src/main/resources/db/migration
```

The application validates the schema with Hibernate and applies Flyway migrations from the classpath.

## Project Structure

```text
src/main/kotlin/com/horlach/repository
|-- config
|-- controllers
|-- domain
|-- error
|-- repositories
|-- security
`-- services
```

## Notes

- Uploaded files are limited to `5MB`.
- CORS is configured through the `CORS_ORIGIN` environment value.
- JWT, database, mail, and storage settings must be provided through environment variables or `.env`.

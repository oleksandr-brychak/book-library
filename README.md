# Book Library (In-Memory)

Simple library for lending books. Designed as a layered, in-memory example with thread-safe repository operations and a small console demo via `App`.

## Features
- Find books by **author** or **title** (prefix match, case-insensitive)
- Find books by **ISBN**
- Borrow by **ISBN** (reference books cannot be borrowed)
- Remaining copies by **ISBN**, or by **title/author** prefix
- Track total borrowed count
- In-memory storage with thread-safe operations

## Structure
- `domain`: core model (`Book`, `BookType`, `BookAvailability`)
- `repository`: in-memory storage + indexes (`InventoryRepository`, `InMemoryInventoryRepository`)
- `service`: business logic (`Library`, `LibraryService`)
- `app`: demo entry point (`App`)
- `util`: shared helpers (`LibraryUtils`)

## Requirements
- Java 21
- Maven

## Dependency Versions
- JUnit Jupiter `5.10.2`
- AssertJ `3.25.3`

## Prerequisites (Install)
- JDK 21 (set `JAVA_HOME` and ensure `java -version` shows 21)
- Maven (ensure `mvn -version` works)

## Run
```sh
mvn test
```

```sh
mvn -q -DskipTests compile
java -cp target/classes com.example.library.app.App
```

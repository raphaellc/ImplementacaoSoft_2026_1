package org.example.model;

public record Book(int id, String title, boolean available) {
    public Book setAvailable() {
        return new Book(this.id, this.title, false);
    }
}

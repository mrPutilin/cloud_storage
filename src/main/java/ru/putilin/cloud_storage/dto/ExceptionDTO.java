package ru.putilin.cloud_storage.dto;


public class ExceptionDTO {

    private String message;
    private int id;

    public ExceptionDTO(String message, int id) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }
}

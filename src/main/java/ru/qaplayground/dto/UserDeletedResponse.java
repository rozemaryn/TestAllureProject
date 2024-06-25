package ru.qaplayground.dto;

//TODO попробовать сделать это record
public class UserDeletedResponse {
        Integer code;
        String message;

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public UserDeletedResponse(String message, Integer code) {
            this.message = message;
            this.code = code;
        }
}

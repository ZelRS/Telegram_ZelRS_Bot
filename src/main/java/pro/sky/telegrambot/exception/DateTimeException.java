package pro.sky.telegrambot.exception;

public class DateTimeException extends IncorrectParseCreateCommandException{
    public DateTimeException(String message) {
        super(message);
    }
}

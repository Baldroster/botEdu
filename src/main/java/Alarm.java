import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Alarm {
    private LocalDateTime time;
    private String text;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).withSecond(0).withNano(0);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void alarmHasRang(){

    }

}

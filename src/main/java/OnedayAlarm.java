import java.time.LocalDateTime;

public class OnedayAlarm extends Alarm{
    @Override
    public void alarmHasRang() {
        LocalDateTime emptyTime = null;
        setTime(emptyTime);
    }
}

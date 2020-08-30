public class EverydayAlarm extends Alarm {
    @Override
    public void alarmHasRang() {
        setTime(getTime().plusDays(1));
    }
}

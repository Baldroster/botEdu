import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bot extends TelegramLongPollingBot {

    HashMap<Long, ArrayList<Alarm>> alarms = new HashMap<>();

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new Bot());

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            switch (message.getText().toLowerCase().split(" ")[0]) {
                case "/help":
                    sendMsg(message, message.getFrom().getFirstName() + " Это бот-будильник. Напишите /setEverydayAlarm dd.MM.yyyy HH:mm \"текст_будильника\", чтобы установить ежедневный будильник \n" +
                            "Напишите /setOnetimeAlarm dd.MM.yyyy HH:mm \"текст_будильника\", чтобы установить ежедневный будильник\n" +
                            "Напишите /startWaiting для активации бота");
                    break;
                case "/seteverydayalarm":
                    EverydayAlarm everydayAlarm = new EverydayAlarm();
                    setAlarm(message, everydayAlarm);
                case "/setonetimealarm":
                    OnedayAlarm onedayAlarm = new OnedayAlarm();
                    setAlarm(message, onedayAlarm);
                    break;
                case "/startwaiting":
                    startWaiting();
            }
        }
    }


    public void setAlarm(Message message, Alarm alarm) {
        Pattern patternForText = Pattern.compile("\"(.*)\"");
        Pattern patternForTime = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4} [0-2][0-9]:[0-9]{2}");
        Matcher matcher = patternForText.matcher(message.getText());
        String text;
        String time;
        long chatId = message.getChatId();
        if (matcher.find()) {
            text = matcher.group(1);
        } else {
            sendMsg(message, "текст будильника не был указан");
            return;
        }
        matcher = patternForTime.matcher(message.getText());
        if (matcher.find()) {
            time = matcher.group();
        } else {
            sendMsg(message, "время указано в несуществующем формате");
            return;
        }
        ArrayList<Alarm> alarmList = new ArrayList<>();
        if (alarms.containsKey(chatId)) {
            alarmList = alarms.get(chatId);
        }
        alarm.setText(text);
        try {
            alarm.setTime(time);
        } catch (DateTimeParseException e) {
            sendMsg(message, "Указано несуществующее время");
            return;
        }
        alarmList.add(alarm);
        alarms.put(chatId, alarmList);
        sendMsg(message, "Будильник был установлен");

    }

    public void startWaiting() {

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        for (; ; ) {

            alarms.keySet().forEach(key -> {
               ArrayList<Alarm> alarmList = alarms.get(key);
                for (int i = 0; i < alarmList.size() ; i++) {
                  Alarm alarm = alarmList.get(i);
                    if (alarm.getTime() != null) {
                        if (LocalDateTime.now().isAfter(alarm.getTime())) {
                            sendMessage.setChatId(key);
                            sendMessage.setText(alarm.getText());
                            try {
                                execute(sendMessage);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            alarm.alarmHasRang();

                        }
                    }else {
                        alarmList.remove(i);
                }
                }
            });


        }

    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    public String getBotUsername() {
        return "GameForSchoolBot";
    }

    public String getBotToken() {
        return "1343066345:AAEXZ143r3DkPREneYjy8ubW7mAN5ILWv7w";
    }
}

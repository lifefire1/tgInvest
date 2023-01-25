package com.example.tginvest.service;

import com.example.tginvest.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final InvestService investService;

    @Autowired
    public TelegramBot(BotConfig botConfig, InvestService investService) {
        this.botConfig = botConfig;
        this.investService = investService;
        List <BotCommand> listOfCommands = new ArrayList();
        listOfCommands.add(new BotCommand("/invest", "get information"));
        listOfCommands.add(new BotCommand("/dollar", "dollar chart per day"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException  exception){
            exception.getStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String message = update.getMessage().getText();

            switch (message){
                case "/invest" -> {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
                    try {
                        sendMessage.setText(investService.getInf());
                        execute(sendMessage);
                    } catch (ExecutionException | TelegramApiException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                case "/dollar" -> {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
                    try {
                        sendMessage.setText(investService.getSomeCandles());
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                default -> {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
                    sendMessage.setText("sorry");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}

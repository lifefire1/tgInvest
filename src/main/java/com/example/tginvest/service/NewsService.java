package com.example.tginvest.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NewsService {

    //TODO
    // Добавить возможность просмотра новостей по своему тикеру

    public List getNews(){
        List<StringBuilder> mass = new ArrayList<>();
        Document doc = null;
        try {
          doc  = Jsoup
                  .connect("https://www.tinkoff.ru/invest/stocks/TSLA/pulse/")
                  .get();
        } catch (IOException exception){
            exception.printStackTrace();
        }
        Elements listElements = doc.select("div.pulse-posts-by-ticker__foOEcD.pulse-posts-by-ticker__ioOEcD");
        for (var e:
             listElements) {
            mass.add(new StringBuilder().append(e.text())
                    .append("\n")
                    .append("\n"));
        }
//        log.info(String.valueOf(builder.length()));
//        return builder;
        return mass.subList(0,3);
    }
}

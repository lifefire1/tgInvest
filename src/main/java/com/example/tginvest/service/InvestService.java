package com.example.tginvest.service;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class InvestService {
    private final String token = "t.CLxPGguplDzK79-qlB_0MjkzBLCEzgJ9r8EJiVdUkIdGX1VrYlk21Fls4ulfubV6oj9kTc8ieVdOkjaOGCBPHA";
    //TODO
    // можно разрешить пользователям вводить свой токен
    private final InvestApi readOnlyToken = InvestApi.createReadonly(token);

    private static final DecimalFormat decimalFormat = new DecimalFormat("###.###");

    private static final String badTrendSmile = "\uD83D\uDD34";
    private static final String goodTrendSmile = "\uD83D\uDFE2";

    //TODO
    // Стоит расширить функционал
    public String getInf() throws ExecutionException, InterruptedException {
        StringBuilder res = new StringBuilder();
        res.append("Добрый день! \nВот ваши отслеживаемые акции\n\n");
        getPrice(readOnlyToken,"a22a1263-8e1b-4546-a1aa-416463f104d3",res,"USD","RUB");
        getPrice(readOnlyToken,"BBG000BMHYD1", res, "Jonson & jonson", "$");
        getPrice(readOnlyToken,"BBG000BPH459",res,"Microsoft","$");
        getPrice(readOnlyToken,"BBG0013HJJ31", res, "Euro","RUB");
        return res.toString();
    }

    public static void getPrice(
            InvestApi readOnlyToken,
            String s,
            StringBuilder res,
            String name,
            String currency
                    ) throws ExecutionException, InterruptedException {
        readOnlyToken
                .getMarketDataService()
                .getLastPrices(
                        Collections
                                .singleton(s)
                ).get().forEach(e -> {
                    var newPrice = MapperUtils
                            .quotationToBigDecimal(
                                    e.getPrice()
                            );
                    var oldPrice = MapperUtils
                            .quotationToBigDecimal(
                                    readOnlyToken
                                            .getMarketDataService()
                                            .getClosePricesSync(
                                                    Collections
                                                            .singleton(
                                                                    s
                                                            )
                                            )
                                            .get(0)
                                            .getPrice());
                    res.append(name).append(" \n");
                    //                    .append(t.getFigi())
                    res.append("Цена на момент открытия ").append(decimalFormat.format(oldPrice)).append(" \n")
//                    .append(t.getFigi())
                            .append("Последняя цена ")
                            .append(decimalFormat
                                    .format(
                                            MapperUtils
                                                    .quotationToBigDecimal(
                                                            e.getPrice()
                                                    ))).append(currency).append(" ");

                    if (newPrice.compareTo(oldPrice) > 0){
                        res.append(" " + goodTrendSmile + " ")
                                .append("\n");
                    } else  {
                        res.append(" " + badTrendSmile + " ").append("\n");
                    }
                });
        res.append("\n");
    }

    public @NonNull String getSomeCandles() throws ExecutionException, InterruptedException {
        StringBuilder res = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        readOnlyToken.getMarketDataService().
        res.append("Цена доллара за последний день с интервалом в час\n");
        Instant instant1 = Clock.system(ZoneId.of("Europe/Moscow")).instant();
        Instant instant2 = instant1.minus(1, ChronoUnit.DAYS);
//        Instant instant3 = instant1.
        var market = readOnlyToken
                .getMarketDataService()
                .getCandles("a22a1263-8e1b-4546-a1aa-416463f104d3",instant2,instant1, CandleInterval.CANDLE_INTERVAL_HOUR);
        market.get().forEach( e -> {

            Date myDate = Date.from(Instant.ofEpochSecond(e.getTime().getSeconds()));

            res.append(simpleDateFormat.format(myDate)).append(" ");
            res.append(decimalFormat.format(MapperUtils.quotationToBigDecimal(e.getClose()))).append(" RUB");
            res.append("\n");
        });
        return res.toString();
    }


}

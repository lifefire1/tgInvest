package com.example.tginvest.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.text.DecimalFormat;
import java.util.Collections;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class InvestService {
    private final String token = "t.CLxPGguplDzK79-qlB_0MjkzBLCEzgJ9r8EJiVdUkIdGX1VrYlk21Fls4ulfubV6oj9kTc8ieVdOkjaOGCBPHA";
    //TODO
    // можно разрешить пользователям вводить свой токен
    private final InvestApi readOnlyToken = InvestApi.createReadonly(token);

    private static DecimalFormat decimalFormat = new DecimalFormat("###.###");

    private static String badTrendSmile = "\uD83D\uDD34";
    private static String goodTrendSmile = "\uD83D\uDFE2";

    //TODO
    // Стоит расширить функционал
    public String getInf() throws ExecutionException, InterruptedException {
        StringBuilder res = new StringBuilder();
        res.append("Добрый день! \nВот ваши отслеживаемые акции\n");
        getPrice(readOnlyToken,"a22a1263-8e1b-4546-a1aa-416463f104d3",res,"USD","RUB");
        getPrice(readOnlyToken,"BBG000BMHYD1", res, "Jonson & jonson", "$");
        getPrice(readOnlyToken,"BBG000BPH459",res,"Microsoft","$");
        getPrice(readOnlyToken,"BBG0013HJJ31", res, "Euro","RUB");
        return res.toString();
    }

    public static StringBuilder getPrice(
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
                    res.append(name + " \n");
                    res.append("Цена на момент открытия " + decimalFormat.format(oldPrice) + " \n")
//                    .append(t.getFigi())
                            .append("Последняя цена ")
                            .append(decimalFormat
                                    .format(
                                            MapperUtils
                                                    .quotationToBigDecimal(
                                                            e.getPrice()
                                                    )))
                            .append(currency + " ");

                    if (newPrice.compareTo(oldPrice) > 0){
                        res.append(" " + goodTrendSmile + " ")
                                .append("\n");
                    } else  {
                        res.append(" " + badTrendSmile + " ").append("\n");
                    }
                });
        return res;
    }
}

package com.example.tginvest.service;

import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class EarlService {
    private static final String token = "t.CLxPGguplDzK79-qlB_0MjkzBLCEzgJ9r8EJiVdUkIdGX1VrYlk21Fls4ulfubV6oj9kTc8ieVdOkjaOGCBPHA";

    private static final InvestApi readOnlyToken = InvestApi.createReadonly(token);

    private static final Instant instant1 = Clock.system(ZoneId.of("Europe/Moscow")).instant();

    private static final Instant instant2 = instant1.minus(1, ChronoUnit.DAYS);

    public TimeSeriesCollection createImageEarl () throws ExecutionException, InterruptedException {
        TimeSeries s1 = new TimeSeries("график доллара");
        var market = readOnlyToken
                .getMarketDataService()
                .getCandles(
                        "a22a1263-8e1b-4546-a1aa-416463f104d3"
                        ,instant2
                        ,instant1
                        ,CandleInterval.CANDLE_INTERVAL_HOUR);
        market.get().forEach(e -> {

            Date myDate = Date.from(Instant.ofEpochSecond(e.getTime().getSeconds()));
            Day day = new Day(myDate.getDay(),myDate.getMonth() + 1, myDate.getYear() + 1900);
            s1.add(new Hour(myDate.getHours(),day), MapperUtils.quotationToBigDecimal(e.getClose()));
        });
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);

        return dataset;
    }


    public File getGraph () throws ExecutionException, InterruptedException, IOException {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "График доллара",
                "",
                "",
                createImageEarl(),
                true,
                true,
                false
        );
        int width = 800;
        int height = 800;
        File res = new File("res.png");
        ChartUtilities.saveChartAsPNG(res,chart,width,height);
        return res;
    }
}

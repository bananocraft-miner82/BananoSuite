package miner82.bananosuite.classes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class DateCalculator {

    public static LocalDateTime GetDateOfLastDayOfWeek(DayOfWeek dayOfWeek) {

        LocalDateTime date = LocalDate.now().atStartOfDay();

        return date.with(TemporalAdjusters.previous(dayOfWeek));

    }

}

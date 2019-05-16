package pl.kk.services.common.datamodel.domain.reporting;

import org.apache.commons.lang3.tuple.Pair;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public enum PredefinedPeriod {

    YESTERDAY {
        @Override
        public Map.Entry<ZonedDateTime, ZonedDateTime> getTimestampRange() {
            ZonedDateTime now = ZonedDateTime.now();
            return Pair.of(now.minusDays(1).truncatedTo(ChronoUnit.DAYS), now.truncatedTo(ChronoUnit.DAYS));
        }
    }, LAST_7_DAYS {
        public Map.Entry<ZonedDateTime, ZonedDateTime> getTimestampRange() {
            ZonedDateTime now = ZonedDateTime.now();
            return Pair.of(now.minusDays(7).truncatedTo(ChronoUnit.DAYS), now.truncatedTo(ChronoUnit.DAYS));
        }
    }, LAST_MONTH {
        @Override
        public Map.Entry<ZonedDateTime, ZonedDateTime> getTimestampRange() {
            ZonedDateTime now = ZonedDateTime.now();
            return Pair.of(now.minusMonths(1).truncatedTo(ChronoUnit.DAYS), now.truncatedTo(ChronoUnit.DAYS));
        }
    };

    public abstract java.util.Map.Entry<ZonedDateTime, ZonedDateTime> getTimestampRange();
}

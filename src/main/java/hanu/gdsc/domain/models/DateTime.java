package hanu.gdsc.domain.models;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class DateTime {
    private ZonedDateTime value;

    public DateTime(ZonedDateTime value) {
        this.value = value;
    }

    public DateTime(String value) {
        this.value = ZonedDateTime.parse(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateTime dateTime = (DateTime) o;
        return Objects.equals(value, dateTime.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public static DateTime now() {
        return new DateTime(ZonedDateTime.now());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public long toMillis() {
        long e = value.toInstant().toEpochMilli();
        Instant i = Instant.ofEpochMilli(e);
        return i.toEpochMilli();
    }
    public ZonedDateTime toZonedDateTime() {
        return value;
    }

}

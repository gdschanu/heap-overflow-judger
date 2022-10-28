package hanu.gdsc.domain.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hanu.gdsc.infrastructure.json.MillisecondSerializer;

@JsonSerialize(using = MillisecondSerializer.class)
public class Millisecond {
    private long value;

    public static Millisecond max(Millisecond a, Millisecond b) {
        if (a.greaterThan(b))
            return a;
        return b;
    }

    public static Millisecond fromSecond(Float val) {
        long m = Math.round(val * 1000);
        return new Millisecond(m);
    }

    public Millisecond(long millisecond) {
        this.value = millisecond;
    }

    public boolean greaterThan(Millisecond that) {
        return this.value > that.value;
    }

    public long getValue() {
        return value;
    }

    public long toSecond() {
        return value / 1000;
    }

    @Override
    public String toString() {
        return "Millisecond{" +
                "value=" + value +
                '}';
    }
}

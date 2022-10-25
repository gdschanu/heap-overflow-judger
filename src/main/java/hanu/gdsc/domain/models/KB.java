package hanu.gdsc.domain.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class KB {
    private double value;

    public static KB max(KB a, KB b) {
        if (a.greaterThan(b))
            return a;
        return b;
    }

    public KB(long value) {
        this.value = value;
    }

    public KB(double value) {
        this.value = value;
    }

    public boolean greaterThan(KB that) {
        return this.value > that.value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "KB{" +
                "value=" + value +
                '}';
    }
}

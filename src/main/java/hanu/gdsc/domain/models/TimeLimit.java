package hanu.gdsc.domain.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

public class TimeLimit {

    private ProgrammingLanguage programmingLanguage;
    private Millisecond timeLimit;

    public static final Millisecond MAX = new Millisecond(10000);

    private TimeLimit(ProgrammingLanguage programmingLanguage, Millisecond timeLimit) {
        this.programmingLanguage = programmingLanguage;
        this.timeLimit = timeLimit;
    }


    public ProgrammingLanguage getProgrammingLanguage() {
        return programmingLanguage;
    }

    public Millisecond getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Millisecond timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeLimit timeLimit = (TimeLimit) o;
        return programmingLanguage == timeLimit.programmingLanguage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(programmingLanguage);
    }

    @Override
    public String toString() {
        return timeLimit.toString();
    }
}

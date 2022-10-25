package hanu.gdsc.domain.models;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

public class MemoryLimit {
    private ProgrammingLanguage programmingLanguage;
    private KB memoryLimit;

    private MemoryLimit(ProgrammingLanguage programmingLanguage, KB memoryLimit) {
        this.programmingLanguage = programmingLanguage;
        this.memoryLimit = memoryLimit;
    }

    public ProgrammingLanguage getProgrammingLanguage() {
        return programmingLanguage;
    }

    public KB getMemoryLimit() {
        return memoryLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryLimit that = (MemoryLimit) o;
        return programmingLanguage == that.programmingLanguage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(programmingLanguage);
    }

    @Override
    public String toString() {
        return memoryLimit.toString();
    }
}

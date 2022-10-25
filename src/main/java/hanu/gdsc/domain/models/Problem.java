package hanu.gdsc.domain.models;

import java.util.Collections;
import java.util.List;

public class Problem extends IdentitifedVersioningDomainObject {
    private String name;
    private String description;
    private Id author;
    private List<MemoryLimit> memoryLimits;
    private List<TimeLimit> timeLimits;
    private List<ProgrammingLanguage> allowedProgrammingLanguages;
    private String serviceToCreate;

    private Problem(Id id, long version, String name, String description, Id author, List<MemoryLimit> memoryLimits, List<TimeLimit> timeLimits, List<ProgrammingLanguage> allowedProgrammingLanguages, String serviceToCreate) {
        super(id, version);
        this.name = name;
        this.description = description;
        this.author = author;
        this.memoryLimits = memoryLimits;
        this.timeLimits = timeLimits;
        this.allowedProgrammingLanguages = allowedProgrammingLanguages;
        this.serviceToCreate = serviceToCreate;
    }

    public MemoryLimit getMemoryLimitByProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        for (MemoryLimit memoryLimit : getMemoryLimits()) {
            if (memoryLimit.getProgrammingLanguage().equals(programmingLanguage)) {
                return memoryLimit;
            }
        }
        return null;
    }

    public TimeLimit getTimeLimitByProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        for (TimeLimit timeLimit : getTimeLimits()) {
            if (timeLimit.getProgrammingLanguage().equals(programmingLanguage)) {
                return timeLimit;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Id getAuthor() {
        return author;
    }

    public List<MemoryLimit> getMemoryLimits() {
        return Collections.unmodifiableList(memoryLimits);
    }

    public List<TimeLimit> getTimeLimits() {
        return Collections.unmodifiableList(timeLimits);
    }

    public List<ProgrammingLanguage> getAllowedProgrammingLanguages() {
        return allowedProgrammingLanguages;
    }

    public String getServiceToCreate() {
        return serviceToCreate;
    }
}

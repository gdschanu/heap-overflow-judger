package hanu.gdsc.domain.models;


public class SubmissionEvent extends IdentifiedDomainObject {
    private Id problemId;
    private Status status;

    private Id coderId;
    private DateTime submittedAt;
    private int passedTestCasesCount;
    private int totalTestCasesCount;
    private String serviceToCreate;


    public SubmissionEvent(Id id,
                           Id problemId,
                           Status status,
                           Id coderId,
                           DateTime submittedAt,
                           int passedTestCasesCount,
                           int totalTestCasesCount,
                           String serviceToCreate) {
        super(id);
        this.problemId = problemId;
        this.status = status;
        this.coderId = coderId;
        this.submittedAt = submittedAt;
        this.passedTestCasesCount = passedTestCasesCount;
        this.totalTestCasesCount = totalTestCasesCount;
        this.serviceToCreate = serviceToCreate;
    }

    public static SubmissionEvent create(Id problemId,
                                         Status status,
                                         Id coderId,
                                         DateTime submittedAt,
                                         int passedTestCasesCount,
                                         int totalTestCasesCount,
                                         String serviceToCreate) {
        return new SubmissionEvent(
                Id.generateRandom(),
                problemId,
                status,
                coderId,
                submittedAt,
                passedTestCasesCount,
                totalTestCasesCount,
                serviceToCreate
        );
    }

    public Id getProblemId() {
        return problemId;
    }

    public Status getStatus() {
        return status;
    }

    public Id getCoderId() {
        return coderId;
    }
}

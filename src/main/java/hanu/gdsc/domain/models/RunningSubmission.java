package hanu.gdsc.domain.models;

public class RunningSubmission extends IdentitifedVersioningDomainObject {
    private Id coderId;
    private Id problemId;
    private String serviceToCreate;
    private String code;
    private ProgrammingLanguage programmingLanguage;
    private DateTime submittedAt;

    private int judgingTestCase;
    private int totalTestCases;

    private RunningSubmission(Id id,
                             long version,
                             Id coderId,
                             Id problemId,
                             String serviceToCreate,
                             String code,
                             ProgrammingLanguage programmingLanguage,
                             DateTime submittedAt,
                             int judgingTestCase,
                             int totalTestCases) {
        super(id, version);
        this.coderId = coderId;
        this.problemId = problemId;
        this.serviceToCreate = serviceToCreate;
        this.code = code;
        this.programmingLanguage = programmingLanguage;
        this.submittedAt = submittedAt;
        this.judgingTestCase = judgingTestCase;
        this.totalTestCases = totalTestCases;
    }

    public void setJudgingTestCase(int judgingTestCase) {
        this.judgingTestCase = judgingTestCase;
    }

    public void setTotalTestCases(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public Id getCoderId() {
        return coderId;
    }

    public Id getProblemId() {
        return problemId;
    }

    public String getServiceToCreate() {
        return serviceToCreate;
    }

    public String getCode() {
        return code;
    }

    public ProgrammingLanguage getProgrammingLanguage() {
        return programmingLanguage;
    }

    public DateTime getSubmittedAt() {
        return submittedAt;
    }

    public int getJudgingTestCase() {
        return judgingTestCase;
    }

    public int getTotalTestCases() {
        return totalTestCases;
    }


}

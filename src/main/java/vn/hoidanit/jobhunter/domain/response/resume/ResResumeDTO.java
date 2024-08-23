package vn.hoidanit.jobhunter.domain.response.resume;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.ResumeStateEnum;

@Getter
@Setter
public class ResResumeDTO {
    private long id;

    private String email;
    private String url;
    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private UserResume user;
    private JobResume job;

    @Getter
    @Setter
    public static class UserResume {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class JobResume {
        private long id;
        private String name;
    }
}

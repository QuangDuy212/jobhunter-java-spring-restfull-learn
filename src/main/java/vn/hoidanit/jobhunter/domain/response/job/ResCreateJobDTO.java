package vn.hoidanit.jobhunter.domain.response.job;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.LevelEnum;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private boolean active;
    private LevelEnum level;
    private String description;
    private Instant startDate;
    private List<String> skills;
    private Instant endDate;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}

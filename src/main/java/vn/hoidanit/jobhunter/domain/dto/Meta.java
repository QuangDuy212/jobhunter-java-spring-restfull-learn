package vn.hoidanit.jobhunter.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Meta {
    private int page;
    private int pageSize;
    private int pages;
    private long total;
}

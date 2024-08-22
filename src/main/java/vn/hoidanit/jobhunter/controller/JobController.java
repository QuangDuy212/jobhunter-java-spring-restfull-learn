package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJob;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job reqJob) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(reqJob));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJob> putMethodName(@Valid @RequestBody Job reqJob) throws IdInvalidException {
        boolean checkExist = this.jobService.isExistId(reqJob.getId());
        if (!checkExist) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.ok().body(this.jobService.handleUpdateJob(reqJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete job by id")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        boolean checkExist = this.jobService.isExistId(id);
        if (!checkExist) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.handleDeleteJob(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/jobs")
    @ApiMessage("Fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> fetchAllJobs(@Filter Specification<Job> spec,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok().body(this.jobService.fetchAllJobs(spec, pageable));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Fetch job by id")
    public ResponseEntity<Job> fetchJobById(@PathVariable("id") long id) throws IdInvalidException {
        boolean checkExist = this.jobService.isExistId(id);
        if (!checkExist) {
            throw new IdInvalidException("Job not found");
        }
        Job job = this.jobService.fetchJobById(id);
        return ResponseEntity.ok().body(job);
    }
}

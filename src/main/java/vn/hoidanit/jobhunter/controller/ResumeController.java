package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    @Autowired
    private FilterBuilder filterBuilder;

    private final ResumeService resumeService;
    private final UserService userService;
    private final JobService jobService;

    public ResumeController(ResumeService resumeService, UserService userService, JobService jobService) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.jobService = jobService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> createNewResume(@Valid @RequestBody Resume reqResume)
            throws IdInvalidException {
        boolean check = this.resumeService.checkResumeExistByUserAndJob(reqResume);
        if (!check) {
            throw new IdInvalidException("User/Job not found");
        }
        // create
        Resume resume = this.resumeService.handleCreateResume(reqResume);

        // convert to rescreate
        ResCreateResumeDTO res = this.resumeService.convertResumeToResCreateResumeDTO(resume);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/resumes")
    @ApiMessage("update a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume reqResume) throws IdInvalidException {
        Resume currentResume = this.resumeService.fetchResumeById(reqResume.getId());
        if (currentResume == null) {
            throw new IdInvalidException("Resume not found");
        }
        Resume resume = this.resumeService.handleUpdateResume(reqResume);
        // convert resume to ResUpdateResumeDTO to display
        ResUpdateResumeDTO res = this.resumeService.convertResumeToResUpdateResumeDTO(resume);
        // return ResponseEntity.status(HttpStatus.OK).body(ericUser);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("fetch resume by id")
    public ResponseEntity<ResResumeDTO> fetchResumeById(@PathVariable("id") long id) throws IdInvalidException {
        Resume checkResume = this.resumeService.fetchResumeById(id);
        if (checkResume == null) {
            throw new IdInvalidException("Resume not found");
        }
        // convert resume to ResUserDTO to display
        ResResumeDTO res = this.resumeService.convertResumeToResResumeDTO(checkResume);
        // return ResponseEntity.status(HttpStatus.OK).body(user);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/resumes")
    @ApiMessage("fetch all resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAllUsers(
            @Filter Specification<Resume> spec,
            Pageable pageable) {

        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }
        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(jobInSpec);
        return ResponseEntity.ok(this.resumeService.fetchAllResumes(finalSpec, pageable));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("delete resume by id")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        Resume resume = this.resumeService.fetchResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume not found");
        }
        this.resumeService.handleDeleteResume(id);
        // return ResponseEntity.status(HttpStatus.OK).body("id: " + id);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable)
            throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.fetchResumeByUser(pageable));
    }
}

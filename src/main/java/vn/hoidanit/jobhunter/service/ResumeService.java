package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserService userService;
    private final JobService jobService;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, UserService userService, JobService jobService) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.jobService = jobService;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // check user by id
        if (resume.getUser() == null)
            return false;
        User user = this.userService.fetchUserById(resume.getUser().getId());
        if (user == null)
            return false;

        // check job by id
        if (resume.getJob() == null)
            return false;
        Job job = this.jobService.fetchJobById(resume.getJob().getId());
        if (job == null)
            return false;
        return true;
    }

    public Resume handleCreateResume(Resume reqResume) {
        if (reqResume.getUser() != null) {
            long idUser = reqResume.getUser().getId();
            User user = this.userService.fetchUserById(idUser);
            reqResume.setUser(user);
        }
        if (reqResume.getJob() != null) {
            long idJob = reqResume.getJob().getId();
            Job job = this.jobService.fetchJobById(idJob);
            reqResume.setJob(job);
        }
        return this.resumeRepository.save(reqResume);
    }

    public ResCreateResumeDTO convertResumeToResCreateResumeDTO(Resume resume) {
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        return res;
    }

    public Resume handleUpdateResume(Resume reqResume) {
        Resume resume = this.fetchResumeById(reqResume.getId());
        if (reqResume.getStatus() != null) {
            resume.setStatus(reqResume.getStatus());
        }
        return this.resumeRepository.save(resume);
    }

    public Resume fetchResumeById(long id) {
        Optional<Resume> resume = this.resumeRepository.findById(id);
        if (resume.isPresent())
            return resume.get();
        return null;
    }

    public ResUpdateResumeDTO convertResumeToResUpdateResumeDTO(Resume resume) {
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public ResResumeDTO convertResumeToResResumeDTO(Resume resume) {
        ResResumeDTO res = new ResResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        if (resume.getUser() != null) {
            ResResumeDTO.UserResume user = new ResResumeDTO.UserResume();
            user.setId(resume.getUser().getId());
            user.setName(resume.getUser().getName());
            res.setUser(user);
        }
        if (resume.getJob() != null) {
            ResResumeDTO.JobResume job = new ResResumeDTO.JobResume();
            job.setId(resume.getJob().getId());
            job.setName(resume.getJob().getName());
            res.setJob(job);
            res.setCompanyName(resume.getJob().getCompany().getName());
        }
        return res;
    }

    public ResultPaginationDTO fetchAllResumes(Specification<Resume> spec, Pageable pageable) {

        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        List<ResResumeDTO> listResume = pageResume.getContent()
                .stream().map(item -> this.convertResumeToResResumeDTO(item)).collect(Collectors.toList());
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listResume);
        return rs;
    }

    public void handleDeleteResume(long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        List<ResResumeDTO> listResume = pageResume.getContent()
                .stream().map(item -> this.convertResumeToResResumeDTO(item)).collect(Collectors.toList());
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listResume);
        return rs;
    }
}

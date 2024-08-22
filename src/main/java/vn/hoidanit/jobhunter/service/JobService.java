package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRespository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRespository skillRespository;
    private final SkillService skillService;
    private final CompanyService companyService;

    public JobService(JobRepository jobRepository, SkillRespository skillRespository, SkillService skillService,
            CompanyService companyService) {
        this.jobRepository = jobRepository;
        this.skillRespository = skillRespository;
        this.skillService = skillService;
        this.companyService = companyService;
    }

    public ResCreateJobDTO handleCreateJob(Job j) {
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRespository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }

        // create job
        Job currentJob = this.jobRepository.save(j);

        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(s -> s.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public boolean isExistId(long id) {
        return this.jobRepository.existsById(id);
    }

    public ResUpdateJob handleUpdateJob(Job j) {
        // Optional<Job> jobOptional = this.jobRepository.findById(reqJob.getId());
        // Job newJob = jobOptional.get();
        // if (jobOptional.isPresent()) {
        // newJob.setActive(reqJob.isActive());
        // newJob.setLevel(reqJob.getLevel());
        // if (reqJob.getName() != null)
        // newJob.setName(reqJob.getName());
        // if (reqJob.getLocation() != null)
        // newJob.setLocation(reqJob.getLocation());
        // if (reqJob.getSalary() > 0)
        // newJob.setSalary(reqJob.getSalary());
        // if (reqJob.getLevel() != null)
        // newJob.setLevel(reqJob.getLevel());
        // if (reqJob.getDescription() != null)
        // newJob.setDescription(reqJob.getDescription());
        // if (reqJob.getStartDate() != null)
        // newJob.setStartDate(reqJob.getStartDate());
        // if (reqJob.getEndDate() != null)
        // newJob.setEndDate(reqJob.getEndDate());
        // if (reqJob.getSkills() != null) {
        // List<Skill> reqSkills = reqJob.getSkills();
        // List<Long> listIds = new ArrayList<Long>();
        // for (Skill skill : reqSkills) {
        // listIds.add(skill.getId());
        // }
        // reqJob.setSkills(this.skillService.fetchListSkillByListId(listIds));
        // }
        // }
        // return this.jobRepository.save(newJob);
        // check skills
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRespository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }

        // create job
        Job currentJob = this.jobRepository.save(j);

        // convert response
        ResUpdateJob dto = new ResUpdateJob();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(s -> s.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public Job fetchJobById(long id) {
        Optional<Job> job = this.jobRepository.findById(id);
        if (job.isPresent())
            return job.get();
        return null;
    }

    public ResultPaginationDTO fetchAllJobs(Specification<Job> spec, Pageable pageable) {

        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        List<Job> listJob = pageJob.getContent();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listJob);
        return rs;
    }

}

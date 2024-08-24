package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRespository;

@Service
public class SkillService {
    private final SkillRespository skillRespository;

    public SkillService(SkillRespository skillRespository) {
        this.skillRespository = skillRespository;
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRespository.save(skill);
    }

    public boolean isExistName(String name) {
        return this.skillRespository.existsByName(name);
    }

    public boolean isExistId(long id) {
        return this.skillRespository.existsById(id);
    }

    public Skill handleUpdateSkill(Skill reqSkill) {
        Optional<Skill> skill = this.skillRespository.findById(reqSkill.getId());
        skill.get().setName(reqSkill.getName());
        return this.skillRespository.save(skill.get());
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable) {

        Page<Skill> pageSkill = this.skillRespository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();

        List<Skill> listSkill = pageSkill.getContent();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listSkill);
        return rs;
    }

    public Optional<Skill> fetchSkillById(long id) {
        return this.skillRespository.findById(id);
    }

    public void handleDeleteSkill(long id) {
        // delete job (inside job_skill table)
        Optional<Skill> skillOptional = this.skillRespository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete subscriber (inside subscriber_skill table)
        currentSkill.getSubscribers().forEach(job -> job.getSkills().remove(currentSkill));

        // delete skill
        this.skillRespository.deleteById(id);
    }

    public List<Skill> fetchListSkillByListId(List<Long> listIds) {
        return this.skillRespository.findByIdIn(listIds);
    }
}

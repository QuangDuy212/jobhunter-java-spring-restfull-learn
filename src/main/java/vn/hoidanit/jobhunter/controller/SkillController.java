package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.RestResponse;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill reqSkill) throws IdInvalidException {
        if (reqSkill.getName() == null) {
            throw new IdInvalidException("Vui lòng truyền tên");
        }
        boolean checkExist = this.skillService.isExistName(reqSkill.getName());
        if (checkExist) {
            throw new IdInvalidException("Skill name = " + reqSkill.getName() + " đã tồn tại.");
        }
        Skill skill = this.skillService.handleCreateSkill(reqSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(skill);
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch all skills")
    public ResponseEntity<ResultPaginationDTO> fetchAllSkills(@Filter Specification<Skill> spec,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok().body(this.skillService.fetchAllSkills(spec, pageable));
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("Fetch all skills")
    public ResponseEntity<Skill> fetchSkillById(@PathVariable("id") long id) throws IdInvalidException {
        boolean checkIdExist = this.skillService.isExistId(id);
        if (!checkIdExist) {
            throw new IdInvalidException("Skill với ID = " + id + " không tồn tại");
        }
        Optional<Skill> skill = this.skillService.fetchSkillById(id);
        return ResponseEntity.ok().body(skill.get());
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skills")
    public ResponseEntity<Skill> putMethodName(@Valid @RequestBody Skill reqSkill) throws IdInvalidException {
        boolean checkIdExist = this.skillService.isExistId(reqSkill.getId());
        if (!checkIdExist) {
            throw new IdInvalidException("Skill với ID = " + reqSkill.getId() + " không tồn tại");
        }
        boolean checkNameExist = this.skillService.isExistName(reqSkill.getName());
        if (checkNameExist) {
            throw new IdInvalidException("Skill name = " + reqSkill.getName() + " đã tồn tại");
        }
        Skill skill = this.skillService.handleUpdateSkill(reqSkill);
        return ResponseEntity.ok().body(skill);
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("delete skill by id")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        boolean checkIdExist = this.skillService.isExistId(id);
        if (!checkIdExist) {
            throw new IdInvalidException("Skill với id = " + id + " không tồn tại");
        }
        this.skillService.handleDeleteSkill(id);
        // return ResponseEntity.status(HttpStatus.OK).body("id: " + id);
        return ResponseEntity.ok(null);
    }

}

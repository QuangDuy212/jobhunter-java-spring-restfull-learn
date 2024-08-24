package vn.hoidanit.jobhunter.service;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SubscriberRespository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRespository subscriberRespository;
    private final SkillService skillService;

    public SubscriberService(SubscriberRespository subscriberRespository, SkillService skillService) {
        this.subscriberRespository = subscriberRespository;
        this.skillService = skillService;
    }

    public boolean isExistEmail(String email) {
        return this.subscriberRespository.existsByEmail(email);
    }

    public Subscriber fetchSubscriberById(long id) {
        Optional<Subscriber> sOptional = this.subscriberRespository.findById(id);
        if (sOptional.isPresent())
            return sOptional.get();
        return null;
    }

    public Subscriber handleCreatSubscriber(Subscriber reqSubscriber) {
        if (reqSubscriber.getSkills() != null) {
            List<Long> listIds = reqSubscriber.getSkills()
                    .stream().map(i -> i.getId())
                    .collect(Collectors.toList());
            List<Skill> listSkills = this.skillService.fetchListSkillByListId(listIds);
            reqSubscriber.setSkills(listSkills);
        }
        return this.subscriberRespository.save(reqSubscriber);
    }

    public Subscriber handleUpdateSubscriber(Subscriber reqSubscriber) {
        Subscriber currentSubscriber = this.fetchSubscriberById(reqSubscriber.getId());
        if (reqSubscriber.getSkills() != null) {
            List<Long> listIds = reqSubscriber.getSkills()
                    .stream().map(i -> i.getId())
                    .collect(Collectors.toList());
            List<Skill> listSkills = this.skillService.fetchListSkillByListId(listIds);
            currentSubscriber.setSkills(listSkills);
        }
        return this.subscriberRespository.save(currentSubscriber);
    }
}

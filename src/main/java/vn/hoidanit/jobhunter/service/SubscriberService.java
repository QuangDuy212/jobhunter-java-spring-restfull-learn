package vn.hoidanit.jobhunter.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.email.ResEmailJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final JobRepository jobRepository;
    private final SkillService skillService;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, JobRepository jobRepository,
            SkillService skillService, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.jobRepository = jobRepository;
        this.skillService = skillService;
        this.emailService = emailService;
    }

    public boolean isExistEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber fetchSubscriberById(long id) {
        Optional<Subscriber> sOptional = this.subscriberRepository.findById(id);
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
        return this.subscriberRepository.save(reqSubscriber);
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
        return this.subscriberRepository.save(currentSubscriber);
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;

    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }

    }

    public Subscriber findByName(String email) {
        return this.subscriberRepository.findByEmail(email);
    }
}

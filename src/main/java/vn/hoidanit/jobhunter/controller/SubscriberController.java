package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("create a subscriber")
    public ResponseEntity<Subscriber> createNewSubscriber(@Valid @RequestBody Subscriber reqSubscriber)
            throws IdInvalidException {
        // check exist
        if (this.subscriberService.isExistEmail(reqSubscriber.getEmail())) {
            throw new IdInvalidException("Email existed");
        }
        // create new
        Subscriber subscriber = this.subscriberService.handleCreatSubscriber(reqSubscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriber);
    }

    @PutMapping("/subscribers")
    @ApiMessage("update a subscriber")
    public ResponseEntity<Subscriber> updateAPermission(@RequestBody Subscriber reqSubscriber)
            throws IdInvalidException {

        // check exist by id
        Subscriber currentSubscriber = this.subscriberService.fetchSubscriberById(reqSubscriber.getId());
        if (currentSubscriber == null) {
            throw new IdInvalidException("Subscriber not found");
        }
        // update
        Subscriber subscriber = this.subscriberService.handleUpdateSubscriber(reqSubscriber);
        // convert user to ResUpdateUserDTO to display
        return ResponseEntity.ok(subscriber);
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skills")
    public ResponseEntity<Subscriber> getSubscriberSkill()
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.findByName(email));
    }
}

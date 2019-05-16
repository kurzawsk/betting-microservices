package pl.kk.services.reporting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kk.services.common.datamodel.dto.reporting.SendEmailDTO;
import pl.kk.services.common.oauth2.Roles;
import pl.kk.services.reporting.service.EmailService;

import javax.validation.Valid;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    @PreAuthorize(Roles.ADMIN)
    public ResponseEntity send(@RequestBody @Valid SendEmailDTO sendEmailDTO){
        if (sendEmailDTO.isHtml()){
            emailService.sendHtml(sendEmailDTO.getSubject(), sendEmailDTO.getContent());
        }else{
            emailService.send(sendEmailDTO.getSubject(), sendEmailDTO.getContent());
        }
        return ResponseEntity.ok("");
    }
}

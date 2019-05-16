package pl.kk.services.mdm.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kk.services.common.datamodel.dto.mdm.BookmakerDTO;
import pl.kk.services.common.oauth2.Roles;
import pl.kk.services.mdm.service.mapping.BookmakerService;

import java.util.List;

@RestController
@RequestMapping("/bookmaker")
public class BookmakerController {

    private final BookmakerService bookmakerService;

    public BookmakerController(BookmakerService bookmakerService) {
        this.bookmakerService = bookmakerService;
    }

    @GetMapping
    @PreAuthorize(Roles.USER)
    public List<BookmakerDTO> find() {
        return bookmakerService.find();
    }

}

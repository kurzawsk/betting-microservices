package pl.kk.services.mdm.service.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kk.services.common.datamodel.dto.mdm.BookmakerDTO;
import pl.kk.services.mdm.repository.BookmakerRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class BookmakerService {

    private final BookmakerRepository bookmakerRepository;
    private final BookmakerConverter bookmakerConverter;

    @Autowired
    public BookmakerService(BookmakerRepository bookmakerRepository, BookmakerConverter bookmakerConverter) {
        this.bookmakerRepository = bookmakerRepository;
        this.bookmakerConverter = bookmakerConverter;
    }

    @Transactional(readOnly = true)
    public List<BookmakerDTO> find() {
        return bookmakerRepository
                .findAll()
                .stream()
                .map(bookmakerConverter::toDTO)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<BookmakerDTO> find(List<Long> ids) {
        return bookmakerRepository
                .findAllById(ids)
                .stream()
                .map(bookmakerConverter::toDTO)
                .collect(toList());
    }
}

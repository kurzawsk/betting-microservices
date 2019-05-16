package pl.kk.services.mdm.service.mapping;

import org.springframework.stereotype.Service;
import pl.kk.services.common.datamodel.dto.mdm.BookmakerDTO;
import pl.kk.services.mdm.model.domain.Bookmaker;

@Service
public class BookmakerConverter {

    public BookmakerDTO toDTO(Bookmaker domain) {
        return BookmakerDTO.builder()
                .id(domain.getId())
                .name(domain.getName())
                .alternativeNames(domain.getAlternativeNames())
                .taxPercent(domain.getTaxPercent())
                .url(domain.getUrl())
                .build();
    }

}

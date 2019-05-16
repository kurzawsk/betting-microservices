package pl.kk.services.common.service.mdm;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pl.kk.services.common.datamodel.dto.mdm.*;
import pl.kk.services.common.oauth2.FeignClientConfiguration;

import javax.validation.Valid;
import java.util.List;

@FeignClient(
        name = "mdm-service",
        configuration = FeignClientConfiguration.class
)
public interface MdmServiceClient {

    @PostMapping("/match")
    AddMatchResponseDTO add(@RequestBody AddMatchRequestDTO addMatchRequestDTO);

    @PutMapping("/match/{id}")
    BasicMatchDTO setMatchResult(@RequestBody SetMatchResultDTO updateMatchResultDTO, @PathVariable("id") Long id);

    @PostMapping("/match/odd")
    void addOdds(@Valid @RequestBody List<AddMatchOddDTO> addMatchOddDTOs);

    @GetMapping("/bookmaker")
    List<BookmakerDTO> findAllBookmakers();

}

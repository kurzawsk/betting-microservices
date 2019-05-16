package pl.kk.services.common.misc;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.kk.services.common.datamodel.dto.PagedResultDTO;
import pl.kk.services.common.datamodel.dto.PagedSearchRequestDTO;

import java.util.List;

public class RestUtils {

    public static final String BASIC_INFO_ONLY_PARAMETER = "basic-info-only";
    private static final int MAX_PAGE_SIZE = 2000;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_ATTRIBUTE = "id";

    public static Pageable createPageRequest(PagedSearchRequestDTO pagedSearchRequestDTO) {
        Integer size = (Integer) ObjectUtils.defaultIfNull(pagedSearchRequestDTO.getSize(), DEFAULT_PAGE_SIZE);
        Integer page = (Integer) ObjectUtils.defaultIfNull(pagedSearchRequestDTO.getPage(), 0);

        if (size > MAX_PAGE_SIZE) {
            throw new BusinessValidationException("Page size cannot be greater than " + MAX_PAGE_SIZE);
        }

        String sortAtt = (String) ObjectUtils.defaultIfNull(pagedSearchRequestDTO.getSortAtt(), DEFAULT_SORT_ATTRIBUTE);
        return PageRequest.of(page,
                size,
                new Sort(pagedSearchRequestDTO.isSortDesc() ? Sort.Direction.DESC : Sort.Direction.ASC, sortAtt));
    }

    public static <T> PagedResultDTO<T> getPagedResult(List<T> items, long totalElements) {
        return PagedResultDTO.<T>builder()
                .items(items)
                .totalItemsCount(totalElements)
                .build();
    }
}

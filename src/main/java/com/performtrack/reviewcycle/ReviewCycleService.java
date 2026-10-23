package com.performtrack.reviewcycle;

import com.performtrack.common.BusinessRuleException;
import com.performtrack.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ReviewCycleService {

    private final ReviewCycleRepository reviewCycleRepository;

    public ReviewCycleService(ReviewCycleRepository reviewCycleRepository) {
        this.reviewCycleRepository = reviewCycleRepository;
    }

    @Transactional
    public ReviewCycleResponse create(ReviewCycleRequest request) {

        validateDates(request);

        String name = normalizeName(request.name());

        if (reviewCycleRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessRuleException(
                    "A review cycle with this name already exists."
            );
        }

        ReviewCycle cycle = new ReviewCycle();
        cycle.setName(name);
        cycle.setStartDate(request.startDate());
        cycle.setEndDate(request.endDate());
        cycle.setActive(true);

        ReviewCycle saved = reviewCycleRepository.save(cycle);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewCycleResponse> getAll() {

        return reviewCycleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewCycleResponse getById(Long id) {

        ReviewCycle cycle = findCycle(id);

        return toResponse(cycle);
    }

    @Transactional
    public ReviewCycleResponse update(
            Long id,
            ReviewCycleRequest request) {

        ReviewCycle cycle = findCycle(id);

        validateDates(request);

        String name = normalizeName(request.name());

        if (!cycle.getName().equalsIgnoreCase(name)
                && reviewCycleRepository.existsByNameIgnoreCase(name)) {

            throw new BusinessRuleException(
                    "Another review cycle already uses this name."
            );
        }

        cycle.setName(name);
        cycle.setStartDate(request.startDate());
        cycle.setEndDate(request.endDate());

        ReviewCycle saved = reviewCycleRepository.save(cycle);

        return toResponse(saved);
    }

    @Transactional
    public void deactivate(Long id) {

        ReviewCycle cycle = findCycle(id);
        cycle.setActive(false);

        reviewCycleRepository.save(cycle);
    }

    private void validateDates(ReviewCycleRequest request) {

        if (request.startDate() != null
                && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {

            throw new BusinessRuleException(
                    "End date cannot be before start date."
            );
        }
    }

    private ReviewCycle findCycle(Long id) {

        return reviewCycleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review cycle not found with id: " + id
                        )
                );
    }

    private ReviewCycleResponse toResponse(ReviewCycle cycle) {

        return new ReviewCycleResponse(
                cycle.getId(),
                cycle.getName(),
                cycle.getStartDate(),
                cycle.getEndDate(),
                cycle.isActive()
        );
    }

    private String normalizeName(String name) {

        return name.trim().toLowerCase(Locale.ROOT);
    }
}

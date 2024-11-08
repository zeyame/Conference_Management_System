package service;

import domain.model.Conference;
import dto.ConferenceDTO;
import repository.ConferenceRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    public ConferenceService(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    public List<ConferenceDTO> findByIds(Set<String> ids) {
        List<Conference> conferences = conferenceRepository.findByIds(ids);
        return conferences.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ConferenceDTO mapToDTO(Conference conference) {
        return new ConferenceDTO(
            conference.getId(),
            conference.getOrganizerId(),
            conference.getName(),
            conference.getDescription(),
            conference.getStartDate(),
            conference.getEndDate()
        );
    }
}

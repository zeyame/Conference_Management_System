package service;

import domain.factory.ConferenceFactory;
import domain.factory.UserFactory;
import domain.model.Conference;
import domain.model.User;
import dto.ConferenceDTO;
import dto.ConferenceFormDTO;
import exception.ConferenceCreationException;
import exception.UserRegistrationException;
import repository.ConferenceRepository;
import util.LoggerUtil;
import util.PersistenceService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    public ConferenceService(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    public void create(ConferenceFormDTO conferenceFormDTO) {
        // creating conference instance
        Conference conference = ConferenceFactory.createConference(conferenceFormDTO);

        // attempting to save validated conference to file storage with retries if necessary
        boolean isSavedToFile = PersistenceService.saveWithRetry(conference, conferenceRepository::save, 3);
        if (!isSavedToFile) {
            conferenceRepository.removeFromMemory(conference);
            LoggerUtil.getInstance().logError("Conference creation failed. Could not save conference to file storage.");
            throw ConferenceCreationException.savingData();
        }

        LoggerUtil.getInstance().logInfo("Conference with name '" + conference.getName() + "' has successfully been created.");
    }

    public List<ConferenceDTO> findByIds(Set<String> ids) {
        List<Conference> conferences = conferenceRepository.findByIds(ids);
        return conferences.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public boolean isNameTaken(String name) {
        return conferenceRepository.findByName(name).isPresent();
    }

    public boolean isTimePeriodAvailable(Date startDate, Date endDate) {
        List<Conference> conferences = conferenceRepository.findAll();
        return conferences.stream()
                .noneMatch(conference ->
                        (startDate.before(conference.getEndDate()) && endDate.after(conference.getStartDate())) ||
                        endDate.equals(conference.getStartDate()) ||
                        startDate.equals(conference.getEndDate())
                );
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

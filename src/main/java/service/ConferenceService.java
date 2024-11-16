package service;

import domain.factory.ConferenceFactory;
import domain.model.Conference;
import dto.ConferenceDTO;
import dto.ConferenceFormDTO;
import exception.SavingDataException;
import repository.ConferenceRepository;
import util.LoggerUtil;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    public ConferenceService(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    public String create(ConferenceFormDTO conferenceFormDTO) {
        // creating conference instance
        Conference conference = ConferenceFactory.createConference(conferenceFormDTO);

        // attempting to save validated conference to file storage with retries if necessary
        boolean isSavedToFile = conferenceRepository.save(conference);
        if (!isSavedToFile) {
            LoggerUtil.getInstance().logError("Conference creation failed. Could not save conference to file storage.");
            throw new SavingDataException("An unexpected error occurred while saving conference data. Please try again later.");
        }

        LoggerUtil.getInstance().logInfo("Conference with name '" + conference.getName() + "' has successfully been created.");
        return conference.getId();
    }

    public void deleteById(String conferenceId) {
        conferenceRepository.deleteById(conferenceId);
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

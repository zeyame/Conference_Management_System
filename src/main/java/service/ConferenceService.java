package service;

import domain.factory.ConferenceFactory;
import domain.model.Conference;
import dto.ConferenceDTO;
import exception.ConferenceNotFoundException;
import exception.SavingDataException;
import exception.SessionCreationException;
import repository.ConferenceRepository;
import util.LoggerUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    public ConferenceService(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    public String create(ConferenceDTO conferenceDTO) {
        // creating conference instance
        Conference conference = ConferenceFactory.createConference(conferenceDTO);

        // attempting to save validated conference to file storage with retries if necessary
        boolean isSavedToFile = conferenceRepository.save(conference);
        if (!isSavedToFile) {
            LoggerUtil.getInstance().logError("Conference creation failed. Could not save new conference to file storage.");
            throw new SavingDataException("An unexpected error occurred while saving conference data.");
        }

        LoggerUtil.getInstance().logInfo("Conference with name '" + conference.getName() + "' has successfully been created.");
        return conference.getId();
    }

    public void registerSession(String id, String sessionId) {
        Optional<Conference> conferenceOptional = conferenceRepository.findById(id);
        if (conferenceOptional.isEmpty()) {
            throw new ConferenceNotFoundException(String.format("Conference with id '%s' does not exist.", id));
        }

        Conference conference = conferenceOptional.get();
        conference.addSession(sessionId);

        boolean isConferenceUpdated = conferenceRepository.save(conference);
        if (!isConferenceUpdated) {
            conference.removeSession(sessionId);
            throw new SavingDataException(String.format("Unexpected error occurred when registering session to conference '%s'.",  conference.getName()));
        }
    }

    public ConferenceDTO getById(String id) {
        return conferenceRepository
                .findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ConferenceNotFoundException("Conference with id '" + id + "' could not be found"));
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

    public boolean isTimePeriodAvailable(LocalDate startDate, LocalDate endDate) {
        List<Conference> conferences = conferenceRepository.findAll();
        return conferences.stream()
                .noneMatch(conference ->
                        (startDate.isBefore(conference.getEndDate()) && endDate.isAfter(conference.getStartDate())) ||
                        endDate.equals(conference.getStartDate()) ||
                        startDate.equals(conference.getEndDate())
                );
    }

    public void deleteById(String conferenceId) {
        conferenceRepository.deleteById(conferenceId);
    }

    public ConferenceDTO mapToDTO(Conference conference) {
        String organizerId = conference.getOrganizerId(), name = conference.getName(), description = conference.getDescription();
        LocalDate startDate = conference.getStartDate(), endDate = conference.getEndDate();

        System.out.println(startDate.toString() + endDate.toString());

        return ConferenceDTO.builder(organizerId, name, description, startDate, endDate)
                .assignId(conference.getId())
                .sessions(conference.getSessions())
                .attendees(conference.getAttendees())
                .speakers(conference.getSpeakers())
                .build();
    }
}

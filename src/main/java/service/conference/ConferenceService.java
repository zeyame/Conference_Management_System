package service.conference;

import domain.factory.ConferenceFactory;
import domain.model.Conference;
import domain.model.Session;
import dto.ConferenceDTO;
import dto.SessionDTO;
import exception.ConferenceException;
import exception.UserException;
import repository.ConferenceRepository;
import response.ResponseEntity;
import service.UserService;
import util.CollectionUtils;
import util.LoggerUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ConferenceService {
    private final UserService userService;
    private final ConferenceRepository conferenceRepository;

    public ConferenceService(UserService userService, ConferenceRepository conferenceRepository) {
        this.userService = userService;
        this.conferenceRepository = conferenceRepository;
    }

    public void create(ConferenceDTO conferenceDTO) {
        if (conferenceDTO == null) {
            throw new IllegalArgumentException("ConferenceDTO cannot be null.");
        }

        boolean conferenceSaved = false;
        try {
            validateData(conferenceDTO);

            // creating conference
            Conference conference = ConferenceFactory.createConference(conferenceDTO);

            // attempting to save validated conference to file storage with retries if necessary
            conferenceSaved = conferenceRepository.save(conference, conference.getId());
            if (!conferenceSaved) {
                throw ConferenceException.savingFailure("An unexpected error occurred while saving conference data. Please try again later.");
            }

            // add reference to conference in organizer's managed conferences
            assignConferenceToOrganizer(conference);

            LoggerUtil.getInstance().logInfo(String.format("Conference '%s' has successfully been created.", conference.getName()));
        } catch (ConferenceException e) {
            if (conferenceSaved) {
                rollbackSave(conferenceDTO);
            }

            // throw original exception to be handled by controller
            throw e;
        }
    }

    public void update(ConferenceDTO conferenceDTO) {}

    public void registerSession(String id, SessionDTO sessionDTO) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Conference id cannot be null or empty.");
        }

        Optional<Conference> conferenceOptional = conferenceRepository.findById(id);
        if (conferenceOptional.isEmpty()) {
            throw ConferenceException.notFound(String.format("Conference with id '%s' does not exist.", id));
        }

        Conference conference = conferenceOptional.get();
        conference.addSession(sessionDTO.getId());
        conference.addSpeaker(sessionDTO.getSpeakerId());

        boolean isConferenceUpdated = conferenceRepository.save(conference, conference.getId());
        if (!isConferenceUpdated) {
            conference.removeSession(sessionDTO.getId());
            conference.removeSpeaker(sessionDTO.getSpeakerId());
            throw ConferenceException.savingFailure(String.format("Unexpected error occurred when registering session to conference '%s'. Please try again later.",  conference.getName()));
        }

        LoggerUtil.getInstance().logInfo(String.format("Successfully registered session '%s' to '%s'.", sessionDTO.getName(), conference.getName()));

    }

    public ConferenceDTO getById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Conference id cannot be null or empty.");
        }

        return conferenceRepository
                .findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> ConferenceException.notFound(String.format("Conference with id '%s' could not be found.", id)));
    }

    public List<ConferenceDTO> findAllById(Set<String> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Conference ids cannot be null.");
        }

        // batch fetch conferences corresponding to ids
        List<Optional<Conference>> conferenceOptionals = conferenceRepository.findAllById(ids);

        // extract the valid conferences
        List<Conference> conferences = CollectionUtils.extractValidEntities(conferenceOptionals);

        return conferences.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public boolean isNameTaken(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Conference name cannot be null or empty.");
        }

        return conferenceRepository.findByName(name).isPresent();
    }

    public void deleteById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Conference id cannot be null or empty");
        }
        conferenceRepository.deleteById(id);
    }

    public void removeSession(String id, String sessionId) {
        if (id == null || sessionId == null || id.isEmpty() || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Conference and session ids cannot be null or empty.");
        }

        Optional<Conference> conferenceOptional = conferenceRepository.findById(id);
        if (conferenceOptional.isEmpty()) {
            throw ConferenceException.notFound(String.format("Conference with id '%s' does not exist.", id));
        }

        Conference conference = conferenceOptional.get();
        conference.removeSession(sessionId);

        boolean isConferenceUpdated = conferenceRepository.save(conference, conference.getId());
        if (!isConferenceUpdated) {
            conference.addSession(sessionId);
            throw ConferenceException.savingFailure(String.format("Unexpected error occurred when saving updated conference '%s'. Please try again later.",  conference.getName()));
        }

        LoggerUtil.getInstance().logInfo(String.format("Successfully removed session with id '%s' from '%s'.", sessionId, conference.getName()));
    }

    private void validateData(ConferenceDTO conferenceDTO) {
        if (conferenceDTO == null) {
            throw new IllegalArgumentException("ConferenceDTO cannot be null.");
        }

        // implement validation logic
        String name = conferenceDTO.getName();
        LocalDate startDate = conferenceDTO.getStartDate(), endDate = conferenceDTO.getEndDate();

        // ensure conference name is available
        if (isNameTaken(name)) {
            LoggerUtil.getInstance().logWarning("Validation failed for conference creation. Conference name '" + name + "' is already taken.");
            throw ConferenceException.nameTaken(String.format("Conference name '%s' is already taken.", name));
        }

        // ensure selected time period is available
        if (!isTimePeriodAvailable(startDate, endDate)) {
            LoggerUtil.getInstance().logWarning("Validation failed for conference creation. Dates provided for the conference are not available..");
            throw ConferenceException.timeUnavailable("Another conference is already registered to be held within the time" +
                    " period you selected. Please choose different dates.");
        }

        LoggerUtil.getInstance().logInfo("Validation successful for conference: " + name + " with dates: " + startDate + " - " + endDate);
    }


    private boolean isTimePeriodAvailable(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Conference start and end dates cannot be null");
        }

        List<Conference> conferences = conferenceRepository.findAll();
        return conferences.stream()
                .noneMatch(conference ->
                        (startDate.isBefore(conference.getEndDate()) && endDate.isAfter(conference.getStartDate())) ||
                                endDate.equals(conference.getStartDate()) ||
                                startDate.equals(conference.getEndDate())
                );
    }

    private void assignConferenceToOrganizer(Conference conference) {
        try {
            userService.assignConferenceToOrganizer(conference.getOrganizerId(), conference.getId());
        } catch (IllegalArgumentException | UserException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to assign conference '%s' to organizer with id '%s': %s",
                    conference.getName(), conference.getOrganizerId(), e.getMessage()));
            throw ConferenceException.assignmentToOrganizer("Unable to assign the conference to the organizer. " +
                    "Please ensure the organizer ID is valid and belongs to a user with organizer permissions.");
        }
    }

    private void rollbackSave(ConferenceDTO conferenceDTO) {
        boolean conferenceDeleted = conferenceRepository.deleteById(conferenceDTO.getId());
        if (!conferenceDeleted) {
            LoggerUtil.getInstance().logError("Failed to delete conference during rollback save operation.");
        }
    }

    private ConferenceDTO mapToDTO(Conference conference) {
        String organizerId = conference.getOrganizerId(), name = conference.getName(), description = conference.getDescription();
        LocalDate startDate = conference.getStartDate(), endDate = conference.getEndDate();

        return ConferenceDTO.builder(organizerId, name, description, startDate, endDate)
                .setId(conference.getId())
                .setSessions(conference.getSessions())
                .setAttendees(conference.getAttendees())
                .setSpeakers(conference.getSpeakers())
                .setFeedback(conference.getFeedback())
                .build();
    }
}

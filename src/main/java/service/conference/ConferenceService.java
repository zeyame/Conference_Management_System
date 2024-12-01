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

        boolean isSaved = false;
        try {
            validateData(conferenceDTO);

            // creating conference
            Conference conference = ConferenceFactory.createConference(conferenceDTO);

            // saving conference to storage
            save(conference);
            isSaved = true;

            // add reference to conference in organizer's managed conferences
            assignConferenceToOrganizer(conference);

            LoggerUtil.getInstance().logInfo(String.format("Conference '%s' has successfully been created.", conference.getName()));
        } catch (ConferenceException e) {
            if (isSaved) rollbackSave(conferenceDTO);

            // throw original exception to be handled by controller
            throw e;
        }
    }

    public void update(ConferenceDTO conferenceDTO) {
        if (conferenceDTO == null || conferenceDTO.getId() == null || conferenceDTO.getId().isEmpty()) {
            throw new IllegalArgumentException("ConferenceDTO and its ID cannot be null or empty for updates.");
        }

        boolean isDeleted = false;
        try {
            // delete conference from repository for accurate validation
            delete(conferenceDTO.getId());
            isDeleted = true;

            // validate data
            validateData(conferenceDTO);

            // create a new conference with updated data
            Conference conference = ConferenceFactory.createConference(conferenceDTO);

            // save new conference
            save(conference);

        } catch (ConferenceException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to update conference '%s': %s", conferenceDTO.getName(), e.getMessage()));

            if (isDeleted) rollbackDeletion(conferenceDTO);

            throw ConferenceException.updatingFailure(String.format("Error occurred when updating conference data: %s", e.getMessage()));
        }
    }

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
            throw ConferenceException.registeringSession(String.format("Unexpected error occurred when registering session to conference '%s'. Please try again later.",  conference.getName()));
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


    // YET TO EXPAND THIS METHOD
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

    private void save(Conference conference) {
        boolean saved = conferenceRepository.save(conference, conference.getId());
        if (!saved) {
            throw ConferenceException.savingFailure("An unexpected error occurred when try to save conference data. Please try again later.");
        }
    }

    private void delete(String id) {
        boolean deleted = conferenceRepository.deleteById(id);
        if (!deleted) {
            throw ConferenceException.deletingFailure("An unexpected error occurred when try to delete conference. Please try again later.");
        }
    }

    private void validateData(ConferenceDTO conferenceDTO) {
        if (conferenceDTO == null) {
            throw new IllegalArgumentException("ConferenceDTO cannot be null.");
        }

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

        LoggerUtil.getInstance().logInfo(String.format("Validation successful for conference '%s'.", conferenceDTO.getName()));
    }


    private boolean isNameTaken(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Conference name cannot be null or empty.");
        }

        return conferenceRepository.findByName(name).isPresent();
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
        try {
            delete(conferenceDTO.getId());
        } catch (ConferenceException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to delete conference when rolling back conference creation: %s", e.getMessage()));
        }
    }

    private void rollbackDeletion(ConferenceDTO conferenceDTO) {
        try {
            Conference conference = ConferenceFactory.createConference(conferenceDTO);
            save(conference);
        } catch (ConferenceException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to save conference when rolling back conference deletion: %s", e.getMessage()));
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

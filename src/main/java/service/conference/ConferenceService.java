package service.conference;

import domain.factory.ConferenceFactory;
import domain.model.Conference;
import domain.model.Session;
import dto.ConferenceDTO;
import dto.SessionDTO;
import exception.ConferenceException;
import exception.SessionException;
import exception.UserException;
import repository.ConferenceRepository;
import response.ResponseEntity;
import service.ServiceMediator;
import service.UserService;
import util.CollectionUtils;
import util.LoggerUtil;
import util.validation.ConferenceValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ConferenceService {
    private ServiceMediator serviceMediator;
    private final ConferenceRepository conferenceRepository;

    public ConferenceService(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    public void setServiceMediator(ServiceMediator serviceMediator) {
        this.serviceMediator = serviceMediator;
    }

    public void create(ConferenceDTO conferenceDTO) {
        if (conferenceDTO == null) {
            throw new IllegalArgumentException("ConferenceDTO cannot be null.");
        }

        boolean isSaved = false;
        boolean assignedToOrganizer = false;
        try {
            // prepare data for validation
            List<ConferenceDTO> conferenceDTOs = findAll();
            List<SessionDTO> sessionDTOs = serviceMediator.findSessionsByIds(conferenceDTO.getSessions());

            // validate data
            ConferenceValidatorService.validateData(conferenceDTO, conferenceDTOs, sessionDTOs, false);

            // creating conference
            Conference conference = ConferenceFactory.createConference(conferenceDTO);

            // saving conference to storage
            save(conference);
            isSaved = true;

            // add reference to conference in organizer's managed conferences
            assignConferenceToOrganizer(conference);
            assignedToOrganizer = true;

            // notify attendees and all speakers of new upcoming conference
            ConferenceNotificationService.notifyConferenceCreation(
                    conferenceDTO,
                    serviceMediator.findAllAttendees(),
                    serviceMediator.findAllSpeakers()
            );

            LoggerUtil.getInstance().logInfo(String.format("Conference '%s' has successfully been created.", conference.getName()));
        } catch (Exception e) {
            // rollback changes
            rollbackCreateOrUpdate(conferenceDTO, isSaved, assignedToOrganizer);

            // throw original exception to be handled by controller
            throw new ConferenceException(String.format("An error occurred when creating conference: %s", e.getMessage()));
        }
    }

    public void registerSession(SessionDTO sessionDTO) {
        if (sessionDTO == null) {
            throw new IllegalArgumentException("SessionDTO cannot be null.");
        }
        String conferenceId = sessionDTO.getConferenceId();

        Optional<Conference> conferenceOptional = conferenceRepository.findById(conferenceId);
        if (conferenceOptional.isEmpty()) {
            throw new ConferenceException(String.format("Conference with id '%s' does not exist.", conferenceId));
        }

        Conference conference = conferenceOptional.get();

        // add session and speaker references to conference data
        conference.addSession(sessionDTO.getId());
        conference.addSpeaker(sessionDTO.getSpeakerId());

        boolean isConferenceUpdated = conferenceRepository.save(conference, conference.getId());
        if (!isConferenceUpdated) {
            conference.removeSession(sessionDTO.getId());
            conference.removeSpeaker(sessionDTO.getSpeakerId());
            throw new ConferenceException(String.format("Unexpected error occurred when registering session to conference '%s'. Please try again later.",  conference.getName()));
        }

        LoggerUtil.getInstance().logInfo(String.format("Successfully registered session '%s' to '%s'.", sessionDTO.getName(), conference.getName()));
    }

    public void registerAttendeeToConference(String id, String attendeeId) {
        if (id == null || attendeeId == null || id.isEmpty() || attendeeId.isEmpty()) {
            throw new IllegalArgumentException("Conference id and attendee id cannot be null or empty.");
        }

        Optional<Conference> conferenceOptional = conferenceRepository.findById(id);
        if (conferenceOptional.isEmpty()) {
            throw new ConferenceException(String.format("Conference with id '%s' does not exist.", id));
        }

        // adding attendee id to conference data
        Conference conference = conferenceOptional.get();

        boolean addedConferenceToAttendee = false;
        try {
            conference.addAttendee(attendeeId);

            // adding reference to the conference in attendee data
            serviceMediator.addConferenceToAttendee(id, attendeeId);
            addedConferenceToAttendee = true;

            // saving updated conference
            save(conference);

            ConferenceNotificationService.notifyAttendeeRegistrationToConference(mapToDTO(conference), serviceMediator.getUserById(attendeeId));

        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to register attendee to conference: %s", e.getMessage()));

            // rollback changes
            if (addedConferenceToAttendee) {
                ConferenceRollbackService.rollbackAddingConferenceToAttendee(
                        conference.getId(),
                        attendeeId,
                        serviceMediator::removeConferenceFromAttendee);
            }

            throw new ConferenceException(String.format("An error occurred when registering attendee to conference: %s", e.getMessage()));
        }
    }

    public void addFeedback(String id, String feedbackId) {
        if (id == null || feedbackId == null || id.isEmpty() || feedbackId.isEmpty()) {
            throw new IllegalArgumentException("Invalid conference id and/or feedback id.");
        }

        Optional<Conference> conferenceOptional = conferenceRepository.findById(id);
        if (conferenceOptional.isEmpty()) {
            throw new ConferenceException(String.format("Conference with id '%s' does not exist.", id));
        }

        Conference conference = conferenceOptional.get();
        conference.addFeedback(feedbackId);

        save(conference);
    }

    public ConferenceDTO getById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Invalid conference id.");
        }

        return conferenceRepository
                .findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ConferenceException(String.format("Conference with id '%s' could not be found.", id)));
    }

    public List<ConferenceDTO> findAll() {
        return conferenceRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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

    public List<ConferenceDTO> findAllUpcoming() {
        List<Conference> conferences = conferenceRepository.findAll();

        return conferences.stream()
                .filter(conference -> conference != null &&
                        LocalDateTime.of(conference.getStartDate(), LocalTime.MIN).isAfter(LocalDateTime.now()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public void update(ConferenceDTO conferenceDTO) {
        if (conferenceDTO == null || conferenceDTO.getId() == null || conferenceDTO.getId().isEmpty()) {
            throw new IllegalArgumentException("ConferenceDTO and its ID cannot be null or empty for updates.");
        }

        try {
            // fetch existing conference for merging
            Optional<Conference> conferenceOptional = conferenceRepository.findById(conferenceDTO.getId());
            if (conferenceOptional.isEmpty()) {
                throw new ConferenceException("Conference not found.");
            }

            Conference existingConference = conferenceOptional.get();

            // merge data
            mergeData(existingConference, conferenceDTO);

            // validate data
            List<ConferenceDTO> conferenceDTOs = findAll();
            List<SessionDTO> sessionDTOs = serviceMediator.findSessionsByIds(conferenceDTO.getSessions());
            ConferenceValidatorService.validateData(conferenceDTO, conferenceDTOs, sessionDTOs, true);

            // Create and save updated conference
            Conference updatedConference = ConferenceFactory.createConference(conferenceDTO);
            save(updatedConference);

            // Notify attendees and speakers
            ConferenceNotificationService.notifyConferenceChange(
                    conferenceDTO,
                    serviceMediator.findAllUsersById(conferenceDTO.getAttendees()),
                    serviceMediator.findAllUsersById(conferenceDTO.getSpeakers())
            );

        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to update conference '%s': %s", conferenceDTO.getName(), e.getMessage()));
            throw new ConferenceException(String.format("Error occurred when updating conference data: %s", e.getMessage()));
        }
    }


    public void deleteById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Conference id cannot be null or empty");
        }

        // get conference data
        Optional<Conference> conferenceOptional = conferenceRepository.findById(id);
        if (conferenceOptional.isEmpty()) {
            throw new ConferenceException(String.format("Conference with id '%s' does not exist.", id));
        }

        Conference conference = conferenceOptional.get();
        Set<String> attendees = conference.getAttendees(), speakers = conference.getSpeakers();

        try {
            // delete all sessions within conference
            serviceMediator.deleteAllSessionsById(conference.getSessions());

            // delete all feedback within conference
            serviceMediator.deleteAllFeedbackById(conference.getFeedback());

            // delete conference from repository
            conferenceRepository.deleteById(id);

            // notify attendees and speakers of cancellation
            ConferenceNotificationService.notifyConferenceDeletion(
                    mapToDTO(conference),
                    serviceMediator.findAllUsersById(attendees),
                    serviceMediator.findAllUsersById(speakers)
            );
        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to delete conference '%s': %s", conference.getName(), e.getMessage()));
            throw new ConferenceException(e.getMessage());
        }
    }

    public void removeSession(String id, String sessionId) {
        if (id == null || sessionId == null || id.isEmpty() || sessionId.isEmpty()) {
            throw new IllegalArgumentException("Conference and session ids cannot be null or empty.");
        }

        Optional<Conference> conferenceOptional = conferenceRepository.findById(id);
        if (conferenceOptional.isEmpty()) {
            throw new ConferenceException(String.format("Conference with id '%s' does not exist.", id));
        }

        Conference conference = conferenceOptional.get();
        conference.removeSession(sessionId);

        boolean isConferenceUpdated = conferenceRepository.save(conference, conference.getId());
        if (!isConferenceUpdated) {
            conference.addSession(sessionId);
            throw new ConferenceException(String.format("Unexpected error occurred when saving updated conference '%s'. Please try again later.",  conference.getName()));
        }

        LoggerUtil.getInstance().logInfo(String.format("Successfully removed session with id '%s' from '%s'.", sessionId, conference.getName()));
    }

    public void removeAttendee(String id, String attendeeId) {
        if (id == null || attendeeId == null || id.isEmpty() || attendeeId.isEmpty()) {
            throw new IllegalArgumentException("Conference id and attendee id cannot be null or empty.");
        }

        Optional<Conference> conferenceOptional = conferenceRepository.findById(id);
        if (conferenceOptional.isEmpty()) {
            throw new ConferenceException(String.format("Conference with id '%s' does not exist.", id));
        }

        Conference conference = conferenceOptional.get();

        boolean isSaved = false;
        try {
            conference.removeAttendee(attendeeId);

            save(conference);
            isSaved = true;

            // remove conference reference from attendee's registered conferences
            serviceMediator.removeConferenceFromAttendee(id, attendeeId);

            // notify attendee that he has been unregistered from the conference
            ConferenceNotificationService.notifyAttendeeUnregisteredFromConference(mapToDTO(conference), serviceMediator.getUserById(attendeeId));

        } catch (Exception e) {
            LoggerUtil.getInstance().logError(String.format("Failed to remove attendee with id '%s' from conference '%s': %s", attendeeId, conference.getName(), e.getMessage()));

            // rollback removing attendee from conference
            if (isSaved) ConferenceRollbackService.rollbackRemovingAttendeeFromConference(conference.getId(), attendeeId, this::registerAttendeeToConference);

            throw new ConferenceException(String.format("An unexpected error occurred when removing attendee from conference: %s", e.getMessage()));
        }
    }


    // helpers
    private void save(Conference conference) {
        boolean saved = conferenceRepository.save(conference, conference.getId());
        if (!saved) {
            throw new ConferenceException("An unexpected error occurred when try to save conference data. Please try again later.");
        }
    }

    private void delete(String id) {
        boolean deleted = conferenceRepository.deleteById(id);
        if (!deleted) {
            throw new ConferenceException("An unexpected error occurred when try to delete conference. Please try again later.");
        }
    }

    private void assignConferenceToOrganizer(Conference conference) {
        try {
            serviceMediator.assignConferenceToOrganizer(conference.getId(), conference.getOrganizerId());
        } catch (IllegalArgumentException | UserException e) {
            LoggerUtil.getInstance().logError(String.format("Failed to assign conference '%s' to organizer with id '%s': %s",
                    conference.getName(), conference.getOrganizerId(), e.getMessage()));
            throw new ConferenceException("Unable to assign the conference to the organizer. " +
                    "Please ensure the organizer ID is valid and belongs to a user with organizer permissions.");
        }
    }

    private void rollbackCreateOrUpdate(ConferenceDTO conferenceDTO, boolean isSaved, boolean assignedToOrganizer) {
        // roll back save
        if (isSaved) ConferenceRollbackService.rollbackSave(conferenceDTO.getId(), this::delete);

        // roll back assigning conference to organizer
        if (assignedToOrganizer) ConferenceRollbackService.rollbackAssignmentToOrganzer(
                conferenceDTO.getId(), conferenceDTO.getOrganizerId(), serviceMediator::unassignConferenceFromOrganizer
        );
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

    private void mergeData(Conference existingConference, ConferenceDTO conferenceDTO) {
        conferenceDTO.setSessions(existingConference.getSessions());
        conferenceDTO.setAttendees(existingConference.getAttendees());
        conferenceDTO.setSpeakers(existingConference.getSpeakers());
        conferenceDTO.setFeedback(existingConference.getFeedback());
    }
}

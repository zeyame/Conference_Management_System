package service.conference;

import dto.ConferenceDTO;
import dto.SessionDTO;
import exception.ConferenceException;
import util.LoggerUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ConferenceValidatorService {

    public static void validateData(ConferenceDTO conferenceDTO, List<ConferenceDTO> conferenceDTOs, List<SessionDTO> sessionDTOS, boolean isUpdate) {
        if (conferenceDTO == null) {
            throw new IllegalArgumentException("ConferenceDTO cannot be null.");
        }

        // if update request:
            // remove current conference data from list of conferences for accurate validation of names and dates
            // check conferences dates validity with existing session dates
        if (isUpdate) {
            removeConferenceFromList(conferenceDTO, conferenceDTOs);
            validateConferenceDatesWithExistingSessions(conferenceDTO.getStartDate(), conferenceDTO.getEndDate(), sessionDTOS);
        }

        // ensure conference name is available
        validateConferenceName(conferenceDTO.getName(), conferenceDTOs);

        // ensure conference dates do not clash with other conferences
        validateConferenceDates(conferenceDTOs, conferenceDTO.getStartDate(), conferenceDTO.getEndDate());

        LoggerUtil.getInstance().logInfo(String.format("Validation successful for conference '%s'.", conferenceDTO.getName()));
    }

    private static void validateConferenceDatesWithExistingSessions(LocalDate startDate, LocalDate endDate, List<SessionDTO> sessionDTOS) {
        if (startDate == null || endDate == null || sessionDTOS == null) {
            throw new IllegalArgumentException("Start date, end date, and session dtos cannot be null.");
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        boolean conflictExists = sessionDTOS.stream()
                .anyMatch(sessionDTO ->
                        !sessionDTO.overlapsWith(startDateTime, endDateTime)
                );

        if (conflictExists) {
            throw new ConferenceException("Some of the sessions in this conference fall outside the updated conference dates." +
                    " Please adjust the session times to fit within the new conference schedule.");
        }

    }

    private static void validateConferenceName(String name, List<ConferenceDTO> conferenceDTOS) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Conference name cannot be null or empty.");
        }

        boolean isNameTaken = conferenceDTOS.stream()
                .anyMatch(conferenceDTO -> name.equalsIgnoreCase(conferenceDTO.getName()));

        if (isNameTaken) {
            LoggerUtil.getInstance().logError("Conference name validation failed: Session name is taken.");
            throw new ConferenceException("A conference with this name is already registered. Please choose a different name.");
        }
    }

    private static void validateConferenceDates(List<ConferenceDTO> conferenceDTOS, LocalDate startDate, LocalDate endDate) {
        if (conferenceDTOS == null || startDate == null || endDate == null) {
            throw new IllegalArgumentException("ConferenceDTOs and conference start and end dates cannot be null.");
        }

        // ensure selected time period is available
        if (!isTimePeriodAvailable(conferenceDTOS, startDate, endDate)) {
            LoggerUtil.getInstance().logWarning("Validation failed for conference creation/update. Dates provided for the conference are not available..");
            throw new ConferenceException("Another conference is already registered to be held within the time" +
                    " period you selected. Please choose different dates.");
        }
    }

    private static boolean isTimePeriodAvailable(List<ConferenceDTO> conferenceDTOS, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Conference start and end dates cannot be null");
        }

        return conferenceDTOS.stream()
                .noneMatch(conference ->
                        (startDate.isBefore(conference.getEndDate()) && endDate.isAfter(conference.getStartDate())) ||
                                endDate.equals(conference.getStartDate()) ||
                                startDate.equals(conference.getEndDate())
                );
    }

    private static void removeConferenceFromList(ConferenceDTO conferenceDTO, List<ConferenceDTO> conferenceDTOs) {
        conferenceDTOs.removeIf(conference -> conference.getId().equals(conferenceDTO.getId()));
    }


}

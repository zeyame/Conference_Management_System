package view.observers;

import controller.AttendeeController;
import dto.ConferenceDTO;
import dto.FeedbackDTO;
import dto.UserDTO;
import response.ResponseEntity;
import util.LoggerUtil;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AttendeeConferenceManager implements ConferenceEventObserver {

    private final AttendeeController attendeeController;

    public AttendeeConferenceManager(AttendeeController attendeeController) {
        this.attendeeController = attendeeController;
    }

    @Override
    public void onRegisterForAConference(String attendeeId, String conferenceId, Consumer<String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Request to register attendee with id '%s' for conference '%s' received.", attendeeId, conferenceId));

        ResponseEntity<Void> registerAttendeeResponse = attendeeController.registerForConference(attendeeId, conferenceId);
        if (registerAttendeeResponse.isSuccess()) {
            callback.accept(null);
        } else {
            callback.accept(registerAttendeeResponse.getErrorMessage());
        }
    }

    @Override
    public void onSubmitFeedback(FeedbackDTO feedbackDTO, Consumer<String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Attendee request to submit feedback for conference '%s' received.", feedbackDTO.getConferenceId()));

        ResponseEntity<Void> submitFeedbackResponse = attendeeController.submitFeedback(feedbackDTO);
        if (submitFeedbackResponse.isSuccess()) {
            callback.accept(null);
        } else {
            callback.accept(submitFeedbackResponse.getErrorMessage());
        }
    }

    @Override
    public void onGetUpcomingConferences(String attendeeId, BiConsumer<List<ConferenceDTO>, String> callback) {LoggerUtil.getInstance().logInfo(String.format("Request to fetch upcoming conferences for attendee with id  '%s' received.", attendeeId));

        ResponseEntity<List<ConferenceDTO>> conferencesResponse = attendeeController.getUpcomingConferences(attendeeId);
        if (conferencesResponse.isSuccess()) {
            callback.accept(conferencesResponse.getData(), null);
        } else {
            callback.accept(null, conferencesResponse.getErrorMessage());
        }
    }

    @Override
    public void onConferenceSelected(String conferenceId, BiConsumer<ConferenceDTO, String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Request to fetch data for conference with id '%s' received.", conferenceId));

        ResponseEntity<ConferenceDTO> conferenceResponse = attendeeController.getConference(conferenceId);
        if (conferenceResponse.isSuccess()) {
            callback.accept(conferenceResponse.getData(), null);
        } else {
            callback.accept(null, conferenceResponse.getErrorMessage());
        }
    }

    @Override
    public void onGetOrganizerName(String organizerId, BiConsumer<String, String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Request to fetch organizer name for organizer with id '%s' received.", organizerId));

        ResponseEntity<String> organizerNameResponse = attendeeController.getOrganizerName(organizerId);
        if (organizerNameResponse.isSuccess()) {
            callback.accept(organizerNameResponse.getData(), null);
        } else {
            callback.accept(null, organizerNameResponse.getErrorMessage());
        }
    }

    @Override
    public void onGetSpeakers(String conferenceId, BiConsumer<List<UserDTO>, String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Attendee request to view speakers for conference '%s' received.", conferenceId));

        ResponseEntity<List<UserDTO>> getSpeakersInConferenceResponse = attendeeController.getSpeakersInConference(conferenceId);
        if (getSpeakersInConferenceResponse.isSuccess()) {
            callback.accept(getSpeakersInConferenceResponse.getData(), null);
        } else {
            callback.accept(null, getSpeakersInConferenceResponse.getErrorMessage());
        }
    }

    @Override
    public void onGetSpeakerBios(Set<String> speakerIds, BiConsumer<Map<String, String>, String> callback) {
        LoggerUtil.getInstance().logInfo("Request to fetch speaker bios received.");

        ResponseEntity<Map<String, String>> speakerBiosResponse = attendeeController.getSpeakerBios(speakerIds);
        if (speakerBiosResponse.isSuccess()) {
            callback.accept(speakerBiosResponse.getData(), null);
        } else {
            callback.accept(null, speakerBiosResponse.getErrorMessage());
        }
    }

    @Override
    public void onGetRegisteredConferences(String attendeeId, BiConsumer<List<ConferenceDTO>, String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Request to get registered conferences for attendee with id '%s' received.", attendeeId));

        ResponseEntity<List<ConferenceDTO>> registeredConferencesResponse = attendeeController.getRegisteredConferences(attendeeId);
        if (registeredConferencesResponse.isSuccess()) {
            callback.accept(registeredConferencesResponse.getData(), null);
        } else {
            callback.accept(null, registeredConferencesResponse.getErrorMessage());
        }
    }

    @Override
    public void onLeaveConference(String attendeeId, String conferenceId, Consumer<String> callback) {
        LoggerUtil.getInstance().logInfo(String.format("Request for attendee with id '%s' to leave conference with id '%s' was received.", attendeeId, conferenceId));

        ResponseEntity<Void> leaveConferenceResponse = attendeeController.leaveConference(attendeeId, conferenceId);
        if (leaveConferenceResponse.isSuccess()) {
            callback.accept(null);
        } else {
            callback.accept(leaveConferenceResponse.getErrorMessage());
        }
    }

}

package util.email;

import domain.model.user.UserRole;
import dto.ConferenceDTO;
import dto.SessionDTO;
import dto.UserDTO;

public class EmailContentService {

    // private no-arg constructor to suppress instantiability
    private EmailContentService() {}

    // WELCOME BODY MESSAGE
    public static String getWelcomeMessage(String userName, UserRole role) {
        return switch (role) {
            case ORGANIZER -> getWelcomeMessageForOrganizer(userName);
            case ATTENDEE -> getWelcomeMessageForAttendee(userName);
            case SPEAKER -> getWelcomeMessageForSpeaker(userName);
        };
    }

    // SESSION BODY MESSAGE
    public static String getSessionCreationBody(SessionDTO sessionDTO, UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case ATTENDEE -> getAttendeeSessionCreationMessage(sessionDTO, userDTO.getName());
            case SPEAKER -> getSpeakerSessionCreationMessage(sessionDTO, userDTO.getName());
            case ORGANIZER -> throw new IllegalArgumentException("User must either have attendee or speaker permissions.");
        };
    }

    public static String getSessionChangeBody(SessionDTO sessionDTO, UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case ATTENDEE -> getAttendeeSessionChangeMessage(sessionDTO, userDTO.getName());
            case SPEAKER -> getSpeakerSessionChangeMessage(sessionDTO, userDTO.getName());
            case ORGANIZER -> throw new IllegalArgumentException("User must either have attendee or speaker permissions.");
        };
    }

    public static String getSessionDeletionBody(SessionDTO sessionDTO, UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case ATTENDEE -> getAttendeeSessionDeletionMessage(sessionDTO, userDTO.getName());
            case SPEAKER -> getSpeakerSessionDeletionMessage(sessionDTO, userDTO.getName());
            case ORGANIZER -> throw new IllegalArgumentException("User must either have attendee or speaker permissions.");
        };
    }

    // CONFERENCE BODY MESSAGES
    public static String getConferenceCreationBody(ConferenceDTO conferenceDTO, UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case ATTENDEE -> getAttendeeConferenceCreationMessage(conferenceDTO, userDTO.getName());
            case SPEAKER -> getSpeakerConferenceCreationMessage(conferenceDTO, userDTO.getName());
            case ORGANIZER -> throw new IllegalArgumentException("User must either have attendee or speaker permissions.");
        };
    }

    public static String getConferenceChangeBody(ConferenceDTO conferenceDTO, UserDTO userDTO) {
        return switch (userDTO.getRole()) {
            case ATTENDEE -> getAttendeeConferenceChangeMessage(conferenceDTO, userDTO.getName());
            case SPEAKER -> getSpeakerConferenceChangeMessage(conferenceDTO, userDTO.getName());
            case ORGANIZER -> throw new IllegalArgumentException("User must either have attendee or speaker permissions.");
        };
    }

    public static String getConferenceDeletionBody(ConferenceDTO conferenceDTO, UserDTO userDTO) {
        if (conferenceDTO == null || userDTO == null) {
            throw new IllegalArgumentException("Invalid conference and/or user data.");
        }

        return switch (userDTO.getRole()) {
            case ATTENDEE -> getAttendeeConferenceDeletionMessage(conferenceDTO, userDTO.getName());
            case SPEAKER -> getSpeakerConferenceDeletionMessage(conferenceDTO, userDTO.getName());
            case ORGANIZER -> throw new IllegalArgumentException("User must either have attendee or speaker permissions.");
        };
    }

    public static String getAttendeeRegisteredToConferenceBody(ConferenceDTO conferenceDTO, UserDTO attendee) {
        if (conferenceDTO == null || attendee == null) {
            throw new IllegalArgumentException("Invalid conference and/or attendee data.");
        }

        return String.format("Dear %s,\n\n" +
                        "We are delighted to inform you that you have successfully registered for the following conference:\n\n" +
                        "Name: %s\nDescription: %s\nStart Date: %s\nEnd Date: %s\nLocation: The University of Hertfordshire Campus\n\n" +
                        "We are thrilled to have you join us for this exciting event! This conference promises to offer insightful sessions, engaging speakers, and invaluable networking opportunities. " +
                        "We hope you find it both enriching and inspiring.\n\n" +
                        "Should you have any questions or require assistance leading up to the conference, please feel free to reach out. " +
                        "We look forward to your active participation and hope this will be a memorable experience for you.\n\n" +
                        "Kind regards,\n\n" +
                        "The University of Hertfordshire Team",
                attendee.getName(), conferenceDTO.getName(), conferenceDTO.getDescription(),
                conferenceDTO.getStartDate(), conferenceDTO.getEndDate());
    }

    public static String getAttendeeUnregisteredFromConferenceBody(ConferenceDTO conferenceDTO, UserDTO attendee) {
        if (conferenceDTO == null || attendee == null) {
            throw new IllegalArgumentException("Invalid conference and/or attendee data.");
        }

        return String.format("Dear %s,\n\n" +
                        "We are writing to confirm that you have successfully unregistered from the following conference:\n\n" +
                        "Name: %s\nDescription: %s\nStart Date: %s\nEnd Date: %s\nLocation: The University of Hertfordshire Campus\n\n" +
                        "We are sorry to see you go and truly hope this decision does not hinder your participation in other events. " +
                        "Conferences like '%s' thrive on the diverse perspectives and insights contributed by attendees like you.\n\n" +
                        "If this unregistration was unintentional or if there is any way we can assist you, please do not hesitate to reach out. " +
                        "We would be delighted to welcome you back.\n\n" +
                        "Thank you for your interest in our events, and we hope to see you again in the future.\n\n" +
                        "Kind regards,\n\n" +
                        "The University of Hertfordshire Team",
                attendee.getName(), conferenceDTO.getName(), conferenceDTO.getDescription(),
                conferenceDTO.getStartDate(), conferenceDTO.getEndDate(), conferenceDTO.getName());
    }

    // WELCOME MESSAGES
    private static String getWelcomeMessageForAttendee(String attendeeName) {
        return "Dear " + attendeeName + ",\n\n"
                + "Welcome to the University of Hertfordshire's Conference Platform!\n\n"
                + "We are thrilled that you've chosen to explore the exciting and diverse scientific conferences we offer. "
                + "Our platform is designed to connect you with cutting-edge research, inspiring speakers, and thought-provoking sessions.\n\n"
                + "Feel free to browse through the list of upcoming conferences and register for those that capture your interest. "
                + "This is a wonderful opportunity to expand your knowledge, network with professionals, and be a part of the academic community.\n\n"
                + "Should you need any assistance, our team is here to help you every step of the way.\n\n"
                + "We look forward to your participation!\n\n"
                + "Best regards,\n\n"
                + "The University of Hertfordshire Team";
    }

    private static String getWelcomeMessageForOrganizer(String organizerName) {
        return "Welcome to the Conference Management Platform, " + organizerName + "!\n\n"
                + "We are thrilled to have you as part of our team! As an organizer, you play a vital role in curating "
                + "exceptional events and bringing together participants for memorable experiences.\n\n"
                + "On this platform, you can manage conferences, create engaging sessions, and collaborate with speakers and attendees. "
                + "Your expertise and dedication will ensure the success of our events and strengthen our community.\n\n"
                + "Feel free to explore the upcoming conferences, manage sessions, and utilize all the tools available to "
                + "streamline your responsibilities. If you have any questions, don’t hesitate to reach out.\n\n"
                + "Thank you for your commitment, and we look forward to seeing the incredible work you’ll do!\n\n"
                + "Warm regards,\n\n"
                + "The University of Hertfordshire Team";
    }

    private static String getWelcomeMessageForSpeaker(String speakerName) {
        return "Welcome to the Conference Management Platform, " + speakerName + "!\n\n"
                + "We are delighted to have you join us as a speaker. Your expertise and insights play a key role in "
                + "shaping enriching and inspiring sessions for our attendees.\n\n"
                + "This platform will help you manage your speaking engagements, view session details, and connect with "
                + "event organizers and participants. Feel free to explore the tools and features available to ensure your "
                + "sessions are seamless and impactful.\n\nThank you for being a part of our mission to foster knowledge "
                + "sharing and collaboration. We are excited to witness the value you bring to our events!\n\n"
                + "Warm regards,\n\n"
                + "The University of Hertfordshire Team";
    }


    // SESSION CHANGE MESSAGES
    private static String getAttendeeSessionChangeMessage(SessionDTO sessionDTO, String attendeeName) {
        return String.format("Hello %s,\n\nWe kindly inform you that an update has taken place to one of the sessions you are registered for. " +
                        "These are the updated session details:\n\n" +
                        "Name: %s\nSpeaker: %s\nDescription: %s\nRoom: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\n\n" +
                        "We are sorry if this has caused any inconvenience and we hope that you can still make it to the session.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                attendeeName, sessionDTO.getName(), sessionDTO.getSpeakerName(),
                sessionDTO.getDescription(), sessionDTO.getRoom(), sessionDTO.getDate(),
                sessionDTO.getStartTime(), sessionDTO.getEndTime());
    }

    private static String getSpeakerSessionChangeMessage(SessionDTO sessionDTO, String speakerName) {
        return String.format("Hello %s,\n\nWe kindly inform you that an update has taken place to one of the sessions you are " +
                        "assigned to speak at. These are the updated session details:\n\n" +
                        "Name: %s\nSpeaker: %s\nDescription: %s\nRoom: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\n\n" +
                        "We want to let you know that this has happened due to unforeseen circumstances and we sincerely apologize " +
                        "if this has caused any inconvenience. We hope that you can still speak at the session, but if not, " +
                        "please contact the UH Conference Management team so we can make the necessary adjustments.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                speakerName, sessionDTO.getName(), sessionDTO.getSpeakerName(),
                sessionDTO.getDescription(), sessionDTO.getRoom(), sessionDTO.getDate(),
                sessionDTO.getStartTime(), sessionDTO.getEndTime());
    }

    private static String getAttendeeSessionCreationMessage(SessionDTO sessionDTO, String attendeeName) {
        return String.format("Hello %s,\n\nWe are excited to inform you that a new session has been added to a conference you are registered for. " +
                        "Here are the details of the new session:\n\n" +
                        "Session Name: %s\nSpeaker: %s\nDescription: %s\nRoom: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\n\n" +
                        "We hope this new session piques your interest and we look forward to your participation. Should you need any assistance, feel free to reach out.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                attendeeName, sessionDTO.getName(), sessionDTO.getSpeakerName(),
                sessionDTO.getDescription(), sessionDTO.getRoom(), sessionDTO.getDate(),
                sessionDTO.getStartTime(), sessionDTO.getEndTime());
    }

    private static String getSpeakerSessionCreationMessage(SessionDTO sessionDTO, String speakerName) {
        return String.format("Hello %s,\n\nWe are pleased to inform you that you have been assigned as the speaker for a new session at the upcoming conference. " +
                        "Here are the details of your session:\n\n" +
                        "Session Name: %s\nSpeaker: %s\nDescription: %s\nRoom: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\n\n" +
                        "We are thrilled to have you as part of this event and look forward to your presentation. If you have any questions or need assistance, don't hesitate to get in touch.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                speakerName, sessionDTO.getName(), sessionDTO.getSpeakerName(),
                sessionDTO.getDescription(), sessionDTO.getRoom(), sessionDTO.getDate(),
                sessionDTO.getStartTime(), sessionDTO.getEndTime());
    }

    private static String getAttendeeSessionDeletionMessage(SessionDTO sessionDTO, String attendeeName) {
        return String.format("Hello %s,\n\nWe regret to inform you that the session you were registered for has been removed from the conference schedule. " +
                        "Here are the details of the deleted session:\n\n" +
                        "Session Name: %s\nSpeaker: %s\nDescription: %s\nRoom: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\n\n" +
                        "We sincerely apologize for any inconvenience this may have caused. Please check the updated schedule for other sessions of interest. " +
                        "If you need assistance, feel free to reach out.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                attendeeName, sessionDTO.getName(), sessionDTO.getSpeakerName(),
                sessionDTO.getDescription(), sessionDTO.getRoom(), sessionDTO.getDate(),
                sessionDTO.getStartTime(), sessionDTO.getEndTime());
    }

    private static String getSpeakerSessionDeletionMessage(SessionDTO sessionDTO, String speakerName) {
        return String.format("Hello %s,\n\nWe regret to inform you that the session you were assigned to speak at has been removed from the conference schedule. " +
                        "Here are the details of the deleted session:\n\n" +
                        "Session Name: %s\nSpeaker: %s\nDescription: %s\nRoom: %s\nDate: %s\nStart Time: %s\nEnd Time: %s\n\n" +
                        "We sincerely apologize for any inconvenience this may have caused. Please contact us if you would like to be reassigned to another session or have any questions.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                speakerName, sessionDTO.getName(), sessionDTO.getSpeakerName(),
                sessionDTO.getDescription(), sessionDTO.getRoom(), sessionDTO.getDate(),
                sessionDTO.getStartTime(), sessionDTO.getEndTime());
    }

    // CONFERENCE CREATION MESSAGES
    private static String getAttendeeConferenceCreationMessage(ConferenceDTO conferenceDTO, String attendeeName) {
        return String.format("Hello %s,\n\nWe are thrilled to inform you about a new conference:\n\n" +
                        "Name: %s\nDescription: %s\nStart Date: %s\nEnd Date: %s\nLocation: The University of Hertfordshire Campus\n\n" +
                        "We hope you will join us to explore the exciting topics and engage with fellow attendees.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                attendeeName, conferenceDTO.getName(), conferenceDTO.getDescription(),
                conferenceDTO.getStartDate(), conferenceDTO.getEndDate());
    }

    private static String getSpeakerConferenceCreationMessage(ConferenceDTO conferenceDTO, String speakerName) {
        return String.format("Hello %s,\n\nWe are delighted to invite you as a speaker to a new conference:\n\n" +
                        "Name: %s\nDescription: %s\nStart Date: %s\nEnd Date: %s\nLocation: The University of Hertfordshire Campus\n\n" +
                        "We look forward to your contribution and insights at this exciting event.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                speakerName, conferenceDTO.getName(), conferenceDTO.getDescription(),
                conferenceDTO.getStartDate(), conferenceDTO.getEndDate());
    }


    // CONFERENCE CHANGE MESSAGES
    private static String getAttendeeConferenceChangeMessage(ConferenceDTO conferenceDTO, String attendeeName) {
        return String.format("Hello %s,\n\nPlease note that there has been a change to the conference details:\n\n" +
                        "Name: %s\nDescription: %s\nStart Date: %s\nEnd Date: %s\nLocation: The University of Hertfordshire Campus\n\n" +
                        "We apologize for any inconvenience caused and look forward to your continued participation.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                attendeeName, conferenceDTO.getName(), conferenceDTO.getDescription(),
                conferenceDTO.getStartDate(), conferenceDTO.getEndDate());
    }

    private static String getSpeakerConferenceChangeMessage(ConferenceDTO conferenceDTO, String speakerName) {
        return String.format("Hello %s,\n\nPlease be informed that there has been a change to the conference you are speaking at:\n\n" +
                        "Name: %s\nDescription: %s\nStart Date: %s\nEnd Date: %s\nLocation: The University of Hertfordshire Campus\n\n" +
                        "We apologize for any inconvenience caused and look forward to your participation.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                speakerName, conferenceDTO.getName(), conferenceDTO.getDescription(),
                conferenceDTO.getStartDate(), conferenceDTO.getEndDate());
    }

    // CONFERENCE DELETION MESSAGES
    private static String getAttendeeConferenceDeletionMessage(ConferenceDTO conferenceDTO, String attendeeName) {
        return String.format("Hello %s,\n\nWe regret to inform you that the following conference has been cancelled:\n\n" +
                        "Name: %s\nDescription: %s\nStart Date: %s\nEnd Date: %s\nLocation: The University of Hertfordshire Campus\n\n" +
                        "We sincerely apologize for any inconvenience caused and hope to see you at future events.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                attendeeName, conferenceDTO.getName(), conferenceDTO.getDescription(),
                conferenceDTO.getStartDate(), conferenceDTO.getEndDate());
    }

    private static String getSpeakerConferenceDeletionMessage(ConferenceDTO conferenceDTO, String speakerName) {
        return String.format("Hello %s,\n\nWe regret to inform you that the following conference, where you were scheduled to speak, has been cancelled:\n\n" +
                        "Name: %s\nDescription: %s\nStart Date: %s\nEnd Date: %s\nLocation: The University of Hertfordshire Campus\n\n" +
                        "We sincerely apologize for any inconvenience caused and hope to work with you in future events.\n\n" +
                        "Kind Regards,\n\nThe University of Hertfordshire Team",
                speakerName, conferenceDTO.getName(), conferenceDTO.getDescription(),
                conferenceDTO.getStartDate(), conferenceDTO.getEndDate());
    }
}

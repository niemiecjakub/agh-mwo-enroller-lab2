package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.persistence.MeetingService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") Long id){
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participants = meeting.getParticipants();
        return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addMeetingParticipants(@PathVariable("id") Long id, @RequestBody List<String> logins){
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity("Meeting not found", HttpStatus.NOT_FOUND);
        }
        for (String login: logins) {
            Participant participant = participantService.findByLogin(login);
            if (participant == null) {
                return new ResponseEntity("User not found", HttpStatus.NOT_FOUND);
            }
            meeting.addParticipant(participant);
        }
        meetingService.save(meeting);
        return new ResponseEntity<Meeting>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeMeetingParticipants(@PathVariable("id") Long id, @PathVariable("login") String login){
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity("Meeting not found", HttpStatus.NOT_FOUND);
        }
        Participant participant = participantService.findByLogin(login);
        if (participant == null) {
            return new ResponseEntity("User not found", HttpStatus.NOT_FOUND);
        }
        if (!meeting.getParticipants().contains(participant)){
            return new ResponseEntity("User is not part of this meeting", HttpStatus.NOT_FOUND);
        }
        meeting.removeParticipant(participant);
        meetingService.save(meeting);
        return new ResponseEntity<Meeting>(HttpStatus.OK);
    }
}

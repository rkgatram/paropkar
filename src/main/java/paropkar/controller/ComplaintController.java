package paropkar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paropkar.dao.ComplaintDAO;
import paropkar.model.Complaint;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
public class ComplaintController {

    private final ComplaintDAO complaintDAO;

    @Autowired
    public ComplaintController(final ComplaintDAO complaintDAO) {
        this.complaintDAO = complaintDAO;
    }

    @RequestMapping("/register")
    public CompletableFuture<ResponseEntity<String>> fileComplaint(final Complaint complaint) {
        return complaintDAO.insert(getParams(complaint))
                .thenApply(count -> {
                    if (count > 0) {
                        return ResponseEntity.ok().body("{}");
                    } else {
                        return new ResponseEntity<>("{\"Error\": \"Failed to insert into database\"}",
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }).exceptionally(throwable -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @RequestMapping("/getUser")
    public CompletableFuture<ResponseEntity<Complaint>> getComplaint(final String id) {
        return complaintDAO.getObject(id)
                .thenApply(complaint -> ResponseEntity.ok().body(complaint))
                .exceptionally(throwable -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @RequestMapping("/getAllUser")
    public CompletableFuture<ResponseEntity<List<Complaint>>> getAllComplaints() {
        return complaintDAO.getAll()
                .thenApply(complaints -> ResponseEntity.ok().body(complaints))
                .exceptionally(throwable -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private Object[] getParams(final Complaint complaint) {
        return new Object[]{
                UUID.randomUUID().toString(),
                complaint.getTitle(),
                complaint.getContent(),
                complaint.getCity(),
                complaint.getDepartment(),
                complaint.getType(),
                new Timestamp(new java.util.Date().getTime()),
                complaint.getUser_id(),
                complaint.getStatus()
        };
    }
}
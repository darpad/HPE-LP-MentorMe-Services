package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.Document;
import com.livingprogress.mentorme.entities.Goal;
import com.livingprogress.mentorme.entities.InstitutionalProgram;
import com.livingprogress.mentorme.entities.InstitutionalProgramSearchCriteria;
import com.livingprogress.mentorme.entities.Mentee;
import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.MenteeMentorResponsibility;
import com.livingprogress.mentorme.entities.MenteeMentorTask;
import com.livingprogress.mentorme.entities.Mentor;
import com.livingprogress.mentorme.entities.Paging;
import com.livingprogress.mentorme.entities.Responsibility;
import com.livingprogress.mentorme.entities.SearchResult;
import com.livingprogress.mentorme.entities.Task;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.InstitutionalProgramService;
import com.livingprogress.mentorme.services.MenteeMentorProgramService;
import com.livingprogress.mentorme.services.MenteeService;
import com.livingprogress.mentorme.services.MentorService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * The InstitutionalProgram REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/institutionalPrograms")
@NoArgsConstructor
public class InstitutionalProgramController extends BaseUploadController {
    /**
     * The institutional program service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private InstitutionalProgramService institutionalProgramService;
    
    /**
     * The mentee mentor program service used to create mentee mentor program
     */
    @Autowired
    private MenteeMentorProgramService menteeMentorProgramService;

    /**
     * The mentor service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MentorService mentorService;

    /**
     * The mentee service used to perform operations. Should be non-null after injection.
     */
    @Autowired
    private MenteeService menteeService;


    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkConfigNotNull(institutionalProgramService, "institutionalProgramService");
        Helper.checkConfigNotNull(mentorService, "mentorService");
    }

    /**
     * This method is used to retrieve an entity.
     *
     * @param id the id of the entity to retrieve
     * @return the match entity
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public InstitutionalProgram get(@PathVariable long id) throws MentorMeException {
        return institutionalProgramService.get(id);
    }

    /**
     * This method is used to create an entity.
     *
     * @param entity the entity to create
     * @param documents the documents to upload
     * @return the created entity
     * @throws IllegalArgumentException if entity is null or not valid
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public InstitutionalProgram create(InstitutionalProgram entity,
            @RequestParam("files") MultipartFile[] documents) throws MentorMeException {
        Helper.checkNull(entity, "entity");
        List<Document> docs = Helper.uploadDocuments(getUploadDirectory(), documents);
        entity.setDocuments(docs);
        return institutionalProgramService.create(entity);
    }

    /**
     * This method is used to update an entity.
     *
     * @param id the id of the entity to update
     * @param entity the entity to update
     * @param documents the documents to upload
     * @return the updated entity
     * @throws IllegalArgumentException if id is not positive or entity is null or id of entity is not positive
     * or id of  entity not match id or entity is invalid
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public InstitutionalProgram update(@PathVariable long id, InstitutionalProgram entity,
            @RequestParam("files") MultipartFile[] documents) throws MentorMeException {
        Helper.checkUpdate(id, entity);
        List<Document> docs = Helper.uploadDocuments(getUploadDirectory(), documents);
        entity.setDocuments(docs);
        return institutionalProgramService.update(id, entity);
    }

    /**
     * This method is used to delete an entity.
     *
     * @param id the id of the entity to delete
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@PathVariable long id) throws MentorMeException {
        institutionalProgramService.delete(id);
    }

    /**
     * This method is used to search for entities by criteria and paging params.
     *
     * @param criteria the search criteria
     * @param paging the paging data
     * @return the search result
     * @throws IllegalArgumentException if pageSize is not positive or pageNumber is negative
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(method = RequestMethod.GET)
    public SearchResult<InstitutionalProgram> search(@ModelAttribute InstitutionalProgramSearchCriteria criteria,
            @ModelAttribute Paging paging) throws MentorMeException {
        return institutionalProgramService.search(criteria, paging);
    }

    /**
     * This method is used to retrieve program mentees.
     * @param id the id of the entity to retrieve
     * @return the match program mentees
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/mentees", method = RequestMethod.GET)
    public List<Mentee> getProgramMentees(@PathVariable long id) throws MentorMeException {
        // make sure exist valid program
        institutionalProgramService.get(id);
        return menteeService.getProgramMentees(id);
    }

    /**
     * This method is used to retrieve program mentors.
     * @param id the id of the entity to retrieve
     * @return the match program mentors
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/mentors", method = RequestMethod.GET)
    public List<Mentor> getProgramMentors(@PathVariable long id) throws MentorMeException {
        // make sure exist valid program
        institutionalProgramService.get(id);
        return mentorService.getProgramMentors(id);
    }

    /**
     * This method is used to clone program requested by mentor.
     * @param id the id of the entity to retrieve
     * @return the cloned mentee mentor program
     * @throws IllegalArgumentException if id is not positive
     * @throws EntityNotFoundException if the entity does not exist
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(value = "{id}/clone", method = RequestMethod.POST)
    @Transactional
    public MenteeMentorProgram clone(@PathVariable long id) throws MentorMeException {
        // make sure exist valid program
        InstitutionalProgram instProgram = institutionalProgramService.get(id);
        
        // create MenteeMentorProgram
        LocalDate date = LocalDate.now();
        
        MenteeMentorProgram mmProgram = new MenteeMentorProgram();
        mmProgram.setInstitutionalProgram(instProgram);
        mmProgram.setDocuments(instProgram.getDocuments());
        mmProgram.setUsefulLinks(instProgram.getUsefulLinks());        
        mmProgram.setStartDate(date.toDate());
        
        // clone goals
        if (instProgram.getGoals() != null && !instProgram.getGoals().isEmpty()) {
            List<MenteeMentorGoal> goals = new ArrayList<>();
            for (Goal goal : instProgram.getGoals()) {
                MenteeMentorGoal mmGoal = new MenteeMentorGoal();
                
                mmGoal.setGoal(goal);
                mmGoal.setMenteeMentorProgram(mmProgram);
                mmGoal.setDocuments(goal.getDocuments());
                mmGoal.setUsefulLinks(goal.getUsefulLinks());
                
                // clone tasks
                if (goal.getTasks() != null) {
                    List<MenteeMentorTask> tasks = new ArrayList<>();
                    for (Task task : goal.getTasks()) {
                        MenteeMentorTask mmTask = new MenteeMentorTask();
                        mmTask.setTask(task);
                        mmTask.setMenteeMentorGoal(mmGoal);
                        mmTask.setStartDate(date.toDate());                        
                        date = date.plusDays(task.getDurationInDays());
                        mmTask.setEndDate(date.toDate());
                        
                        tasks.add(mmTask);
                    }                    
                    mmGoal.setTasks(tasks);
                }
                
                goals.add(mmGoal);
            }
            mmProgram.setEndDate(date.toDate());
            mmProgram.setGoals(goals);            
        }
        
        // clone responsibilities
        if (instProgram.getResponsibilities() != null && !instProgram.getResponsibilities().isEmpty()) {
            List<MenteeMentorResponsibility> responsibilities = new ArrayList<>();
        
            for (Responsibility resp : instProgram.getResponsibilities()) {
                MenteeMentorResponsibility mmResp = new MenteeMentorResponsibility();
                
                mmResp.setDate(resp.getDate());
                mmResp.setMenteeMentorProgram(mmProgram);
                mmResp.setMenteeResponsibility(resp.getMenteeResponsibility());
                mmResp.setMentorResponsibility(resp.getMentorResponsibility());
                mmResp.setNumber(resp.getNumber());
                mmResp.setTitle(resp.getTitle());
                mmResp.setResponsibilityId(resp.getId());
                
                responsibilities.add(mmResp);
            }
            mmProgram.setResponsibilities(responsibilities);
        }
        
        // save to database
        menteeMentorProgramService.create(mmProgram);
        
        return mmProgram;
    }
}


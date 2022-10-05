package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.service.ParentService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import static org.modis.EmsApplication.utils.ErrorHandlingUtils.handleValidationErrors;

@RestController
@RequestMapping("/api/parents")
public class ParentRestController {

    private final ParentService parentService;

    @Autowired
    public ParentRestController(ParentService parentService) {
        this.parentService = parentService;
    }

    @GetMapping
    public List<ParentExposeDTO> getAllParents() {
        return parentService.getAllParents();
    }

    @GetMapping("/{id:\\d+}")
    public ParentExposeDTO getParentById(@PathVariable("id") Long id) throws NonexistingEntityException {
        return parentService.getParentById(id);
    }

    @GetMapping("/{id:\\d+}/children")
    public Collection<StudentExposeDTO> getAllChildren(@PathVariable("id") Long id) {
        return parentService.getAllChildren(id);
    }

    @PostMapping("/{id:\\d+}/children")
    public StudentExposeDTO addChild(@PathVariable("id") Long parentId, @RequestParam("studentId") Long studentId) throws NonexistingEntityException, InvalidOperationException {
        return parentService.addChild(parentId, studentId);
    }

    @DeleteMapping("/{id:\\d+}/children")
    public StudentExposeDTO deleteChild(@PathVariable("id") Long parentId, @RequestParam("studentId") Long studentId) throws NonexistingEntityException, InvalidOperationException {
        return parentService.removeChild(parentId, studentId);
    }

    @GetMapping("/count")
    public String getUsersCount() {
        return parentService.parentsCount();
    }


}

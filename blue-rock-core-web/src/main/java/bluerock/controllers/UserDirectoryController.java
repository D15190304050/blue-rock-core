package bluerock.controllers;

import bluerock.api.IUserDirectoryService;
import bluerock.domain.UserDirectory;
import bluerock.model.StorageObject;
import bluerock.params.DeleteParam;
import bluerock.params.CreateDirectoryParam;
import bluerock.params.RenameParam;
import bluerock.params.ShowDirectoryAndFileParam;
import dataworks.web.commons.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directory")
public class UserDirectoryController
{
    @Autowired
    private IUserDirectoryService userDirectoryService;

    @GetMapping("/list")
    public ServiceResponse<List<UserDirectory>> showDirectories(ShowDirectoryAndFileParam showDirectoryParam)
    {
        return userDirectoryService.showDirectories(showDirectoryParam);
    }

    @GetMapping("/contents")
    public ServiceResponse<List<StorageObject>> showContentsInDirectory(ShowDirectoryAndFileParam showDirectoryParam)
    {
        return userDirectoryService.showContentsInDirectory(showDirectoryParam);
    }

    @PostMapping("/create")
    public ServiceResponse<Boolean> createDirectory(@RequestBody CreateDirectoryParam createParam)
    {
        return userDirectoryService.createDirectory(createParam);
    }

    @PostMapping("/rename")
    public ServiceResponse<Boolean> renameDirectory(@RequestBody RenameParam renameParam)
    {
        return userDirectoryService.renameDirectory(renameParam);
    }

    @GetMapping("/canDelete")
    public ServiceResponse<Boolean> canDelete(DeleteParam deleteParam)
    {
        return userDirectoryService.canDelete(deleteParam);
    }

    @PostMapping("/delete")
    public ServiceResponse<Boolean> deleteDirectory(DeleteParam deleteParam)
    {
        return userDirectoryService.deleteDirectory(deleteParam);
    }
}

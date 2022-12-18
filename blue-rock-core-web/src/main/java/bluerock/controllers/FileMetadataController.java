package bluerock.controllers;

import bluerock.api.IFileMetadataService;
import bluerock.domain.FileMetadata;
import bluerock.params.DeleteFilesParam;
import bluerock.params.MoveFilesParam;
import bluerock.params.RenameParam;
import bluerock.params.ShowDirectoryAndFileParam;
import dataworks.web.commons.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/metadata")
public class FileMetadataController
{
    @Autowired
    private IFileMetadataService fileMetadataService;

    @GetMapping("/showFiles")
    public ServiceResponse<List<FileMetadata>> showFiles(ShowDirectoryAndFileParam showFileParam)
    {
        return fileMetadataService.showFiles(showFileParam);
    }

    @GetMapping("/{userId}/{id}")
    public ServiceResponse<FileMetadata> getFileMetadata(@PathVariable("userId") long userId, @PathVariable("id") long id)
    {
        return fileMetadataService.getFileMetadataById(userId, id);
    }

    @PostMapping("/rename")
    public ServiceResponse<Boolean> renameFile(@RequestBody RenameParam renameParam)
    {
        return fileMetadataService.renameFile(renameParam);
    }

    @PostMapping("/move")
    public ServiceResponse<Boolean> moveFiles(@RequestBody MoveFilesParam moveFilesParam)
    {
        return fileMetadataService.moveFiles(moveFilesParam);
    }

    @PostMapping("/delete")
    public ServiceResponse<Boolean> deleteFiles(@RequestBody DeleteFilesParam deleteFilesParam)
    {
        return fileMetadataService.deleteFiles(deleteFilesParam);
    }
}

package bluerock.controllers;

import bluerock.api.IFileMetadataService;
import bluerock.domain.FileMetadata;
import bluerock.params.ShowDirectoryAndFileParam;
import dataworks.web.commons.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{id}")
    public ServiceResponse<FileMetadata> getFileMetadata(@PathVariable("id") long id)
    {
        return fileMetadataService.getFileMetadataById(id);
    }
}

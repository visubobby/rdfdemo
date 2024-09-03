package com.graph.rdfdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RDFFileResourceService {

    @Value("${graph.files.directory}")
    private String fileLocation;

    /**
     * Iterates over all files in a given folder path and returns them as a list of Resources using NIO.
     *
     * @return a list of Resources representing each file in the folder
     * @throws IOException if an I/O error occurs
     */
    public List<Resource> getFilesAsResources() throws IOException {
        try (Stream<Path> paths = Files.list(Paths.get(fileLocation))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(PathResource::new)
                    .collect(Collectors.toList());
        }
    }



}

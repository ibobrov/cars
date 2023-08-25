package ru.job4j.cars.service;

import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.repository.FileRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.util.Files.temporaryFolderPath;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimpleFileServiceTest {
    private final FileRepository fileRepo = mock(FileRepository.class);
    private final SimpleFileService fileService = new SimpleFileService(fileRepo, temporaryFolderPath());

    @Test
    public void whenSaveFileDtoWhenSaveCorrected() {
        var fileDto = new FileDto("test.txt", new byte[] {1, 2, 3});
        var file = new ru.job4j.cars.model.File(fileDto.getName(), "test.txt");
        when(fileRepo.save(file)).thenReturn(file);
        assertThat(fileService.save(fileDto)).isEqualTo(file);
    }

    @Test
    public void whenGetFileDtoByIdWhenReturnCorrectedDto() throws IOException {
        var path = File.createTempFile("file", "txt").getPath();
        try (var writer = new FileWriter(path)) {
            writer.write("str");
        }
        var file = new ru.job4j.cars.model.File(1, "test", path);
        when(fileRepo.findById(1)).thenReturn(Optional.of(file));
        var fileDto = fileService.getFileById(1).get();
        var strContent = new String(fileDto.getContent());
        assertThat(fileDto.getName()).isEqualTo("test");
        assertThat(strContent).isEqualTo("str");
    }

    @Test
    public void whenGetFileByIdWhenReturnEmpty() {
        var fileDto = fileService.getFileById(1);
        assertThat(fileDto).isEqualTo(empty());
    }

}
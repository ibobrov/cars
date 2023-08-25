package ru.job4j.cars.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.exceptions.base.MockitoException;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.NewPostDto;
import ru.job4j.cars.dto.PostDto;
import ru.job4j.cars.dto.PostPreview;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CarRepository;
import ru.job4j.cars.repository.OwnerRepository;
import ru.job4j.cars.repository.PostRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SimplePostServiceTest {
    private final PostRepository postRepo = mock(PostRepository.class);
    private final CarRepository carRepo = mock(CarRepository.class);
    private final OwnerRepository ownerRepo = mock(OwnerRepository.class);
    private final FileService fileService = mock(FileService.class);
    private final SimplePostService simplePostService
            = new SimplePostService(postRepo, carRepo, ownerRepo, fileService);
    private final User user = new User(1);
    private final Owner owner = new Owner(1);
    private final List<FileDto> files = List.of(new FileDto("1", new byte[]{1, 2}));
    private final File file = new File(1, "1", "path");
    private final NewPostDto dto = NewPostDto.of()
            .carModel("carModel")
            .carYear(2000)
            .price(5000)
            .carOdometer(15000)
            .description("desc")
            .engineId(1)
            .photoIds(List.of(1))
            .build();
    private final PostPreview postPreview = PostPreview.of().id(0)
            .title("2000 CAMODEL")
            .carPrice(5000)
            .odometer(15000)
            .photoId(1)
            .build();
    private final Post post = Post.of()
            .description("desc")
            .creationDate(LocalDateTime.now())
            .price(5000)
            .user(new User(1))
            .car(
                    Car.of()
                            .id(0)
                            .name("CARMODEL")
                            .year(2000)
                            .odometer(15000)
                            .engine(new Engine(1))
                            .owner(owner)
                            .owners(Set.of(owner))
                            .build()
            )
            .priceHistories(Set.of(new PriceHistory(0, 0, 5000, LocalDateTime.now())))
            .participates(Set.of())
            .files(Set.of(file))
            .build();

    @Test
    public void whenCreatePostThenReturnTrue() {
        when(ownerRepo.findByUser(user)).thenReturn(Optional.of(owner));
        when(postRepo.save(any())).thenReturn(Post.of().id(1).build());
        assertThat(simplePostService.create(dto, user, files)).isTrue();
    }

    @Test
    public void whenCreatePostThenReturnFalseBecausePostNotSaved() {
        when(ownerRepo.findByUser(user)).thenReturn(Optional.of(owner));
        when(postRepo.save(any())).thenReturn(Post.of().id(0).build());
        assertThat(simplePostService.create(dto, user, files)).isFalse();
    }

    @Test
    public void whenCreatePostThenReturnFalseBecauseOwnerNotExist() {
        when(ownerRepo.findByUser(user)).thenReturn(empty());
        when(postRepo.save(any())).thenReturn(Post.of().id(1).build());
        assertThat(simplePostService.create(dto, user, files)).isFalse();
    }

    @Test
    public void whenCreatePostCaptureAssembledPostAndCompareWithExpectedThenTestPassed() {
        var capt = ArgumentCaptor.forClass(Post.class);

        when(fileService.save(any())).thenReturn(file);
        when(ownerRepo.findByUser(user)).thenReturn(Optional.of(owner));
        when(postRepo.save(capt.capture())).thenReturn(Post.of().id(1).build());

        assertThat(simplePostService.create(dto, user, files)).isTrue();
        assertThat(capt.getValue()).usingRecursiveComparison()
                .ignoringFields("creationDate", "priceHistories")
                .isEqualTo(post);
        assertThat(
                capt.getValue().getPriceHistories().stream().filter(h -> h.getAfter() == 5000)
                        .toList()
                        .size() == 1
        ).isTrue();
        assertThat(capt.getValue().getCreationDate().toLocalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    public void whenFindByIdPostAndAssembledItToDtoThenCheckThatDtoEqualExpected() {
        PostDto dto = PostDto.of()
                .id(0)
                .description("desc")
                .creationDate(LocalDateTime.now())
                .carName("CARMODEL")
                .carYear(2000)
                .carPrice(5000)
                .photoIds(List.of(1))
                .countOwners(1)
                .isOwner(true)
                .carEngine("engine 1")
                .carOdometer(15000)
                .build();
        var car = Car.of()
                .id(0)
                .name("CARMODEL")
                .year(2000)
                .odometer(15000)
                .engine(new Engine(1, "engine 1"))
                .owner(owner)
                .owners(Set.of(owner))
                .build();
        when(postRepo.findById(1)).thenReturn(Optional.ofNullable(post));
        when(carRepo.findById(0)).thenReturn(Optional.ofNullable(car));
        assertThat(simplePostService.findById(1).get())
                .usingRecursiveComparison()
                .ignoringFields("creationDate").isEqualTo(dto);
        assertThat(simplePostService.findById(1).get().getCreationDate().toLocalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    public void whenFindByFiltersThenCallFindByFilter() {
        var thrown = catchThrowable(() -> {
            Map<String, String> filters = new HashMap<>(Map.of("brand", "", "model", "accord"));
            when(postRepo.getAll()).thenThrow(new MockitoException("getAll"));
            when(postRepo.findByFilter(any())).thenThrow(new MockitoException("byFilter"));
            simplePostService.findByFilter(filters);
        });
        AssertionsForClassTypes.assertThat(thrown).isInstanceOf(MockitoException.class);
        AssertionsForClassTypes.assertThat(thrown.getMessage()).isEqualTo("byFilter");
    }

    @Test
    public void whenFindByEmptyFiltersThenCallGetAll() {
        Map<String, String> filters = new HashMap<>(Map.of("brand", "", "model", ""));
        when(postRepo.getAll()).thenReturn(List.of(post));
        assertThat(simplePostService.findByFilter(filters)).isEqualTo(List.of(postPreview));
    }

    @Test
    public void whenFindByFiltersWithBrandAndModelTogetherThenIgnoreBrandAndReturnCorrectedList() {
        Map<String, String> filters = new HashMap<>(Map.of("brand", "brand", "model", "model"));
        when(postRepo.findByFilter(filters)).thenReturn(List.of(post));
        assertThat(simplePostService.findByFilter(filters)).isEqualTo(List.of(postPreview));
        assertThat(filters).isEqualTo(Map.of("model", "model"));
    }

    @Test
    public void whenGetAllThenCallGetAllFromRepoConvertToDtoAndReturn() {
        when(postRepo.getAll()).thenReturn(List.of(post));
        assertThat(simplePostService.getAll()).isEqualTo(List.of(postPreview));
    }

    @Test
    public void whenGetRecommendationThenReturnCorrected() {
        when(postRepo.getAll()).thenReturn(List.of(post, post));
        assertThat(simplePostService.getRecommendation(1)).isEqualTo(List.of(postPreview));
    }

    @Test
    public void whenGetLastDayThenReturnCorrected() {
        when(postRepo.getLastDay()).thenReturn(List.of(post));
        assertThat(simplePostService.getLastDay()).isEqualTo(List.of(postPreview));
    }
}
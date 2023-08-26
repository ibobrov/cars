package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.*;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CarRepository;
import ru.job4j.cars.repository.OwnerRepository;
import ru.job4j.cars.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class SimplePostService implements PostService {
    private final PostRepository postRepo;
    private final CarRepository carRepo;
    private final OwnerRepository ownerRepo;
    private final FileService fileService;

    @Override
    public boolean create(NewPostDto newPost, User user, List<FileDto> files) {
        var rsl = false;
        var owner = ownerRepo.findByUser(user);
        if (owner.isPresent()) {
            var post = Post.of()
                    .description(newPost.getDescription())
                    .creationDate(LocalDateTime.now())
                    .price(newPost.getPrice())
                    .visibility(true)
                    .user(user)
                    .car(
                            Car.of()
                                    .id(0)
                                    .name(newPost.getCarModel().toUpperCase())
                                    .year(newPost.getCarYear())
                                    .odometer(newPost.getCarOdometer())
                                    .engine(new Engine(newPost.getEngineId()))
                                    .owner(owner.get())
                                    .owners(Set.of(owner.get()))
                                    .build()
                    )
                    .priceHistories(Set.of(new PriceHistory(0, 0, newPost.getPrice(), LocalDateTime.now())))
                    .participates(Set.of())
                    .files(saveFiles(files))
                    .build();
            rsl = postRepo.save(post).getId() != 0;
        }
        return rsl;
    }

    @Override
    public Optional<PostDto> findById(int id) {
        return postRepo.findById(id).map(this::assemblePostDto);
    }

    @Override
    public List<PostPreview> findByFilter(Map<String, String> filters) {
        filters.values().removeAll(List.of(""));
        List<PostPreview> rsl;
        if (filters.isEmpty()) {
            rsl = getAll();
        } else {
            if (filters.keySet().containsAll(List.of("brand", "model"))) {
                filters.remove("brand");
            }
            rsl = postRepo.findByFilter(filters).stream().map(this::assemblePostPreview).toList();
        }
        return rsl;
    }

    @Override
    public List<OwnerPostPreview> findByUser(int id) {
        return postRepo.findByUser(id).stream().map(
                post -> new OwnerPostPreview(assemblePostPreview(post), post.isVisibility())
        ).toList();
    }

    @Override
    public List<PostPreview> getRecommendation(int itemCount) {
        return postRepo.getVisible().stream().limit(itemCount).map(this::assemblePostPreview).toList();
    }

    @Override
    public List<PostPreview> getLastDay() {
        return postRepo.getLastDay().stream().map(this::assemblePostPreview).toList();
    }

    @Override
    public List<PostPreview> getAll() {
        return postRepo.getVisible().stream().map(this::assemblePostPreview).toList();
    }

    @Override
    public boolean hide(int id) {
        return postRepo.hide(id);
    }

    private PostPreview assemblePostPreview(Post post) {
        return PostPreview.of()
                .id(post.getId())
                .title(post.getCar().getYear() + " " + post.getCar().getName())
                .odometer(post.getCar().getOdometer())
                .carPrice(post.getPrice())
                .photoId(
                        post.getFiles()
                                .stream()
                                .map(File::getId)
                                .min(Integer::compareTo)
                                .orElse(0)
                ).build();
    }

    private PostDto assemblePostDto(Post post) {
        var dtoBuilder = PostDto.of()
                .id(post.getId())
                .description(post.getDescription())
                .creationDate(post.getCreationDate())
                .carName(post.getCar().getName())
                .carYear(post.getCar().getYear())
                .carPrice(post.getPrice())
                .photoIds(post.getFiles()
                        .stream()
                        .map(File::getId)
                        .sorted()
                        .toList()
                );
        carRepo.findById(post.getCar().getId())
                .ifPresent(car -> {
                    var owners = car.getOwners();
                    dtoBuilder.countOwners(owners.size());
                    dtoBuilder.isOwner(owners.contains(car.getOwner()));
                    dtoBuilder.carEngine(car.getEngine().getName());
                    dtoBuilder.carOdometer(car.getOdometer());
                });
        return dtoBuilder.build();
    }

    private Set<File> saveFiles(List<FileDto> files) {
        var rsl = new HashSet<File>();
        for (var file : files) {
            rsl.add(fileService.save(file));
        }
        return rsl;
    }
}

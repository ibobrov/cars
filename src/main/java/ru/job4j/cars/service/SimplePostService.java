package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.dto.NewPostDto;
import ru.job4j.cars.dto.PostDto;
import ru.job4j.cars.dto.PostPreview;
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
    private static final Comparator<Post> REVERSED_POST_COMPARATOR =
            Comparator.comparing(Post::getId).reversed();

    @Override
    public boolean create(NewPostDto newPost, User user, List<FileDto> files) {
        Optional<Owner> owner = ownerRepo.findByUser(user);
        Car car = null;
        if (owner.isPresent()) {
            car = Car.of()
                    .name(newPost.getCarModel().toUpperCase())
                    .year(newPost.getCarYear())
                    .odometer(newPost.getCarOdometer())
                    .engine(new Engine(newPost.getEngineId()))
                    .owner(owner.get())
                    .owners(Set.of(owner.get()))
                    .build();
            carRepo.save(car);
        }
        Post post = null;
        if (car != null) {
            post = Post.of()
                    .description(newPost.getDescription())
                    .creationDate(LocalDateTime.now())
                    .price(newPost.getPrice())
                    .user(user)
                    .car(car)
                    .priceHistories(Set.of(new PriceHistory(0, 0, newPost.getPrice(), LocalDateTime.now())))
                    .participates(Set.of())
                    .files(saveFiles(files))
                    .build();
        }
        return post != null && postRepo.save(post).getId() != 0;
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
            rsl =  getAll();
        } else {
            if (filters.keySet().containsAll(List.of("brand", "model"))) {
                filters.remove("brand");
            }
            rsl = postRepo.getByFilter(filters).stream().map(this::assemblePostPreview).toList();
        }
        return rsl;
    }

    @Override
    public List<PostPreview> getRecommendation(int itemCount) {
        return postRepo.getAll().stream()
                .sorted(REVERSED_POST_COMPARATOR)
                .limit(itemCount)
                .map(this::assemblePostPreview)
                .toList();
    }

    @Override
    public List<PostPreview> getLastDay() {
        return postRepo.getLastDay().stream()
                .sorted(REVERSED_POST_COMPARATOR)
                .map(this::assemblePostPreview).toList();
    }

    @Override
    public List<PostPreview> getAll() {
        return postRepo.getAll().stream().map(this::assemblePostPreview).toList();
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

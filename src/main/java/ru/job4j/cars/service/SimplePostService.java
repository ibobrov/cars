package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.Filter;
import ru.job4j.cars.dto.PostDto;
import ru.job4j.cars.dto.PostPreview;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.CarRepository;
import ru.job4j.cars.repository.PostRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimplePostService implements PostService {
    private final PostRepository postRepo;
    private final CarRepository carRepo;
    private static final Comparator<PriceHistory> PRICE_HISTORY_COMPARATOR =
            Comparator.comparing(PriceHistory::getCreated);
    private static final Comparator<Post> REVERSED_POST_COMPARATOR =
            Comparator.comparing(Post::getId).reversed();
    private static final Filter EMPTY_FILTER = new Filter();

    @Override
    public Optional<PostDto> findById(int id) {
        return postRepo.findById(id).map(this::assemblePostDto);
    }

    @Override
    public List<PostPreview> findByFilter(Filter filter) {
        if (EMPTY_FILTER.equals(filter)) {
            return getAll();
        }
        if (!"none".equals(filter.getModel())) {
            filter.setBrand("none");
        }
        return postRepo.getByFilter(filter).stream().map(this::assemblePostPreview).toList();
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
                .carPrice(
                        post.getPriceHistories()
                                .stream().max(PRICE_HISTORY_COMPARATOR)
                                .map(PriceHistory::getAfter)
                                .orElse(0L)
                )
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
                .carPrice(
                        post.getPriceHistories()
                                .stream().max(PRICE_HISTORY_COMPARATOR)
                                .map(PriceHistory::getAfter)
                                .orElse(0L)
                )
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
}

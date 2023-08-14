package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.PostDto;
import ru.job4j.cars.dto.PostPreview;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.CarRepository;
import ru.job4j.cars.repository.PostRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimplePostService implements PostService {
    private final PostRepository postRepo;
    private final CarRepository carRepo;
    private static final Comparator<PriceHistory> BY_CREATED = Comparator.comparing(PriceHistory::getCreated);

    @Override
    public Optional<PostDto> findById(int id) {
        Optional<PostDto> rsl = Optional.empty();
        var postOpt = postRepo.findById(id);
        if (postOpt.isPresent()) {
            rsl = Optional.of(
                    assemblePostDto(postOpt.get())
            );
        }
        return rsl;
    }

    @Override
    public List<PostPreview> getAll() {
        var rsl = new ArrayList<PostPreview>();
        for (var post : postRepo.getAll()) {
            rsl.add(assemblePostPreview(post));
        }
        return rsl;
    }

    private PostPreview assemblePostPreview(Post post) {
        var lastPriceHistory = post.getPriceHistories()
                                    .stream()
                                    .max(BY_CREATED);
        var price = lastPriceHistory.map(PriceHistory::getAfter).orElse(0L);
        var firstPhoto = post.getFiles().stream().min(Comparator.comparing(File::getId));
        var photoId = firstPhoto.map(File::getId).orElse(0);
        return new PostPreview(post.getId(), post.getCar().getYear() + " " + post.getCar().getName(),
                price, photoId);
    }

    private PostDto assemblePostDto(Post post) {
        var dtoBuilder = PostDto.of()
                .id(post.getId())
                .description(post.getDescription())
                .creationDate(post.getCreationDate())
                .carName(post.getCar().getName())
                .carYear(post.getCar().getYear())
                .photoIds(post.getFiles()
                        .stream()
                        .map(File::getId)
                        .sorted()
                        .toList()
                );
        var price = post.getPriceHistories().stream().max(BY_CREATED);
        price.ifPresent(p -> dtoBuilder.carPrice(p.getAfter()));
        var car = carRepo.findById(post.getCar().getId());
        if (car.isPresent()) {
            var carOwners = car.get().getOwners();
            dtoBuilder.countOwners(carOwners.size());
            dtoBuilder.isOwner(carOwners.contains(car.get().getOwner()));
        }
        return dtoBuilder.build();
    }
}

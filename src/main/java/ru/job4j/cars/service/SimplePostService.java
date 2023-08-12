package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.PostPreview;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.HibernatePostRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class SimplePostService implements PostService {
    private final HibernatePostRepository postRepo;
    private static final Comparator<PriceHistory> BY_CREATED = Comparator.comparing(PriceHistory::getCreated);

    @Override
    public List<PostPreview> getAll() {
        var rsl = new ArrayList<PostPreview>();
        for (var post : postRepo.getAll()) {
            var price = post.getPriceHistories().stream().max(BY_CREATED).get();
            var photoId = post.getFiles().stream().min(Comparator.comparing(File::getName)).get().getId();
            rsl.add(new PostPreview(post.getId(), post.getCar().getYear() + " " + post.getCar().getName(),
                    price.getAfter(), photoId));
        }
        return rsl;
    }
}

package uz.project.moviehivebot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.project.moviehivebot.entity.Advertisement;
import uz.project.moviehivebot.repository.AdvertisementRepository;

@RequiredArgsConstructor
@Component
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;


    static Advertisement advertisement = new Advertisement();


    public void setDescription(String description) {
        advertisement.setDescription(description);
    }

    public void setPhotoUrl(Long chatId, String savedPhotoUrl) {
        advertisement.setImageUrl(savedPhotoUrl);
    }

    public void save() {
        advertisementRepository.save(advertisement);
        advertisement = new Advertisement();
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public void cancel() {
        advertisement = new Advertisement();
    }
}

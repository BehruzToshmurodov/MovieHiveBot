package uz.project.moviehivebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.project.moviehivebot.entity.Advertisement;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement , Long> {
}

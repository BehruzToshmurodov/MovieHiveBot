package uz.project.moviehivebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.project.moviehivebot.entity.Movie;
@Repository
public interface MovieRepository extends JpaRepository<Movie , Long> {

    Movie getMovieByMovieCode(String code);

}

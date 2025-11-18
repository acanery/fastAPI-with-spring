package example.fastAPI;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


interface SongRepository extends CrudRepository<Song, Long>, PagingAndSortingRepository<Song, Long> {
}

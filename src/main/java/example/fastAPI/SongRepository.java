package example.fastAPI;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

interface SongRepository extends CrudRepository<Song, Long>, PagingAndSortingRepository<Song, Long> {

    @Query("SELECT id FROM song ORDER BY id ASC")
    List<Long> findAllIdsOrderByIdAsc();
}

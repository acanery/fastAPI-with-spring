package example.fastAPI;

import org.springframework.data.repository.CrudRepository;

interface SongRepository extends CrudRepository<Song, Long> {

}

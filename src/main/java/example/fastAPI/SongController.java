package example.fastAPI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/songs")
class SongController {
    private final SongRepository songRepository;

    private SongController(SongRepository songRepository){
        this.songRepository = songRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<Song> findById(@PathVariable Long requestedId){
        Optional<Song> optionalSong = songRepository.findById(requestedId);

        if (optionalSong.isPresent()){
            return ResponseEntity.ok(optionalSong.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    private ResponseEntity<List<Song>> findAll(Pageable pageable){
        Page<Song> page = songRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()));

        return ResponseEntity.ok(page.getContent());
    }

}

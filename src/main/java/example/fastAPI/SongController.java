package example.fastAPI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC,"streams"))));

        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping
    private ResponseEntity<Void> createSong(@RequestBody Song newSongRequest, UriComponentsBuilder ucb) {
        Song savedSong = songRepository.save(newSongRequest);
        URI createdUriLocation = ucb.path("songs/{id}").buildAndExpand(savedSong.id()).toUri();
        return ResponseEntity.created(createdUriLocation).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putSong(@PathVariable Long requestedId, @RequestBody Song requestedSong){
        Optional<Song> song = songRepository.findById(requestedId);
        if (song.isPresent()){
            Song updatedSong = new Song(requestedId, song.get().streams(), requestedSong.duration(), song.get().owner());
            songRepository.save(updatedSong);
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }



}

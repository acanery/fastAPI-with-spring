package example.fastAPI;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;


import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SongApplicationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnSong(){
        ResponseEntity<String> response = restTemplate.getForEntity("/songs/24", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(24);
    }

    @Test
    void shouldNotReturnSongWhichDoesNotExist(){
        ResponseEntity<String> response = restTemplate.getForEntity("/songs/98765",String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnPageOfSongs(){
        ResponseEntity<String> response = restTemplate.getForEntity("/songs",String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int songCount = documentContext.read("$.length()");
        assertThat(songCount).isEqualTo(4);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(24,25,26,27);

    }

    @Test
    void shouldReturnAPageWithGivenRequest(){
        ResponseEntity<String> response = restTemplate
                .getForEntity("/songs?page=0&size=3&sort=streams,desc",String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray streams = documentContext.read("$..streams");
        assertThat(streams).containsExactly(160,140,120);

    }

    @DirtiesContext
    @Test
    void shouldCreateANewSong(){
        Song createdSong1 = new Song(null, 12,140,"popArtist1");
        ResponseEntity<Void> pushResponse1 = restTemplate.postForEntity("/songs",createdSong1, Void.class);
        assertThat(pushResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfSong1 = pushResponse1.getHeaders().getLocation();
        ResponseEntity<String> getResponse1 = restTemplate.getForEntity(locationOfSong1,String.class);
        DocumentContext documentContext1 = JsonPath.parse(getResponse1.getBody());
        String owner1 = documentContext1.read("$.owner");
        assertThat(owner1).isEqualTo("popArtist1");

        Song createdSong2 = new Song(null, 14, 124,"popArtist2");
        ResponseEntity<Void> pushResponse2 = restTemplate.postForEntity("/songs",createdSong2, Void.class);
        assertThat(pushResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfSong2 = pushResponse2.getHeaders().getLocation();
        ResponseEntity<String> getResponse2 = restTemplate.getForEntity(locationOfSong2,String.class);
        DocumentContext documentContext = JsonPath.parse(getResponse2.getBody());
        String owner2 = documentContext.read("$.owner");
        assertThat(owner2).isEqualTo("popArtist2");
    }

    @DirtiesContext
    @Test
    void shouldUpdateTheDuration(){
        int requestedDuration = 160;
        Song songWithNewDuration = new Song(null,null, requestedDuration, null);
        HttpEntity<Song> request = new HttpEntity<>(songWithNewDuration);
        ResponseEntity<Void> putResponse = restTemplate.exchange("/songs/24", HttpMethod.PUT, request,Void.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/songs/24", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        int newDuration = documentContext.read("$.duration");
        assertThat(newDuration).isEqualTo(requestedDuration);
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(24);

    }
}

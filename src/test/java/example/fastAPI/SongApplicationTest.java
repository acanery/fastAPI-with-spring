package example.fastAPI;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SongApplicationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnSong(){
        ResponseEntity<String> response = restTemplate.getForEntity("/songs/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);
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
        assertThat(ids).containsExactlyInAnyOrder(1,2,3,4);

    }
}

package example.fastAPI;

import org.springframework.data.annotation.Id;

record Song(@Id Long id, Integer streams, String owner) {

}

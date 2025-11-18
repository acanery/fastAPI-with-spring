package example.fastAPI;

import org.springframework.data.annotation.Id;

record Song(@Id Long id, String owner, Integer streams) {

}

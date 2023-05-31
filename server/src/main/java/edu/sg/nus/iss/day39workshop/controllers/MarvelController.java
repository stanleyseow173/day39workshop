package edu.sg.nus.iss.day39workshop.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.Response;

import edu.sg.nus.iss.day39workshop.model.MarvelCharacter;
import edu.sg.nus.iss.day39workshop.model.Thumbnail;
import edu.sg.nus.iss.day39workshop.services.MarvelService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

@Controller
@RequestMapping(path = "/")
public class MarvelController {

        @Autowired
        MarvelService marvelSvc;

        @GetMapping(path = "/api/characters")
        @ResponseBody
        public ResponseEntity<String> getCharacters(@RequestParam String startsWith,
                        @RequestParam(defaultValue = "20") Integer limit,
                        @RequestParam(defaultValue = "0") Integer offset) {

                try {
                        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                        // List<MarvelCharacter> characters = marvelSvc.getCharacters(startsWith,
                        // String.valueOf(limit), String.valueOf(offset));

                        marvelSvc.getCharacters(startsWith, String.valueOf(limit), String.valueOf(offset)).stream()
                                        .map(d -> Json.createObjectBuilder()
                                                        .add("id", d.getId())
                                                        .add("name", d.getName())
                                                        .add("description", d.getDescription())
                                                        .add("modified", d.getModified())
                                                        .add("thumbnail",
                                                                        Json.createObjectBuilder()
                                                                                        .add("path", d.getThumbnail()
                                                                                                        .path())
                                                                                        .add("extension", d
                                                                                                        .getThumbnail()
                                                                                                        .extension())
                                                                                        .build())
                                                        .add("resourceURI", d.getResourceURI())
                                                        .build())
                                        .forEach(arrayBuilder::add);

                        // JsonReader reader = Json.createReader(new StringReader(payload));
                        // JsonObject data = reader.readObject();
                        return ResponseEntity.ok(arrayBuilder.build().toString());

                } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return ResponseEntity.status(400)
                                        .body(Json.createObjectBuilder()
                                                        .add("error", e.getMessage())
                                                        .build().toString());

                }
        }

        @GetMapping(path = "/api/character/{cId}")
        @ResponseBody
        public ResponseEntity<String> getCharacterById(@PathVariable String cId) {

                try {
                        MarvelCharacter character = marvelSvc.getCharacterById(cId);
                        return ResponseEntity.ok(character.toJSON().toString());
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return ResponseEntity.status(400)
                                        .body(Json.createObjectBuilder()
                                                        .add("error", e.getMessage())
                                                        .build().toString());
                }

        }

        @GetMapping(path = "api/character/comments")
        @ResponseBody
        public ResponseEntity<String> getComments(@RequestParam String id) {
                List<String> comments = this.marvelSvc.getComments(id);
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (String comment : comments) {
                        arrayBuilder.add(comment);
                }
                // JsonObjectBuilder objBuilder = Json.createObjectBuilder();
                // JsonObject returnJson = objBuilder.add("comments",
                // arrayBuilder.build()).build();
                return ResponseEntity.ok(arrayBuilder.build().toString());
        }

        @PostMapping(path = "api/character/comment")
        @ResponseBody
        public ResponseEntity<String> addComment(@RequestParam String id, @RequestParam String comment) {
                this.marvelSvc.insertComment(id, comment);
                JsonObjectBuilder objBuilder = Json.createObjectBuilder();
                JsonObject returnJson = objBuilder.add("message", "success").build();
                return ResponseEntity.ok(returnJson.toString());
        }

}

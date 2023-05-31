package edu.sg.nus.iss.day39workshop.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class MarvelCharacter {
        private Integer id;
        private String name;
        private String description;
        private String modified;
        private Thumbnail thumbnail;
        private String resourceURI;

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public String getModified() {
                return modified;
        }

        public void setModified(String modified) {
                this.modified = modified;
        }

        public Thumbnail getThumbnail() {
                return thumbnail;
        }

        public void setThumbnail(Thumbnail thumbnail) {
                this.thumbnail = thumbnail;
        }

        public String getResourceURI() {
                return resourceURI;
        }

        public void setResourceURI(String resourceURI) {
                this.resourceURI = resourceURI;
        }

        public MarvelCharacter(Integer id, String name, String description, String modified, Thumbnail thumbnail,
                        String resourceURI) {
                this.id = id;
                this.name = name;
                this.description = description;
                this.modified = modified;
                this.thumbnail = thumbnail;
                this.resourceURI = resourceURI;
        }

        public MarvelCharacter() {
        }

        public JsonObject toJSON() {
                return Json.createObjectBuilder()
                                .add("id", this.getId())
                                .add("name", this.getName())
                                .add("description", this.getDescription())
                                .add("modified", this.getModified())
                                .add("thumbnail",
                                                Json.createObjectBuilder().add("path", this.thumbnail.path())
                                                                .add("extension", this.thumbnail.extension()).build())
                                .add("resourceURI", this.getResourceURI())
                                .build();
        }

        public static MarvelCharacter create(String json) throws IOException {
                MarvelCharacter m = new MarvelCharacter();
                if (json != null) {
                        try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
                                JsonReader r = Json.createReader(is);
                                JsonObject o = r.readObject();
                                m.setId(o.getInt("id"));
                                m.setName(o.getString("name"));
                                m.setDescription(o.getString("description"));
                                m.setModified(o.getString("modified"));
                                Thumbnail thumbnail = new Thumbnail(o.getJsonObject("thumbnail").getString("path"),
                                                o.getJsonObject("thumbnail").getString("extension"));
                                m.setThumbnail(thumbnail);
                                m.setResourceURI(o.getString("resourceURI"));
                                ;
                        }
                }
                return m;
        }
}

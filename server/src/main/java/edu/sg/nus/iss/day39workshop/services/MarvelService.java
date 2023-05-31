package edu.sg.nus.iss.day39workshop.services;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import edu.sg.nus.iss.day39workshop.model.MarvelCharacter;
import edu.sg.nus.iss.day39workshop.model.Thumbnail;
import edu.sg.nus.iss.day39workshop.repositories.MarvelRepository;
import edu.sg.nus.iss.day39workshop.util.MarvelUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.xml.bind.DatatypeConverter;

import static edu.sg.nus.iss.day39workshop.util.MarvelUtil.*;

@Service
public class MarvelService {

        @Autowired
        private MarvelRepository marvelRepo;

        public static final String charactersURL = "https://gateway.marvel.com/v1/public/characters";

        @Value("${marvel.apikey}")
        private String apiKey;

        @Value("${marvel.privatekey}")
        private String privateKey;

        public List<MarvelCharacter> getCharacters(String startsWith, String limit, String offset)
                        throws NoSuchAlgorithmException {

                // Remove all spaces in time string - causes %20 otherwise which leads to error
                String ts = new Timestamp(System.currentTimeMillis()).toString().trim().replaceAll("\\s+", "");

                String get5MDString = getMD5Hash(ts, apiKey, privateKey);

                // Build URI String
                String url = UriComponentsBuilder.fromUriString(charactersURL)
                                .queryParam("ts", ts)
                                .queryParam("apikey", apiKey)
                                .queryParam("hash", get5MDString)
                                .queryParam("nameStartsWith", startsWith)
                                .queryParam("limit", limit)
                                .queryParam("offset", offset)
                                .toUriString();

                System.out.println("ts ===>" + ts);
                System.out.println("apikey ===>" + apiKey);
                System.out.println("hash ===>" + get5MDString);

                System.out.println("url is now ===>" + url);
                RequestEntity<Void> req = RequestEntity.get(url)
                                .accept(MediaType.APPLICATION_JSON)
                                .build();

                RestTemplate template = new RestTemplate();
                ResponseEntity<String> resp = null;

                resp = template.exchange(req, String.class);
                String payload = resp.getBody();

                JsonReader reader = Json.createReader(new StringReader(payload));
                JsonObject data = reader.readObject();

                List<MarvelCharacter> characters = data.getJsonObject("data").getJsonArray("results").stream()
                                .map(v -> v.asJsonObject())
                                .map(o -> new MarvelCharacter(o.getInt("id"), o.getString("name"),
                                                o.getString("description") != null ? o.getString("description") : " ",
                                                o.getString("modified") != null ? o.getString("modified") : " ",
                                                new Thumbnail(o.getJsonObject("thumbnail").getString("path"),
                                                                o.getJsonObject("thumbnail").getString("extension")),
                                                o.getString("resourceURI") != null ? o.getString("resourceURI") : " "))
                                .toList();

                // save to Redis
                for (MarvelCharacter character : characters) {
                        marvelRepo.saveCharacter(character);
                }

                return characters;
        }

        public String getMD5Hash(String ts, String apiKey, String privateKey) throws NoSuchAlgorithmException {
                MessageDigest md = MessageDigest.getInstance("MD5");
                String concatString = ts + privateKey + apiKey;
                System.out.println("ts ===>" + ts);
                System.out.println("privatekey ===>" + privateKey);
                System.out.println("apikey ===>" + apiKey);
                System.out.println(">>>>concatenated string is --->" + concatString);
                md.update(concatString.getBytes());
                byte[] digest = md.digest();
                String MD5Hash = DatatypeConverter.printHexBinary(digest).toLowerCase();
                return MD5Hash;
        }

        public MarvelCharacter getCharacterById(String mId) throws IOException {
                return marvelRepo.getCharacterById(mId);
        }

        public List<String> getComments(String mId) {
                return marvelRepo.getComments(mId);
        }

        public void insertComment(String mId, String comment) {
                marvelRepo.insertComment(mId, comment);
        }
}

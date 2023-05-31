package edu.sg.nus.iss.day39workshop.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jakarta.xml.bind.DatatypeConverter;

public class MarvelUtil {


    public static String getMD5Hash(String ts, String apiKey, String privateKey) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("MD5");
        String concatString = ts.concat(apiKey.concat(privateKey));
        System.out.println(">>>>concatenated string is --->"+ concatString);
        md.update(concatString.getBytes());
        byte[] digest = md.digest();
        String MD5Hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return MD5Hash;
    }
}

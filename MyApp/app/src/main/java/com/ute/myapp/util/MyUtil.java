package com.ute.myapp.util;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.ute.myapp.config.Config;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.model.Chapter;
import com.ute.myapp.model.Genre;
import com.ute.myapp.model.User;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class MyUtil {
    public static String getExtension(String imageUrl) {
        Uri uri = Uri.parse(imageUrl);
        return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
    }

    public static String fieldExist(String field) {
        return "\"" + field + "\"" + " đã tồn tại";
    }

    public static String genreNameExist(String genreName) {
        return "\"" + genreName + "\"" + " đã tồn tại";
    }

    public static String hashedPassword(String password) {
        return BCrypt.withDefaults().hashToString(Constant.COST, password.toCharArray());
    }

    public static boolean isMatch(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }

    public static String listGenreToJson(List<Genre> genreList) {
        Gson gson = new Gson();
        return gson.toJson(genreList);
    }

    public static String fullName(String firstName, String lastName) {
        return lastName + " " + firstName;
    }

    public static String listUserToJson(List<User> userList) {
        Gson gson = new Gson();
        return gson.toJson(userList);
    }

    public static List<User> filterListUser(List<User> userList) {
        return userList.stream().filter(user -> !user.getRoleName().equalsIgnoreCase(Constant.ADMIN)).collect(Collectors.toList());
    }

    public static String convertRoleName(String roleName) {
        if (roleName.equalsIgnoreCase(Constant.CONVERT_USER)) {
            return Constant.USER;
        } else if (roleName.equalsIgnoreCase(Constant.CONVERT_AUTHOR)) {
            return Constant.AUTHOR;
        } else if (roleName.equalsIgnoreCase(Constant.CONVERT_CONTENT_MANAGER)) {
            return Constant.CONTENT_MANAGER;
        }
        return null;
    }

    public static String showLockedTime(LocalDateTime lockedTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return Constant.LOCKED_TIME_PREFIX + lockedTime.format(formatter);
    }

    public static String listStoryToJson(List<Map<String, Object>> mapList) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Long.class, new TypeAdapter<Long>() {
                    @Override
                    public void write(JsonWriter out, Long value) throws IOException {
                        out.value(value);
                    }

                    @Override
                    public Long read(JsonReader in) throws IOException {
                        if (in.peek() == JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        try {
                            String stringValue = in.nextString();
                            return Long.valueOf(stringValue);
                        } catch (NumberFormatException e) {
                            throw new JsonParseException(e);
                        }
                    }
                })
                .create();
        return gson.toJson(mapList);
    }

    public static String mapStoryToJson(Map<String, Object> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static String standardChapter(String chapter) {
        String current = chapter.replaceFirst("chapter", "Chapter ");
        return current.replaceFirst("Chapter", "Chương");
    }


    public static String convertChapter(String chapter) {
        return chapter.replaceFirst("Chương ", "chapter");
    }

    public static String chapterToJson(Chapter chapter) {
        Gson gson = new Gson();
        return gson.toJson(chapter);
    }

    public static Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put(Constant.FIRST_NAME, user.getFirstName());
        mapUser.put(Constant.LAST_NAME, user.getLastName());
        mapUser.put(Constant.USER_NAME, user.getUserName());
        mapUser.put(Constant.PASSWORD, user.getPassword());
        mapUser.put(Constant.PHONE, user.getPhone());
        mapUser.put(Constant.DATE_OF_BIRTH, user.getDateOfBirth());
        mapUser.put(Constant.EMAIL, user.getEmail());
        if (user.getImageUrl() == null) {
            mapUser.put(Constant.IMAGE_URL, Constant.DEFAULT_AVATAR_URL);
        } else {
            mapUser.put(Constant.IMAGE_URL, user.getImageUrl());
        }
        if (user.getRoleName() == null) {
            mapUser.put(Constant.ROLE_NAME, Constant.DEFAULT_ROLE_NAME);
        } else {
            mapUser.put(Constant.ROLE_NAME, user.getRoleName());
        }
        mapUser.put(Constant.USER_STATUS, Constant.DEFAULT_STATUS);
        mapUser.put(Constant.LOCKED_TIME, Constant.DEFAULT_LOCKED_DATE);
        return mapUser;
    }

    public static Map<String, Object> convertUserToMapUpdate(User user) {
        Map<String, Object> mapUser = new HashMap<>();
        mapUser.put(Constant.DATE_OF_BIRTH, user.getDateOfBirth());
        mapUser.put(Constant.EMAIL, user.getEmail());
        mapUser.put(Constant.FIRST_NAME, user.getFirstName());
        mapUser.put(Constant.IMAGE_URL, user.getImageUrl());
        mapUser.put(Constant.LAST_NAME, user.getLastName());
        mapUser.put(Constant.LOCKED_TIME, user.getLockedTime());
        mapUser.put(Constant.PASSWORD, user.getPassword());
        mapUser.put(Constant.PHONE, user.getPhone());
        mapUser.put(Constant.ROLE_NAME, user.getRoleName());
        mapUser.put(Constant.USER_STATUS, user.isStatus());
        mapUser.put(Constant.USER_NAME, user.getUserName());
        return mapUser;
    }

    public static Map<String, Object> convertGenreToMap(Genre genre) {
        Map<String, Object> mapGenre = new HashMap<>();
        mapGenre.put(Constant.GENRE_NAME, genre.getGenreName());
        mapGenre.put(Constant.GENRE_STATUS, genre.isStatus());
        return mapGenre;
    }

    public static String convertEmail(String email) {
        int atIndex = email.indexOf("@");
        String userName = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        StringBuilder maskedUsername = new StringBuilder(String.valueOf(userName.charAt(0)));
        for (int i = 1; i < atIndex; i++) {
            maskedUsername.append("*");
        }
        maskedUsername.append(domain);
        return maskedUsername.toString();
    }

    public static String randomOTP() {
        int length = 6;
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public static void sendMail(String recipient, String OTP) {
        String content = "Nhập " + "<span style=\"font-weight: bold; color: blue;\">" + OTP + "</span>" + " trên " + Constant.APP_NAME + " để xác thực tài khoản. Mã xác thực này chỉ tồn tại trong 5 phút.";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", Config.SMTP_SERVER);
        properties.put("mail.smtp.port", Config.SMTP_PORT);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.SENDER, Config.PASSWORD);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            mimeMessage.setSubject(Constant.APP_NAME);
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(content, Constant.MINE_TEXT_HTML);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            mimeMessage.setContent(multipart);
            Thread thread = new Thread(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.itacademy.petAcademy.models;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordGenerator {

    public static String generatePassword(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String generatePasswordWithSpecialChars(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        return RandomStringUtils.random(length, characters);
    }

}
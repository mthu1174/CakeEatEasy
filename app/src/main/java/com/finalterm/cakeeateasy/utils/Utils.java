// app/src/main/java/com/finalterm/cakeeateasy/Utils.java
package com.finalterm.cakeeateasy.utils; // <-- Nhớ đổi package cho đúng với project của bạn

import java.security.SecureRandom;

public class Utils {

    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    /**
     * Sinh ra một chuỗi ID ngẫu nhiên gồm chữ hoa và số.
     * @param length Độ dài của chuỗi ID cần tạo.
     * @return Chuỗi ID ngẫu nhiên.
     */
    public static String generateRandomOrderId(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Độ dài phải lớn hơn 0");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALPHANUMERIC_CHARACTERS.length());
            sb.append(ALPHANUMERIC_CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
package com.starshop.service.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @owner: L. Ký HMAC-SHA512 theo chuẩn VNPAY sandbox.
 * Bí mật (tmn-code, hash-secret) đọc từ application.yml (biến môi trường).
 */
@Slf4j
@Service
public class VnpayServiceImpl implements VnpayService {

    @Value("${starshop.vnpay.tmn-code}")    private String tmnCode;
    @Value("${starshop.vnpay.hash-secret}") private String hashSecret;
    @Value("${starshop.vnpay.pay-url}")     private String payUrl;
    @Value("${starshop.vnpay.return-url}")  private String returnUrl;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public String createPaymentUrl(BigDecimal amountVnd, String txnRef, String orderInfo, String clientIp) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        Map<String, String> p = new TreeMap<>();
        p.put("vnp_Version", "2.1.0");
        p.put("vnp_Command", "pay");
        p.put("vnp_TmnCode", tmnCode);
        p.put("vnp_Amount", amountVnd.multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        p.put("vnp_CurrCode", "VND");
        p.put("vnp_TxnRef", txnRef);
        p.put("vnp_OrderInfo", orderInfo);
        p.put("vnp_OrderType", "other");
        p.put("vnp_Locale", "vn");
        p.put("vnp_ReturnUrl", returnUrl);
        p.put("vnp_IpAddr", clientIp == null ? "127.0.0.1" : clientIp);
        p.put("vnp_CreateDate", now.format(FMT));
        p.put("vnp_ExpireDate", now.plusMinutes(15).format(FMT));

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Iterator<Map.Entry<String, String>> it = p.entrySet().iterator(); it.hasNext(); ) {
            var e = it.next();
            String enc = URLEncoder.encode(e.getValue(), StandardCharsets.US_ASCII);
            hashData.append(e.getKey()).append('=').append(enc);
            query.append(URLEncoder.encode(e.getKey(), StandardCharsets.US_ASCII)).append('=').append(enc);
            if (it.hasNext()) { hashData.append('&'); query.append('&'); }
        }
        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        return payUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    @Override
    public boolean verifyReturn(Map<String, String> params) {
        Map<String, String> p = new TreeMap<>(params);
        String received = p.remove("vnp_SecureHash");
        p.remove("vnp_SecureHashType");
        if (received == null) return false;

        StringBuilder hashData = new StringBuilder();
        for (Iterator<Map.Entry<String, String>> it = p.entrySet().iterator(); it.hasNext(); ) {
            var e = it.next();
            hashData.append(e.getKey()).append('=')
                    .append(URLEncoder.encode(e.getValue(), StandardCharsets.US_ASCII));
            if (it.hasNext()) hashData.append('&');
        }
        String expected = hmacSHA512(hashSecret, hashData.toString());
        return expected.equalsIgnoreCase(received) && "00".equals(params.get("vnp_ResponseCode"));
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception ex) {
            log.error("Lỗi ký VNPAY", ex);
            throw new IllegalStateException("Không ký được VNPAY", ex);
        }
    }
}

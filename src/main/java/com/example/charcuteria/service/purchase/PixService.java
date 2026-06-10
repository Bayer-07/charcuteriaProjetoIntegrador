package com.example.charcuteria.service.purchase;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@Service
public class PixService {

    @Value("${pix.key:test_key}")
    private String pixKey;

    @Value("${pix.merchant.name:test_name}")
    private String merchantName;

    @Value("${pix.merchant.city:test_city}")
    private String merchantCity;

    public PixResult generatePixForPurchase(BigDecimal amount, Long purchaseId) {
        String txid = UUID.randomUUID().toString().replace("-", "").substring(0, 25).toUpperCase();

        String payload = buildPixPayload(amount, txid);
        String base64Image = generateQrCodeBase64(payload);

        return new PixResult(payload, base64Image, amount, txid);
    }

    private String buildPixPayload(BigDecimal amount, String txid) {
        String formattedAmount = String.format(Locale.US, "%.2f", amount);

        String gui      = "br.gov.bcb.pix";
        String mai      = tlv("00", gui) + tlv("01", pixKey);
        String adf      = tlv("62", tlv("05", txid));

        StringBuilder sb = new StringBuilder();
        sb.append("000201");
        sb.append(tlv("26", mai));
        sb.append("52040000");
        sb.append("5303986");
        sb.append(tlv("54", formattedAmount));
        sb.append("5802BR");
        sb.append(tlv("59", merchantName));
        sb.append(tlv("60", merchantCity));
        sb.append(adf);
        sb.append("6304");
        sb.append(calculateCRC16CCITT(sb.toString()));

        return sb.toString();
    }

    private String tlv(String tag, String value) {
        return tag + String.format("%02d", value.length()) + value;
    }

    private String calculateCRC16CCITT(String payload) {
        int crc = 0xFFFF;
        for (byte b : payload.getBytes(StandardCharsets.UTF_8)) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                crc = (crc & 0x8000) != 0 ? (crc << 1) ^ 0x1021 : crc << 1;
            }
            crc &= 0xFFFF;
        }
        return String.format("%04X", crc);
    }

    private String generateQrCodeBase64(String payload) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(payload, BarcodeFormat.QR_CODE, 300, 300, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar o QR Code em memória", e);
        }
    }

    // Immutable record for internal service communication
    public record PixResult(String payload, String qrCodeBase64, BigDecimal amount, String txid) {}
}
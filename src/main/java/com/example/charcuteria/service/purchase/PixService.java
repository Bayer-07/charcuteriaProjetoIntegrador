package com.example.charcuteria.service.purchase;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class PixService {

    public PixResult generatePixForPurchase(BigDecimal amount, Long purchaseId) {
        String txid = "TX" + UUID.randomUUID().toString().replace("-", "").substring(0, 15).toUpperCase();

        String payload = String.format(
            "00020126330014br.gov.bcb.pix01111234567890052040000530398654%05.2f5802BR5916Koch Charcutaria6008Cascavel62070503%s6304A1B2",
            amount, txid
        );

        String base64Image = generateQrCodeBase64(payload);

        return new PixResult(payload, base64Image, amount, txid);
    }

    private String generateQrCodeBase64(String payload) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(payload, BarcodeFormat.QR_CODE, 300, 300);

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

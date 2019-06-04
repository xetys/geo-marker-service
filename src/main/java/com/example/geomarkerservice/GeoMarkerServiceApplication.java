package com.example.geomarkerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

@SpringBootApplication
@RestController
public class GeoMarkerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeoMarkerServiceApplication.class, args);
    }

    @GetMapping(value = "/marker-{fillColor}-{state}-{scale}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public void drawMarker(
            @PathVariable String fillColor,
            @PathVariable String state,
            @PathVariable Integer scale,
            HttpServletResponse response
    ) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(30 * scale, 40 * scale, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedImage.createGraphics();

        // set transparent background
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, 30 * scale, 40 * scale);
        g2d.setComposite(AlphaComposite.Src);

        // draw marker body
        g2d.setColor(parseColor(fillColor));
        g2d.fillOval(0, 0, 30 * scale, 30 * scale);

        QuadCurve2D q = new QuadCurve2D.Float();
        q.setCurve(1 * scale, 20 * scale, 15 * scale, 60 * scale, 29 * scale, 20 * scale);
        g2d.fill(q);

        g2d.setColor(Color.white);
        g2d.fillOval(7 * scale, 7 * scale, 16 * scale, 16 * scale);

        g2d.setColor(getColorByState(state));
        g2d.fillOval(10 * scale, 10 * scale, 10 * scale, 10 * scale);


        // Disposes of this graphics context and releases any system resources that it is using.
        g2d.dispose();

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        ImageIO.write(bufferedImage, "png", response.getOutputStream());
    }

    private Color getColorByState(String state) {
        switch (state) {
            case "active":
            case "online":
                return Color.green;
            case "offline":
            case "inactive":
                return Color.red;
            default:
                return Color.yellow;
        }
    }

    private Color parseColor(String hex) {
        return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16));
    }
}

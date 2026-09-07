package service;

import jakarta.servlet.http.Part;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class ImageStorageService {

    public static final String DEFAULT_IMAGE_PATH =
            "uploads/default-object.png";

    private static final long MAX_FILE_SIZE_BYTES =
            5 * 1024 * 1024L;

    private static final int MAX_WIDTH = 6000;
    private static final int MAX_HEIGHT = 6000;

    private static final long MAX_PIXELS =
            25_000_000L;

    private static final Set<String> ALLOWED_MIME_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png"
            );

    private static final Pattern MANAGED_IMAGE_PATTERN =
            Pattern.compile(
                    "^uploads/"
                            + "[0-9a-fA-F]{8}-"
                            + "[0-9a-fA-F]{4}-"
                            + "[0-9a-fA-F]{4}-"
                            + "[0-9a-fA-F]{4}-"
                            + "[0-9a-fA-F]{12}"
                            + "\\.(jpg|png)$"
            );

    public String storeIfPresent(
            Part part,
            Path uploadDirectory)
            throws IOException {

        if (part == null
                || part.getSize() == 0) {

            return null;
        }

        if (part.getSize()
                > MAX_FILE_SIZE_BYTES) {

            throw new IllegalArgumentException(
                    "L'image ne doit pas dépasser 5 Mo."
            );
        }

        String contentType =
                part.getContentType();

        if (contentType == null
                || !ALLOWED_MIME_TYPES.contains(
                        contentType
                                .trim()
                                .toLowerCase(
                                        Locale.ROOT
                                )
                )) {

            throw new IllegalArgumentException(
                    "Seules les images JPEG et PNG "
                            + "sont autorisées."
            );
        }

        try (InputStream inputStream =
                     part.getInputStream();

             ImageInputStream imageInputStream =
                     ImageIO.createImageInputStream(
                             inputStream
                     )) {

            if (imageInputStream == null) {

                throw new IllegalArgumentException(
                        "Fichier image invalide."
                );
            }

            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(
                            imageInputStream
                    );

            if (!readers.hasNext()) {

                throw new IllegalArgumentException(
                        "Fichier image invalide."
                );
            }

            ImageReader reader =
                    readers.next();

            try {

                reader.setInput(
                        imageInputStream,
                        true,
                        true
                );

                String detectedFormat =
                        reader
                                .getFormatName()
                                .toLowerCase(
                                        Locale.ROOT
                                );

                String extension;
                String outputFormat;

                if ("jpeg".equals(detectedFormat)
                        || "jpg".equals(
                                detectedFormat
                        )) {

                    extension = ".jpg";
                    outputFormat = "jpg";

                } else if ("png".equals(
                        detectedFormat
                )) {

                    extension = ".png";
                    outputFormat = "png";

                } else {

                    throw new IllegalArgumentException(
                            "Seules les images JPEG "
                                    + "et PNG sont autorisées."
                    );
                }

                int width =
                        reader.getWidth(0);

                int height =
                        reader.getHeight(0);

                long pixels =
                        (long) width * height;

                if (width <= 0
                        || height <= 0
                        || width > MAX_WIDTH
                        || height > MAX_HEIGHT
                        || pixels > MAX_PIXELS) {

                    throw new IllegalArgumentException(
                            "Dimensions d'image "
                                    + "non autorisées."
                    );
                }

                BufferedImage image =
                        reader.read(0);

                if (image == null) {

                    throw new IllegalArgumentException(
                            "Fichier image invalide."
                    );
                }

                Files.createDirectories(
                        uploadDirectory
                );

                String filename =
                        UUID.randomUUID()
                                + extension;

                Path storedFile =
                        uploadDirectory
                                .resolve(filename)
                                .normalize();

                try {

                    boolean written =
                            ImageIO.write(
                                    image,
                                    outputFormat,
                                    storedFile.toFile()
                            );

                    if (!written) {

                        throw new IOException(
                                "Impossible d'écrire "
                                        + "l'image."
                        );
                    }

                } catch (IOException
                         | RuntimeException ex) {

                    Files.deleteIfExists(
                            storedFile
                    );

                    throw ex;
                }

                return "uploads/" + filename;

            } finally {

                reader.dispose();
            }
        }
    }

    public boolean deleteManagedImage(
            String imagePath,
            Path uploadDirectory)
            throws IOException {

        if (imagePath == null
                || imagePath.isBlank()
                || DEFAULT_IMAGE_PATH.equals(
                        imagePath
                )
                || !MANAGED_IMAGE_PATTERN
                        .matcher(imagePath)
                        .matches()) {

            return false;
        }

        String filename =
                imagePath.substring(
                        "uploads/".length()
                );

        Path root =
                uploadDirectory
                        .toAbsolutePath()
                        .normalize();

        Path file =
                root.resolve(filename)
                        .normalize();

        if (!root.equals(
                file.getParent()
        )) {

            return false;
        }

        return Files.deleteIfExists(file);
    }
}
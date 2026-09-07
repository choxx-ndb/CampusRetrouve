package service;

import jakarta.servlet.http.Part;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageStorageServiceTest {

    @TempDir
    Path tempDir;

    private final ImageStorageService imageStorageService =
            new ImageStorageService();

    @Test
    void shouldReturnNullWhenNoImageIsProvided()
            throws Exception {

        Part emptyPart =
                mock(Part.class);

        when(emptyPart.getSize())
                .thenReturn(0L);

        assertAll(
                () -> assertNull(
                        imageStorageService
                                .storeIfPresent(
                                        null,
                                        tempDir
                                )
                ),
                () -> assertNull(
                        imageStorageService
                                .storeIfPresent(
                                        emptyPart,
                                        tempDir
                                )
                )
        );
    }

    @Test
    void shouldStoreAndRewriteValidPng()
            throws Exception {

        byte[] original =
                createImageBytes(
                        "png",
                        20,
                        20
                );

        byte[] trailing =
                "UNTRUSTED-TRAILING-DATA"
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        byte[] uploaded =
                Arrays.copyOf(
                        original,
                        original.length
                                + trailing.length
                );

        System.arraycopy(
                trailing,
                0,
                uploaded,
                original.length,
                trailing.length
        );

        Part part =
                createPart(
                        uploaded,
                        "image/png"
                );

        String storedPath =
                imageStorageService
                        .storeIfPresent(
                                part,
                                tempDir
                        );

        assertNotNull(storedPath);

        assertTrue(
                storedPath.matches(
                        "uploads/"
                                + "[0-9a-fA-F\\-]{36}"
                                + "\\.png"
                )
        );

        Path storedFile =
                resolveStoredFile(
                        storedPath
                );

        assertTrue(
                Files.exists(storedFile)
        );

        assertNotNull(
                ImageIO.read(
                        storedFile.toFile()
                )
        );

        assertFalse(
                Arrays.equals(
                        uploaded,
                        Files.readAllBytes(
                                storedFile
                        )
                )
        );
    }

    @Test
    void shouldStoreAndRewriteValidJpeg()
            throws Exception {

        byte[] uploaded =
                createImageBytes(
                        "jpg",
                        20,
                        20
                );

        Part part =
                createPart(
                        uploaded,
                        "image/jpeg"
                );

        String storedPath =
                imageStorageService
                        .storeIfPresent(
                                part,
                                tempDir
                        );

        assertNotNull(storedPath);

        assertTrue(
                storedPath.matches(
                        "uploads/"
                                + "[0-9a-fA-F\\-]{36}"
                                + "\\.jpg"
                )
        );

        Path storedFile =
                resolveStoredFile(
                        storedPath
                );

        assertTrue(
                Files.exists(storedFile)
        );

        assertNotNull(
                ImageIO.read(
                        storedFile.toFile()
                )
        );
    }

    @Test
    void shouldRejectNonImageContentWithSpoofedImageMime()
            throws Exception {

        byte[] uploaded =
                "This is not an image."
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        Part part =
                createPart(
                        uploaded,
                        "image/jpeg"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> imageStorageService
                        .storeIfPresent(
                                part,
                                tempDir
                        )
        );

        assertDirectoryEmpty();
    }

    @Test
    void shouldRejectValidImageWithUnsupportedMime()
            throws Exception {

        byte[] uploaded =
                createImageBytes(
                        "png",
                        20,
                        20
                );

        Part part =
                createPart(
                        uploaded,
                        "application/octet-stream"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> imageStorageService
                        .storeIfPresent(
                                part,
                                tempDir
                        )
        );

        assertDirectoryEmpty();
    }

    @Test
    void shouldRejectGifEvenWhenMimeIsSpoofedAsJpeg()
            throws Exception {

        byte[] uploaded =
                createImageBytes(
                        "gif",
                        20,
                        20
                );

        Part part =
                createPart(
                        uploaded,
                        "image/jpeg"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> imageStorageService
                        .storeIfPresent(
                                part,
                                tempDir
                        )
        );

        assertDirectoryEmpty();
    }

    @Test
    void shouldRejectFileLargerThanFiveMegabytes()
            throws Exception {

        Part part =
                mock(Part.class);

        when(part.getSize())
                .thenReturn(
                        5 * 1024 * 1024L + 1
                );

        when(part.getContentType())
                .thenReturn("image/jpeg");

        assertThrows(
                IllegalArgumentException.class,
                () -> imageStorageService
                        .storeIfPresent(
                                part,
                                tempDir
                        )
        );

        assertDirectoryEmpty();
    }

    @Test
    void shouldRejectImageWithExcessiveDimensions()
            throws Exception {

        byte[] uploaded =
                createImageBytes(
                        "png",
                        6001,
                        1
                );

        Part part =
                createPart(
                        uploaded,
                        "image/png"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> imageStorageService
                        .storeIfPresent(
                                part,
                                tempDir
                        )
        );

        assertDirectoryEmpty();
    }

    @Test
    void shouldDeleteManagedUuidImage()
            throws Exception {

        String filename =
                UUID.randomUUID()
                        + ".jpg";

        Path file =
                tempDir.resolve(
                        filename
                );

        Files.write(
                file,
                new byte[]{1, 2, 3}
        );

        boolean deleted =
                imageStorageService
                        .deleteManagedImage(
                                "uploads/" + filename,
                                tempDir
                        );

        assertTrue(deleted);

        assertFalse(
                Files.exists(file)
        );
    }

    @Test
    void shouldNotDeleteDefaultOrUnmanagedImage()
            throws Exception {

        Path defaultImage =
                tempDir.resolve(
                        "default-object.png"
                );

        Path unmanagedImage =
                tempDir.resolve(
                        "manual-image.jpg"
                );

        Files.write(
                defaultImage,
                new byte[]{1}
        );

        Files.write(
                unmanagedImage,
                new byte[]{2}
        );

        boolean defaultDeleted =
                imageStorageService
                        .deleteManagedImage(
                                ImageStorageService
                                        .DEFAULT_IMAGE_PATH,
                                tempDir
                        );

        boolean unmanagedDeleted =
                imageStorageService
                        .deleteManagedImage(
                                "uploads/manual-image.jpg",
                                tempDir
                        );

        assertAll(
                () -> assertFalse(
                        defaultDeleted
                ),
                () -> assertFalse(
                        unmanagedDeleted
                ),
                () -> assertTrue(
                        Files.exists(
                                defaultImage
                        )
                ),
                () -> assertTrue(
                        Files.exists(
                                unmanagedImage
                        )
                )
        );
    }

    private Part createPart(
            byte[] bytes,
            String contentType)
            throws Exception {

        Part part =
                mock(Part.class);

        when(part.getSize())
                .thenReturn(
                        (long) bytes.length
                );

        when(part.getContentType())
                .thenReturn(
                        contentType
                );

        when(part.getInputStream())
                .thenAnswer(
                        invocation ->
                                new ByteArrayInputStream(
                                        bytes
                                )
                );

        return part;
    }

    private byte[] createImageBytes(
            String format,
            int width,
            int height)
            throws Exception {

        int imageType =
                "png".equalsIgnoreCase(format)
                        ? BufferedImage.TYPE_INT_ARGB
                        : BufferedImage.TYPE_INT_RGB;

        BufferedImage image =
                new BufferedImage(
                        width,
                        height,
                        imageType
                );

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        boolean written =
                ImageIO.write(
                        image,
                        format,
                        output
                );

        if (!written) {

            throw new IllegalStateException(
                    "Format de test non supporté : "
                            + format
            );
        }

        return output.toByteArray();
    }

    private Path resolveStoredFile(
            String storedPath) {

        String filename =
                Path.of(storedPath)
                        .getFileName()
                        .toString();

        return tempDir.resolve(filename);
    }

    private void assertDirectoryEmpty()
            throws Exception {

        try (var files =
                     Files.list(tempDir)) {

            assertEquals(
                    0,
                    files.count()
            );
        }
    }
}
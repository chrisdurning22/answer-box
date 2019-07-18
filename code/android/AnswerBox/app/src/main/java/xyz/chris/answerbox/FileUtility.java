package xyz.cathal.answerbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A helper class to assist with various aspects of file management
 *
 * @author Cathal Conroy
 */

final class FileUtility {

    static File getFile(Context context, xyz.cathal.answerbox.File file) {
        File storageDir = context.getExternalFilesDir("downloads");
        return new File(storageDir.getPath() + "/" + file.getFileName());
    }

    /**
     * Decodes a file from Base64.
     *
     * @param context The calling context
     * @param inputFile The file to be decoded
     * @return The decoded file
     */
    static File fromBase64(Context context, xyz.cathal.answerbox.File inputFile) {
        File storageDir = context.getExternalFilesDir("downloads");
        File file = new File(storageDir.getPath() + "/" + inputFile.getFileName());
        if (file.exists() && !file.isDirectory()) {
            return file;
        }

        String hash = inputFile.hash;
        String extension = inputFile.extension;
        String data = inputFile.data;
        File output = null;

        try {
            output = createImageFile(context, hash, extension);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        byte[] decodedBytes = Base64.decode(data, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            if (extension.equals("jpg") || extension.equals("jpeg")) {
                decodedImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            } else if (extension.equals("png")) {
                decodedImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    /**
     * Encodes a file in Base64.
     *
     * @param file The file to be converted
     * @return The converted file
     */
    static String toBase64(File file) {
        String output = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            output = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    /**
     * Returns a files extension, *as deduced from its file name. This does NOT guarantee or even
     * check if the extension if legitimate*.
     *
     * @param file The file from which to derive the extension
     * @return The file's extension
     */
    static String getExtension(File file) {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        return name.substring(i + 1);
    }

    /**
     * Creates a file in the application's external directory.
     *
     * @param context The calling context
     * @param hash The file's hash (used to name the new file)
     * @param extension The file's extension
     * @return The newly created file
     * @throws IOException Throws a FileNotFoundException if the file cannot be created
     */
    private static File createImageFile(Context context, String hash, String extension) throws IOException {
        File storageDir = context.getExternalFilesDir("downloads");
        String fileName = hash + "." + extension;
//        File image = File.createTempFile(
//                hash,  /* prefix */
//                "." + extension,         /* suffix */
//                storageDir      /* directory */
//        );
        File image = new File(storageDir, fileName);

        return image;
    }
}

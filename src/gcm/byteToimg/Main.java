package gcm.byteToimg;

import java.io.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
//
//        ByteArrayInputStream bis;
//        File file = new File("f7393cea-9302-4864-8571-bf348e1ec453");
//        try {
//            byte[] fileContent = Files.readAllBytes(file.toPath());
//            BufferedImage bImage2 = null;
//            bImage2 = ImageIO.read(new ByteArrayInputStream(fileContent));
//            ImageIO.write(bImage2, "jpg", new File("output.jpg") );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //bis=fileContent;
//
//        System.out.println("image created");
//        File file = new File("f7393cea-9302-4864-8571-bf348e1ec453");
//        try {
//            byte[] img= Files.readAllBytes(file.toPath());
//            for(int i=0; i< img.length ; i++) {
//                System.out.print(img[i] +" ");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        byte[] img= new byte[]{1,1,1,1,1,1,};
        String imgStr = new String(img);
        System.out.println(imgStr);
    }
}

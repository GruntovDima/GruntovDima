package Bilateral;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.text.DecimalFormat;

public class BilateralFilter {
    public static double SigmaS;
    public static double SigmaR;
    private static double[][] getSpaceArray(double SigmaS, int size) {
        double[][] ans = new double[size * 2 + 1][size * 2 + 1];
        for(int i = 0; i < size * 2 + 1; i++)
            for(int j = 0; j < size * 2 + 1; j++) {
                ans[i][j] = Math.exp(- (((i - size) * (i - size) + (j - size) * (j - size)) / (2 * SigmaS * SigmaS)));
            }
        return ans;
    }
    private static double[] getColorArray(double SigmaR) {
        int n = 256;
        double[] ColorArray = new double[n];
        for(int i = 0; i < n; i++) {
            ColorArray[i] = Math.exp(-(i*i / (2.0 * SigmaR * SigmaR)));
        }
        return ColorArray;
    }
    public static void main(String[] args) {
        if(args.length != 4) {
            System.out.println("Wrong number of argumets\nRight is File, SigmaD, SigmaR and radius\n");
            return;
        }
        String FileName = args[0];
        SigmaS = Double.parseDouble(args[1]);
        SigmaR = Double.parseDouble(args[2]);
        int r = Integer.parseInt(args[3]);
        DecimalFormat Format = new DecimalFormat("%###.#");
        try{
            File file = new File(FileName);
            BufferedImage image = ImageIO.read(file);
            int m = image.getHeight();
            int n = image.getWidth();
            BufferedImage answer = new BufferedImage(n, m, image.getType());
            double[] sum = new double[3];
            double[] ColorArray = getColorArray(SigmaR);
            double[][] SpaceArray = getSpaceArray(SigmaS, r);
            for(int i = 0; i < n; i++) {
                System.out.println(Format.format(((double)(i + 1) / n)));
                for(int j = 0; j < m; j++) {
                    Color now = new Color(image.getRGB(i, j));
                    sum[0] = sum[1] = sum[2] = 0.0;
                    double WRed = 0;
                    double WBlue = 0;
                    double WGreen = 0;
                    for(int k = -r; k <= r; k++) {
                        if(i + k < 0 || i + k >= n) continue;
                        for(int z = -r; z <= r; z++) {
                            if(j + z < 0 || j + z >= m) continue;
                            Color color = new Color(image.getRGB(i + k, j + z));
                            double ws, wRed, wGreen, wBlue;
                            ws = SpaceArray[k + r][z + r];
                            wRed = ws * ColorArray[Math.abs(now.getRed() - color.getRed())];
                            wBlue = ws * ColorArray[Math.abs(now.getBlue() - color.getBlue())];
                            wGreen = ws * ColorArray[Math.abs(now.getGreen() - color.getGreen())];
                            WBlue+=wBlue;
                            WGreen+=wGreen;
                            WRed+=wRed;
                            sum[0] += (color.getRed() * wRed);
                            sum[1] += (color.getGreen() * wGreen);
                            sum[2] += (color.getBlue() * wBlue);

                        }
                    }
                    sum[0] /= WRed;
                    sum[1] /= WGreen;
                    sum[2] /= WBlue;
                    for(int z = 0; z < 3; z++)
                        sum[z] = sum[z] < 0 ? 0 : Math.min(sum[z], 255);

                    Color newColor = new Color((int)sum[0], (int)sum[1], (int)sum[2]);
                    answer.setRGB(i, j, newColor.getRGB());
                }
            }


            File output = new File("output.jpg");
            ImageIO.write(answer, "jpg", output);

        } catch (IOException e) {
            System.out.println("File " + FileName + " Not found or something else\n" + e.getMessage());
        }

    }
}

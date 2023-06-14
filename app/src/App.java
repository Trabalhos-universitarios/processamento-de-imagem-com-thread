import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class App {
    public static final String ARQUIVO_ORIGEM = "./img/many-flowers.jpg";
    public static final String ARQUIVO_DESTINO = "./out/many-flowers.jpg";

    public static void main(String[] args) throws IOException {

        String color = "\u001B["; // função que permite trocar cor no console

        BufferedImage ImagemOriginal = ImageIO.read(new File(ARQUIVO_ORIGEM));
        BufferedImage ImagemResultado = new BufferedImage(ImagemOriginal.getWidth(), ImagemOriginal.getHeight(), BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();
        recolorirSemThread(ImagemOriginal, ImagemResultado);
        int numberOfThreads = 4;
        //recolorMultithreaded(ImagemOriginal, ImagemResultado, numberOfThreads);
        //recolorFracionado(ImagemOriginal, ImagemResultado, numberOfThreads);

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        File outputFile = new File(ARQUIVO_DESTINO);
        ImageIO.write(ImagemResultado, "jpg", outputFile);

        if (numberOfThreads <= 0) {
            System.out.println(color+34+"m"+"\nTempo de execussão sem threads: "+String.valueOf(duration)+color+"m");
        } else {
            System.out.println(color+33+"m"+"\n*** Imagem 2140 x 2140 ***"+color+"m");
            System.out.println(color+33+"m"+"\nTempo de execussão com "+numberOfThreads+" threads: "+String.valueOf(duration)+color+"m");
        }
    }

    //grafico numero de threads vs tempo
    // FUNCAO NOVA SOLICITADA NA ATIVIDADE
    public static void recolorMultithreaded(BufferedImage ImagemOriginal, BufferedImage ImagemResultado, int partes) {
        List<Thread> threads = new ArrayList<>();

        int height = ImagemOriginal.getHeight() / partes;

        for (int i = 0; i < partes; i++) {
            final int multiplicadorInicio = i;
            int xInicio = 0;
            int yInicio = height * multiplicadorInicio;

            int xFim = ImagemOriginal.getWidth();
            int yFim = yInicio + height;

            Thread thread = new Thread(() -> {
                recolorirImagem(ImagemOriginal, ImagemResultado, xInicio, yInicio, xFim, yFim);
            });

            threads.add(thread);
            thread.start();
        }

        // Aguarda todas as threads terminarem
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void recolorFracionado(BufferedImage ImagemOriginal, BufferedImage ImagemResultado, int partes) {
    	int width = ImagemOriginal.getWidth();
    	int height = ImagemOriginal.getHeight()/partes;

    	for(int i = 0; i < partes; i++) {
    		final int multiplicadorInicio = i;
    		int xInicio = 0;
    		int yInicio = height*multiplicadorInicio;

    		recolorirImagem(ImagemOriginal, ImagemResultado, xInicio, yInicio, width, height);
    	}
    }

    public static void recolorirSemThread(BufferedImage ImagemOriginal, BufferedImage ImagemResultado) {
        recolorirImagem(ImagemOriginal, ImagemResultado, 0, 0, ImagemOriginal.getWidth(), ImagemOriginal.getHeight());
    }

    public static void recolorirImagem(BufferedImage ImagemOriginal, BufferedImage ImagemResultado, int leftCorner, int topCorner, int width, int height) {
        for(int x = leftCorner ; x < leftCorner + width && x < ImagemOriginal.getWidth() ; x++) {
            for(int y = topCorner ; y < topCorner + height && y < ImagemOriginal.getHeight() ; y++) {
                recolorirPixel(ImagemOriginal, ImagemResultado, x , y);
            }
        }
    }

    public static void recolorirPixel(BufferedImage ImagemOriginal, BufferedImage ImagemResultado, int x, int y) {
        int rgb = ImagemOriginal.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;
	//aqui vamos popular os novos pixels
	//se o pixel em quest�o for um tom de cinza, vamos aumentar o n�vel de vermelho em 10; o de verde diminuir 80, azul dimiuir 20
        if(ehNivelDeCinza(red, green, blue)) {
	    //para n�o exceder o valor m�ximo (255) pegamos o min
            newRed = Math.min(255, red + 90);
            newGreen = Math.max(0, green - 90);
            //para n�o passar o 0 pegamos o max
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        //M�todo para setar valor rgb na coordenada do pixel da imagem
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(ImagemResultado, x, y, newRGB);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }
    //metodo para verificar se o pixel � tom de cinza (estar� na parte branca da flor)
    //Checa se todos os componentes tem uma intensidade similar (< 30 - determinado empiricamente)
    public static boolean ehNivelDeCinza(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs( green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        //opera��o de OR deslocando para esquerda em cada cor
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 10;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 5;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}


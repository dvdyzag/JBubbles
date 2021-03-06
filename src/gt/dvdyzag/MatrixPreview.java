package gt.dvdyzag;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.NoSuchElementException;
import javax.swing.JFileChooser;

/**
 * Validador del archivo de entrada
 * Facilita al usario escoger escenario
 * @author David Yzaguirre Gonzalez
 * Carne 200819312
 * Seccion B
 */
public class MatrixPreview extends javax.swing.JPanel implements PropertyChangeListener {

    private JFileChooser jfc;
    //Para dibujar la matriz en memoria
    //private BufferedImage TempBI;
    private BufferedImage matrix;
    private Graphics2D g2d;
    //Lee el archivo
    private BufferedReader input;
    //Guarda el tipo de escenario
    public int scene;
    public static int ballCount;
    public static final int Columns = 10;
    public static final int Rows = 20;
    //Es la matriz de fichas
    public static Bubble DimeMatrix[][];
    public static int surface[][] = new int[2][Columns];
    public static int maximumSurface;
    //Variables utilizados para dibujar sobre el JLabel
    //"it" de iteraciones
    private int Yit;
    private boolean ValidFile;
    private Font font;
    private String mensaje;

    /** Creates new form MatrixPreview_200819312 */
    public MatrixPreview(JFileChooser jfc) {
        initComponents();
        this.jfc = jfc;
        scene = 1;
        mensaje = "Seleccione un nivel";
        font = new Font("Dialog", Font.BOLD, 12);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Devuelve true, si el archivo seleccionado
     * es valido, de lo contrario false
     **/
    public boolean isValidFile(){
        return ValidFile;
    }

    /**
     * actualiza el tipo de escenario escogido
     **/
    public void saveScene(int scene) {
        this.scene = scene;
    }

    /**
     * Si el archivo es valido, lo muestra
     * en el panel
     * @param file
     **/
    private void checkFile(File file) throws IOException {
        if (file == null){
            mensaje = "Seleccione un nivel";
            repaint();
            return;
        }
        if (checkScene(file)) {
            ValidFile = true;
            updateMatrix(file);
        } else {
            mensaje = "No es valido";
            ValidFile = false;
            repaint();
        }
    }

    private boolean checkScene(File file) throws IOException {
        System.out.println("comporbando archivo");
        int limit = ((scene==3)?10:20);
        boolean ColPass = true;
        //boolean RowPass = true;
        boolean endRowOne = false;
        boolean endRowTwo = false;
        FileReader fr = null;
        BufferedReader br = null;

        fr = new FileReader(file);
        br = new BufferedReader(fr);
        String fila;
        /**
         * Debe recorrer el archivo, en busca de anomalias
         * Si no las hay, da Visto Bueno para jugar
         **/
        //Contador de filas
        int i = -1;
        while ((fila = br.readLine()) != null && ++i <= limit) {

            //Revisa los caracteres
            for (int col = 0; col < Columns; col++) {
                if (!validChar(fila.charAt(2 * col))) {
                    br.close();
                    fr.close();
                    return false;
                }
            }
            //Revisa el final del archivo
            if (fila.equals("0,0,0,0,0,0,0,0,0,0")) {
                endRowOne = true;
                Yit = (i+1) * Bubble.DimeSize;
                break;
            }
            //Revisa tamanio de la fila
            if (fila.length() != 2 * Columns - 1) {
                ColPass = false;
                break;
            }
        }
        if ((fila = br.readLine()) == null && scene != 3 && endRowOne && ColPass) {
            br.close();
            fr.close();
            return true;
        }
        if (scene == 3) {
            while ((fila) != null && i++ <= limit*2) {

                //Revisa los caracteres
                for (int col = 0; col < Columns; col++) {
                    if (!validChar(fila.charAt(2 * col))) {
                        br.close();
                        fr.close();
                        return false;
                    }
                }
                //Revisa el final del archivo
                if (fila.equals("0,0,0,0,0,0,0,0,0,0")) {
                    endRowTwo = true;
                    Yit = (i) * Bubble.DimeSize;
                    break;
                }
                //Revisa tamanio de la fila
                if (fila.length() != 2 * Columns - 1) {
                    ColPass = false;
                    break;
                }
                fila = br.readLine();
            }
        }
        fila = br.readLine();//La ultima lectura
        br.close();
        fr.close();
        if (fila == null && endRowOne && endRowTwo && ColPass) {

            return true;
        }
        return false;
    }

    /**
     * devuelve true si el caracter es valido,
     * de lo contrario devuelve false
     **/
    private boolean validChar(char color) {
        boolean value = false;
        switch (color) {
            case 'A':
            case 'V':
            case 'R':
            case 'Y':
            case '0':
                value = true;
        }
        return value;
    }

    /**
     *
     **/
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            System.out.println("Actualizando");
            File file = jfc.getSelectedFile();
            checkFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Percibe cambios de seleccion entre archivos
     * Lee el archivo de texto y crea la matriz
     * inmediatamente llama al metodo redibujar()
     **/
    public void updateMatrix(File file) {
        System.out.println ("actualizando matriz");
        FileReader fr = null;
        //BufferedImage TempBI = new BufferedImage(Columns * Dime_200819312.DimeSize, Rows * Dime_200819312.DimeSize, BufferedImage.TYPE_INT_RGB);
        BufferedImage TempBI = new BufferedImage(Columns * Bubble.DimeSize, Yit, BufferedImage.TYPE_INT_RGB);
            try {
                //Rows+1 por si el archivo ya tiene ocupado las 20 posiciones
                DimeMatrix = new Bubble[Rows+1][Columns];
                fr = new FileReader(file);
                input = new BufferedReader(fr);
                //System.out.println("This is Yi" + Yit);
                
                g2d = TempBI.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//antialiasing
                Bubble.resetBubbles();
                switch (scene) {
                    case 1:
                        startScene1();
                        break;
                    case 2:
                        startScene2();
                        break;
                    default:
                        startScene3();
                }
            } catch (FileNotFoundException fileNotFoundException) {
                System.err.println("Error opening file.");
                fileNotFoundException.printStackTrace();
                //System.exit( 1 );
            } catch (NoSuchElementException elementException) {
                System.err.println("No hay archivo");
                elementException.printStackTrace();
            } catch (IllegalStateException stateException) {
                System.err.println("Error de lectura");
                stateException.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error IOException");
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (fr != null) {
                        fr.close();
                    }
                } catch (IOException e) {
                    System.out.println("No se pudo cerrar el archivo");
                    e.printStackTrace();
                }
                matrix = TempBI;
                ballCount = Bubble.getBubbleCount();
                g2d.dispose();
                repaint();
            }
    }

    /**
     * Lee el archivo de texto y crea la matriz
     * inmediatamente llama al metodo redibujar()
     *
     **/
    /**
     * Crea la matriz para el escenario uno
     **/
    private void startScene1() throws NoSuchElementException,
            NoSuchElementException,
            IllegalStateException,
            IOException {
        String fila = input.readLine().toUpperCase();
        for (int i = 0; i < Rows; i++) {//antes comparaba !fila.equals("0,0,0")
            for (int j = 0; j < Columns; j++) {
                DimeMatrix[i][j] = new Bubble(fila.charAt(2 * j));
                if (fila.charAt(2 * j) != '0') {
                    Bubble.addBubble();
                    g2d.drawImage(DimeMatrix[i][j],
                            Bubble.DimeSize * j,
                            Bubble.DimeSize * i,
                            null);
                }
            }
            fila = input.readLine().toUpperCase();
            /**
             * Lo siguiente es para manipular solo la matriz
             * para conseguir las ubicaciones de las casillas
             * vacias y guardar esos puntos en superficie[0][columna]
             * 1ro, verefica si llego al penultima linea
             * 2do, ciclo q repasa a las columnas
             * 3ro, repasa desde la posicion de fila del Archivo
             * y no la posicion 19
             **/
            if (fila.equals("0,0,0,0,0,0,0,0,0,0")) {
                for (int k = 0; k < Columns; k++) {
                    int L = i;
                    while (L >= 0 && DimeMatrix[L][k].getBubbleColor() == null) {
                        L--;
                    }
                    surface[0][k] = L + 1;//Este es el espacio disponible
                }
                maximumSurface = i + 1;//posicion desocupada maxima
                break;//que se salga del primer ciclo For
            }

        }
    }

    /**
     * Crea la matriz para el escenario dos
     *
     **/
    private void startScene2() throws NoSuchElementException,
            NoSuchElementException,
            IllegalStateException,
            IOException {
        String fila = input.readLine().toUpperCase();
        for (int i = 0; i < Rows; i++) {
            for (int j = 0; j < Columns; j++) {
                DimeMatrix[i][j] = new Bubble(fila.charAt(2 * j));
                if (fila.charAt(2 * j) != '0') {
                    Bubble.addBubble();
                    g2d.drawImage(DimeMatrix[i][j],
                            Bubble.DimeSize * (Columns - j - 1),
                            Bubble.DimeSize * (Yit/25 - i - 1),
                            null);
                }
            }
            fila = input.readLine().toUpperCase();
            /**
             * Lo siguiente es para manipular solo la matriz
             * para conseguir las ubicaciones de las casillas
             * vacias y guardar esos puntos en superficieSup
             * 1ro, verefica si llego al penultima linea
             * 2do, ciclo q repasa a las columnas
             * 3ro, repasa desde la posicion de fila del Archivo
             * y no la posicion 19
             **/
            if (fila.equals("0,0,0,0,0,0,0,0,0,0")) {
                for (int k = 0; k < Columns; k++) {
                    int L = i;
                    while (L >= 0 && DimeMatrix[L][k].getBubbleColor() == null) {
                        L--;
                    }
                    surface[1][k] = L + 1;//Este es el espacio disponible
                }
                maximumSurface = i + 1;//posicion desocupada maxima
                break;//Se sale del primer ciclo FOR
            }
        }
    }

    /**
     * Crea la matriz para el escenario 3
     **/
    private void startScene3() throws NoSuchElementException,
            NoSuchElementException,
            IllegalStateException,
            IOException {
        String fila = input.readLine().toUpperCase();
        for (int i = 0; !fila.equals("0,0,0,0,0,0,0,0,0,0") && i < Rows / 2; i++) {
            for (int j = 0; j < Columns; j++) {
                DimeMatrix[i][j] = new Bubble(fila.charAt(2 * j));
                if (fila.charAt(2 * j) != '0') {
                    Bubble.addBubble();
                    g2d.drawImage(DimeMatrix[i][j],
                            Bubble.DimeSize * (j),
                            Bubble.DimeSize * (i),
                            null);
                }
            }
            fila = input.readLine().toUpperCase();
            /**
             * Lo siguiente es para manipular solo la matriz
             * para conseguir las ubicaciones de las casillas
             * vacias y guardar esos puntos en superficieSup
             * 1ro, verefica si llego al penultima linea
             * 2do, ciclo q repasa a las columnas
             * 3ro, repasa desde la posicion de fila del Archivo
             * y no la posicion 19
             **/
            if (fila.equals("0,0,0,0,0,0,0,0,0,0")) {
                for (int k = 0; k < Columns; k++) {
                    int L = i;
                    while (L >= 0 && DimeMatrix[L][k].getBubbleColor() == null) {
                        L--;
                    }
                    surface[0][k] = L + 1;//Este es el espacio disponible
                }
                maximumSurface = i + 1;//posicion desocupada maxima
                //System.out.println("valor de i up: "+i+" con max: "+max);
                //break;
                /**
                 * Es necesario que continue con el ciclo para leer
                 * adecuadamente el archivo
                 **/
            }

        }

        fila = input.readLine().toUpperCase();
        for (int i = 10; i < Rows; i++) {
            for (int j = 0; j < Columns; j++) {
                DimeMatrix[i][j] = new Bubble(fila.charAt(2 * j));
                if (fila.charAt(2 * j) != '0') {
                    Bubble.addBubble();
                    g2d.drawImage(DimeMatrix[i][j],
                    Bubble.DimeSize * (Columns - j - 1),
                    Yit-Bubble.DimeSize * (i + 1-10),
                    null);
                }

            }
            fila = input.readLine().toUpperCase();
            /**
             * Lo siguiente es para manipular solo la matriz
             * para conseguir las ubicaciones de las casillas
             * vacias y guardar esos puntos en superficieSup
             * 1ro, verefica si llego al penultima linea
             * 2do, ciclo q repasa a las columnas
             * 3ro, repasa desde la posicion de fila del Archivo
             * y no la posicion 19
             **/
            if (fila.equals("0,0,0,0,0,0,0,0,0,0")) {
                for (int k = 0; k < Columns; k++) {//k itera por columnas
                    int L = i;
                    while (L >= 0 && DimeMatrix[L][k].getBubbleColor() == null) {
                        L--;
                    }
                    surface[1][k] = L + 1;//Este es el espacio disponible
                }
                //System.out.println("valor de down i: "+i);
                //System.out.println("comparando i+1-10: "+(i+1-10)+" con max actual: "+max);
                if (i - 9 > maximumSurface) {//si para la matriz inferior tiene mas fichas en una fila, i+1-10
                    maximumSurface = i - 9;//era i+1-10
                }                //System.out.println("ultimo max: "+max);
                break;//sale del segundo FOR
            }
        }
    }

    /**
     *
     **/
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2dlabel = (Graphics2D)g;
        g2dlabel.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//antialiasing
        g2dlabel.setColor(Color.RED);
        g2dlabel.fill3DRect(0, 0, getWidth(), getHeight(), true);

        if (ValidFile) {
            //Calcula el factor de escala
            int w = matrix.getWidth();
            int h = matrix.getHeight();
            int side = Math.max(w, h);
            double scale = 200.0 / (double) side;
            w = (int) (scale * (double) w);
            h = (int) (scale * (double) h);
            g2dlabel.drawImage(matrix, 0, 0, w, h, this);
        } else {
            g2dlabel.setFont(font);
            g2dlabel.setColor(Color.BLACK);
            g2dlabel.drawString(mensaje, 21, 95);
        }
        g2dlabel.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}

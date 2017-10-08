/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Package1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Andres
 */
public class Frame1 extends javax.swing.JFrame {

    ArrayList<Node> nodos;
    ArrayList<Edge> arcos;
    int mat[][];
    String nodoinicial = null, nodofinal = null;
    int cont;
    int iniciox, inicioy, finx, finy, inx, iny, fnx, fny;

    /**
     * Creates new form Frame1
     */
    public Frame1() {
        cont = 0;
        nodos = new ArrayList<>();
        arcos = new ArrayList<>();
        setLocation(20, 10);
        setResizable(false);
        initComponents();
        Cargar();
        MatrizDeCostos();
    }

    private void Cargar() {
        String cadena;
        try (FileReader f = new FileReader("Files/Nodos.txt"); BufferedReader b = new BufferedReader(f)) {
            while ((cadena = b.readLine()) != null) {
                String[] vector = cadena.split(",");  //NombreDeNodo,posx,posy
                nodos.add(new Node((vector[0]), Integer.parseInt(vector[1]), Integer.parseInt(vector[2]), Color.BLACK));
            }
        } catch (IOException ex) {
            System.out.println("Hubo un problema con las configuraciones internas..");
            System.exit(0);
        }
        try (FileReader f = new FileReader("Files/Aristas.txt"); BufferedReader b = new BufferedReader(f)) {
            while ((cadena = b.readLine()) != null) {
                String[] vector = cadena.split(",");
                arcos.add(new Edge(Integer.parseInt(vector[0]), Integer.parseInt(vector[1]), Integer.parseInt(vector[2]),
                        Integer.parseInt(vector[3]), Integer.parseInt(vector[4]), Integer.parseInt(vector[5]), Integer.parseInt(vector[6])));
            }
        } catch (IOException ex) {
            System.out.println("Hubo un problema con las configuraciones internas..");
            System.exit(0);
        }
        mat = new int[nodos.size()][nodos.size()];
    }

    private void dibujar(Graphics g) {
        int conta = 1;
        g.setColor(Color.black);
        for (Node nodo : nodos) {
            //g.fillOval(nodo.posx-8, nodo.posy-8, 20, 20);
            g.drawString(Integer.toString(conta), nodo.posx - 11, nodo.posy - 10);
            conta++;
        }
        g.setColor(Color.DARK_GRAY);
        arcos.stream().forEach((arco) -> {
            g.drawLine(arco.x1, arco.y1, arco.x2, arco.y2);
        });
    }

    private void Choosingpoints(Graphics g) {
        PanelMap.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cont == 1) {
                    g.setColor(Color.red);
                    g.fillOval(e.getX() - 13, e.getY() - 13, 25, 25);
                    g.setColor(Color.white);
                    g.drawString("L", e.getX() - 4, e.getY() + 4);
                    finx = e.getX();
                    finy = e.getY();
                    cont++;
                    select.setEnabled(false);
                    Calcularnodoscerca(iniciox, inicioy, finx, finy, PanelMap.getGraphics());
                    //GettingStarted(PanelMap.getGraphics());
                    Dijkstra(PanelMap.getGraphics());
                }
                if (cont == 0) {
                    g.setColor(Color.red);
                    g.fillOval(e.getX() - 13, e.getY() - 13, 25, 25);
                    g.setColor(Color.white);
                    g.drawString("P", e.getX() - 4, e.getY() + 4);
                    iniciox = e.getX();
                    inicioy = e.getY();
                    cont++;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });
    }

    private void Calcularnodoscerca(int x1, int y1, int x2, int y2, Graphics g) {
        //Buscar el nodo que esté más cerca del punto inicial..
        int dist = 2000, x = 0, y = 0;
        double dist2;
        for (Node nodo : nodos) {
            dist2 = Math.sqrt(Math.pow(Math.abs(x1 - nodo.posx), 2) + Math.pow(Math.abs(y1 - nodo.posy), 2));
            if (dist2 < dist) {
                dist = (int) dist2;
                nodoinicial = nodo.name;
                x = nodo.posx;
                y = nodo.posy;
            }
        }
        g.setColor(Color.red);
        g.fillOval(x - 8, y - 8, 20, 20);
        inx = x;
        iny = y;

        //Buscar el nodo que esté más cerca del punto final...
        dist = 2000;
        x = 0;
        y = 0;
        for (Node nodo : nodos) {
            dist2 = Math.sqrt(Math.pow(Math.abs(x2 - nodo.posx), 2) + Math.pow(Math.abs(y2 - nodo.posy), 2));
            if (dist2 < dist) {
                dist = (int) dist2;
                nodofinal = nodo.name;
                x = nodo.posx;
                y = nodo.posy;
            }
        }
        g.setColor(Color.red);
        g.fillOval(x - 8, y - 8, 20, 20);
        fnx = x;
        fny = y;
    }

    private void MatrizDeCostos() {
        for (int i = 0; i < nodos.size(); i++) {
            for (int j = 0; j < nodos.size(); j++) {
                if (i == j) {
                    mat[i][j] = 0;
                } else {
                    mat[i][j] = 333;
                }
            }
        }

        for (int i = 0; i < arcos.size(); i++) {
            int ini = arcos.get(i).nodoinicial;
            int fin = arcos.get(i).nodofinal;
            int dis = arcos.get(i).dist;
            mat[ini - 1][fin - 1] = dis;
        }

//        for (int i=0; i<nodos.size();i++) {
//            System.out.print("Nodo "+(i+1)+": ");
//            for (int j=0; j<nodos.size();j++) {
//                System.out.print(mat[i][j]+"|");
//            }System.out.println();
//        }
    }

    private void GettingStarted(Graphics g) {
        ArrayList<Integer> camino = new ArrayList<>();
        int vec[] = {3000, 3000, 3000, 3000};
        int currentx[] = {0, 0, 0, 0};
        int currenty[] = {0, 0, 0, 0};
        int i;
        String begin = nodoinicial;
        camino.add(Integer.parseInt(begin));
        g.setColor(Color.red);
        g.fillOval(inx - 13, iny - 13, 25, 25);
        while (!begin.equals(nodofinal)) {
            i = 0;
            for (Edge arco : arcos) {
                if (Integer.toString(arco.nodoinicial).equals(begin)) {
                    //g.fillOval(arco.x2-13, arco.y2-13, 25, 25);
                    vec[i] = arco.nodofinal;
                    currentx[i] = arco.x2;
                    currenty[i] = arco.y2;
                    i++;
                }
            }
            //Los datos que hay en el vector diferentes de cero, son los nodos adyacentes
            //Elegir el que esté más cerca al nodofinal
            int choose = 3000, indexch = 0;
            for (int j = 0; j < vec.length; j++) {
                //Recorrer el vector y ver cual tiene una distancia menor al punto final..
                if (vec[j] != 3000) {
                    //Ahora mi inicio es current y mi fin es nodofinal;
                    int dis = (int) Math.sqrt(Math.pow(Math.abs(currentx[j] - fnx), 2) + Math.pow(Math.abs(currenty[j] - fny), 2));
                    if (dis < choose) {
                        indexch = j;
                        choose = dis;
                    }
                }
            }
            //g.fillOval(currentx[indexch]-13, currenty[indexch]-13, 25, 25);
            camino.add(vec[indexch]);
            begin = "" + vec[indexch];
        }

        //Buscar los nodos que estan en el arraylist en lo de aristas, y pintar las lineas, perdon x las tildes y eso..
        i = 0;
        while (i < camino.size() - 1) {
            int datoini = camino.get(i);
            int datofin = camino.get(i + 1);
            for (Edge arco : arcos) {
                if (arco.nodoinicial == datoini && arco.nodofinal == datofin) {
                    WidthLine(PanelMap.getGraphics(), arco.x1, arco.y1, arco.x2, arco.y2);
                    break;
                }
            }
            i++;
        }
    }

    

    private void Dijkstra(Graphics g) {
        
        //Mat es mi matriz de costos de todos los nodos (ya está hecha arriba!)..
        
        int nodoactual = Integer.parseInt(nodoinicial); //Nodo donde inicia el recorrido y que irá variando..
        String path[][] = new String[nodos.size()][nodos.size()];
        for (int i = 0; i < nodos.size(); i++) {
            for (int j = 0; j < nodos.size(); j++) {
                path[i][j] = String.valueOf(nodoactual)+"_50000";
            }
        }//Lleno mi matriz de distancias con el nodo actual y un número "infinito"..

        boolean nodosdisponibles[] = new boolean[nodos.size()]; //Los nodos por los que voy pasando y ya no pasaré más..
        //Están en False todos los nodos por defecto, cuando visito alguno, cambio a True..
        nodosdisponibles[nodoactual - 1] = true;//iniciar el punto inicial como ya visitado..
        int p = 0;
        int suma=0;
        while(p<nodos.size()){
            //Recorro la lista de aristas para ver las que inician en nodoactual, luego agrego los nodos finles de esa..
            for (int i = 0; i<arcos.size(); i++) {
                if(i==(nodoactual-1)){
                    if(i==0){
                        path[p][4]=String.valueOf(197);
                    }
                    continue;   //No tengo nada que hacer en la columna del nodo actual porque ya sé su distancia..
                }
                if(arcos.get(i).nodoinicial==nodoactual){
                    //Mirar que el nodofinal de este, no haya sido selecionado aún..
                    if(!nodosdisponibles[arcos.get(i).nodofinal-1]){
                        String aux1=path[p][arcos.get(i).nodofinal-1];
                        String aux[]=aux1.split("_");
                        if((arcos.get(i).dist+suma)<Integer.parseInt(aux[1])){
                            //Asignar la suma más la distancia de esa arista..
                            String val=String.valueOf(suma+arcos.get(i).dist);
                            path[p][arcos.get(i).nodofinal-1]=String.valueOf(nodoactual)+"_"+val;
                        }
                    }
                }
            }
            //Ya está llena toda esa fila, busca el menor o igual ahora..
            int min=50000;
            int index=0;
            for (int i = 0; i < nodos.size(); i++) {
                if(!nodosdisponibles[i]){//O sea si el nodo no ha sido seleccionado..
                    String aux1=path[p][i];
                    String aux[]=aux1.split("_");
                    if(Integer.parseInt(aux[1])<=min){
                        min=Integer.parseInt(aux[1]);
                        index = i;
                    }
                }
            }   //Así encuentro el menor de la columna..
            nodosdisponibles[index]=true;
            nodoactual=index+1;
            suma=min;
            p++;
            if(p<path.length){  //Lleno la fila que viene con la anterior..
                System.arraycopy(path[p-1], 0, path[p], 0, path.length);
            }
        }
        //Ya aquí la matriz de recorridos está lista..
        //Puedo obtener mi camino..
        String finalpath=nodofinal+";";
        String aux=path[p-1][Integer.parseInt(nodofinal)-1];
        int var=0;
        while(var!=Integer.parseInt(nodoinicial)){
            String help[]=aux.split("_");
            var=Integer.parseInt(help[0]);
            if(var!=Integer.parseInt(nodoinicial)){
                finalpath+=var+";";
                aux=path[p-1][var-1];
            }else{
                finalpath+=var;
            }
        }
        String help[]=finalpath.split(";");
        int i = 0;
        while(i<help.length-1){
            for (Edge arco : arcos) {
                if(arco.nodofinal==Integer.parseInt(help[i]) && arco.nodoinicial==Integer.parseInt(help[i+1])){
                    WidthLine(PanelMap.getGraphics(), arco.x1, arco.y1, arco.x2, arco.y2);
                }
            }
            i++;
        }
    }
    
    private void WidthLine(Graphics g, int x1, int y1, int x2, int y2) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(8));
        g2.draw(new Line2D.Float(x1, y1, x2, y2));
    }
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelMain = new javax.swing.JPanel();
        PanelMap = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        PanelData = new javax.swing.JPanel();
        select = new javax.swing.JButton();
        PanelTitle = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(153, 153, 153));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/LabPicture.png"))); // NOI18N

        javax.swing.GroupLayout PanelMapLayout = new javax.swing.GroupLayout(PanelMap);
        PanelMap.setLayout(PanelMapLayout);
        PanelMapLayout.setHorizontalGroup(
            PanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PanelMapLayout.setVerticalGroup(
            PanelMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        select.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        select.setText("Seleccionar punto de partida y llegada");
        select.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelDataLayout = new javax.swing.GroupLayout(PanelData);
        PanelData.setLayout(PanelDataLayout);
        PanelDataLayout.setHorizontalGroup(
            PanelDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDataLayout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addComponent(select)
                .addGap(31, 31, 31))
        );
        PanelDataLayout.setVerticalGroup(
            PanelDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDataLayout.createSequentialGroup()
                .addContainerGap(57, Short.MAX_VALUE)
                .addComponent(select, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | java.awt.Font.BOLD, jLabel3.getFont().getSize()+22));
        jLabel3.setText("Tienda la Prosperidad");

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getStyle() | java.awt.Font.BOLD, jLabel2.getFont().getSize()+22));
        jLabel2.setText("Calculadora de rutas");

        javax.swing.GroupLayout PanelTitleLayout = new javax.swing.GroupLayout(PanelTitle);
        PanelTitle.setLayout(PanelTitleLayout);
        PanelTitleLayout.setHorizontalGroup(
            PanelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        PanelTitleLayout.setVerticalGroup(
            PanelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelTitleLayout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout PanelMainLayout = new javax.swing.GroupLayout(PanelMain);
        PanelMain.setLayout(PanelMainLayout);
        PanelMainLayout.setHorizontalGroup(
            PanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMainLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(PanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelMainLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(PanelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(130, Short.MAX_VALUE))
        );
        PanelMainLayout.setVerticalGroup(
            PanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(PanelData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PanelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addComponent(PanelMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectActionPerformed
        dibujar(PanelMap.getGraphics());
        Choosingpoints(PanelMap.getGraphics());
    }//GEN-LAST:event_selectActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frame1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelData;
    private javax.swing.JPanel PanelMain;
    public javax.swing.JPanel PanelMap;
    private javax.swing.JPanel PanelTitle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton select;
    // End of variables declaration//GEN-END:variables

}

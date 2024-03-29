package cliente.Chats;


import cliente.Chat;
import cliente.Conexion;
import cliente.entidades.Usuario;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Chats3 extends javax.swing.JFrame{

    Conexion net;
    DefaultListModel<String> mlu;

    public Chats3(){
        net = Conexion.getInstance();
        net.setServer(leerIP(), 1234);
        net.setInterfaz(new Chat());
        net.enviar("NICK " + leerNick());
        mlu = new DefaultListModel<>();
        initComponents();
        setComponentsExtras();
        new Thread(new Runnable(){
            @Override
            public void run(){
                net.escucharServidor();
            }
        }).start();
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                net.enviar("EXIT");
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fieldMsg = new javax.swing.JTextField();
        btEnviar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        areaMensajes = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fieldMsg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                fieldMsgKeyPressed(evt);
            }
        });

        btEnviar.setText("Enviar");
        btEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEnviarActionPerformed(evt);
            }
        });

        areaMensajes.setEditable(false);
        areaMensajes.setColumns(20);
        areaMensajes.setLineWrap(true);
        areaMensajes.setRows(5);
        areaMensajes.setToolTipText("");
        areaMensajes.setWrapStyleWord(true);
        jScrollPane2.setViewportView(areaMensajes);

        jList1.setModel(mlu);
        jList1.setFixedCellHeight(20);
        jScrollPane3.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(fieldMsg)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                                        .addComponent(btEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                                        .addComponent(jScrollPane3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(fieldMsg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btEnviar))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEnviarActionPerformed
        net.enviar(fieldMsg.getText());
        fieldMsg.setText("");
    }//GEN-LAST:event_btEnviarActionPerformed

    private void fieldMsgKeyPressed(KeyEvent evt) {//GEN-FIRST:event_fieldMsgKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            btEnviarActionPerformed(null);
        }
    }//GEN-LAST:event_fieldMsgKeyPressed

    public static void main(String args[]){
        try{
            for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
                if("Nimbus".equals(info.getName())){
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex){
            java.util.logging.Logger.getLogger(Chat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                new Chat().setVisible(true);
            }
        });
    }

    // Variables declaracion
    private javax.swing.JTextArea areaMensajes;
    private javax.swing.JButton btEnviar;
    private javax.swing.JTextField fieldMsg;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables

    public void agregarUsuario(Usuario u){
        mlu.addElement(u.getNick());
    }

    public void agregarMensaje(String s){
        areaMensajes.append(s + "\n");
    }

    public void limpiarListado(){
        mlu.clear();
    }

    private void setComponentsExtras(){
        DefaultCaret caret = (DefaultCaret)areaMensajes.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        jList1.setFixedCellHeight(20);
        setLocationRelativeTo(null);
        fieldMsg.requestFocus();
    }

    private String leerIP(){
        return JOptionPane.showInputDialog(null, "Introduce la IP del servidor", "127.0.0.1");
    }

    private String leerNick(){
        return JOptionPane.showInputDialog(null, "Introduce tu nombre de usuario", "Usuario");
    }
}
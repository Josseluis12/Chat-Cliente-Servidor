package cliente.Chats;


import cliente.Chat;
import cliente.Conexion;
import cliente.entidades.Usuario;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Chats1 extends JFrame{

    Conexion net;
    DefaultListModel<String> mlu;

    public Chats1(){
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

        fieldMsg = new JTextField();
        btEnviar = new JButton();
        jScrollPane2 = new JScrollPane();
        areaMensajes = new JTextArea();
        jScrollPane3 = new JScrollPane();
        jList1 = new JList();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(fieldMsg)
                                        .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                                        .addComponent(btEnviar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                                        .addComponent(jScrollPane3))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(fieldMsg, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
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
            for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
                if("Nimbus".equals(info.getName())){
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex){
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
    private JTextArea areaMensajes;
    private JButton btEnviar;
    private JTextField fieldMsg;
    private JList jList1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
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
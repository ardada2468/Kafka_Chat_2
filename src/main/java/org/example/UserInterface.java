package org.example;

import org.example.KUtils.KConsumer;
import org.example.KUtils.KProducer;
import org.example.Messeging.Message;
import org.example.Messeging.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserInterface extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField usernameField;
    User user = new User("Anonymous");
    KProducer producer = new KProducer();
    KConsumer consumer = new KConsumer(this.user.getName());

    public void appendToChat(String s){
        chatArea.append(s);
    }

    public UserInterface() {
        setTitle("Chat App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        usernameField = new JTextField();
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setUserName();
            }
        });

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(usernameField, BorderLayout.NORTH);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String username = usernameField.getText();
        String message = messageField.getText();
        if (!username.isEmpty() && !message.isEmpty()) {
            Message m = new Message(message, username);
            producer.send(m);
            messageField.setText("");
        }
    }

    private void setUserName() {
        String username = usernameField.getText();
        if (!username.isEmpty()) {
            user.setName(username);
            chatArea.append("Username updated to: " + user.getName());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UserInterface UI = new UserInterface();

                // Separate thread for running the consumer
                Thread consumerThread = new Thread() {
                    @Override
                    public void run() {
                        UI.consumer.run(UI);
                    }
                };
                consumerThread.start();
                UI.setVisible(true);
            }

        });
    }
}


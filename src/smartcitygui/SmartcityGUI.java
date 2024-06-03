package smartcitygui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SmartcityGUI {
    private JFrame frame;
    private JPanel panelMain, panelAdmin, panelUser;
    private CardLayout cardLayout;
    private List<Citizen> citizens;
    private AdminService adminService;
    private UserService userService;

    public SmartcityGUI() {
        citizens = new ArrayList<>();
        adminService = new AdminService(citizens);
        userService = new UserService(citizens);

        frame = new JFrame("Smartcity Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        cardLayout = new CardLayout();
        panelMain = new JPanel(cardLayout);

        createMainPanel();
        createAdminPanel();
        createUserPanel();

        frame.add(panelMain);
        frame.setVisible(true);
    }

    private void createMainPanel() {
        JPanel panel = new JPanel();
        JButton adminButton = new JButton("Admin");
        JButton userButton = new JButton("User");

        adminButton.addActionListener(e -> cardLayout.show(panelMain, "Admin"));
        userButton.addActionListener(e -> cardLayout.show(panelMain, "User"));

        panel.add(adminButton);
        panel.add(userButton);

        panelMain.add(panel, "Main");
    }

    private void createAdminPanel() {
        panelAdmin = new JPanel(new GridLayout(0, 1));
        JButton registerCitizenButton = new JButton("Register Citizen");
        JButton viewFeedbackButton = new JButton("Review Feedback");
        JButton sendNotificationButton = new JButton("Send Notification");
        JButton backButton = new JButton("Back");

        registerCitizenButton.addActionListener(e -> showRegisterCitizenDialog());
        viewFeedbackButton.addActionListener(e -> reviewFeedback());
        sendNotificationButton.addActionListener(e -> showSendNotificationDialog());
        backButton.addActionListener(e -> cardLayout.show(panelMain, "Main"));

        panelAdmin.add(registerCitizenButton);
        panelAdmin.add(viewFeedbackButton);
        panelAdmin.add(sendNotificationButton);
        panelAdmin.add(backButton);

        panelMain.add(panelAdmin, "Admin");
    }

    private void createUserPanel() {
        panelUser = new JPanel(new GridLayout(0, 1));
        JButton viewInfoButton = new JButton("View Citizen Information");
        JButton provideFeedbackButton = new JButton("Provide Feedback");
        JButton checkNotificationsButton = new JButton("Check Notifications");
        JButton backButton = new JButton("Back");

        viewInfoButton.addActionListener(e -> showViewCitizenDialog());
        provideFeedbackButton.addActionListener(e -> showProvideFeedbackDialog());
        checkNotificationsButton.addActionListener(e -> showCheckNotificationsDialog());
        backButton.addActionListener(e -> cardLayout.show(panelMain, "Main"));

        panelUser.add(viewInfoButton);
        panelUser.add(provideFeedbackButton);
        panelUser.add(checkNotificationsButton);
        panelUser.add(backButton);

        panelMain.add(panelUser, "User");
    }

    private void showRegisterCitizenDialog() {
        JDialog dialog = createCitizenDialog("Register Citizen", (citizen) -> {
            adminService.registerCitizen(citizen);
            JOptionPane.showMessageDialog(frame, "Citizen registered successfully.");
        });
        dialog.setVisible(true);
    }

    private void showViewCitizenDialog() {
        String id = JOptionPane.showInputDialog(frame, "Enter Citizen ID:");
        if (id == null || id.isEmpty()) return;

        Citizen citizen = userService.viewCitizenInformation(id);
        if (citizen != null) {
            JOptionPane.showMessageDialog(frame, "Citizen Information:\n" + citizen);
        } else {
            JOptionPane.showMessageDialog(frame, "Citizen not found.");
        }
    }

    private void showProvideFeedbackDialog() {
        String id = JOptionPane.showInputDialog(frame, "Enter Citizen ID:");
        if (id == null || id.isEmpty()) return;

        String feedback = JOptionPane.showInputDialog(frame, "Enter feedback:");
        if (feedback == null || feedback.isEmpty()) return;

        userService.provideFeedback(id, feedback);
    }

    private void showSendNotificationDialog() {
        String message = JOptionPane.showInputDialog(frame, "Enter notification message:");
        if (message == null || message.isEmpty()) return;

        adminService.sendNotification(message);
    }

    private void showCheckNotificationsDialog() {
        String id = JOptionPane.showInputDialog(frame, "Enter Citizen ID:");
        if (id == null || id.isEmpty()) return;

        Citizen citizen = userService.viewCitizenInformation(id);
        if (citizen != null) {
            JOptionPane.showMessageDialog(frame, "Notifications:\n" + String.join("\n", citizen.getNotifications()));
        } else {
            JOptionPane.showMessageDialog(frame, "Citizen not found.");
        }
    }

    private void reviewFeedback() {
        StringBuilder feedbacks = new StringBuilder();
        for (Citizen citizen : citizens) {
            if (!citizen.getFeedbacks().isEmpty()) {
                feedbacks.append("Feedback from Citizen ID: ").append(citizen.getId()).append("\n");
                for (String feedback : citizen.getFeedbacks()) {
                    feedbacks.append("Feedback: ").append(feedback).append("\n");
                }
            }
        }
        JOptionPane.showMessageDialog(frame, feedbacks.length() > 0 ? feedbacks.toString() : "No feedback available.");
    }

    private JDialog createCitizenDialog(String title, CitizenCallback callback) {
        JDialog dialog = new JDialog(frame, title, true);
        dialog.setLayout(new GridLayout(0, 2));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField employmentStatusField = new JTextField();
        JTextField taxRecordsField = new JTextField();
        JTextField governmentProgramsField = new JTextField();

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Employment Status:"));
        dialog.add(employmentStatusField);
        dialog.add(new JLabel("Tax Records:"));
        dialog.add(taxRecordsField);
        dialog.add(new JLabel("Government Programs:"));
        dialog.add(governmentProgramsField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            try {
                Citizen citizen = new Citizen(idField.getText(), nameField.getText(), addressField.getText(), emailField.getText(), phoneField.getText(), employmentStatusField.getText(), taxRecordsField.getText(), governmentProgramsField.getText());
                callback.onCitizenCreated(citizen);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel());
        dialog.add(submitButton);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        return dialog;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartcityGUI::new);
    }

    @FunctionalInterface
    private interface CitizenCallback {
        void onCitizenCreated(Citizen citizen);
    }
}

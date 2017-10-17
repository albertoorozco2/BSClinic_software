import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;


public class Login extends JFrame implements ActionListener {

    //private static final Object BorderLayout = null;
    JTextField username = null;
    JTextField password = null;
    JFrame frame = new JFrame();
    JFrame locked = new JFrame();

    Integer tries = 0;

    String un = "recep";
    String pw = "recep";

    public Login() {
        //billFrame billFrame = new billFrame();
        //recepFrame recepFrame = new recepFrame();
    	//doctorFrame doctorFrame = new doctorFrame();
       Timer();

    }


    public void Timer() {
        if (tries >= 3) {
        	locked.setVisible(true);
            JOptionPane.showMessageDialog(null, "    You got 3 incorrect login attempts.\nBS Clinic Software will be locked for 2 minutes");
            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            tries = 0;
        	locked.setVisible(false);
            LoginWindow();
        } else {
        	locked.setVisible(false);
            LoginWindow();

        }



    }

    public void LoginWindow() {
    	
        frame.setVisible(true);
        frame.setTitle("BS Clinic Software");  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 800);
        frame.setMinimumSize(new Dimension(400, 780));
        
        locked.setTitle("BS Clinic Software");  
        locked.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        locked.setSize(900, 800);
        locked.setMinimumSize(new Dimension(400, 780));
        locked.add(new JLabel("               You got 3 incorrect login attempts.\nBS Clinic Software will be locked for 2 minutes"));

        JPanel panelMain = new JPanel(new GridLayout(4, 1));
        getContentPane().add(panelMain);
        JPanel panelform = new JPanel(new GridBagLayout());


        JPanel panelem = new JPanel(new GridBagLayout());
        getContentPane().add(panelem);
        panelem.setSize(500, 0);
        JPanel panelem2 = new JPanel(new GridBagLayout());
        getContentPane().add(panelem2);
        panelem.setSize(500, 0);

        JPanel panelicon = new JPanel(new GridBagLayout());
        getContentPane().add(panelicon);
        panelicon.setSize(500, 200);

        //JPanel panelform = new JPanel (new GridBagLayout());
        //panelMain.add(panelform);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;

        JLabel bs = new JLabel(new ImageIcon(getClass().getResource("bs.png")));
        //InputStream input = classLoader.getResourceAsStream("bs.jpg");
        //bs.setIcon(new ImageIcon("bs.png"));

        JLabel lun = new JLabel("Username");

        username = new JTextField(20);
        JLabel lpass = new JLabel("Password");

        password = new JPasswordField(20);


        JButton login = new JButton("Login!");


        login.addActionListener(this);
        login.setActionCommand("login");

        panelicon.add(bs);
        validate();
        panelform.add(lun, c);
        c.gridy++;
        panelform.add(lpass, c);
        c.gridy++;

        c.gridx = 1;
        c.gridy = 0;
        panelform.add(username, c);
        c.gridy++;
        panelform.add(password, c);
        c.gridy++;
        panelform.add(login, c);
        c.gridy++;
        panelMain.add(panelem);
        panelMain.add(panelicon);
        panelMain.add(panelform);
        panelMain.add(panelem2);
        frame.add(panelMain);
        frame.setVisible(true);
		validate();
		repaint();





    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new Login();
    }

    public void LogTry() {
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();

        } catch (Exception e) {}


        Connection conn = null;
        Statement stmt = null;
        //ResultSet rs = null;
        try {
            conn =
                DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");

            // Do something with the Connection
            stmt = conn.createStatement();

            // or alternatively, if you don't know ahead of time that
            // the query will be a SELECT...

            String un = username.getText();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (stmt.execute("INSERT INTO `BS`.`Log` (`LTime`, `LUsername`) VALUES ('" + timestamp + "', '" + un + "');")) {

            }

            // loop over results

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }



    }


    public void loginWithDatabase() {



        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();

        } catch (Exception e) {}


        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn =
                DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");

            // Do something with the Connection
            stmt = conn.createStatement();

            // or alternatively, if you don't know ahead of time that
            // the query will be a SELECT...

           String un = username.getText();
           String pw = password.getText();

          //  String un = "recep";
          //  String pw = "recep";
            
            if (stmt.execute("select UType from BS.user where UUsername = '" + un + "' and UPass = '" + pw + "'")) {
                rs = stmt.getResultSet();
            }

            // loop over results
            if (rs.next()) {
                //   while(rs.next()){

                String type = rs.getString("UType");


                if (type.equals("recep")) {

                    frame.setVisible(false);
                    recepFrame recepFrame = new recepFrame();
                    JOptionPane.showMessageDialog(null, "Welcome to Receptionist Dashboard");
                } else if (type.equals("bill")) {
                    frame.setVisible(false);
                    billFrame billFrame = new billFrame();
                    JOptionPane.showMessageDialog(null, "Welcome to Billing Dashboard");
                } else if (type.equals("doctor")) {
                    frame.setVisible(false);
                    doctorFrame doctorFrame = new doctorFrame();
                    JOptionPane.showMessageDialog(null, "Welcome to Doctor Dashboard");
                }
            } else {
                frame.setVisible(false);
                tries++;
                JOptionPane.showMessageDialog(null, "Wrong Credentials Please Try Again");
                Timer();

            }


            //  } 


        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }





    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // if you want to set a label for each of the buttons
        // and then redirect the user to a different part of the program
        // you can use the getActionCommand to check which button
        // has sent the request
        if (e.getActionCommand().equals("login")) {
            LogTry();
            loginWithDatabase();
            //JOptionPane.showMessageDialog(null, "My Goodness, this is so concise");    	        	

        } else if (e.getActionCommand().equals("login")) {

        }





    }

}
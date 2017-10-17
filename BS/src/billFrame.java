import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class billFrame extends JFrame implements ActionListener {



    String[] patients = new String[50]; //list of patients from Sql
    JList list; //jlist for patients
    String[] services = new String[9]; //list of services from sql
    String bills; //list of bills from sql
    JList listbills; //jlist for bills
    int patselid = 1; //id of patient selected
    String patsel = ""; //name of patiente selected
    int billsel = 0; //name of patiente selected
    String patseldetails = ""; //details of the patient selected
    int patseluntot;
    JPanel billpanel = new JPanel();

    JScrollPane scroll2 = new JScrollPane(billpanel);
    //billpanel.setBorder(BorderFactory.createLineBorder(Color.black));


    public billFrame() {
        billDash();
    }

    public void billDash() {
        // Main Frame for Billing deparment
        billpanel.setLayout(new BoxLayout(billpanel, BoxLayout.Y_AXIS));

        JFrame bill = new JFrame();
        bill.setVisible(true);
        bill.setTitle("BS Clinic Software - Billing Dashboard");
        bill.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bill.setSize(1000, 800);
        bill.setMinimumSize(new Dimension(1000, 800));
        bill.setLayout(new GridLayout(1, 1));

        // Main panel cotains all the elemts
        JPanel panelMain = new JPanel();
        panelMain.setSize(1000, 800);
        panelMain.setLayout(new BorderLayout());

        // panel header contains header title, icon, clock, and log out button.
        JPanel panelHeader = new JPanel(new GridLayout(1, 3));
		JLabel bs = new JLabel(new ImageIcon(getClass().getResource("icon.png")));
        //bs.setIcon(new ImageIcon("icon.png"));
        bs.setHorizontalAlignment(JLabel.CENTER);
        panelHeader.add(bs);
        JLabel dash = new JLabel("BILLING DASHBOARD");
        dash.setFont(new Font("Helvetica Neue", Font.PLAIN, 25));
        panelHeader.add(dash);
        JPanel rightdash = new JPanel(new GridLayout(2, 1));
        rightdash.add(new ClockPanel());
        JButton logout = new JButton("LOGOUT");
        logout.addActionListener(this);
        logout.setActionCommand("logout");

        rightdash.add(logout);
        panelHeader.add(rightdash);

        // panel body contais the list and the actions panels
        JPanel panelBody = new JPanel(new GridLayout(1, 2));

        // panel actions just contais the list of patients
        JPanel panelList = new JPanel(new GridLayout(1, 2));

        // method to populate the array of patients and list of
        ListPatients();
        list = new JList(patients);


        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Arial", Font.PLAIN, 18));
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                patsel = (String) list.getSelectedValue();
                patientselect();
                totalunpaid();
                billpanel.removeAll();;
                ListBills();
                bill.dispose();
                billDash();


            }
        });
        JLabel listpat = new JLabel("List of Patients");
        listpat.setHorizontalAlignment(JLabel.CENTER);

        panelList.add(listpat);
        JScrollPane scroll1 = new JScrollPane(list);
        panelList.add(scroll1);

        // panel of activities for bill
        JPanel panelActs = new JPanel(new GridLayout(3, 1));

        JLabel ps = new JLabel(
            "<html>Patient ID:" + patselid + "<br>  Patient Name: <b>" + patsel + "</b><br>" + " Address: " + patseldetails + "<br>TOTAL UNPAID: <b><u>€" + patseluntot + ".00</u></b> </html>",
            SwingConstants.CENTER);
        ps.setFont(new Font("Helvetica", Font.PLAIN, 20));
        JButton jl2 = new JButton("BILL LETTER UP TO DATE");
        jl2.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(jl2);
        jl2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                if (patsel == "") {
                    JOptionPane.showMessageDialog(null, "Please! chose a patient.");

                } else {
                    // TODO Auto-generated method stub
                    JFrame bill = new JFrame();
                    bill.setTitle("Bill Letter");
                    bill.setSize(550, 500);
                    bill.setVisible(true);


                    JLabel billco = new JLabel("<html>" + patsel + "<br>" + patseldetails + "<br><br>Dear " + patsel + "This letter is a reminder that the balance on your account <br>in the amount of " + patseluntot + "EURO is due now.<br><br> " + bills + "<br>We accept MasterCard, VISA and Discover.<br><br>If your payment is already on its way, <br>we thank you and ask that you please disregard this notice. <br>If not, we would appreciate receipt of your payment as soon as possible.<br><br> If you would like to further discuss the details of your account, <br>please do not hesitate to call patient billing at (087)444-1937.<br><br><br>Kind Regards<br><br>BS Clinic<br>Billing Department");
                    JScrollPane scroll3 = new JScrollPane(billco);

                    bill.add(scroll3);
                }
            }
        });

        JPanel cont = new JPanel();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        panelActs.add(ps);

        JLabel billalbel = new JLabel("Generate a letter for the client with their bills up to date");
        billalbel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cont.add(billalbel);

        cont.add(jl2);

        panelActs.add(cont);
        panelActs.add(scroll2);

        panelBody.add(panelList, BorderLayout.CENTER);
        panelBody.add(panelActs);
        panelMain.add(panelHeader, BorderLayout.PAGE_START);
        panelMain.add(panelBody);
        bill.add(panelMain);

        validate();
        repaint();

    }


    public void ListPatients() {

        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();

        } catch (Exception e) {}

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
            stmt = conn.createStatement();

            if (stmt.execute("select PName from BS.Patient where PStatus='Active'  ORDER BY PName")) {
                rs = stmt.getResultSet();
            }

            // loop over results
            int i = 0;
            while (rs.next()) {

                patients[i] = rs.getString("PName");
                i++;

            }

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

    }
    public void ListBills() {

        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();

        } catch (Exception e) {}

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
            stmt = conn.createStatement();

            if (stmt.execute("select * from BS.Bill WHERE PID = " + patselid + "")) {
                rs = stmt.getResultSet();
            }

            // loop over results
            bills = "";
            int i = 0;
            while (rs.next()) {

                bills = bills + "Bill ID " + rs.getString("BID") + " Date " + rs.getString("BTime") + " Total €" + rs.getString("BTotal") + " " + rs.getString("BStatus") + "<br>";
                int BID = rs.getInt("BID");
                String BTime = rs.getString("BTime");
                String BServices = rs.getString("BServices");
                int BTotal = rs.getInt("BTotal");
                String BStatus = rs.getString("BStatus");
                JLabel jl = new JLabel("Bill ID: " + String.valueOf(BID) + " " + BStatus);
                JLabel jl0 = new JLabel(BTime + " €" + String.valueOf(BTotal));
                String blp;
                if (BStatus.equals("UNPAID")) {
                    blp = "PAY";
                } else {
                    blp = "-";

                }


                JButton jl1 = new JButton(blp);

                this.add(jl1);


                jl1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {


                        try {

                            Class.forName("com.mysql.jdbc.Driver").newInstance();

                        } catch (Exception e) {}

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
                            stmt = conn.createStatement();

                            if (stmt.execute("UPDATE BS.Bill SET BStatus ='PAID'  WHERE BID =" + BID + ";")) {}



                        } catch (SQLException ex) {
                            // handle any errors
                            System.out.println("SQLException: " + ex.getMessage());
                            System.out.println("SQLState: " + ex.getSQLState());
                            System.out.println("VendorError: " + ex.getErrorCode());
                        }


                        billpanel.setVisible(false);
                        billpanel.removeAll();
                        totalunpaid();
                        ListBills();
                        billpanel.setVisible(true);
                        // TODO Auto-generated method stub
                        JOptionPane.showMessageDialog(null, "Status changed to PAID");
                    }
                });
                String bld= "DELETE";
                
                JButton jl2 = new JButton(bld);

                this.add(jl2);


                jl2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {


                        try {

                            Class.forName("com.mysql.jdbc.Driver").newInstance();

                        } catch (Exception e) {}

                        Connection conn = null;
                        Statement stmt = null;
                        try {
                            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
                            stmt = conn.createStatement();

                            if (stmt.execute("DELETE FROM BS.Bill WHERE BID="+BID+";")) {}



                        } catch (SQLException ex) {
                            // handle any errors
                            System.out.println("SQLException: " + ex.getMessage());
                            System.out.println("SQLState: " + ex.getSQLState());
                            System.out.println("VendorError: " + ex.getErrorCode());
                        }


                        billpanel.setVisible(false);
                        billpanel.removeAll();
                        totalunpaid();
                        ListBills();
                        billpanel.setVisible(true);
                        // TODO Auto-generated method stub
                        JOptionPane.showMessageDialog(null, "Bill deleted");
                    }
                });

                
                
                JPanel linebill = new JPanel(new GridLayout(1, 4));
             
                JPanel buttbill = new JPanel(new GridLayout(1, 2));
                buttbill.add(jl1);
                buttbill.add(jl2);
                linebill.add(buttbill);
                linebill.add(jl);
                linebill.add(jl0);

                billpanel.add(linebill);


                i++;

            }

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

    }

    public void patientselect() {

        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();

        } catch (Exception e) {}

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
            stmt = conn.createStatement();

            if (stmt.execute("select * from BS.Patient where PName = '" + patsel + "'")) {
                rs = stmt.getResultSet();
            }

            // loop over results
            while (rs.next()) {
                patselid = rs.getInt("PID");
                patseldetails = rs.getString("PAddress") + "<br> Telephone: " + rs.getString("PPhone");

            }

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

    }

    public void totalunpaid() {

        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();

        } catch (Exception e) {}

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
            stmt = conn.createStatement();

            if (stmt.execute("SELECT SUM(BTotal) from BS.Bill WHERE PID = " + patselid + " AND BStatus = 'UNPAID'")) {

                rs = stmt.getResultSet();
            }

            // loop over results
            while (rs.next()) {
                patseluntot = rs.getInt("SUM(BTotal)");

            }

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("logout")) {
            System.exit(0);

        }

    }

}
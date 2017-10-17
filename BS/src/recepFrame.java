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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class recepFrame extends JFrame implements ActionListener {

	String[] patients = new String[50];// list of patients from Sql
	JList list; // jlist for patients
	String[] services = new String[9]; // list of services from sql
	int patselid = 0; // id of patient selected
	String patsel = ""; // name of patiente selected
	String patseldetails = "";// details of the patient selected
	JTextArea textArea = new JTextArea(5, 80);// texare for messages
	JTextField phonereach = new JTextField(20);// phone to call back for
												// messages
	JTextArea textArea2 = new JTextArea(5, 80); // textare for notes of
												// appoiments
	DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
	JFormattedTextField dateappfield = new JFormattedTextField(format);// date
	int ss; // id of service selected
	String ssr; // name of service selected
	int ssrp; // price of service selected

	public recepFrame() {
		recepDash();
	}

	public void recepDash() {
		// Main Frame for RECEPTIONIST
		JFrame recep = new JFrame();
		ListServices();
		recep.setVisible(true);
		recep.setTitle("BS Clinic Software - Reception Dashboard");
		recep.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		recep.setSize(1000, 800);
		recep.setMinimumSize(new Dimension(1000, 800));
		recep.setLayout(new GridLayout(1, 1));

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
		JLabel dash = new JLabel("RECEPTION DASHBOARD");
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
				recep.dispose();
				recepDash();
			}
		});
		JLabel listpat = new JLabel("List of Patients");
		listpat.setHorizontalAlignment(JLabel.CENTER);

		panelList.add(listpat);
		JScrollPane scroll1 = new JScrollPane(list);
		panelList.add(scroll1);

		// panel of activities for reception
		JPanel panelActs = new JPanel(new GridLayout(4, 1));

		JLabel ps = new JLabel("<html>Patient ID:" + patselid + "<br>  Patient Name: <b>" + patsel + "</b><br>"
				+ " Address: " + patseldetails + "</html>", SwingConstants.CENTER);
		ps.setFont(new Font("Helvetica", Font.PLAIN, 20));

		// panel of messages for reception
		JPanel panelMess = new JPanel();
		panelMess.setLayout(new BoxLayout(panelMess, BoxLayout.Y_AXIS));
		panelMess.add(new JLabel("<html><b>New Messages</b></html>"));

		JScrollPane scroll2 = new JScrollPane(textArea);
		scroll2.setMaximumSize(scroll2.getPreferredSize());
		textArea.setEditable(true);
		JPanel notmess = new JPanel(new GridLayout());
		notmess.add(new JLabel("Message"));
		notmess.add(scroll2);
		panelMess.add(notmess);

		phonereach.setMaximumSize(phonereach.getPreferredSize());
		JButton SNM = new JButton("Save New Message");
		SNM.addActionListener(this);
		SNM.setActionCommand("mess");
		SNM.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel phmess = new JPanel(new GridLayout(1, 2));
		phmess.add(new JLabel("Phone to reach on"));
		phmess.add(phonereach);
		panelMess.add(phmess);
		panelMess.add(SNM);

		// panel of messages for reception
		JPanel panelApp = new JPanel();
		panelApp.setLayout(new BoxLayout(panelApp, BoxLayout.Y_AXIS));
		panelApp.add(new JLabel("<html><b>New Appoiment</b></html>"));

		JScrollPane scroll3 = new JScrollPane(textArea2);
		scroll3.setMaximumSize(scroll3.getPreferredSize());
		textArea2.setEditable(true);
		JPanel notapp = new JPanel(new GridLayout());
		notapp.add(new JLabel("Notes for Appoiment"));
		notapp.add(scroll3);
		panelApp.add(notapp);

		JButton SNA = new JButton("Save New Appoiment");
		SNA.addActionListener(this);
		SNA.setActionCommand("app");
		SNA.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel dateapp = new JPanel(new GridLayout(1, 2));
		dateapp.add(new JLabel("Appoiment date yyyy-mm-dd"));
		dateapp.add(dateappfield);
		panelApp.add(dateapp);
		panelApp.add(SNA);

		JPanel panelBill = new JPanel();
		panelBill.setLayout(new BoxLayout(panelBill, BoxLayout.Y_AXIS));

		panelBill.add(new JLabel("<html><b>New Bill</b></html>"));
		JComboBox serlist = new JComboBox(services);
		serlist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ss = serlist.getSelectedIndex();
			}
		});

		panelBill.add(serlist);
		JButton SNB = new JButton("Save New Bill");
		SNB.addActionListener(this);
		SNB.setActionCommand("bill");
		SNB.setAlignmentX(Component.CENTER_ALIGNMENT);

		panelBill.add(SNB);

		panelActs.add(ps);

		panelActs.add(panelMess);
		panelActs.add(panelApp);
		panelActs.add(panelBill);
		panelBody.add(panelList, BorderLayout.CENTER);
		panelBody.add(panelActs);
		panelMain.add(panelHeader, BorderLayout.PAGE_START);
		panelMain.add(panelBody);
		recep.add(panelMain);

		validate();
		repaint();

	}

	public void ListPatients() {

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();

		} catch (Exception e) {
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
			stmt = conn.createStatement();

			if (stmt.execute("select PName from BS.Patient where PStatus='Active' ORDER BY PName")) {
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

	public void patientselect() {

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();

		} catch (Exception e) {
		}

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

	public void ListServices() {

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();

		} catch (Exception e) {
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
			stmt = conn.createStatement();

			if (stmt.execute("select * from BS.Services")) {
				rs = stmt.getResultSet();
			}

			// loop over results
			int i = 0;
			while (rs.next()) {

				services[i] = rs.getString("SDescription") + " €" + rs.getString("SPrice");
				i++;

			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	public void messsql() {
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();

		} catch (Exception e) {
		}

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
			stmt = conn.createStatement();

			String ta = textArea.getText();
			String pr = phonereach.getText();

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			if (stmt.execute("INSERT INTO `BS`.`Message` (`MTime`, `MMessage`, `MPhone`, `MRead`,`PID`) VALUES ('"
					+ timestamp + "', '" + ta + "', '" + pr + "', 'UNREAD', '" + patselid + "');")) {

			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	public void appsql() {
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();

		} catch (Exception e) {
		}

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
			stmt = conn.createStatement();

			String ta2 = textArea2.getText();
			String da = dateappfield.getText();

			if (stmt.execute("INSERT INTO `BS`.`Appoiment` (`ANote`, `ADate`, `PID`) VALUES ('" + ta2 + "', '" + da
					+ "', " + patselid + ");")) {

			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	public void billsqlget() {
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();

		} catch (Exception e) {
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
			stmt = conn.createStatement();

			ss = ss + 1;
			if (stmt.execute("select * from BS.Services WHERE SID = " + ss + ";")) {
				rs = stmt.getResultSet();
			}
			while (rs.next()) {
				ssr = rs.getString("SDescription");
				ssrp = rs.getInt("SPrice");

			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	public void billsqlset() {
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();

		} catch (Exception e) {
		}

		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
			stmt = conn.createStatement();

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			if (stmt.execute("INSERT INTO `BS`.`Bill` (`BTime`, `BServices`, `BTotal`,`PID`,`BStatus`) VALUES ('"
					+ timestamp + "', '" + ssr + "', " + ssrp + ", " + patselid + ", 'UNPAID');")) {

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

		} else if (command.equals("mess")) {

			if (patselid == 0) {
				JOptionPane.showMessageDialog(null, "Please! chose a patient.");

			} else if (textArea.getText().equals("") || phonereach.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Please! fill the message and the phone fields.");

			} else {
				messsql();
				JOptionPane.showMessageDialog(null, "The Message has been save sucesfully.");
			}

		} else if (command.equals("app")) {
			if (patselid == 0) {
				JOptionPane.showMessageDialog(null, "Please! chose a patient.");

			} else if (textArea2.getText().equals("") || dateappfield.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Please! fill the notes and the date fields.");

			} else {
				appsql();
				JOptionPane.showMessageDialog(null, "The appoiment has been save sucesfully.");
			}

		} else if (command.equals("bill")) {

			if (patselid == 0) {
				JOptionPane.showMessageDialog(null, "Please! chose a patient.");

			} else {

				billsqlget();
				billsqlset();

				JOptionPane.showMessageDialog(null, "The Bill has been save sucesfully.");

			}

		}

	}

}

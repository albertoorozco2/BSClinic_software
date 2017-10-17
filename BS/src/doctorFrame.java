import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class doctorFrame extends JFrame implements ActionListener {

	JFrame doctor = new JFrame(); // Main Frame
	JPanel panelMain = new JPanel(); // Main Panel
	JPanel panelBody = new JPanel(new GridLayout(1, 2));// panelbody
	JPanel msgspanel = new JPanel(); // panel
	String[] patients = new String[50]; // list of patients from Sql
	String msgs; // list of bills from sql
	int patselid; // id of patient selected
	String patsel = "no patient selected"; // name of patiente selected
	String patseldetails = "no patient selected"; // details of the patient
												// selected
	String notes;
	String[] medications = new String[12]; // list of services from sql
	int ms;

	String[] columnNames = { "DATE/TIME", "PATIENT", "Notes", };

	Object[][] data = new Object[100][3];

	public doctorFrame() {
		ListAppo();
		ListMsgs();
		billDash();
	}

	public void billDash() {
		JPanel cont = new JPanel();
		JPanel medpre = new JPanel(new GridLayout(1, 2));

		doctor.setVisible(true);
		doctor.setTitle("BS Clinic Software - Doctor Dashboard");
		doctor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		doctor.setSize(1100, 800);
		doctor.setMinimumSize(new Dimension(1100, 800));
		doctor.setLayout(new GridLayout(1, 1));

		msgspanel.setLayout(new BoxLayout(msgspanel, BoxLayout.Y_AXIS));
		// msgspanel.setSize(100, 100);
		// msgspanel.add(new JLabel("NEW MESSAGES"));

		// Main panel cotains all the elemts
		panelMain.setSize(1000, 800);
		panelMain.setLayout(new BorderLayout());

		// panel header contains header title, icon, clock, and log out button.
		JPanel panelHeader = new JPanel(new GridLayout(1, 3));
		JLabel bs = new JLabel(new ImageIcon(getClass().getResource("icon.png")));
		//bs.setIcon(new ImageIcon("icon.png"));
		bs.setHorizontalAlignment(JLabel.CENTER);
		panelHeader.add(bs);
		JLabel dash = new JLabel("DOCTOR DASHBOARD");
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

		// panel actions just contais the list of patients
		JPanel panelList = new JPanel(new GridLayout(3, 1));

		// method to populate the array of patients and list of
		ListPatients();
		JList list = new JList(patients);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFont(new Font("Arial", Font.PLAIN, 18));
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				patsel = (String) list.getSelectedValue();
				reset();

			}
		});

		JScrollPane scroll1 = new JScrollPane(list);
		JScrollPane scroll2 = new JScrollPane(msgspanel);
		JPanel appopanel = new JPanel();

		JLabel NLPL = new JLabel("List of Patients");
		NLPL.setHorizontalAlignment(JLabel.CENTER);
		panelList.add(NLPL);
		panelList.add(scroll1);
		JLabel NLAL = new JLabel("List of Appointments");
		NLAL.setHorizontalAlignment(JLabel.CENTER);
		panelList.add(NLAL);
		// panelList.add(scroll6);

		JTable table = new JTable(data, columnNames);
		table.setSize(800, 200);
		table.setMinimumSize(getSize());
		table.getColumnModel().getColumn(0).setMinWidth(100);

		table.getColumnModel().getColumn(1).setPreferredWidth(40);
		table.getColumnModel().getColumn(2).setPreferredWidth(1);
		table.setPreferredScrollableViewportSize(new Dimension(150, 70));
		table.setFillsViewportHeight(true);
		JScrollPane scroll6 = new JScrollPane(table);
		panelList.add(scroll6);

		JLabel NML = new JLabel("New Messages");
		NML.setHorizontalAlignment(JLabel.CENTER);
		panelList.add(NML);

		panelList.add(scroll2);

		// panel of activities for doctor
		JPanel panelActs = new JPanel(new GridLayout(3, 1));

		JLabel ps = new JLabel("<html>Patient ID:" + patselid + "<br>  Patient Name: <b>" + patsel + "</b><br>"
				+ " Address: " + patseldetails + "<br> </html>", SwingConstants.CENTER);
		ps.setFont(new Font("Helvetica", Font.PLAIN, 20));

		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));

		JButton delete = new JButton("DELETE PATIENT");
		delete.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.add(delete);
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (patselid == 0) {
					JOptionPane.showMessageDialog(null, "Please chose a patient.");

				} else {
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
						if (stmt.execute("UPDATE BS.Patient SET PStatus ='Inactive'  WHERE PID =" + patselid + ";")) {

							// rs = stmt.getResultSet();
						}

						JOptionPane.showMessageDialog(null, "The Patient has been deleted succesfully.");
						reset();

					} catch (SQLException ex) {
						// handle any errors
						System.out.println("SQLException: " + ex.getMessage());
						System.out.println("SQLState: " + ex.getSQLState());
						System.out.println("VendorError: " + ex.getErrorCode());
					}
				}

			}

		});
		JLabel deletelab = new JLabel("CLICK BUTTON TO DELETE ALL PATIENT RECORDS");
		deletelab.setAlignmentX(Component.CENTER_ALIGNMENT);
		cont.add(deletelab);
		cont.add(delete);
		panelActs.add(ps);

		JLabel noteslalbel = new JLabel("Notes and Prescriptions");
		JLabel notesnotes = new JLabel(notes);

		JScrollPane scroll3 = new JScrollPane(notesnotes);
		noteslalbel.setAlignmentX(Component.CENTER_ALIGNMENT);
		cont.add(scroll3);

		JPanel med = new JPanel();
		med.setBorder(BorderFactory.createLineBorder(Color.black));
		med.add(new JLabel("<html><b>New Note<b></html>"));
		JTextArea textArea2 = new JTextArea(7, 18);
		textArea2.setEditable(true);
		JScrollPane scroll4 = new JScrollPane(textArea2);

		med.add(scroll4);

		JButton snnjb = new JButton("Save New Note");
		snnjb.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(snnjb);
		snnjb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (patselid == 0) {
					JOptionPane.showMessageDialog(null, "Please! chose a patient.");
				} else {

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
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());

						if (stmt.execute("INSERT INTO `BS`.`Notes` (`NTime`, `NNote`, `PID`) VALUES ('" + timestamp
								+ "', '" + textArea2.getText() + "', '" + patselid + "');")) {

							rs = stmt.getResultSet();
						}
						JOptionPane.showMessageDialog(null, "New Note has been save.");

						reset();

					} catch (SQLException ex) {
						// handle any errors
						System.out.println("SQLException: " + ex.getMessage());
						System.out.println("SQLState: " + ex.getSQLState());
						System.out.println("VendorError: " + ex.getErrorCode());
					}

				}
			}

		});

		med.add(snnjb);

		JPanel pre = new JPanel();
		pre.setBorder(BorderFactory.createLineBorder(Color.black));

		pre.add(new JLabel("<html><b>New Prescription</b></html>"));
		JComboBox serlist = new JComboBox(medications);
		serlist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ms = serlist.getSelectedIndex();
			}
		});

		pre.add(serlist);

		JButton SNP = new JButton("Save New Prescription");
		SNP.addActionListener(this);
		SNP.setActionCommand("Savepres");
		SNP.setAlignmentX(Component.CENTER_ALIGNMENT);

		JTextField fmtf = new JTextField(20);

		JButton fmjb = new JButton("Find Medication Info");
		fmjb.setAlignmentX(Component.CENTER_ALIGNMENT);

		this.add(fmjb);
		fmjb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				// TODO Auto-generated method stub
				JFrame medi = new JFrame();
				medi.setTitle("Medication Info");
				medi.setSize(600, 800);
				medi.setVisible(true);
				String data = fmtf.getText();
				String medinfo = "<html>YOU TYPED " + data + " THESE MEDICATIONS MATCHED<br><br>";

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
					if (stmt.execute("SELECT * FROM BS.Medication WHERE `MDescription` like '%" + data
							+ "%' OR `MName` like '%" + data + "%' ")) {

						rs = stmt.getResultSet();
					}

					// loop over results
					while (rs.next()) {
						String str = rs.getString("MDescription");
						String parsedStr = str.replaceAll("(.{100})", "$1<br>");
						medinfo = medinfo + "<br> <b>Medication Category: " + rs.getString("MCategory")
								+ " Medication Name:</b> " + rs.getString("MName") + "</b><br> Description<br>"
								+ parsedStr + "<br><br>";

					}

				} catch (SQLException ex) {
					// handle any errors
					System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());
				}

				JLabel medico = new JLabel(medinfo);

				JScrollPane scroll3 = new JScrollPane(medico);
				medi.add(scroll3);
			}

		});

		pre.add(SNP);
		pre.add(new JLabel("<html><br><b>Find Medication Information</b></html>"));
		pre.add(new JLabel("<html>type name or description in the field</html>"));

		pre.add(fmtf);

		pre.add(fmjb);

		medpre.setBorder(BorderFactory.createLineBorder(Color.black));
		medpre.add(med);
		medpre.add(pre);

		panelActs.add(cont);
		panelActs.add(medpre);
		panelBody.add(panelList, BorderLayout.CENTER);
		panelBody.add(panelActs);
		panelMain.add(panelHeader, BorderLayout.PAGE_START);
		panelMain.add(panelBody);
		doctor.add(panelMain);

		validate();
		repaint();

	}

	public void reset() {
		panelBody.removeAll();
		panelMain.removeAll();
		patientselect();
		ListNotes();
		
		// totalunpaid();

		ListMsgs();
		ListMedications();
		billDash();
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

	public void ListNotes() {

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

			if (stmt.execute(
					"select PTime, MID, MCategory, MName from BS.Prescription INNER JOIN BS.Medication ON Prescription.Medication_MID=Medication.MID	where Prescription.Patient_PID = "
							+ patselid + " ORDER BY PTime DESC")) {

				rs = stmt.getResultSet();
			}
			notes = "<html>";
			notes = notes + "LIST PRESCRIPTION / NOTES / APPOINTMENTS<br><br>     PRESCRIPTIONS-------------------------------<br>";
			// loop over results
			while (rs.next()) {

				String PTime = rs.getString("PTime");
				String MCategory = rs.getString("MCategory");
				String MName = rs.getString("MName");

				notes = notes + " Date " + PTime + " Medication " + MCategory + "  " + MName + "<br>";
			}
			samecat();
			
			if (stmt.execute("select NTime, NNote FROM BS.Notes where Notes.PID = " + patselid + " ORDER BY NTime DESC")) {
				rs = stmt.getResultSet();
			}
			notes = notes+ "<br>     NOTES--------------------------------------<br>";
			// loop over results
			while (rs.next()) {

				String NTime = rs.getString("NTime");
				String NNote = rs.getString("NNote");

				notes = notes + " Date " + NTime + " Note " + NNote + "<br>";

			}

			
			if (stmt.execute(
					"SELECT ADate, ANote FROM BS.Appoiment WHERE PID="+patselid+" ORDER BY ADate DESC")) {

				rs = stmt.getResultSet();
				
			}
			notes = notes + "<br>      APPOINTMENTS-------------------------------<br>";
			// loop over results
			while (rs.next()) {

				String ADate = rs.getString("ADate");
				String ANote = rs.getString("ANote");

				notes = notes + " Date " + ADate.substring(0, ADate.length() - 8) + "HRS Medication " + ANote + "<br>";
			}
			
			
			

			// notes = notes+"</html> ";

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	public void ListAppo() {

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

			if (stmt.execute(
					"SELECT ADate, PName, ANote FROM BS.Appoiment INNER JOIN BS.Patient ON Patient.PID = Appoiment.PID ORDER BY ADate DESC")) {

				rs = stmt.getResultSet();
			}
			// loop over results
			int i = 0;
			while (rs.next()) {

				String sdate = rs.getString("ADate");
				String sname = rs.getString("PName");
				data[i][0] = sdate.substring(0, sdate.length() - 8) + "HRS";
				data[i][1] = sname;
				data[i][2] = rs.getString("ANote");
				i++;

			}
			// notes = "</html>";

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	public void samecat() {

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

			if (stmt.execute(
					"SELECT COUNT(MCategory) FROM BS.Medication INNER JOIN BS.Prescription ON Prescription.Medication_MID=Medication.MID WHERE Prescription.Patient_PID = "
							+ patselid + " GROUP BY MCategory  HAVING ( COUNT(MCategory) > 1 )")) {

				rs = stmt.getResultSet();
			}
			while (rs.next()) {

				JOptionPane.showMessageDialog(null, "PATIENT " + patsel
						+ " HAS BEEN PRESCRIBED WITH MEDICATION FROM THE SAME CATEGORY MORE THAN ONE TIME");

				notes = notes
						+ "PATIENT HAS BEEN PRESCRIBED WITH MEDICATION FROM THE SAME CATEGORY MORE THAN ONE TIME<br>";
			}

			//notes = notes + "</html>  ";

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	public void ListMedications() {

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

			if (stmt.execute("SELECT MCategory, MName FROM BS.Medication")) {
				rs = stmt.getResultSet();
			}

			// loop over results
			int i = 0;
			while (rs.next()) {

				medications[i] = "Category: " + rs.getString("MCategory") + " Medication " + rs.getString("MName");
				i++;

			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

	public void ListMsgs() {

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

			if (stmt.execute(
					"select MID, MTime, MMessage, MPhone, MRead, PName from BS.Message INNER JOIN BS.Patient ON Patient.PID=Message.PID WHERE MRead ='UNREAD' ORDER BY MTime DESC")) {
				rs = stmt.getResultSet();
			}

			// loop over results
			msgs = "";
			while (rs.next()) {

				int MID = rs.getInt("MID");
				String MTime = rs.getString("MTime");

				String str = rs.getString("MMessage");
				String MMessage = str.replaceAll("(.{30})", "$1<br>");

				String MPhone = rs.getString("MPhone");
				String MRead = rs.getString("MRead");
				String PName = rs.getString("PName");
				JLabel jl = new JLabel(MRead);
				JLabel jl0 = new JLabel(MTime);
				JLabel jl3 = new JLabel("<html>Msg:" + MMessage + " </html>");

				JLabel jl4 = new JLabel("Phone: " + MPhone);
				JLabel jl5 = new JLabel("Patient: " + PName);

				String blp = "Mark as read";

				JButton jl1 = new JButton(blp);

				this.add(jl1);

				jl1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {

						try {

							Class.forName("com.mysql.jdbc.Driver").newInstance();

						} catch (Exception e) {
						}

						Connection conn = null;
						Statement stmt = null;
						try {
							conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/BS?user=root&password=");
							stmt = conn.createStatement();

							if (stmt.execute("UPDATE BS.Message SET MRead ='READ'  WHERE MID =" + MID + ";")) {
							}

						} catch (SQLException ex) {
							// handle any errors
							System.out.println("SQLException: " + ex.getMessage());
							System.out.println("SQLState: " + ex.getSQLState());
							System.out.println("VendorError: " + ex.getErrorCode());
						}

						msgspanel.setVisible(false);
						msgspanel.removeAll();
						ListMsgs();
						msgspanel.setVisible(true);
						// TODO Auto-generated method stub
						JOptionPane.showMessageDialog(null, "Message status has changed to READ");
					}
				});

				JPanel linebill = new JPanel();
				linebill.setLayout(new BoxLayout(linebill, BoxLayout.Y_AXIS));

				linebill.add(jl);
				linebill.add(jl0);
				linebill.add(jl3);

				linebill.add(jl4);
				linebill.add(jl5);

				linebill.add(jl1);

				msgspanel.add(linebill);

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

	public void presqlset() {
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

			if (stmt.execute("INSERT INTO `BS`.`Prescription` (`PTime`, `Medication_MID`, `Patient_PID`) VALUES ('"
					+ timestamp + "', " + (ms + 1) + ", " + patselid + ")")) {

			}

			reset();

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

		} else if (command.equals("Savepres")) {

			if (patselid == 0) {
				JOptionPane.showMessageDialog(null, "Please! chose a patient.");

			} else {

				presqlset();

				JOptionPane.showMessageDialog(null, "The Prescription has been save sucesfully.");

			}

		}
	}

}
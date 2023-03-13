package org.atm.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ATMOperations {

	public static void main(String[] args)
			throws NumberFormatException, IOException, SQLException, ClassNotFoundException, ParseException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("==============================================================================");
		System.out.println("=====================  WELCOME TO SBI ATM ================================");
		System.out.println("==============================================================================");
		

			System.out.println("==============================================================================");
			System.out.println("===========================  CUSTOMERS DETAILS  ================================");
			System.out.println("==============================================================================");

			System.out.print("\t Enter your Customer Number:");
			long customerNumber = Long.parseLong(br.readLine());
			System.out.print("\t Enter Your PIN:");
			String atmPin = br.readLine();
			try {
				Connection conn = MysqlConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement("select atmPin ,accName from accounts where customerNumber=?");
				ps.setLong(1, customerNumber);
				ResultSet result = ps.executeQuery();
				String PIN = null;
				String name= null;
				
				boolean login = false;
				while (result.next()) {
					PIN = result.getString("atmPin");
					name= result.getString("accName");
					login = true;
				}

				if (PIN.equals(atmPin)) {
					System.out
							.println("==============================================================================");

					System.out.println("===============================  Welcome " + name +"  ====================================");

					System.out
							.println("==============================================================================");
					

					String status = "Y";
					do {

						System.out.println("\t\t  1 --> Deposit Amount");
						System.out.println("\t\t  2 --> Withdraw Amount");
						System.out.println("\t\t  3 --> Fund Transfer");
						System.out.println("\t\t  4 --> Balance Check");
						System.out.println("\t\t  5 --> Change PIN");
						System.out.println("\t\t  6 --> Exit/Logout");
						System.out.println(
								"==============================================================================");
						System.out.print("Enter your choice:");
						int operationCode = Integer.parseInt(br.readLine());
						ResultSet res;
						switch (operationCode) {
						case 1:
							System.out.println("Enter the deposit amount:");
							double depositAmount = Double.parseDouble(br.readLine());

							ps = conn.prepareStatement("select * from accounts where customerNumber=?");
							ps.setLong(1, customerNumber);
							res = ps.executeQuery();
							double existingBalance = 0.0;
							long accId = 0;
							while (res.next()) {
								existingBalance = res.getDouble("accBalance");
								accId = res.getLong("accId");
							}

							existingBalance = existingBalance + depositAmount;

							ps = conn.prepareStatement("update accounts set accBalance=? where customerNumber=?");
							ps.setDouble(1, existingBalance);
							ps.setLong(2, customerNumber);

							if (ps.executeUpdate() > 0) {
								ps = conn.prepareStatement("insert into transactions values(?,?,?,?,?,?)");
								Timestamp timestamp = new Timestamp(System.currentTimeMillis());
								long transactionId = timestamp.getTime();

								ps.setLong(1, transactionId);
								ps.setDate(2, new Date(System.currentTimeMillis()));
								ps.setDouble(3, existingBalance);
								ps.setString(4, "deposit");
								ps.setLong(5, accId);
								ps.setLong(6, accId);

								ps.executeUpdate();

								System.out.println(
										"==============================================================================");
								System.out.println("Balance Updated!!");
								System.out.println("New account balance is :" + existingBalance);
								System.out.println(
										"==============================================================================");

							}

							System.out.println("Do you want to continue?(Y/N)");
							status = br.readLine();

							if (status.equals("n") || status.equals("N")) {
								login = false;
							}

							break;
						case 2:
							System.out.println("Enter the withdraw amount:");
							double withdrawAmount = Double.parseDouble(br.readLine());

							ps = conn.prepareStatement("select * from accounts where customerNumber=?");
							ps.setLong(1, customerNumber);
							res = ps.executeQuery();
							existingBalance = 0.0;
							accId = 0;
							while (res.next()) {
								existingBalance = res.getDouble("accBalance");
								accId = res.getLong("accId");
							}

							existingBalance = existingBalance - withdrawAmount;

							ps = conn.prepareStatement("update accounts set accBalance=? where customerNumber=?");
							ps.setDouble(1, existingBalance);
							ps.setLong(2, customerNumber);

							if (ps.executeUpdate() > 0) {
								ps = conn.prepareStatement("insert into transactions values(?,?,?,?,?,?)");
								Timestamp timestamp = new Timestamp(System.currentTimeMillis());
								long transactionId = timestamp.getTime();

								ps.setLong(1, transactionId);
								ps.setDate(2, new Date(System.currentTimeMillis()));
								ps.setDouble(3, existingBalance);
								ps.setString(4, "withdraw");
								ps.setLong(5, accId);
								ps.setLong(6, accId);

								ps.executeUpdate();

								System.out.println(
										"==============================================================================");
								System.out.println("Balance Updated!!");
								System.out.println("New account balance is :" + existingBalance);
								System.out.println(
										"==============================================================================");

							}

							System.out.println("Do you want to continue?(Y/N)");
							status = br.readLine();

							if (status.equals("n") || status.equals("N")) {
								login = false;
							}

							break;

						case 3:
							System.out.println("Enter the transaction amount:");
							double amount = Double.parseDouble(br.readLine());

							System.out.println("Enter the receiver account Id:");
							long receiverAccId = Long.parseLong(br.readLine());

							// fetching sender account balance
							ps = conn.prepareStatement("select * from accounts where customerNumber=?");
							ps.setLong(1, customerNumber);
							res = ps.executeQuery();
							double senderexistingBalance = 0.0;
							accId = 0;
							while (res.next()) {
								senderexistingBalance = res.getDouble("accBalance");
								accId = res.getLong("accId");
							}

							// fetching receiver account balance
							ps = conn.prepareStatement("select * from accounts where accId=?");
							ps.setLong(1, receiverAccId);
							res = ps.executeQuery();
							double receiverExistingBalance = 0.0;
							long rcvraccId = 0;
							while (res.next()) {
								receiverExistingBalance = res.getDouble("accBalance");
								rcvraccId = res.getLong("accId");
							}

							if (senderexistingBalance > amount && rcvraccId != 0) {
								senderexistingBalance = senderexistingBalance - amount;
								receiverExistingBalance = receiverExistingBalance + amount;

								ps = conn.prepareStatement("update accounts set accBalance=? where accId=?");
								ps.setDouble(1, senderexistingBalance);
								ps.setLong(2, accId);
								ps.executeUpdate();

								ps = conn.prepareStatement("update accounts set accBalance=? where accId=?");
								ps.setDouble(1, receiverExistingBalance);
								ps.setLong(2, rcvraccId);
								ps.executeUpdate();

								ps = conn.prepareStatement("insert into transactions values(?,?,?,?,?,?)");
								Timestamp timestamp = new Timestamp(System.currentTimeMillis());
								long transactionId = timestamp.getTime();

								ps.setLong(1, transactionId);
								ps.setDate(2, new Date(System.currentTimeMillis()));
								ps.setDouble(3, amount);
								ps.setString(4, "transfer");
								ps.setLong(5, accId);
								ps.setLong(6, rcvraccId);

								if (ps.executeUpdate() > 0) {

									System.out.println(
											"==============================================================================");
									System.out.println("Transaction successful!!");
									System.out.println("New account balance is :" + senderexistingBalance);
									System.out.println(
											"==============================================================================");
								} else {
									System.out.println(
											"==============================================================================");
									System.out.println("Transaction failed!!");
									System.out.println(
											"==============================================================================");

								}

								System.out.println("Do you want to continue?(Y/N)");
								status = br.readLine();

								if (status.equals("n") || status.equals("N")) {
									login = false;
								}

							} else if (senderexistingBalance < amount) {
								System.out.println(
										"==============================================================================");
								System.out.println("Insufficient account balance!!");
								System.out.println(
										"==============================================================================");

							} else if (rcvraccId == 0) {
								System.out.println(
										"==============================================================================");
								System.out.println("Invalid receiver id!!");
								System.out.println(
										"==============================================================================");

							}

							break;

						case 4:
							ps = conn.prepareStatement("select * from accounts where customerNumber=?");
							ps.setLong(1, customerNumber);
							res = ps.executeQuery();
							double balance = 0.0;
							while (res.next()) {
								balance = res.getDouble("accBalance");

							}
							System.out.println(
									"==============================================================================");
							System.out.println("Current account balance is :" + balance);
							System.out.println(
									"==============================================================================");
							System.out.println("Do you want to continue?(Y/N)");
							status = br.readLine();

							if (status.equals("n") || status.equals("N")) {
								login = false;
							}

							break;
						case 5:
							System.out.println("Enter the old PIN:");
							String oldPIN = br.readLine();

							System.out.println("Enter the new PIN:");
							String newPIN = br.readLine();

							System.out.println("Re-enter the new PIN:");
							String rePIN = br.readLine();

							ps = conn.prepareStatement("select * from accounts where customerNumber=?");
							ps.setLong(1, customerNumber);

							res = ps.executeQuery();
							String existingPIN = null;
							while (res.next()) {
								existingPIN = res.getString("atmPIN");

							}

							if (existingPIN.equals(oldPIN)) {
								if (newPIN.equals(rePIN)) {
									ps = conn.prepareStatement("update accounts set atmPIN=? where customerNumber=?");
									ps.setString(1, newPIN);
									ps.setLong(2, customerNumber);

									if (ps.executeUpdate() > 0) {
										System.out.println(
												"==============================================================================");
										System.out.println("PIN changed successfully!!");
										System.out.println(
												"==============================================================================");

									} else {
										System.out.println(
												"==============================================================================");
										System.out.println("Problem in PIN changed!!");
										System.out.println(
												"==============================================================================");

									}

								} else {
									System.out.println(
											"==============================================================================");
									System.out.println("New PIN and retype PIN must be same!!");
									System.out.println(
											"==============================================================================");

								}
							} else {
								System.out.println(
										"==============================================================================");
								System.out.println("Old PIN is wrong!!");
								System.out.println(
										"==============================================================================");

							}
							System.out.println("Do you want to continue?(Y/N)");
							status = br.readLine();

							if (status.equals("n") || status.equals("N")) {
								login = false;
							}
							break;

						case 6:
							login = false;
							break;

						default:
							System.out.println("Wrong Choice!!");
							break;

						}

					} while (login);
					System.out.println("==============================================================================");
					System.out.println("Bye. Have a nice day!!");
					System.out.println("==============================================================================");

				} else {
					System.out
							.println("==============================================================================");
					System.out
							.println("================================  Wrong PIN  ============================");
					System.out
							.println("==============================================================================");
				}
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("==============================================================================");
				System.out.println("===========================  Wrong customerNumber/PIN  ========================");
				System.out.println("==============================================================================");

			}

		} 

			
		}
	





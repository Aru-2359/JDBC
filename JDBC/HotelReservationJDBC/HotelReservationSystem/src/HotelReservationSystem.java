import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Arunetri23:5977";

    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM reservations");
            PreparedStatement selectIDStatement = connection.prepareStatement("SELECT reservation_id FROM reservations WHERE reservation_id = ?;");
            PreparedStatement selectExistingRoomStatement = connection.prepareStatement("SELECT reservation_id FROM reservations WHERE room_number = ?;");
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO reservations (guest_name, room_number, contact_number) VALUES (?, ?, ?);");
            PreparedStatement getStatement = connection.prepareStatement("SELECT room_number FROM reservations WHERE reservation_id = ? AND guest_name = ?;");
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE reservations SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ?;");
            PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM reservations WHERE reservation_id = ?;");
            while(true){
                System.out.println();
                System.out.println("HOTEL RESERVATION SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1.Reserve a Room");
                System.out.println("2.View Reservations");
                System.out.println("3.Get Room Number");
                System.out.println("4.Update Reservation");
                System.out.println("5.Delete Reservation");
                System.out.println("0.Exit");
                System.out.println();
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();
                switch (choice) {
                    case 1 -> reserveRoom(insertStatement, selectExistingRoomStatement, sc);
                    case 2 -> viewReservations(selectStatement);
                    case 3 -> getRoomNumber(getStatement, sc);
                    case 4 -> updateReservation(updateStatement, sc);
                    case 5 -> deleteReservation(deleteStatement, selectIDStatement, sc);
                    case 0 -> {
                        exit();
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Invalid Choice!");
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(PreparedStatement preparedStatement, PreparedStatement selectExistingRoomStatement, Scanner sc){
        System.out.print("Enter guest name: ");
        String guestName = sc.next();
        System.out.print("Enter room number: ");
        int roomNumber = sc.nextInt();
        System.out.print("Enter contact number: ");
        String contactNumber = sc.next();
        if(roomReservationExists(selectExistingRoomStatement, roomNumber)){
            System.out.println("Reservation For Room Number " + roomNumber + " already exists!");
            return;
        }
        try{
            preparedStatement.setString(1, guestName);
            preparedStatement.setInt(2, roomNumber);
            preparedStatement.setString(3, contactNumber);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0){
                System.out.println("Reservation Successful!");
            }else{
                System.out.println("Reservation Failed!");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    private static void viewReservations(PreparedStatement preparedStatement){
        try{
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("Current Reservations:");
            while(rs.next()){
                int reservation_id = rs.getInt("reservation_id");
                String guestName = rs.getString("guest_name");
                int roomNumber = rs.getInt("room_number");
                String contactNumber = rs.getString("contact_number");
                String reservationDate = rs.getTimestamp("reservation_date").toString();

                System.out.println("ReservationId :" + reservation_id);
                System.out.println("Guest: " + guestName);
                System.out.println("RoomNumber: " + roomNumber);
                System.out.println("ContactNumber: " + contactNumber);
                System.out.println("ReservationDate: " + reservationDate);
                System.out.println("---------------------------------------------------------------------------------");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    private static void getRoomNumber(PreparedStatement preparedStatement, Scanner sc) {
        try{
            System.out.print("Enter Reservation ID: ");
            int reservationId = sc.nextInt();
            System.out.print("Enter Guest Name: ");
            String guestName = sc.next();

            //String query = "SELECT room_number from reservations WHERE reservation_id = " + reservation_id + "and guest_name = '" + guestName + "';";
            preparedStatement.setInt(1, reservationId);
            preparedStatement.setString(2, guestName);

            try(ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    int roomNumber = rs.getInt("room_number");
                    System.out.println("Room Number for ReservationID : " + reservationId + " and Guest : " + guestName + " is : " + roomNumber);
                } else {
                    System.out.println("No Reservation Found for the given ReservationID and Guest!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void updateReservation(PreparedStatement preparedStatement, Scanner sc){
        System.out.print("Enter Reservation ID: ");
        int reservationId = sc.nextInt();
        System.out.print("Enter guest name: ");
        String newGuestName = sc.next();
        System.out.print("Enter room number: ");
        int newRoomNumber = sc.nextInt();
        System.out.print("Enter contact number: ");
        String newContactNumber = sc.next();

        try{
            preparedStatement.setString(1, newGuestName);
            preparedStatement.setInt(2, newRoomNumber);
            preparedStatement.setString(3, newContactNumber);
            preparedStatement.setInt(4, reservationId);

            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0){
                System.out.println("Reservation Updated Successfully!");
            }else{
                System.out.println("Reservation Update Failed!");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static void deleteReservation(PreparedStatement preparedStatement, PreparedStatement selectIdStatement, Scanner sc){
        try{
            System.out.print("Enter Reservation ID: ");
            int reservationId = sc.nextInt();

            if(!reservationExists(selectIdStatement, reservationId)){
                System.out.println("Reservation not found for the given Reservation ID!");
                return;
            }
            preparedStatement.setInt(1, reservationId);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows > 0){
                System.out.println("Reservation Deleted Successfully!");
            }else{
                System.out.println("Reservation Deletion Failed!");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(PreparedStatement selectIdStatement, int reservationId){
        try{
            selectIdStatement.setInt(1, reservationId);
            ResultSet rs = selectIdStatement.executeQuery();
            return rs.next();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean roomReservationExists(PreparedStatement selectExistingRoomStatement, int roomNumber){
        try{
            selectExistingRoomStatement.setInt(1, roomNumber);
            ResultSet rs = selectExistingRoomStatement.executeQuery();
            return rs.next();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thank You For Using Hotel Reservation System!");
    }
}